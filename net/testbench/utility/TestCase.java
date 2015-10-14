package net.testbench.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public abstract class TestCase {

	public final String linefeed = "\n\r";
	
	private ArrayList<HashMap<String, String>> testResults;
	
	private String tcPrefix = "Verify ";
	private String tcSuffix = "";

	private String username = "";
	
	private Properties props;
//	private Logger logger;
	
	
	
	public TestCase() {
		// TODO Auto-generated constructor stub
	}

	public void saveTestResult(final String tcName, final String result, final String notes) {
		
		HashMap<String, String> resultMap = new HashMap<String, String>();
		
		resultMap.put("tc_name", tcName);
		resultMap.put("result", result);
		resultMap.put("notes", "user: [" + this.username + "], (" + notes + ")");
		
//		logger.info(tcName + "-" + result + "-" + notes);
		testResults.add(resultMap);
	}

	/** getters/setters section */
	public ArrayList<HashMap<String, String>> getTestResults() {
		return testResults;
	}

	public void setTestResults(ArrayList<HashMap<String, String>> testResults) {
		this.testResults = testResults;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTcPrefix() {
		return tcPrefix;
	}

	public void setTcPrefix(String tcPrefix) {
		this.tcPrefix = tcPrefix;
	}

	public String getTcSuffix() {
		return tcSuffix;
	}

	public void setTcSuffix(String tcSuffix) {
		this.tcSuffix = tcSuffix;
	}

	public Properties getProps() {
		return props;
	}

	public void setProps(Properties props) {
		this.props = props;
	}

}
