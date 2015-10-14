package net.testbench.jira;

//import net.testbench.testlink.TestLinkApi;
import net.testbench.utility.Test;
//import net.testbench.utility.TestBenchUtilities;
import net.testbench.utility.TestLinkUtilities;

public class TestCaseFromJira extends Test {
	
	private String[] tmp = {"", ""};
	
	
	public TestCaseFromJira() {
//		super();
		setUp("import_jira_data_properties.xml");
		
	}

	public void start() {
		JiraApi ja = new JiraApi();
		
		ja.initRestApiQuery(props);
		
		ja.setJiraIssues();
		
		TestLinkUtilities tlu = new TestLinkUtilities(props);
		
		tlu.saveJiraDataToTestCases(ja.getJiraIssues());
		
		tlu.createTestCaseFromJiraData(ja.getJiraIssues());
	}
	
	
	public void stop() {
		// nothing yet
	}
	
	public static void main(String[] args) {
		TestCaseFromJira jt = new TestCaseFromJira();
		 
		jt.start();
		jt.stop();
		System.exit(0);
	}
}
