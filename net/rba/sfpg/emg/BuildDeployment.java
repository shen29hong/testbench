/**
 * Build_Deployment.java, Jun 2, 2015
 * Copyright to HONSHEN
 */
package net.rba.sfpg.emg;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import net.testbench.sfdc.SFDCPartnerQuery;
import net.testbench.utility.TestBenchUtilities;

/**
 * @author HONSHEN
 *
 */
public class BuildDeployment {

	private SFDCPartnerQuery sfdcpq;
	
	private HashMap<String, String> deployedBuilds; //key=sandbox name, value=build name
	private ArrayList<HashMap<String, String>> deployedBuildList;
	private String buildPromotionObjectName;
	private String buildPromotionObjectAttributes;

	private Logger logger;
	/**
	 * 
	 */
	
	public BuildDeployment() {	
		logger = Logger.getLogger(this.getClass().getName());
	}

	public void setEMGBuildData(final String propsDir) {
		
		Properties sfdcProps = TestBenchUtilities.getProperties(propsDir + "sam_sfdc_properties.xml");
		
		sfdcpq = new SFDCPartnerQuery(sfdcProps, false);
		
		Properties emgHubProps = TestBenchUtilities.getProperties(propsDir + "emghub_properties.xml");
		
		HashMap<String, String> validSandboxes = configValidSanboxes(emgHubProps.getProperty("Sandboxes"));
		
		int checkingPeriod = TestBenchUtilities.getInt(emgHubProps.getProperty("Check_Period"));
		
		String startTime = getTestTimestampBefore(checkingPeriod); // if schedule 30 minutes, pass 30 minutes
		
		buildPromotionObjectName = emgHubProps.getProperty("Build_Promotion_ObjectName");
		buildPromotionObjectAttributes = emgHubProps.getProperty("Build_Promotion_Attributes");
		String buildPromotionCondition = emgHubProps.getProperty("Build_Promotion_Condition");		
		
		buildPromotionCondition += startTime;
		
		deployedBuildList = sfdcpq.getAllRecordList(buildPromotionObjectAttributes, buildPromotionObjectName, buildPromotionCondition);

		if  ( deployedBuildList == null ) {
			logger.info("No build deployed to any sandbox yet.");
			return;
		} 

		HashMap<String, String> buildMapping = sfdcpq.getObjectRecordsForMapping(
				emgHubProps.getProperty("Build_Attributes"), 
				emgHubProps.getProperty("Build_ObjectName"), 
				emgHubProps.getProperty("Build_KeyName"));

		HashMap<String, String> sandboxMapping = sfdcpq.getObjectRecordsForMapping(
				emgHubProps.getProperty("Environment_Component_Instance_Attributes"), 
				emgHubProps.getProperty("Environment_Component_Instance_ObjectName"), 
				emgHubProps.getProperty("Environment_Component_Instance_KeyName"));
		
		setSandboxNames(deployedBuildList, sandboxMapping, buildMapping, validSandboxes);
	}

	private void setSandboxNames(
			final ArrayList<HashMap<String, String>> deployedBuildList, 
			final HashMap<String, String> sandboxMapping, 
			final HashMap<String, String> buildMapping,
			final HashMap<String, String> validSandboxes) {
		
		HashMap<String, String> deployedBuilds = new HashMap<String, String>();
		
		for(HashMap<String, String> deployedBuild : deployedBuildList) {
			String sandboxName = sandboxMapping.get(deployedBuild.get("Environment_Component_Instance__c"));
			String buildName = buildMapping.get(deployedBuild.get("Build_Label__c"));
			String additionalNotes = deployedBuild.get("Additional_Notes__c");

			sandboxName = sandboxName.trim().toLowerCase();
			// filter out the unwanted conditions
			if ( sandboxName == null || 
				 validSandboxes.get(sandboxName) == null || 
				 ! bvtRequired(buildName, additionalNotes) ) {
				continue;
			}
			
			
			
			logger.info("sandbox: " + sandboxName + ", build: " + buildName);
			deployedBuilds.put(sandboxName, buildName);
			if ( updateBuildPromotionStatus(deployedBuild.get("Id"), "BVT in progress") ) {
				logger.info(" succeeded to set status=[BVT in progress] ");
			} else {
				logger.error("  failed to set status=[BVT in progress] ");
			}
		}		

		this.setDeployedBuilds(deployedBuilds);
	}
	
	private boolean bvtRequired(final String buildName, final String addtionalNotes) {
		String tmpName = buildName.toLowerCase();
				
		String tmpNotes = addtionalNotes.toLowerCase();
		if ( tmpName.contains("hotfix") || 
			 tmpName.contains("hot fix") || 
			 tmpNotes.startsWith("boomi") || 
			 tmpNotes.startsWith("csu") ) { // add the exception condition here
			return false;
		}
		
		return true;
	}
	
	private String getTestTimestampBefore(final int minutes) {
		/**
		 * get the time stamp in SFDC time stamp format and time zone - GMT (minutes) before the current time (whenever)
		 */
		Date date= new Date(); //current date NOW
		
		long currentTime = date.getTime(); //the number of milliseconds since January 1, 1970, 00:00:00 GMT upto NOW
		
		long thirtyMins = minutes * 60 * 1000; // in milliseconds;
		
		long thirtyMinsBeforeCurrentTime = currentTime - thirtyMins; // hard to explain it!
		
		Date dateThirtyMinsBeforeCurrentTime = new Date(thirtyMinsBeforeCurrentTime);
		
		DateFormat sfdcTimestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.CANADA);
		
		sfdcTimestamp.setTimeZone(TimeZone.getTimeZone("GMT"));

		String ret = sfdcTimestamp.format(dateThirtyMinsBeforeCurrentTime);
		
		return  ret;
	}
	
	private boolean updateBuildPromotionStatus(final String id, final String status) {
		
		String objectName = this.buildPromotionObjectName;
		String colNames = this.buildPromotionObjectAttributes;
		String condition = " Id ='" + id + "' ";
		String updateFieldName = "Build_Promotion_Status__c";
		String updateFieldValue = status;
		
		boolean success = sfdcpq.updateOneSObjectRecord(objectName, colNames, condition, updateFieldName, updateFieldValue);
				
		return success;		
	}

	private HashMap<String, String> configValidSanboxes(final String src) {		
		
		HashMap<String, String> map = new HashMap<String, String>();
		
		if ( src == null ) {
			return map;
		}
		
		String[] tmp = src.split(";");
		
		for(String n : tmp) {
			map.put(n, "");
		}
		return map;
	}


	public HashMap<String, String> getDeployedBuilds() {
		return deployedBuilds;
	}

	public void setDeployedBuilds(HashMap<String, String> deployedBuilds) {
		this.deployedBuilds = deployedBuilds;
	}

} // end of file
