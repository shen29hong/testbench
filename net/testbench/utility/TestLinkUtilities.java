package net.testbench.utility;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

//import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionType;
//import br.eti.kinoshita.testlinkjavaapi.constants.TestImportance;
//import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import net.testbench.testlink.TestLinkApi;

public class TestLinkUtilities {

	private Properties props;
	
	public TestLinkUtilities(Properties props) {
		this.props = props;
	}

	public void createTestCaseFromJiraData(final ArrayList<HashMap<String, String>> jiraData) {
		
		TestLinkApi tlapi = new TestLinkApi();
		
		if ( !tlapi.initializeTestLink() ) {
			TestBenchUtilities.say("failed to initialize TestLink API connection. Nothing done.");
			return;
		}
		
		String testProjectKey = props.getProperty("jiraProject");
		
		Integer testProjectId = tlapi.getProjectIdByProjectKey(testProjectKey);
		
		if ( testProjectId == 0) {
			TestBenchUtilities.say("failed to get project id for " + testProjectKey);
			return;			
		}

		String testSuiteName = props.getProperty("jiraFixVersion");
		
		String testSuiteDetails = "<p>The " + testProjectKey + " - " + testSuiteName + " contains JIRAs for the release " + testSuiteName + ".</p>";
		
		Integer testSuiteId = tlapi.createTestSuite(testProjectId, testSuiteName, testSuiteDetails);
		
		if ( testSuiteId == 0) {
			TestBenchUtilities.say("failed to craete test suite");
			return;			
		}
		
		for(int idx=0; idx<jiraData.size(); idx++) {
			HashMap<String, String> jira = jiraData.get(idx);
			
			String fixVerions = jira.get("fixVersions");
			
			if ( ! testSuiteName.equals(fixVerions) ) { 
				continue;
			}

			String name = jira.get("key") + " - " + jira.get("summary");
			String summary = "<p><span class=\"summary\">" + jira.get("summary") + "</span>."
					+ "<p>" + jira.get("description") +"</p>";
			Integer tcId = tlapi.createTestCase(
					testProjectId, // testProjectId
					testSuiteId, // testSuiteId 
					name, // testCaseName
		            summary); 
			if ( tcId == 0) {
				TestBenchUtilities.say("failed to craete test case " + name);
//				return;			
			}
			
		}
	}
	
	public void saveJiraDataToTestCases(final ArrayList<HashMap<String, String>> jiraData) {
		
		String projectName = props.getProperty("jiraProject");
		String testSuiteName = props.getProperty("jiraFixVersion");
		
		StringBuilder tcsb = new StringBuilder();
		
		tcsb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
//		tcsb.append("<testsuite name=\"" + projectName + "\">\n"); 
//		tcsb.append("<details><![CDATA[<p>The " + projectName + " contains the QA test user accounts for all RBA SFDC sandboxes. The Users of this test suite, the QA team members shall keep the passwords updated if required.</p>]]></details>\n");

		tcsb.append("<testsuite name=\"" + testSuiteName + "\">\n"); 
		tcsb.append("<details><![CDATA[<p>The " + projectName + " - " + testSuiteName + " contains JIRAs for the release " + testSuiteName + ".</p>]]></details>\n");
		
		for(int idx=0; idx<jiraData.size(); idx++) {
			
			HashMap<String, String> jira = jiraData.get(idx);
			
			String fixVerions = jira.get("fixVersions");
			
			if ( ! testSuiteName.equals(fixVerions) ) { 
				continue;
			}
						
			tcsb.append("<testcase name=\"" + jira.get("key") + " - " + jira.get("summary") + "\">\n");
			tcsb.append("<summary><![CDATA["
					+ "<p><span class=\"summary\">" + jira.get("summary") + "</span>."
					+ "<p>" + jira.get("description") +"</p>"
					+ "]]></summary>\n");
			tcsb.append("<preconditions><![CDATA[n/a]]></preconditions>");
			tcsb.append("<status><![CDATA[7]]></status>");
			tcsb.append("<importance><![CDATA[3]]></importance>");
			tcsb.append("<execution_type><![CDATA[2]]></execution_type>");
			tcsb.append("<estimated_exec_duration><![CDATA[20]]></estimated_exec_duration>");
			
			tcsb.append("</testcase>\n");

		}
		
		tcsb.append("</testsuite>\n");

		String today = TestBenchUtilities.getTimestamp("long");
		
		writeToFile("C://temp//jira_test_cases_" + today + ".xml", tcsb.toString() );

	}

//	private void getTestSteps() {
//		
//	}
	private void writeToFile(final String filename, final String data) {
		BufferedWriter bw = null;
		
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename, false), "UTF8") ) ;
			bw.write(data);
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
		} finally {
			if (bw != null) try{
				bw.close();
			}catch(IOException e2){
				//ignore
			}
		}
	}

	
}
