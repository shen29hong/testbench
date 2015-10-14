/**
 * 
 */
package net.testbench.testlink;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import net.testbench.utility.TestBenchUtilities;
import br.eti.kinoshita.testlinkjavaapi.TestLinkAPI;
import br.eti.kinoshita.testlinkjavaapi.constants.ActionOnDuplicate;
import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionType;
import br.eti.kinoshita.testlinkjavaapi.constants.TestCaseDetails;
import br.eti.kinoshita.testlinkjavaapi.constants.TestImportance;
import br.eti.kinoshita.testlinkjavaapi.model.Build;
import br.eti.kinoshita.testlinkjavaapi.model.Execution;
//import br.eti.kinoshita.testlinkjavaapi.model.Execution;
import br.eti.kinoshita.testlinkjavaapi.model.Platform;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import br.eti.kinoshita.testlinkjavaapi.model.TestPlan;
import br.eti.kinoshita.testlinkjavaapi.model.TestProject;
import br.eti.kinoshita.testlinkjavaapi.model.TestSuite;
import br.eti.kinoshita.testlinkjavaapi.util.TestLinkAPIException;

/**
 * @author honshen
 *
 */
public class TestLinkApi {
	/**
	 * hard-coded to tl10
	 */
//	private String url = "http://10.20.71.80:80/tl12/lib/api/xmlrpc/v1/xmlrpc.php";	
//	private String devKey = "c9c92d972142e583bd0de88716bcfea8";// testlink: "0856292c39034cc525ff4f91627e8a9f";
	
	private String url = "http://10.20.71.80:80/testlink/lib/api/xmlrpc/v1/xmlrpc.php";
	private String devKey = "0856292c39034cc525ff4f91627e8a9f";
	private TestLinkAPI api = null;

	
	private HashMap<String, Integer> tcIdsForTestPlan = null;
	private ArrayList<String> tcNamesForTestPlan = null;
	private HashMap<String, String> tcInfoForTestPlan = null;
	
	private HashMap<String, TestCase> testCasesForTestPlan = null;
	
	private Integer testPlanId = 0;
	
	private String testPlan = "";
	private String testProject = "";
	private String testSuite = "";
	
	private String buildName = "";
	private Integer buildId = 0;
	private String bugId = "";
	
	private HashMap<String, String> validSandboxes;
	/**
	 * 
	 */
 	public TestLinkApi() {
		// TODO Auto-generated constructor stub
	}

 	public TestLinkApi(Properties props) {
		// TODO Auto-generated constructor stub
 		url = props.getProperty("testlink_url");
 		devKey = props.getProperty("devKey");
	}
 	
	public boolean initializeTestLink() {
		URL testlinkURL = null;
	        
	    try {
	    	testlinkURL = new URL(url);   
            api = new TestLinkAPI(testlinkURL, devKey);
            
            return true;
		} catch ( MalformedURLException mue )   {
			mue.printStackTrace( System.err );
			System.exit(-1);
		} catch( TestLinkAPIException te) {
			te.printStackTrace( System.err );
			System.exit(-1);
		} catch( Exception te) {
			te.printStackTrace();
		}

	    return false;
	}
	
	public void setTesPlanIdByName( final String name, final String project ) {
	    
		this.testPlan = name;
		this.testProject = project;
		
	    TestPlan tp = api.getTestPlanByName(name, project);
	    
	    if ( tp == null ) {
	    	api.createTestPlan(name, project, "Please update Test Plan Details!!!!", true, true);
	    	
	    	tp = api.getTestPlanByName(name, project);
	    }
	    
	    if ( tp != null ) {
	    
	    	this.testPlanId = tp.getId();
	    }
	    
	    
	    setAllTestCaseIdsByTestSuiteIds(getAllTestSuiteIdsByTestPlanId(this.testPlanId));
//	    return 0;
	}
	
	public void setAllTestCases(final String projectName, final String testPlanName, final String[] testSuiteNames ) {
		
	    this.testPlan = testPlanName;
		this.testProject = projectName;
//		this.testSuite = testSuiteName;
		
	    TestPlan tp = api.getTestPlanByName(testPlanName, projectName);
	    
	    if ( tp == null ) {
	    	TestBenchUtilities.sayError("Make sure the test plan " + testPlanName + " exists in test project " + projectName);
	    	
	    } else {
	    	this.testPlanId = tp.getId();	    
	    
		    Integer suiteId = getTestSuiteIdByTestPlanIdAndTestSuiteName(this.testPlanId, testSuiteNames);
		    
		    setAllTestCaseIdsByTestSuiteId(suiteId);
		}
	}

