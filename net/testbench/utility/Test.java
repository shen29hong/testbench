package net.testbench.utility;
/**
 * @author HONSHEN
 *
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import net.testbench.db.TestLinkDBQuery;
import net.testbench.sfdc.SFDCPartnerQuery;
import net.testbench.testlink.TestLinkApi;

public abstract class Test {

	public final String LIST_VIEW_BUTTONS = "view list input buttons";
	public final String LIST_VIEW_COLUMNS = "view list displayed columns";
	public final String DETAILS_BUTTONS = "details input buttons";
	public final String DETAILS_FIELDS = "details displayed fields";

	public final String TR_START = "<tr>";
	public final String TR_END = "</tr>";

	public final String TD_START = "<td>";
	public final String TD_END = "</td>";
	
	public final String[] INFO_SECTIONS = {"view list input buttons", "view list displayed columns", "details input buttons", "details displayed fields"};
	
	private HashMap<String, HashMap<String, ArrayList<HashMap<String, String>>>> testCasesData;
	private Email email;
	private boolean failed = false;
	
	protected String testProjectName;
	protected String testPlanName;
	protected String testPlatform;
	protected String testBuildName = "";
	protected String bugId;

	protected String sandbox = "";

	protected String sfdcpqRequired;
	protected Properties props;
	
	protected String propsDir;
	
	private HashMap<String, HashMap<String, String>> allQaUsers;

	protected SFDCPartnerQuery sfdcpq;
	
	
	public Test() {
		// nothing is required
	}

	public void setUp(final String propsFileName) {
		
		String fileSeparator = System.getProperty("file.separator");
		String userHomeDocsDir = System.getProperty("user.home") + 
				fileSeparator + "Documents" + fileSeparator;
		
		this.propsDir = userHomeDocsDir + "tests" + fileSeparator + "properties_folder" + fileSeparator;
	
		this.props = TestBenchUtilities.getProperties(propsDir + propsFileName);

		String logLocationName = userHomeDocsDir + "tests" + fileSeparator + "logs" + fileSeparator;
				
		String className = this.getClass().getSimpleName();
		
		this.testProjectName = props.getProperty("testlinkProject");
		this.testPlanName = props.getProperty("testlinkTestPlan");
		this.testPlatform = props.getProperty("testlinkPlatform");
		
		if ( this.sandbox.isEmpty() ) {
			this.sandbox = props.getProperty("test env");
		}
		
		if ( this.testBuildName.isEmpty() ) {
			this.testBuildName = props.getProperty(this.sandbox + "Build");
		}
		
		this.testBuildName += " " + this.sandbox;
		
		this.bugId = props.getProperty("bugId");

		logLocationName += className + "_" + this.sandbox + ".log";
		
		System.setProperty("log_name", logLocationName);
		
		org.apache.log4j.PropertyConfigurator.configure( propsDir + "log4j.properties");

		String qaUserRequired = this.props.getProperty("qaUserRequired");
		
		if ( qaUserRequired != null && qaUserRequired.toLowerCase().equals("yes") ) {
			setAllQaUsers(propsDir);
		}
		
		sfdcpqRequired = this.props.getProperty("sfdcpqRequired");
	
		if ( sfdcpqRequired != null && sfdcpqRequired.toLowerCase().equals("yes") ) {
			
			Properties sfdcProps = TestBenchUtilities.getProperties(propsDir + this.sandbox + "_sfdc_properties.xml");
			
			sfdcpq = new SFDCPartnerQuery(sfdcProps, false);
		}
		
		setFailed(false);
		
		Properties bvtEmailProps = TestBenchUtilities.getProperties(propsDir + "bvt_email_properties.xml");
		email = new Email(bvtEmailProps);		
	}

	public void sendReportInEmail(final String project, final String subject, final String body) {
		email.setSubject(subject);
		email.setBody(body);
		email.setFailed(this.failed);
		email.send(project, false);	

	}
//	public void setUp(final String propsFileName, final String[] testSuites) {
//		
//		String fileSeparator = System.getProperty("file.separator");
//		String userHomeDocsDir = System.getProperty("user.home") + 
//				fileSeparator + "Documents" + fileSeparator;
//		
//		this.propsDir = userHomeDocsDir + "tests" + fileSeparator + "properties_folder" + fileSeparator;
//	
//		this.props = TestBenchUtilities.getProperties(propsDir + propsFileName);
//
//		String logLocationName = userHomeDocsDir + "tests" + fileSeparator + "logs" + fileSeparator;
//				
//		String className = this.getClass().getSimpleName();
//		
//		lockFilePathAndName = logLocationName + className + ".lock";
//		
//		if ( this.isTestLocked() ) {
//			TestBenchUtilities.say("The test " + className + " is in progress ... skipping");
//			System.exit(0);
//		} 
//		
//		if ( ! this.lockTest() ) {
//			TestBenchUtilities.sayError(" Faield to lock the test " + className + ", aborting");
//			System.exit(1);			
//		}		
//		
//		this.testProjectName = props.getProperty("testlinkProject");
//		this.testPlanName = props.getProperty("testlinkTestPlan");
//		this.testPlatform = props.getProperty("testlinkPlatform");
//		this.sandbox = props.getProperty("test env");
//		
//		this.passRateThreshold = TestBenchUtilities.getFloat(props.getProperty("PassRateThreashold"));
//		
//		this.bugId = props.getProperty("bugId");
//
//		logLocationName += className + "_" + this.sandbox + ".log";
//		
//		System.setProperty("log_name", logLocationName);
//		
//		org.apache.log4j.PropertyConfigurator.configure( propsDir + "log4j.properties");
//		
//		HashMap<String, String> validSandboxes = configValidSanboxes(props.getProperty("Sandboxes"));
//		
//		String apiPropertyFileName = "testlink_api_properties.xml";
//		tlapi = new TestLinkApi(TestBenchUtilities.getProperties(this.propsDir + apiPropertyFileName));
//		tlapi.initializeTestLink();
//		tlapi.setValidSandboxes(validSandboxes);
//		
//		this.testBuildName = "";
//		
//		ArrayList<String> buildNames = this.getLatestNotExecutedBuilds(testSuites, this.passRateThreshold);
//		
//		if ( buildNames == null || buildNames.size() == 0 ) {
//			this.testBuildName = props.getProperty(this.sandbox + "Build");
//			TestBenchUtilities.say(" No build found from TestLink, running test with " + this.testBuildName);
//		} else {
//			for(String bname : buildNames) {
//				if ( bname.endsWith(this.sandbox) ) {
//					this.testBuildName = bname;
//					break;
//				}
//			}
//		}
//		if ( this.testBuildName.isEmpty() ) {
//			TestBenchUtilities.sayError(" the required build is not deployed to " + this.sandbox + " yet, no test done.");
//			this.unlockTest();
//			System.exit(1);			
//		}
////		this.testBuildName = buildNames.get(0);
////		String[] tmp = this.testBuildName.split("\\s");
////		if ( tmp.length == 3 ) {
////			this.sandbox = tmp[2].trim();
////		} else {
////			TestBenchUtilities.sayError(" can't get the sandbox name due to incorrect build name format :" + this.testBuildName + ", aborting .. ");
////			this.unlockTest();
////			System.exit(1);
////		}			
//
//		String qaUserRequired = this.props.getProperty("qaUserRequired");
//		
//		if ( qaUserRequired != null && qaUserRequired.toLowerCase().equals("yes") ) {
//			setAllQaUsers(propsDir);
//		}
//		
//		sfdcpqRequired = this.props.getProperty("sfdcpqRequired");
//	
//		if ( sfdcpqRequired != null && sfdcpqRequired.toLowerCase().equals("yes") ) {
//			
//			Properties sfdcProps = TestBenchUtilities.getProperties(propsDir + this.sandbox + "_sfdc_properties.xml");
//			
//			sfdcpq = new SFDCPartnerQuery(sfdcProps, false);
//		}
//		
//		setFailed(false);
//		
//	}
	
//	private HashMap<String, String> configValidSanboxes(final String src) {
//		
//		
//		HashMap<String, String> map = new HashMap<String, String>();
//		
//		if ( src == null ) {
//			return map;
//		}
//		
//		String[] tmp = src.split(";");
//		
//		for(String n : tmp) {
//			map.put(n, "");
//		}
//		return map;
//	}
	public void saveTestResultsToTestLink(final ArrayList<HashMap<String, String>> testResults) {
		
//		TestLinkApi tlapi = new TestLinkApi();
		String apiPropertyFileName = "testlink_api_properties.xml";
		
		TestLinkApi tlapi = new TestLinkApi(TestBenchUtilities.getProperties(this.propsDir + apiPropertyFileName));
		
		tlapi.initializeTestLink();
		
		tlapi.setTesPlanIdByName( this.testPlanName, this.testProjectName);
		
		tlapi.setBuildIdByName(this.testBuildName);
		tlapi.setBugId(this.bugId);	
		
		tlapi.reportTestResults(this.testPlatform, testResults);
	}

	public String getEmailFromTestResults(final boolean fullReport, final ArrayList<HashMap<String, String>> testResults) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("<p><span class=\"item\">TestLink project:</span> <span class=\"object\">" + this.testProjectName + "</span></p>\n");
		sb.append("<p><span class=\"item\">TestLink test plan:</span> <span class=\"object\">" + this.testPlanName + "</span></p>\n");
		sb.append("<p><span class=\"item\">Build under test:</span> <span class=\"object\">" + this.testBuildName + "</span></p>\n");
		sb.append("<p><span class=\"item\">Sandbox under test:</span> <span class=\"object\">" + this.sandbox + "</span></p>\n");		
		
		sb.append("<p><span class=\"item\">Total test cases:</span> <span class=\"number\">" + testResults.size() + "</span></p>\n");
		
		String resultsWithError =  "";
		StringBuilder fullReportBody = new StringBuilder();
		
		int errCount = 0;
		
		for(int idx=0; idx<testResults.size(); idx++) {
			
			Map<String, String>  ts = testResults.get(idx);
			String tcName = ts.get("tc_name");
			String notes = ts.get("notes");
		
			String status = ts.get("result");

			String tmp = "<tr><td><span class=\"" + status + "\">" + (status.equals("pass") ? "PASS" : "FAIL") + "</span></td><td>" + tcName + "</td><td>" + notes + "</td></tr>";
			fullReportBody.append(tmp);

			if ( status != null && status.equals("fail") ) {
				resultsWithError += "<tr><td><span class=\"fail\">FAIL</span></td><td>" + tcName + "</td><td>" + notes + "</td></tr>"; 
				errCount++;
				setFailed(true);
			}			
		}
		if ( errCount > 0 ) {
			sb.append("<p><span class=\"item\">Test Results:</span> <span class=\"error\">Completed with " + errCount + " error" + (errCount > 1 ? "s" : "")+ ". The details are followed:</span></p>");
			setFailed(true);
		}
		
		if ( fullReport ) {
			sb.append("<table class=\"time_period\">\n");
			sb.append("<tr><th>Result</th><th>Test case name</th><th>Notes</th></tr>\n" + fullReportBody);
			sb.append("</table>\n");

		} else {
			if ( errCount == 0 ) {
				sb.append("<p><span class=\"item\">BVT Results:</span> <span class=\"pass\">PASS</span></p>");
			} else {			
				sb.append("<p><span class=\"item\">BVT Results:</span> <span class=\"error\">Completed with " + errCount + " error" + (errCount > 1 ? "s" : "")+ ". The details are followed:</span></p>");
				sb.append("<table class=\"time_period\">\n");
				sb.append("<tr><th>Result</th><th>Test case name</th><th>Notes</th></tr>\n" + resultsWithError);
				sb.append("</table>\n");
				this.failed = true;
			}	
		} 
		
		return sb.toString();
	}
	
	public HashMap<String, String> getTestCasesFromTestLink() {
		
		String apiPropertyFileName = "testlink_api_properties.xml";
		
		TestLinkApi tlapi = new TestLinkApi(TestBenchUtilities.getProperties(this.propsDir + apiPropertyFileName));
		
		tlapi.initializeTestLink();
		
		tlapi.setTesPlanIdByName( this.testPlanName, this.testProjectName);
		
		return tlapi.getTcInfoForTestPlan();
	}

	public HashMap<String, String> getTestCasesFromTestLink(final String[] testSuites) {
		
		String apiPropertyFileName = "testlink_api_properties.xml";
		
		TestLinkApi tlapi = new TestLinkApi(TestBenchUtilities.getProperties(this.propsDir + apiPropertyFileName));
		
		tlapi.initializeTestLink();
		
		tlapi.setTesPlanIdByName( this.testPlanName, this.testProjectName);
		tlapi.setAllTestCases(this.testProjectName, this.testPlanName, testSuites);
		return tlapi.getTcInfoForTestPlan();
	}
	
//	public ArrayList<String> getLatestNotExecutedBuilds(final String[] testSuites, final float passRateThreshold) {
//		
//		return tlapi.getLatestNotExecutedBuildsBy(this.testProjectName, this.testPlanName, testSuites, passRateThreshold);
//	}

	public void showTestResults(final ArrayList<HashMap<String, String>> testResults) {
		
		for(HashMap<String, String> result : testResults) {
			
			TestBenchUtilities.showMap(result);
		}
	}

	public HashMap<String, String> getTestUser(final String userInfo) {	
		
		for(String username : allQaUsers.keySet()) {
			if ( username.startsWith(userInfo+"@") && username.endsWith(this.sandbox) ) {
				HashMap<String, String> testUser = new HashMap<String, String>();
				testUser.put("username", username);
				testUser.put("password", allQaUsers.get(username).get("password"));
				testUser.put("fullname", getFullName(allQaUsers.get(username).get("summary")));
				return testUser;
			}
		}
		
		return null;
	}

	private String getFullName(final String src) {
		String ret = "";
		String startTag = "<span class=\"light_passed\">";
		int start = src.indexOf(startTag);
		
		if ( start != -1 ) {
			start += startTag.length();
		}
		int end = src.indexOf("</span>", start);
		
		if ( end != -1 ) {
			ret = src.substring(start, end);
		}
		return ret;
	}
	
	public SFDCPartnerQuery getSfdcpq() {
		return sfdcpq;
	}

	private void setAllQaUsers(final String propsDir) {
		
		String dbPropertyFileName = "testlink_db_properties.xml";
		
		TestLinkDBQuery tlDBQuery = new TestLinkDBQuery(TestBenchUtilities.getProperties(this.propsDir + dbPropertyFileName));

		allQaUsers = tlDBQuery.getAllQaUserRecords();
						
	}

//	private Map<String, String> getExecutionResults(final String propsDir, final String queryStmt, final String keyName, final String valueName) {
//		
//		String dbPropertyFileName = "testlink_db_properties.xml";
//		
//		TestLinkDBQuery tlDBQuery = new TestLinkDBQuery(TestBenchUtilities.getProperties(this.propsDir + dbPropertyFileName));
//
//		Map<String, String> executionResults = tlDBQuery.getCustomeFieldsRecords(queryStmt, keyName, valueName);
//		
//		return executionResults;
//	}


	public void setTestCasesInfo() {
		
		HashMap<String, String> testCasesInfo = getTestCasesFromTestLink();
		
		testCasesData = new  HashMap<String, HashMap<String, ArrayList<HashMap<String, String>>>>();
		
		for(String tcName : testCasesInfo.keySet()) {
			
			
			TestBenchUtilities.say("tc: " + tcName);
			String tcSummary =  testCasesInfo.get(tcName);
			TestBenchUtilities.say("summary: " + tcSummary);
			
			testCasesData.put(tcName, getTestCaseSummaryData(tcSummary));
		}		
	}
	
	public void showTestCasesInfo() {
		
		
		for(String tcName : testCasesData.keySet()) {
			
			TestBenchUtilities.say( "TC name: " + tcName);
			
			for(int i=0; i<INFO_SECTIONS.length; i++) {
				String section = INFO_SECTIONS[i];
				TestBenchUtilities.say( section + ": ");
				
				if ( testCasesData.get(tcName) == null ) {
					TestBenchUtilities.say( "\t\t\t\tno data ");
					continue;
				}
				ArrayList<HashMap<String, String>> dataList = testCasesData.get(tcName).get(section);
				for(int j=0; j<dataList.size(); j++) {
					HashMap<String, String> data = dataList.get(j);
					
					String info = "";
					for(String key : data.keySet() ) {
						info += key + "\t";
					}
					info += "\n";
					for(String key : data.keySet() ) {
						info += data.get(key) + "\t";
					}
					TestBenchUtilities.say(info);
				}				
			}
		}
		
	}
	private HashMap<String, ArrayList<HashMap<String, String>>> getTestCaseSummaryData(final String summary) {
		
		HashMap<String, ArrayList<HashMap<String, String>>> testCasesSummaryData = new HashMap<String, ArrayList<HashMap<String, String>>>();
		
		String section = "";
		
		for(int i=0; i<INFO_SECTIONS.length; i++) {
			section = INFO_SECTIONS[i];
			
			int start = summary.indexOf(section);
			
			if ( start == -1) {
				TestBenchUtilities.sayError("test case summary: " + summary + " not contain any information for: " + section);
				return null;
			}
			start += section.length();
			
			start = summary.indexOf("<table", start);
			
			if ( start == -1) {
				TestBenchUtilities.sayError("test case summary: " + summary + " not contain any information for: <table");
				return null;
			}
			
			int end = summary.indexOf("</table>", start);
			
			if ( end == -1) {
				TestBenchUtilities.sayError("test case summary: " + summary + " not contain any information for: </table>");
				return null;
			}
			
			String tbody = summary.substring(start, end);
			
			ArrayList<HashMap<String, String>> sectionData = getSectionData(tbody);
			testCasesSummaryData.put(section, sectionData);
		}
		
		return testCasesSummaryData;
	}
	
	private ArrayList<HashMap<String, String>> getSectionData(final String tbody) {
		
		ArrayList<HashMap<String, String>> tbodyData = new ArrayList<HashMap<String, String>>();
		
		int begin = tbody.indexOf(TR_START);
		
		if ( begin == -1 ) {
			TestBenchUtilities.sayError("test case summary table body: " + tbody + " not contain any information for: " + TR_START);
			return tbodyData;
		}
		begin += TR_START.length();
		
		int end = tbody.indexOf(TR_END, begin);
		
		String headerRow = tbody.substring(begin, end);
		
		ArrayList<String> headers = getCellData(headerRow);
		
		end += TR_END.length();
		
		int start = tbody.indexOf(TR_START, end);
		
		if ( start == -1 ) {
			HashMap<String, String> tInfo = getInfoData(headers, headers);			
			tbodyData.add(tInfo);
		}
		
		while ( start != -1 ) {
			start += TR_START.length();
			end = tbody.indexOf(TR_END, start);
			
			String dataRow = tbody.substring(start, end);
			
			ArrayList<String> cells = getCellData(dataRow);
			
			HashMap<String, String> tInfo = getInfoData(headers, cells);
			
			tbodyData.add(tInfo);
			
			start = tbody.indexOf(TR_START, (end + TR_END.length()));
		}

		return tbodyData;
	}
	
	
	private ArrayList<String> getCellData(final String row) {
		ArrayList<String> data = new ArrayList<String>();
		
		int start = row.indexOf(TD_START);
		
		if ( start == -1 ) {
			TestBenchUtilities.sayError("test case summary table body row: " + row + " not contain any information for: " + TD_START);
			return data;			
		}
		
		while ( start != -1 ) {
			start += TD_START.length();
			int end = row.indexOf(TD_END, start);
			
			String cellData = row.substring(start, end);
			
			data.add(cellData);
			start = row.indexOf(TD_START, (end + TD_END.length()));
		}
		
		return data;
	}
	
	private HashMap<String, String> getInfoData(final ArrayList<String> headers, final ArrayList<String> cells) {
		HashMap<String, String> tInfo = new HashMap<String, String>();
		
		if ( headers.size() != cells.size() ) {
			TestBenchUtilities.sayError("test case summary table header and cell: size not matched");
			return tInfo;
		}
		
		for(int i=0; i<headers.size(); i++) {
			String key = headers.get(i);
			String value = cells.get(i);
			tInfo.put(key, value);
		}
		return tInfo;
	}

	public String getTestBuildName() {
		return testBuildName;
	}

	public void setTestBuildName(String testBuildName) {
		this.testBuildName = testBuildName;
	}

	public String getSandbox() {
		return sandbox;
	}

	public void setSandbox(String sandbox) {
		this.sandbox = sandbox;
	}
	
	public boolean isFailed() {
		return failed;
	}

	public void setFailed(boolean failed) {
		this.failed = failed;
	}

}
