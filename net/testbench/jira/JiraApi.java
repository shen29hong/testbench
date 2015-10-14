package net.testbench.jira;

//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.UnsupportedEncodingException;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;

import net.testbench.utility.TestBenchUtilities;

public class JiraApi {

	private Properties props;
	private String jiraQueryUrl;
	
    private ArrayList<HashMap<String, String>> jiraIssues;
    
    private String nextKey = "";
    private HashMap<String, String> jiraIssue = null;
    
    private Map<String, String> jiraRequiredFields = null;
    
    private int lastStartAt = 0;

    private Logger logger;
    
	public JiraApi() {
		logger = Logger.getLogger(this.getClass().getName());
	}

	public Properties getProps() {
		return props;
	}

	public void setProps(Properties props) {
		this.props = props;
	}

	public void initRestApiQuery(final Properties props) {
//		this.props = props;
//		
//		String jiraHome = props.getProperty("jiraHome");
//		String jiraRestApiVersion = props.getProperty("jiraRestApiVersion");
//		String jiraUsername = props.getProperty("jiraUsername");
//		String jiraPassword = props.getProperty("jiraPassword");
//		String jiraProject = props.getProperty("jiraProject");
////		String jiraFixVersion = props.getProperty("jiraFixVersion");
//		String jiraResolution = props.getProperty("jiraResolution");
//		String jiraStatus = props.getProperty("jiraStatus");
//		String jiraOrCondition = props.getProperty("jiraOrCondition");
//		
//		String jql= "project=\"" + jiraProject + "\"";
//		jql += jiraResolution.trim().isEmpty() ? "" : " AND resolution=\"" + jiraResolution + "\"";
////		jql += jiraFixVersion.trim().isEmpty() ? "" : " AND fixVersion IN (versionMatch(\"" + jiraFixVersion + "\"))";
//		jql += jiraStatus.trim().isEmpty()     ? "" : " AND Status =\"" + jiraStatus + "\"";
////				" AND fixVersion IN (versionMatch(" + jiraFixVersion + ")) AND Status = \"" + jiraStatus + "\"";
//		
//		if ( !jiraOrCondition.isEmpty() ) {
//			jql += " OR " + jiraOrCondition;
//		}
//		
//		String encodedJql = encodeQuery(jql);
//		
//		jiraQueryUrl = jiraHome + jiraRestApiVersion + encodedJql +
//				"&os_username=" + jiraUsername + "&os_password=" + jiraPassword;
//		
//		logger.info(jiraQueryUrl);
//		initJiraRequiredFields();
	}
	
	public ArrayList<HashMap<String, String>> getJiraIssues() {
		return jiraIssues;
	}

	public void setJiraIssues() {
//		if ( jiraQueryUrl == null) {
//			TestBenchUtilities.sayError("jiraQueryUrl not initialized yet");
//			System.exit(1);
//		}
//
//		if ( jiraRequiredFields == null ) {
//			TestBenchUtilities.sayError("jiraRequiredFields not initialized yet");
//			System.exit(1);			
//		}
//
//		String jiraQueryTimeout = props.getProperty("jiraQueryTimeout");
//		
//		String jiraJSON = getJSONData(jiraQueryUrl, TestBenchUtilities.getInt(jiraQueryTimeout));
//		
//		if ( jiraJSON == null ) {
//			TestBenchUtilities.sayError("jiraQueryUrl failed");
//			System.exit(1);			
//		}
//
//
//		JSONParser jsonParser = new JSONParser();
//		
//		try {
//			Object parsedAllObject = jsonParser.parse(jiraJSON);
//			
//			@SuppressWarnings("unchecked")
//			Map<String, Object> parsedObjectsMap = ((Map<String, Object>) parsedAllObject);
//
//			for(String key : parsedObjectsMap.keySet() ) {
//				
//				Object obj = parsedObjectsMap.get(key);
//				 
//				if ( obj.getClass() == null ) {
//					continue;
//				}
//					 
//				if ( obj.getClass().equals(JSONArray.class) && key.equals(props.getProperty("jiraJsonIssues")) ) {
//					
//					this.setJiraIssues((JSONArray) obj);
//				
//				}
//			}
//			
//			String startAt = parsedObjectsMap.get("startAt").toString();
//			String maxResults = parsedObjectsMap.get("maxResults").toString();
//			String total = parsedObjectsMap.get("total").toString();
//			
//			if ( !isCompleted(startAt, maxResults, total) ) { // updated the query url as well
//				
//				setJiraIssues();
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.exit(1);	
//		}
		
	}
	
	private boolean isCompleted(final String startAt, final String maxResults, final String total) {
		
		if ( startAt == null || maxResults == null || total == null) {
			return true;
		}
		
		int start = TestBenchUtilities.getInt(startAt);
		
		if ( lastStartAt == 0 ) {
			lastStartAt = start;
		}
		
		int max = TestBenchUtilities.getInt(maxResults);
		int tot = TestBenchUtilities.getInt(total);
		
		if ( tot < max || tot < lastStartAt ) {
			return true;
		} else {
			lastStartAt += max;
			jiraQueryUrl += "&startAt=" + lastStartAt;
			
			if ( tot > 1000 ) {
				jiraQueryUrl += "&maxResults=1000";
			} else {
				jiraQueryUrl += "&maxResults=" + total ;
			}
			
			logger.info(jiraQueryUrl);
			
			return false;
		}
	}
	