	public ArrayList<String> getLatestNotExecutedBuildsBy(final String testProject, final String testPlan, final String[] testSuites, final float passRateThreshold) {
		/*
		 * 	tcIdsForTestPlan.put(tcName, tcId);
			tcNamesForTestPlan.add(tcName);
		 */		
	    TestPlan tp = api.getTestPlanByName(testPlan, testProject);
	   
	    if ( tp == null ) {
	    	TestBenchUtilities.say(" not valid test project and test plan " + testProject + " " + testPlan + " aborting ...");
	    	return null;
	    }

		ArrayList<String> buildNames = new ArrayList<String>();
		setAllTestCases(testProject, testPlan, testSuites);
		int total = this.testCasesForTestPlan.size();
		TestBenchUtilities.say("total  test cases " + total );
		
		Build latestBuild = api.getLatestBuildForTestPlan(tp.getId());
		String tmpBuildName = latestBuild.getName();
		
		ArrayList<Build> latestBuilds = getLatestBuilds(tmpBuildName);
		
		for(Build bd : latestBuilds) {
			total = this.testCasesForTestPlan.size();
			Integer bId = bd.getId();
			TestBenchUtilities.say("latest build id " + bd.getId() + ", name " + bd.getName());
			
			HashMap<Integer, String> buildIds = new HashMap<Integer, String>();
			
			float passed = 0;
			float failed = 0;
			float notrun = 0;
			
			for(String tcName : this.testCasesForTestPlan.keySet()) {
							
				TestCase tc = this.testCasesForTestPlan.get(tcName);
				String fullexternalId = tc.getFullExternalId();
				String eId = fullexternalId.substring(9);
				int externalId = TestBenchUtilities.getInt(eId);
				try {
					Execution e = api.getLastExecutionResult(this.testPlanId, tc.getId(), externalId);
					ExecutionStatus es = e.getStatus();
					String status = es.toString();
					
					Integer buildId = e.getBuildId();
					
					TestBenchUtilities.say("tc name: " + tcName + ", version " + tc.getVersion() + ", execution status: " + es.toString() + ", build id: " + buildId) ;
					buildIds.put(buildId, "executed on " + tcName);
					
					if ( buildId.equals(bId) ) {
						if ( status.equals("p") ) {
							passed++;
						} else if ( status.equals("f") ) {
							failed++;
						} else {
							notrun++;
						}
					} 
				} catch (TestLinkAPIException e) {
					
//					e.printStackTrace();
					
					String msg = e.getMessage();
					
					if ( msg.contains("is not associated with Test Plan") ) {
//						this.testCasesForTestPlan.remove(tcName);
						total--;
					}
				}
			}
			
//			total = this.testCasesForTestPlan.size();
			
			TestBenchUtilities.say("updated total test cases: " + total);
			
			float passedRate = (passed / total) * 100;
			float failedRate = (failed / total) * 100;
			float notrunRate = (notrun/ total) * 100;
			DecimalFormat df = new DecimalFormat("#.00");
			
			TestBenchUtilities.say("passed rate: " + df.format(passedRate) + "%");
			TestBenchUtilities.say("failed rate: " + df.format(failedRate) + "%");
			TestBenchUtilities.say("not run rate: " + df.format(notrunRate) + "%");
			
			if ( passedRate < passRateThreshold ) {
				buildNames.add( bd.getName() );
			}
		}
		return buildNames;
	}
	
	public void setBuildIdByName( final String name ) {
    	
		this.buildName = name;
		boolean found = false;
		if ( this.testPlanId == 0 ) {
			System.out.println("Test plan id not set");
			return;
		}
	
		Build[] bds = api.getBuildsForTestPlan(this.testPlanId);
    
		if ( bds != null ) {
			for(Build bd : bds ) {
//				System.out.println(bd.getName());
		
				if ( bd.getName().equals(buildName)) {
					buildId = bd.getId();
					found = true;
				}
			}
		}
	    
		if ( !found ) {
			Build bd = api.createBuild(this.testPlanId, name, name + " created by TestLink API, please update the build description!!!");
			this.buildId = bd.getId();
		}
	}

	public void createBuild( final String buildName, final String buildNotes ) {
    	
		this.buildName = buildName;
		
		if ( this.testPlanId == 0 ) {
			System.out.println("Test plan id not set");
			return;
		}
	
		Build[] bds = api.getBuildsForTestPlan(this.testPlanId);
    
		Build createdBuild = null;
		
		if ( bds != null ) {
			for(Build bd : bds ) {
		
				if ( bd.getName().equals(buildName)) {
					createdBuild = bd;
					break;
				}
			}
		}
	    
		if ( createdBuild != null ) { // update the build notes
			String existingNotes = createdBuild.getNotes();
			if ( existingNotes.contains("BVT in progress") ) {
				createdBuild.setNotes(buildNotes);
			}
		} else { // create the build
			Build bd = api.createBuild(this.testPlanId, buildName, buildNotes);
//			this.buildId = bd.getId();
		}
	}

