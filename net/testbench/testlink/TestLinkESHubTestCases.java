package net.testbench.testlink;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

//import net.testbench.utility.TestBenchUtilities;

public class TestLinkESHubTestCases {

//	public final String LIST_VIEW_BUTTONS = "view list input buttons";
//	public final String LIST_VIEW_COLUMNS = "view list displayed columns";
//	public final String DETAILS_BUTTONS = "details input buttons";
//	public final String DETAILS_FIELDS = "details displayed fields";

	public final String TR_START = "<tr>";
	public final String TR_END = "</tr>";

	public final String TD_START = "<td>";
	public final String TD_END = "</td>";
	
	public final String[] INFO_SECTIONS = {"view list input buttons", "view list displayed columns", "details input buttons", "details displayed fields"};
	
	/** 
	 * The input data from TestLink API to this class
	 * 
	 * testCasesInfo
	 * key=tcName, value=tcSummary 
	 * 
	 * */
	private HashMap<String, String> testCasesInfo;
	
	/** 
	 * The output data from this class
	 * 
	 * testCasesData:
	 * key=tcName, value= HashMap<String, ArrayList<HashMap<String, String>>>
	 * 				key=INFO_SECTIONS, value=ArrayList<HashMap<String, String>>		
	 * 								   list of map with keys {type, title|value, enabled, Exempted}	
	 * */	
	private HashMap<String, HashMap<String, ArrayList<HashMap<String, String>>>> testCasesData;

	private Logger logger;
	
	public TestLinkESHubTestCases() {
		logger = Logger.getLogger(this.getClass().getName());
	}

	public void initTestCasesData() {
		
//		HashMap<String, String> testCasesInfo = getTestCasesFromTestLink();
		
		testCasesData = new  HashMap<String, HashMap<String, ArrayList<HashMap<String, String>>>>();
		
		for(String tcName : testCasesInfo.keySet()) {
			
			logger.info("tc: " + tcName);	
			
			if ( tcName.equals("The ES Viewer user accesses to ES Versions and selects an ES version as viewer" )) {
				logger.info("tc: " + tcName + " debug");
			}
//			TestBenchUtilities.say("tc: " + tcName);
			String tcSummary =  testCasesInfo.get(tcName);
//			TestBenchUtilities.say("summary: " + tcSummary);
			logger.info("summary: " + tcSummary);
			
			testCasesData.put(tcName, getTestCaseSummaryData(tcSummary));
		}		
	}
	

	public void showTestCasesInfo() {		
		
		for(String tcName : testCasesData.keySet()) {
			
			logger.info( "TC name: " + tcName);
			
			for(int i=0; i<INFO_SECTIONS.length; i++) {
				String section = INFO_SECTIONS[i];
				logger.info( section + ": ");
				
				if ( testCasesData.get(tcName) == null ) {
					logger.info( "\t\t\t\tno data ");
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
					logger.info(info);
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
				logger.error("test case summary: " + summary + " not contain any information for: " + section);
				testCasesSummaryData.put(section, null);
				continue;
			}
			start += section.length();
			
			start = summary.indexOf("<table", start);
			
			if ( start == -1) {
				logger.error("test case summary: " + summary + " of section " + section + " not contain any information for: <table");
				testCasesSummaryData.put(section, null);
				continue;
			}
			
			int end = summary.indexOf("</table>", start);
			
			if ( end == -1) {
				logger.error("test case summary: " + summary + " of section " + section + " not contain any information for: </table>");
				testCasesSummaryData.put(section, null);
				continue;
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
			logger.error("test case summary table body: " + tbody + " not contain any information for: " + TR_START);
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
			logger.error("test case summary table body row: " + row + " not contain any information for: " + TD_START);
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
			logger.error("test case summary table header and cell: size not matched");
			return tInfo;
		}
		
		for(int i=0; i<headers.size(); i++) {
			String key = headers.get(i);
			String value = cells.get(i);
			tInfo.put(key, value);
		}
		return tInfo;
	}

	/** getters/ setters */
	public HashMap<String, String> getTestCasesInfo() {
		return testCasesInfo;
	}

	public void setTestCasesInfo(HashMap<String, String> testCasesInfo) { 
		this.testCasesInfo = testCasesInfo;
	}

	public HashMap<String, HashMap<String, ArrayList<HashMap<String, String>>>> getTestCasesData() {
		return testCasesData;
	}

	public void setTestCasesData(
			HashMap<String, HashMap<String, ArrayList<HashMap<String, String>>>> testCasesData) {
		this.testCasesData = testCasesData;
	}

}