	private void initJiraRequiredFields() {
		
		String requiredFields = props.getProperty("jiraJsonRequiredDataFields");
		String requiredNextFields = props.getProperty("jiraJsonRequiredDataNextFields");
		
		this.jiraQueryUrl += "&fields=\"" + requiredFields + "\"";		
		
		String[] requiredFieldsKey = requiredFields.split(",");
		String[] requiredFieldsValue = requiredNextFields.split(",");
		
		if ( requiredFieldsKey.length != requiredFieldsValue.length ) {
			TestBenchUtilities.sayError("length not equal! abort ...");
			System.exit(1);			
			
		}
		jiraRequiredFields = new HashMap<String, String>();
		
		for(int i=0; i<requiredFieldsKey.length; i++) {
			String value = requiredFieldsValue[i];
			
			if ( value.equals("not") ) {
				value = "";
			}
			jiraRequiredFields.put(requiredFieldsKey[i], value);
		}
		
		logger.info(jiraQueryUrl);
	}


//	private void setJiraIssues(final JSONArray jsonArray ) {
//		/**
//		 * top level
//		 */
//		jiraIssues = new ArrayList<HashMap<String, String>>();
//		
//		for(int idx=0; idx<jsonArray.size(); idx++ ) {
//			
//		
//			jiraIssue = new HashMap<String, String>();
//			
//			Object obj = jsonArray.get(idx);
//			
//			if ( obj.getClass() == null ) {
//				continue;
//			}			
//			
//			if ( obj.getClass().equals(JSONObject.class) ) {				
//				processJsonObject((JSONObject) obj);
//			} 
//
//			jiraIssues.add(jiraIssue);
//		}
//
//		showJiras();
//	}
//
//	private void showJiras() {
//		
//		for(int idx=0; idx<jiraIssues.size(); idx++ ) {
//			
//			logger.info("JIRA: " + idx);
//			HashMap<String, String> jira = jiraIssues.get(idx);
//			for( String key : jira.keySet() ) {
//				logger.info(key + "=" + jira.get(key));
//			}
//			
//		}
//	}
//
//	private void processJsonObject(final JSONObject jsonObject) {
//		
//		for(Object objKey : jsonObject.keySet() ) {
//			
//			Object objValue = jsonObject.get(objKey);
//			
//			if ( objValue == null ) {
//				continue;
//			}
//			
//			if ( objValue.getClass() == null ) {
//				continue;
//			}
//			String cName = objValue.getClass().getName();
//			
//			logger.info("classname=[" + cName + "]");
//			
//			if ( objValue.getClass().equals(JSONObject.class) && objKey.equals(props.getProperty("jiraJsonFields")) ) {
//				
//				processJsonObject((JSONObject) objValue);
//
//			} else {
//				
//				String myKey = objKey.toString();
//				String myValue = objValue.toString();
//				logger.info("key=[" + myKey + "];  value=[" + myValue + "]");
//				
//				if ( jiraRequiredFields.get(myKey) != null ) {
//						
//					nextKey = jiraRequiredFields.get(myKey);
//					
//					if ( !nextKey.isEmpty() ) {
//						
//						String v = "";
//						if ( myKey.equals("fixVersions")) {
//							v = getValueFromJsonArray((JSONArray) objValue, nextKey);
//						} else {
//							v = getValueFromJsonObject((JSONObject) objValue, nextKey);
//						}
//						jiraIssue.put(myKey, v);
//						
//					} else {
//
//						jiraIssue.put(myKey, myValue);
//
//					}
//				}
//			}
//		}
//	}
//
//	private String getValueFromJsonObject(final JSONObject jsonObject, final String keyName) {
//		
//		String retValue = "";
//		
//		for(Object objKey : jsonObject.keySet() ) {
//			
//			if ( keyName.equals(objKey.toString())) {
//				Object objValue = jsonObject.get(objKey);
//				retValue = objValue.toString();
//			}
//		}
//		return retValue;
//	}
//
//	private String getValueFromJsonArray(final JSONArray jsonArray, final String keyName) {
//		
//		String retValue = "";
//		
//		for(int idx=0; idx<jsonArray.size(); idx++ ) {
//			
//			Object obj = jsonArray.get(idx);
//			
//			if ( obj.getClass() == null ) {
//				continue;
//			}
//			
//			if ( obj.getClass().equals(JSONObject.class) ) {
//				retValue = getValueFromJsonObject((JSONObject) obj, keyName);
//				if ( !retValue.isEmpty() ) {
//					break;
//				}				
//			} 
//		}
//		return retValue;
//	}
//
//	
//	private String getJSONData(final String url, final int timeout) {
//		
//	    try {
//	    	
//	        URL u = new URL(url);
//	        
//	        HttpURLConnection c = (HttpURLConnection) u.openConnection();
//	        c.setRequestMethod("GET");
//	        c.setRequestProperty("Content-length", "0");
//	        c.setUseCaches(false);
//	        c.setAllowUserInteraction(false);
//	        c.setConnectTimeout(timeout);
//	        c.setReadTimeout(timeout);
//	        c.connect();
//	        int status = c.getResponseCode();
//
//	        switch (status) {
//	            case 200:
//	            case 201:
//	                BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
//	                StringBuilder sb = new StringBuilder();
//	                String line;
//	                while ((line = br.readLine()) != null) {
//	                    sb.append(line+"\n");
//	                }
//	                br.close();
//	                return sb.toString();
//	        }
//
//	    } catch (MalformedURLException ex) {
//	    	TestBenchUtilities.sayError(ex.getMessage());
//	    } catch (IOException ex) {
//	    	TestBenchUtilities.sayError(ex.getMessage());
//	    }
//	    
//	    return null;
//	}
//	
//	private String encodeQuery(String jql) {
//		
//		String encodeUrl = "";
//		try {
//		
//			encodeUrl = URLEncoder.encode(jql, "UTF-8");
//
//		} catch (UnsupportedEncodingException e) {
//			
//			e.printStackTrace();
//			System.exit(3);
//		}
//		
//		return encodeUrl;
//	}

}