	public void setBugId(String bugId) {
		this.bugId = bugId;
	}

	private ArrayList<Build> getLatestBuilds(final String tmpName) {
		
		ArrayList<Build> buildIds = new ArrayList<Build>();
		String[] tmp = tmpName.split(" ");
		
		if ( tmp.length != 3 ) {
			TestBenchUtilities.sayError("  incorrect build name format: " + tmpName + ", aborting ..");
			return buildIds;
		}
		
		String buildPrefix = tmp[0] + " " + tmp[1];
//		int start = tmpName.indexOf("test04f");
//		
//		if ( start == -1 ) {
//			start = tmpName.indexOf("uat04f");
//		}
//		
//		if ( start != -1 ) {
//			start--;
//			buildPrefix = tmpName.substring(0, start);
//		} 

		Build[] bds = api.getBuildsForTestPlan(this.testPlanId);
	    
		if ( bds != null ) {
			for(Build bd : bds ) {
				String bname = bd.getName();
				String[] t = bname.split(" ");
				if ( t.length != 3 ) {
					continue;
				}
				if ( bname.startsWith(buildPrefix) && this.validSandboxes.get(t[2]) != null ) {
					buildIds.add(bd);
				}
			}
		}

		return buildIds;
		
	}
	private Integer[] getAllTestSuiteIdsByTestPlanId(final Integer tpId) {
		 TestSuite[] tss = api.getTestSuitesForTestPlan(tpId);

		 return getIds(tss);		
	}

	private Integer getTestSuiteIdByTestPlanIdAndTestSuiteName(final Integer tpId, final String[] testSuiteNames) {
		 
		TestSuite[] tss = api.getTestSuitesForTestPlan(tpId);
		 
		int idx = 0;
		int size = testSuiteNames.length;
		if ( size < 1 ) {
			TestBenchUtilities.sayError("No test suite names defined");
			return null;
		}
		
		Integer tsId = null;
		
		while ( idx < size ) {
			for(TestSuite ts : tss) {
				 String name = ts.getName();
				 
				 if ( name != null && name.startsWith(testSuiteNames[idx]) ) {
					 idx++;
					 tsId = ts.getId();
					 tss = api.getTestSuitesForTestSuite(tsId);
					 break;
				 }
			}
		}

		 return tsId;		
	}

	private void setAllTestCaseIdsByTestSuiteIds(final Integer[] tss) {
		
		tcIdsForTestPlan = new HashMap<String, Integer>();
		tcNamesForTestPlan = new ArrayList<String>();
		tcInfoForTestPlan = new HashMap<String, String>();
		
		for(int idx=0; idx<tss.length; idx++ ) {
			TestCase[] tcs = api.getTestCasesForTestSuite(tss[idx], true, TestCaseDetails.FULL); 
			
			for(int idy=0; idy<tcs.length; idy++ ) {
				
				TestCase tc = tcs[idy];
				Integer tcId= tc.getId();
				String tcName = tc.getName();
				
				String summary = tc.getSummary();
				tcInfoForTestPlan.put(tcName, summary);
				
				tcIdsForTestPlan.put(tcName, tcId);
				tcNamesForTestPlan.add(tcName);
			}
		}
		
//		return testcaseIds;
	}

	private void setAllTestCaseIdsByTestSuiteId(final Integer tssId) {
		
		tcIdsForTestPlan = new HashMap<String, Integer>();
		tcNamesForTestPlan = new ArrayList<String>();
		tcInfoForTestPlan = new HashMap<String, String>();
		testCasesForTestPlan = new HashMap<String, TestCase>();		

		TestCase[] tcs = api.getTestCasesForTestSuite(tssId, true, TestCaseDetails.FULL); 
		
		for(int idy=0; idy<tcs.length; idy++ ) {
			
			TestCase tc = tcs[idy];
			
			Integer tcId= tc.getId();
			String tcName = tc.getName();
			
			
			String summary = tc.getSummary();
			tcInfoForTestPlan.put(tcName, summary);
			
			tcIdsForTestPlan.put(tcName, tcId);
			tcNamesForTestPlan.add(tcName);
			
			if ( testCasesForTestPlan.get(tcName) == null ) {	
				testCasesForTestPlan.put(tcName, tc);
			} else {
				Integer curr = tc.getVersion();
				Integer pre  = testCasesForTestPlan.get(tcName).getVersion();
				
				if ( curr > pre ) {
					testCasesForTestPlan.remove(tcName);
					testCasesForTestPlan.put(tcName, tc);
				}
			}
		}
		
	}

	public HashMap<String, Integer> getTcIdsForTestPlan() {
		return tcIdsForTestPlan;
	}

	public ArrayList<String> getTcNamesForTestPlan() {
		return tcNamesForTestPlan;
	}

	public HashMap<String, String> getTcInfoForTestPlan() {
		return tcInfoForTestPlan;
	}

	public void reportTestResults(final String platformName, final ArrayList<HashMap<String, String>> testResults) {
		
		if ( tcIdsForTestPlan == null ) {
			
			System.out.println("test case IDs not set");
			return;
		}
		
		Integer platformId = getPlatformId(platformName);
//		String platformName = "";
		
		System.out.println("Saving " + testResults.size() + " test case results to TestLink");
		
		for(int idx=0; idx<testResults.size(); idx++) {
			Map<String, String>  ts = testResults.get(idx);
			String tcName = ts.get("tc_name");
			String notes = ts.get("notes");
		
			Integer tcId = tcIdsForTestPlan.get(tcName);
			
			if ( tcId == null ) {
				System.out.println("Error: " + this.testProject + " test case - " + tcName + " not found from TestLink for " + this.testPlan);
		
			} else {
				String status = ts.get("result");
				
				if ( status != null ) {
					api.reportTCResult(tcId, null, testPlanId, 
							status.equals("pass") ? ExecutionStatus.PASSED : ExecutionStatus.FAILED, 
							buildId, buildName, notes, true, 
							status.equals("pass") ? null : this.bugId.isEmpty() ? null : this.bugId, 
							platformId, platformName, null, true);
				} else {
					System.out.println("Error: no status");
				}
			}

		
		}
		
		System.out.println("Done");
		
	}

	private Integer getPlatformId(final String platform) {
		
		Integer platformId = 0;
	
		try {
			Platform[] tps = api.getTestPlanPlatforms(this.testPlanId);
			if ( tps != null ) {
				
				for(int i=0; i<tps.length; i++) {
					platformId = tps[0].getId();
					String name = tps[0].getName();
					
					if ( platform.equals(name) ) break;
				}
			}

		} catch (TestLinkAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		
		return platformId;
	}
/*	public void updateTestResults(final String jiraId) {
		
		if ( tcIdsForTestPlan == null ) {
			
			System.out.println("test case IDs not set");
			return;
		}
		
		Integer platformId = 0;
		String platformName = "";

		try {
			Platform[] tps = api.getTestPlanPlatforms(this.testPlanId);
			if ( tps != null ) {
				platformId = tps[0].getId();
				platformName = tps[0].getName();
			}

		} catch (TestLinkAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		
		for(String tc_name : tcIdsForTestPlan.keySet() ) {
		
			
			Integer tcId = tcIdsForTestPlan.get(tc_name);
			
//			TestCase tc = api.getTestCase(tcId, null, null);
			
//			Execution  e = api.getLastExecutionResult(testPlanId, tcId, null);
			
//			ExecutionStatus es = e.getStatus();
			
//			ExecutionStatus es = tc.getExecutionStatus();
			// doesn't work since the ExecutionStatus not set properly.
//			if ( es == ExecutionStatus.FAILED ) {
//				api.reportTCResult(tcId, null, testPlanId, ExecutionStatus.FAILED, 
//			    		buildId, buildName, "updated with JIRA ID " + jiraId, true, jiraId, platformId, platformName, null, true);
//			}
//			
//			if ( es == ExecutionStatus.NOT_RUN ) {
//				api.reportTCResult(tcId, null, testPlanId, ExecutionStatus.BLOCKED, 
//			    		buildId, buildName, "The access group and permission set stated in the Test case title are not configured in AD yet. The test is blocked.", true, jiraId, platformId, platformName, null, true);
//				
//			}		
		}
		
		System.out.println("Done");
		
	} */

	private Integer[] getIds(Object[] srcArr) {
		if ( srcArr == null ) {
			return null;
		}
		
		int size = srcArr.length;
		Integer[] ret = new Integer[size];
		String name = srcArr.getClass().getName();
		
		for(int idx=0; idx<size; idx++) {
			
			if ( name.contains("TestSuite") ) {
				TestSuite ts = (TestSuite) srcArr[idx];
				ret[idx] = ts.getId();
			}
			
			if ( name.contains("Build") ) {
				Build bd = (Build) srcArr[idx];
				ret[idx] = bd.getId();
			}			 

		}
		return ret;
	}
	
	public Integer getProjectIdByProjectKey(final String testProjectKeyName) {
		
		TestProject[] existingTPs = api.getProjects();
		
		Integer tpId = 0;
		for(TestProject tp : existingTPs) {
			if ( testProjectKeyName.equals(tp.getPrefix()) ) {
				tpId = tp.getId();
				TestBenchUtilities.say("found project id: " + tpId);
				return tpId;
			}
		}
		
		return 0;		
	}
	
	public Integer createTestSuite(final Integer testProjectId, final String testSuiteName, final String testSuteDetails) {
			
		Integer tsId = null;
		try {
			
			tsId = getTestSuiteId(testProjectId, testSuiteName);
			
			if ( tsId != 0) {
				return tsId;
			}
			
			TestSuite ts = api.createTestSuite(testProjectId, testSuiteName, testSuteDetails, 
					null, 0, true, ActionOnDuplicate.CREATE_NEW_VERSION );
			
			if ( ts == null ) {
				TestBenchUtilities.sayError("failed to crate test suite: " + testSuiteName);
				return 0;
			}
			
			return ts.getId();
		} catch (TestLinkAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	private Integer getTestSuiteId(final Integer testProjectId, final String testSuiteName ) {
		TestSuite[] tss = api.getFirstLevelTestSuitesForTestProject(testProjectId);
		
		for(TestSuite ts : tss) {
			if ( testSuiteName.equals(ts.getName()) ) {
				return ts.getId();
			}
		}
		return 0;
	}
	
	public Integer createTestCase(final Integer testProjectId, final Integer testSuiteId, 
			final String testCaseName, final String testCaseSummary) {
		
		try {
		     TestCase tc = api.createTestCase(
		    		 testCaseName, // testCaseName
		    		 testSuiteId, // testSuiteId
		    		 testProjectId, // testProjectId
		             "honshen", // authorLogin
		             testCaseSummary, // summary
		             null, // steps
		             null, // preconditions
		             TestImportance.HIGH, // importance
		             ExecutionType.MANUAL, // execution
		             new Integer(10), // order
		             null, // internalId
		             true, // checkDuplicatedName 
		             ActionOnDuplicate.CREATE_NEW_VERSION ); // actionOnDuplicatedName
		     			
			if ( tc == null ) {
				TestBenchUtilities.sayError("failed to crate test case: " + testCaseName);
				return 0;
			}
			
			return tc.getId();
		} catch (TestLinkAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

//	private Integer getIdByName(final Object[] srcArr, final String name) {
//		if ( srcArr == null ) {
//			return null;
//		}
//		
//		int size = srcArr.length;
//		Integer[] ret = new Integer[size];
//		for(int idx=0; idx<size; idx++) {
//			if ( srcArr[idx].getClass().getName().equals("TestSuite") ) {
//				TestSuite ts = (TestSuite) srcArr[idx];
//				String tName = ts.getName();
//				
//				if (tName != null ) {
//					if ( tName.equals(name) ) {
//						return ts.getId();
//					}
//				}
//			}
//			
//			if ( srcArr[idx].getClass().getName().equals("Build") ) {
//				Build bd = (Build) srcArr[idx];
//				String bName = bd.getName();
//				
//				if (bName != null ) {
//					if ( bName.equals(name) ) {
//						return bd.getId();
//					}
//				}
//			}			 
//
//		}
//		return null;
//	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDevKey() {
		return devKey;
	}

	public void setDevKey(String devKey) {
		this.devKey = devKey;
	}

	public HashMap<String, String> getValidSandboxes() {
		return validSandboxes;
	}

	public void setValidSandboxes(HashMap<String, String> validSandboxes) {
		this.validSandboxes = validSandboxes;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		TestLinkApi tl = new TestLinkApi();
		tl.initializeTestLink();
//		tl.setTesPlanIdByName("SFP BVT All releases", "SFDCSUST:Salesforce Program");
		String[] tsnames = {"EquipmentStandardsHub", "Build Verification"};
//		tl.setAllTestCases("SFDCSUST:Salesforce Program", "SFP BVT All releases", tsnames);
//		float threashold = 80;
		ArrayList<String> blist = tl.getLatestNotExecutedBuildsBy("SFDCSUST:Salesforce Program", "SFP BVT All releases", tsnames, 90);
		
		TestBenchUtilities.showArray(blist);

		
		
//		tl.setBuildIdByName("sfdctest_2015-01-21");
//		tl.updateTestResults("R1SFA03-2560");
		
	}

}
