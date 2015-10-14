package net.testbench.sfdc;
/**
 * @author HONSHEN
 *
 */

import java.util.ArrayList;
import java.util.HashMap;
//import java.util.Properties;

import java.util.Properties;

import org.apache.log4j.Logger;

import net.testbench.utility.TestBenchUtilities;











import com.sforce.bulk.UpdateResult;
import com.sforce.soap.partner.DescribeGlobalResult;
import com.sforce.soap.partner.DescribeGlobalSObjectResult;
//import com.sforce.rest.DescribeLayout.Field;
import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.DescribeSObjectResult;
import com.sforce.soap.partner.FieldType;
import com.sforce.soap.partner.PicklistEntry;
//import com.sforce.soap.partner.Connector;
//import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.UpsertResult;
//import com.sforce.soap.partner.SaveResult;
//import com.sforce.soap.partner.Error;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
//import com.sforce.ws.ConnectorConfig;

public class SFDCPartnerQuery {
	
//    private ConnectorConfig config;
//    private PartnerConnection connection;
	private Connect connect;
    
    private Logger logger;
    
//    private String timeStamp = "";
//    private boolean traceEnabled = false;
    private boolean debug = false;
    
//    private boolean savingData = false;
//    private String testEnv = "";
    private volatile String lastErrMsg ="";
    private volatile HashMap<String, String> lastErrMessage = null;

	/**
	 * 
	 */
    
	public SFDCPartnerQuery(final Properties props, final boolean traceEnabled) {
		
		logger = Logger.getLogger(this.getClass().getName());
		
		connect = new Connect();

		lastErrMessage = new HashMap<String, String>();
	    
		connect.setTraceEnabled(traceEnabled);
	    connect.setConnection(props);
	}

//	public SFDCPartnerQuery() {
//		
//		logger = Logger.getLogger(this.getClass().getName());
//
//		config = new ConnectorConfig();
//	    config.setUsername("tlinkapi@rbauction.com.dev7csam");
//	    config.setPassword("P@$$word01" + "6CCSxQ7y5g5wdwXmjg8B4iLW");
//
//	    config.setTraceMessage(this.traceEnabled);
//	    lastErrMessage = new HashMap<String, String>();
//	}

//	public SFDCPartnerQuery(final String dir) {
//		
//		logger = Logger.getLogger(this.getClass().getName());
//		
//		Properties props = TestBenchUtilities.getProperties(dir + "sam_sfdc_properties.xml");
//
//		config = new ConnectorConfig();
//	    config.setUsername(props.getProperty("username"));
//	    config.setPassword(props.getProperty("password") + props.getProperty("token"));
//
//	    config.setTraceMessage(this.traceEnabled);
//	    lastErrMessage = new HashMap<String, String>();
//	}


	/**
	 * valid testEnvironment: dev | qa
	 * @param testEnvironment
	 */
//	public SFDCPartnerQuery(final String testEnvironment) {
//		
//		logger = Logger.getLogger(this.getClass().getName());
//		tt = new TestTools();
////		this.testEnv = testEnvironment;
//		Properties prop = tt.getProperties(testEnvironment + "_sfdc_properties.xml");
//		config = new ConnectorConfig();
//	    config.setUsername(prop.getProperty("sfdcusername"));
//	    config.setPassword(prop.getProperty("sfdcpassword") + prop.getProperty("sfdctoken"));
//	    
//	    if ( testEnvironment.equals("prod") ) {
//	    	final String prodEndPoint = "https://login.salesforce.com/services/Soap/u/28.0"; 
//	    	config.setAuthEndpoint(prodEndPoint);
//	    	config.setServiceEndpoint(prodEndPoint);
//	    } // else 	  prodEndPoint = "https://test.salesforce.com/services/Soap/u/28.0"  
//	    
//	    config.setTraceMessage(this.traceEnabled);
//	    lastErrMessage = new HashMap<String, String>();
//	}

	/**
	 * @param args
	 */

//	public SFDCPartnerQuery(final String username, final String password, final String token) {
//		
//		logger = Logger.getLogger(this.getClass().getName());
//		config = new ConnectorConfig();
//	    config.setUsername(username);
//	    config.setPassword(password + token);
//	    config.setTraceMessage(true);
//	    lastErrMessage = new HashMap<String, String>();
//	}


//	public void setTraceEnabled(boolean traceEnabled) {
//		this.traceEnabled = traceEnabled;
//	}


	public String getOneValueById(final String objName, final String colName, final String id) {
		
		if ( connect != null ) {
			logger.error("fail to get sfdc db connection");
			return null;
		}
		
		String qstmt = "SELECT " + colName + " FROM " + objName + " WHERE Id = '" + id + "'";
		qstmt = appendStmt(qstmt);
//		if ( objName.equals("User") || objName.equals("RecordType") ) {
//			qstmt += "";
//		} else {
//			qstmt += " AND IsDeleted=false";
//		}
		
		QueryResult queryResults = getQueryResult(qstmt);
		
		if ( queryResults == null ) {
			logger.error("fail to get record by query: " + qstmt);
			return null;
		}
		
		if (queryResults.getRecords().length != 1) {
			logger.error("fail to get a single record by query: " + qstmt);
			return null;
		}
			 
		SObject s = queryResults.getRecords()[0];
		
		String tmp = (String) s.getField(colName);
		
		return tmp;
	}

	public String getOneValueByCondition(final String objName, final String colName, final String condition) {
		
		String tName = Thread.currentThread().getName();
		/**
		 * condition = WHERE RB_Customer_No = '' or WHERE sth like ''
		 */
		if ( connect == null ) {
			logger.error("fail to get sfdc db connection");
			return null;
		}
		
		String qstmt = "SELECT " + colName + " FROM " + objName;
		
		if ( condition.length() > 0 ) {
			if ( condition.trim().toUpperCase().startsWith("WHERE") ) {
				qstmt += " " + condition;
			} else {
				qstmt += " WHERE " + condition;
			}
		}

		qstmt = appendStmt(qstmt);
//		if ( objName.equals("User") || objName.equals("RecordType") ) {
//			qstmt += "";
//		} else {
//			qstmt += " AND IsDeleted=false";
//		}

		QueryResult queryResults = getQueryResult(qstmt);
		
		if ( queryResults == null ) {
			logger.error("fail to get record by query: " + qstmt);
			setLastErrMsg("fail to get record");
			setLastErrMessage(tName, "fail to get record");
			return null;
		}
		
		int size = queryResults.getRecords().length;
		
		if (size != 1) {
			logger.error("fail to get a single record by query: " + qstmt);
			
			if (size == 0) {
				setLastErrMsg("no record found");
				setLastErrMessage(tName, "no record found");
			} else {
				setLastErrMsg("found 2 many records");
				setLastErrMessage(tName, "found 2 many records");
			}
			return null;
		}
			 
		SObject s = queryResults.getRecords()[0];
		
		String tmp = (String) s.getField(colName);
		
		return tmp;
	}


	public HashMap<String, String> getObjectRecordsForMapping(final String qryAttributes, final String qryObjectName, final String pKey) {
		logger.info("start getting object: " + qryObjectName);
		HashMap<String, String> ret = new HashMap<String, String>();
		
		if ( connect == null ) 
			return ret;
		
		String stmt = "SELECT " + qryAttributes + " FROM " + qryObjectName ; 
		
		logger.info("stmt: " + stmt);
		String[] attrs = qryAttributes.trim().split(",\\s*");
		
		if ( attrs.length != 2 ) {
			logger.error("incorrect attributes, should be just two (2).");
			return ret;
		}
		
		QueryResult queryResults = getQueryResult(stmt);
		
		if ( queryResults == null ) {
			logger.error("incorrect query, no results - " + stmt);
			return ret;
		}
		
		boolean done = false;
		
		if (queryResults.getSize() > 0) {
	    	  
			while (!done) {
				
				SObject[] qr = queryResults.getRecords();		
					
				for(int i=0;i<qr.length;i++) {

					SObject s = qr[i]; 
					
					String theKey = "";
					String theValue = "";
		            
					for(String key : attrs) {
		            	
		            	String value ="";
		            	if (  s.getField(key) != null ) {
		            		value = s.getField(key).toString();
		            	}
		            	
		            	if ( key.equals(pKey) ) {
		            		theKey = value;
		            	} else {
		            		theValue = value;
		            	}
		            }
					
		            ret.put(theKey, theValue);
				}
				
				if (queryResults.isDone()) {
					done = true;
				} else {
					queryResults = getMoreQueryResult(queryResults);
				}	            
			}

	    }
		
		logger.info("end of getting object: " + qryObjectName);
		
		return ret;
	}

	public HashMap<String, String> getObjectRecordsForMapping(final String qryAttributes, final String qryObjectName, final String pKey, final String condition) {
		logger.info("start getting object: " + qryObjectName);
		HashMap<String, String> ret = new HashMap<String, String>();
		
		if ( connect == null ) 
			return ret;
		
		String stmt = "SELECT " + qryAttributes + " FROM " + qryObjectName + " WHERE Project__r.Name ='" + condition + "'"; 
		
		logger.info("stmt: " + stmt);
		String[] attrs = qryAttributes.trim().split(",\\s*");
		
		if ( attrs.length != 2 ) {
			logger.error("incorrect attributes, should be just two (2).");
			return ret;
		}
		
		QueryResult queryResults = getQueryResult(stmt);
		
		if ( queryResults == null ) {
			logger.error("incorrect query, no results - " + stmt);
			return ret;
		}
		
		if (queryResults.getSize() > 0) {
	    	  
            for (SObject s: queryResults.getRecords()) {

            	String theKey = "";
            	String theValue = "";
	            for(String key : attrs) {
	            	String value ="";
	            	if (  s.getField(key) != null ) {
	            		
	            		value = s.getField(key).toString();
	            		if ( debug )
	            			logger.info( key + "=" + value);
	            		
	            		if ( value.equals("894454") ) {
	            			System.out.println( key + "=" + value);
	            		}
	            	}
	            	
	            	if ( key.equals(pKey) ) {
	            		theKey = value;
	            	} else {
	            		theValue = value;
	            	}
	            	if ( debug )
	            		logger.info( key + ": " + value );
         	
	            }

	            ret.put(theKey, theValue);
            }
	    }
		
		logger.info("end of getting object: " + qryObjectName);
		
		return ret;
	}

	public HashMap<String, String> getMappingForObjectRecords(final String qryAttributes, final String qryObjectName, final String pKey, final String condition) {
		logger.info("start getting object: " + qryObjectName);
		HashMap<String, String> ret = new HashMap<String, String>();
		
		if ( connect == null ) 
			return ret;
		
		String stmt = "SELECT " + qryAttributes + " FROM " + qryObjectName + " ";
		stmt += ( condition.toLowerCase().startsWith("where") ) ? condition : (" WHERE " + condition); 
		
		logger.info("stmt: " + stmt);
		String[] attrs = qryAttributes.trim().split(",\\s*");
		
		if ( attrs.length != 2 ) {
			logger.error("incorrect attributes, should be just two (2).");
			return ret;
		}
		
		QueryResult queryResults = getQueryResult(stmt);
		
		if ( queryResults == null ) {
			logger.error("incorrect query, no results - " + stmt);
			return ret;
		}
		
		if (queryResults.getSize() > 0) {
	    	  
            for (SObject s: queryResults.getRecords()) {

            	String theKey = "";
            	String theValue = "";
	            for(String key : attrs) {
	            	String value ="";
	            	if (  s.getField(key) != null ) {
	            		
	            		value = s.getField(key).toString();
	            		if ( debug )
	            			logger.info( key + "=" + value);
	            		
	            		if ( value.equals("894454") ) {
	            			System.out.println( key + "=" + value);
	            		}
	            	}
	            	
	            	if ( key.equals(pKey) ) {
	            		theKey = value;
	            	} else {
	            		theValue = value;
	            	}
	            	if ( debug )
	            		logger.info( key + ": " + value );
         	
	            }

	            ret.put(theKey, theValue);
            }
	    }
		
		logger.info("end of getting object: " + qryObjectName);
		
		return ret;
	}


	public HashMap<String, HashMap<String, String>> getTableInfo(final String qryAttributes, final String qryTablelName, final String pKey) {
		logger.info("start getting table: " + qryTablelName);
		HashMap<String, HashMap<String, String>> ret = new HashMap<String, HashMap<String, String>>();
		
		if ( connect == null ) 
			return ret;
		
		String stmt = "SELECT " + qryAttributes + " FROM " + qryTablelName ; 
		
		logger.info("stmt: " + stmt);
		String[] attrs = qryAttributes.trim().split(",\\s*");
		
		QueryResult queryResults = getQueryResult(stmt);
		
		if ( queryResults == null ) {
			return ret;
		}
		
		if (queryResults.getSize() > 0) {
	    	  
            for (SObject s: queryResults.getRecords()) {
            	HashMap<String, String> rec = new HashMap<String, String>();
	            for(String key : attrs) {
	            	String value ="";
	            	if (  s.getField(key) != null ) {
	            		
	            		value = s.getField(key).toString();
	            		if ( debug )
	            			logger.info( key + "=" + value);
	            		
	            		if ( value.equals("894454") ) {
	            			System.out.println( key + "=" + value);
	            		}
	            	}
	            	
	            	rec.put(key, value);
	            	if ( debug )
	            		logger.info( key + ": " + value );
         	
	            }

	            Object o = s.getField(pKey);

	            if ( o != null ) {		
	            	ret.put(o.toString(), rec);  
	            	
	            	if ( o.toString().equals("003J000000odvGWIAY") )
	            		System.out.println( "found" );
	            }
            }
	    }
		
		logger.info("end of getting table: " + qryTablelName);
		
		return ret;
	}

	public HashMap<String, String> getOneRecord(final String qryAttributes, final String tableName, final String condition) {
		String tName = Thread.currentThread().getStackTrace()[1].getMethodName(); //Thread.currentThread().getName();
		logger.info(tName);
		
		HashMap<String, String> ret = new HashMap<String, String>();
		
		if ( connect == null ) {
			
			return null;
		}
		
		String stmt = "SELECT " + qryAttributes + " FROM " + tableName + " WHERE " + condition;
		stmt = appendStmt(stmt);
//		if ( tableName.equals("User") || tableName.equals("RecordType") ) {
//			stmt += "";
//		} else {
//			stmt += " AND IsDeleted=false";
//		}
		
		logger.info("stmt: " + stmt);
		
		String[] attrs = qryAttributes.split(",\\s*");
		
		QueryResult queryResults = getQueryResult(stmt);
		
		if ( queryResults == null ) {
			logger.error("got null " + stmt);
			return null;
		}
		
		SObject[] qr = queryResults.getRecords();
		
		if (qr.length == 1) {
			
			SObject s = qr[0]; 

            for(String key : attrs) {
            	String value ="";
            	if (  s.getField(key) != null )
            		value = s.getField(key).toString();
            	
            	ret.put(key, value);
            }            
 
            return ret;
            
	    } else if (qr.length == 0) {
	    
	    	logger.error("no records found by " + stmt);
	    	setLastErrMsg("no record found");
	    	setLastErrMessage(tName, "no record found");
	    
	    } else if (qr.length > 1 && condition.contains("LIKE") ) {
	    	
			SObject s = qr[0]; 

            for(String key : attrs) {
            	String value ="";
            	if (  s.getField(key) != null )
            		value = s.getField(key).toString();
            	
            	ret.put(key, value);
            }            
 
            return ret;

	    } else if (qr.length > 1 ) {

	    	logger.error("multiple records found by " + stmt);
	    	setLastErrMsg("multiple records found");
	    	setLastErrMessage(tName, "multiple records found");
	    
	    }
		
		return null;
	}

	public ArrayList<HashMap<String, String>> getRecords(final String qryAttributes, final String tableName, final String condition) {
		String tName = Thread.currentThread().getName();
		logger.info(tName);		
		
		ArrayList<HashMap<String, String>> ret = new  ArrayList<HashMap<String, String>>();
		
		if ( connect == null ) {
			
			return null;
		}
		
		String stmt = "SELECT " + qryAttributes + " FROM " + tableName;
		
		stmt += condition.startsWith("WHERE") ? " " : " WHERE ";
		stmt += condition;
		
		stmt = appendStmt(stmt); // handle IsDeleted and IsArchived
		
		logger.info("stmt: " + stmt);
		
		String[] attrs = qryAttributes.split(",\\s*");
		
		QueryResult queryResults = getQueryResult(stmt);
		
		if ( queryResults == null ) {
			logger.error("got null " + stmt);
			return null;
		}
		
		SObject[] qr = queryResults.getRecords();		
		
		if (qr.length > 0) {
			
			for(int i=0;i<qr.length;i++) {
				HashMap<String, String> rec = new HashMap<String, String>();
				SObject s = qr[i]; 
	
	            for(String key : attrs) {
	            	String value ="";
	            	if (  s.getField(key) != null )
	            		value = s.getField(key).toString();
	            	
	            	rec.put(key, value);
	            }            
	            ret.add(rec);
			}
            return ret;
            
	    } else if (qr.length == 0) {
	    
	    	logger.error("no records found by " + stmt);
	    	setLastErrMsg("no record found");
	    	setLastErrMessage(tName, "no record found");
	    
	    } 
		
		return null;
	}

	public HashMap<String, HashMap<String, String>> getAllRecords(final String qryAttributes, final String tableName, final String condition, final String pKey) {
		String tName = Thread.currentThread().getName();
		logger.info(tName);		
		
		HashMap<String, HashMap<String, String>> ret = new  HashMap<String, HashMap<String, String>>();
		
		if ( connect == null ) {
			
			return null;
		}
		
		String stmt = "SELECT " + qryAttributes + " FROM " + tableName;
		
		if ( !condition.isEmpty() ) {
			stmt += condition.startsWith("WHERE") ? " " : " WHERE ";
			stmt += condition;
		}
		
		logger.info("stmt: " + stmt);
		
		String[] attrs = qryAttributes.split(",\\s*");
		
		QueryResult queryResults = getQueryResult(stmt);
		
		if ( queryResults == null ) {
			logger.error("got null " + stmt);
			return null;
		}
		
		boolean done = false;
		
		if ( queryResults.getSize() > 0 ) {
		
			while (!done) {
				SObject[] qr = queryResults.getRecords();		
					
				for(int i=0;i<qr.length;i++) {
					HashMap<String, String> rec = new HashMap<String, String>();
					SObject s = qr[i]; 
					String mKey = "";
		            for(String key : attrs) {
		            	
		            	String value ="";
		            	if (  s.getField(key) != null ) {
		            		value = s.getField(key).toString();
			            	if ( key.equals(pKey) ) { //"Requirement_Id__c")) {
			            		mKey = value;
			            	}
		            	}
		            	
		            	rec.put(key, value);
		            }            
		            ret.put(mKey, rec);
				}
				
				if (queryResults.isDone()) {
					done = true;
				} else {
					queryResults = getMoreQueryResult(queryResults);
				}	            
			}
		} else {
		
			return null;
		}
		
		return ret;
	}

	public ArrayList<HashMap<String, String>> getAllRecordList(final String qryAttributes, final String tableName, final String condition) {
		String tName = Thread.currentThread().getStackTrace()[1].getMethodName();
		logger.info(tName);		
		
		ArrayList<HashMap<String, String>> ret = new  ArrayList<HashMap<String, String>>();
		
		if ( connect == null ) {
			
			return null;
		}
		
		String stmt = "SELECT " + qryAttributes + " FROM " + tableName;
		
		if ( !condition.isEmpty() ) {
			stmt += condition.startsWith("WHERE") ? " " : " WHERE ";
			stmt += condition;
		}
		
		logger.info("stmt: " + stmt);
		
		String[] attrs = qryAttributes.split(",\\s*");
		
		QueryResult queryResults = getQueryResult(stmt);
		
		if ( queryResults == null ) {
			logger.error("got null " + stmt);
			return null;
		}
		
		boolean done = false;
		
		if ( queryResults.getSize() > 0 ) {
		
			while (!done) {
				SObject[] qr = queryResults.getRecords();		
					
				for(int i=0;i<qr.length;i++) {
					HashMap<String, String> rec = new HashMap<String, String>();
					SObject s = qr[i]; 

					for(String key : attrs) {
		            	
		            	String value ="";
		            	if (  s.getField(key) != null ) {
		            		value = s.getField(key).toString();
		            	}
		            	
		            	rec.put(key, value);
		            }            
		            ret.add(rec);
				}
				
				if (queryResults.isDone()) {
					done = true;
				} else {
					queryResults = getMoreQueryResult(queryResults);
				}	            
			}
		} else {
			logger.error("queryResults.getSize() == 0 " + stmt);
			return null;
		}
		
		return ret;
	}

	private String appendStmt(final String src) {

		if ( src.contains("IsDeleted")) {
			return src;
		}

		String ret = src.trim();
		
		
		if ( ret.contains("FROM User") || ret.contains("FROM RecordType") || ret.contains("IsDeleted=false") ) {
			return ret;
		}
		
		if ( ret.endsWith("AND") ) {
			return ret + " IsDeleted=false";
		} 
		
		return ret + " AND IsDeleted=false";
	}
	
	
	public HashMap<String, String> getOneRecordById(final String qryAttributes, final String tableName, final String id) {
		
		logger.info(Thread.currentThread().getStackTrace()[1].getMethodName());
		
		HashMap<String, String> ret = new HashMap<String, String>();
		
		if ( connect == null ) {			
			return null;
		}
		
		String stmt = "SELECT " + qryAttributes + " FROM " + tableName + " WHERE Id = '" + id + "'";
		
		stmt = appendStmt(stmt);
//		if ( tableName.equals("User") || tableName.equals("RecordType") ) {
//			stmt += "";
//		} else {
//			stmt += " AND IsDeleted=false";
//		}
		
		String[] attrs = qryAttributes.split(",\\s*");
		
		QueryResult queryResults = getQueryResult(stmt);
		
		if ( queryResults == null ) {
			logger.error("got null " + stmt);
			return null;
		}
		
		SObject[] qr = queryResults.getRecords();
		
		if (qr.length == 1) {
			
			SObject s = qr[0]; 

            for(String key : attrs) {
            	String value ="";
            	if (  s.getField(key) != null )
            		value = s.getField(key).toString();
            	
            	ret.put(key, value);
            }            
 
            return ret;
            
	    } else if (qr.length == 0) {
	    
	    	logger.error("no records found by " + stmt);
	    
	    } else if (qr.length > 1) {

	    	logger.error("multiple records found by " + stmt);
	    
	    }
		
		return null;
	}

	
	public boolean isActivUser(final String userName) {
		String tName = Thread.currentThread().getName();
		logger.info(tName);
		
		if ( userName.isEmpty() ) {
			logger.error("missing user name");
			return false;
		}

		if ( connect == null ) {	
			return false;
		}
		
		String alias = TestBenchUtilities.getUserAlias(userName);
		
		String stmt = "SELECT Id, Alias, IsActive FROM User ";
		stmt += "WHERE Name ='" + userName + "' OR Alias='" +alias + "'";

		logger.info("stmt: " + stmt);
		
		QueryResult queryResults = getQueryResult(stmt);
		
		if ( queryResults == null ) {
			logger.error("got null " + stmt);
			return false;
		}
		
		SObject[] qr = queryResults.getRecords();		
		
		if (qr.length > 0) {
				
			SObject s = qr[0]; 
			
           	if (  s.getField("IsActive") != null ) {
        		String active = s.getField("IsActive").toString();
        		
        		if ( active.toLowerCase().equals("true") )
        			return true;
        		else
        			return false;
        	}
	
           
	    } else if (qr.length == 0) {
	    
	    	logger.error("no records found by " + stmt);
	    	setLastErrMessage(tName, "no record found");
	    
	    } 
		
		return false;
	}
	
	public HashMap<String, String> getAliasByConditions(final String conditions) {
		String tName = Thread.currentThread().getName();
		logger.info(tName);
		
		if ( conditions.isEmpty() ) {
			logger.error("missing condition ");
			return null;
		}
		
		HashMap<String, String> ret = new HashMap<String, String>();
		
		if ( connect == null ) {			
			return null;
		}
		
		String stmt = "SELECT Id, Alias FROM User ";
		stmt += "WHERE Id IN (" + conditions + ")";
		
		logger.info("stmt: " + stmt);
		
		QueryResult queryResults = getQueryResult(stmt);
		
		if ( queryResults == null ) {
			logger.error("got null " + stmt);
			return null;
		}
		
		SObject[] qr = queryResults.getRecords();		
		
		if (qr.length > 0) {
			
			for(int i=0;i<qr.length;i++) {
				
				SObject s = qr[i]; 
				String key = "";
				String value = "";
	           	if (  s.getField("Id") != null ) {
            		key = s.getField("Id").toString();
            	}
	
	           	if (  s.getField("Alias") != null ) {
            		value = s.getField("Alias").toString();
            	}
	           	ret.put(key, value);
 			}
            return ret;
            
	    } else if (qr.length == 0) {
	    
	    	logger.error("no records found by " + stmt);
	    	setLastErrMsg("no record found");
	    	setLastErrMessage(tName, "no record found");
	    
	    } 
		
		return null;
	}

	private QueryResult getQueryResult(final String stmt) {
		String tName = Thread.currentThread().getName();
		logger.info(tName + ": " + Thread.currentThread().getStackTrace()[1].getMethodName());
		
		try {
			
			QueryResult queryResults = connect.getConnection().queryAll(stmt);
			return queryResults;
			
		} catch (ConnectionException e) {
			logger.error("exception caught when querying for " + stmt + " from sfdc", e);
			e.printStackTrace();
			System.exit(7);
		}
		return null;
		
	}

	private QueryResult getMoreQueryResult(final QueryResult qr) {

		String tName = Thread.currentThread().getName();
		if ( debug ) logger.info(tName + ": " + Thread.currentThread().getStackTrace()[1].getMethodName());

		try {
			
			QueryResult queryResults = connect.getConnection().queryMore(qr.getQueryLocator());
			
			return queryResults;
			
		} catch (ConnectionException e) {
			logger.error("exception caught when querying for more from sfdc", e);
			e.printStackTrace();
			System.exit(7);
		}
	
		return null;
		
	}

//	private void setConnect() {
//	
//		logger.info(Thread.currentThread().getStackTrace()[1].getMethodName());
//		
//
//		connect.setConnection();			
//	}

	public String getLastErrMsg() {
		return lastErrMsg;
	}

	public void setLastErrMsg(String lastErrMsg) {
		this.lastErrMsg = lastErrMsg;
	}

	public HashMap<String, String> getLastErrMessage() {
		return lastErrMessage;
	}

	private void setLastErrMessage(String key, String lastErrMessage) {
		this.lastErrMessage.put(key, lastErrMessage);
	}

	public String[] getObjectFieldNames(final String objectName) {
		
		String[] objectFieldNames = null;
		
	    try {
	        // Make the describe call
	        DescribeSObjectResult describeSObjectResult = connect.getConnection().describeSObject(objectName);
	        // Get the fields
	        Field[] fields = describeSObjectResult.getFields();

	        objectFieldNames = new String[fields.length]; 
	        
	        // Iterate through each field and gets its properties 
	        for (int i = 0; i < fields.length; i++) {
	        	Field field = fields[i];
	        	objectFieldNames[i] = field.getName();
   
		    }

		} catch (ConnectionException ce) {
		    ce.printStackTrace();
	    }
	    
	    return objectFieldNames;
	}

	public String getObjectFields(final String objectName) {
		
		String objectFieldNames = null;
		
	    try {
	        // Make the describe call
	        DescribeSObjectResult describeSObjectResult = connect.getConnection().describeSObject(objectName);
	        // Get the fields
	        Field[] fields = describeSObjectResult.getFields();

	        
	        // Iterate through each field and gets its properties
	        int size = fields.length;
	        for (int i=0; i<size ; i++) {
	        	
	        	Field field = fields[i];
	        	objectFieldNames += field.getName();
	        	if ( i != size ) {
	        		objectFieldNames += ",";
	        	}
   
		    }

		} catch (ConnectionException ce) {
		    ce.printStackTrace();
	    }
	    
	    return objectFieldNames;
	}

	
	private ArrayList<String> getWantedAllObjectTypes(DescribeGlobalSObjectResult[] describeGlobalSObjectResults) {
		
		ArrayList<String> wanted = new ArrayList<String>();
		
		for(DescribeGlobalSObjectResult result : describeGlobalSObjectResults) {
			String objectName = result.getName();
			
			if ( objectName.startsWith("REFES_") ||  objectName.startsWith("ortoo_e2a") ||  !objectName.endsWith("__c") ||  objectName.startsWith("dsfs__") ) {
				continue;
			}
			wanted.add(objectName);
		}
		return wanted;
	}
	
	private ArrayList<String[]> getAllObjectTypes(ArrayList<String> wantedList) {
		
		ArrayList<String[]> allObjectTypes = new ArrayList<String[]>();
		int total = wantedList.size();
		int limit = 100;
		
		int j=0;
		String[] subObjectNames = new String[limit];
		
    	for(int i=0; i<total; i++) {
    		if ( j < limit ) {
    			subObjectNames[j++] = wantedList.get(i);
    		} else {
    			allObjectTypes.add(subObjectNames);
    			j=0;
    			int diff = total - limit;
    			if ( diff < limit ) {
    				subObjectNames = new String[diff-1];
    			} else {
    				subObjectNames = new String[limit];
    			}
    		}
    	}
    	allObjectTypes.add(subObjectNames);
	
		return allObjectTypes;
	}
	
	
	
	public void describeSObjects() {
//		public void describeSObjectSample(final String objectName, final boolean apiName) {	
	    try {
	    	
	    	DescribeGlobalResult describeGlobalResult = connect.getConnection().describeGlobal();
	    	
	    	DescribeGlobalSObjectResult[] describeGlobalSObjectResults = describeGlobalResult.getSobjects();
	    	
	    	int objectLength =  describeGlobalSObjectResults.length;
	    	TestBenchUtilities.say("Total objects: " + objectLength);
	    	
	    	ArrayList<String> wantedAllObjectTypes = getWantedAllObjectTypes(describeGlobalSObjectResults);
	    	TestBenchUtilities.say("Total wanted objects: " + wantedAllObjectTypes.size());
	    	
	    	ArrayList<String[]> allObjectTypes = getAllObjectTypes(wantedAllObjectTypes);
	    	
//	    	String[] objectNames = new String[objectLength];
//	    	
//	    	int limit = 99;
//	    	int j=0;
	    	
//	    	for(DescribeGlobalSObjectResult objResult : describeGlobalSObjectResults) {
//	    	for(int i=0; i<objectLength; i++) {
//	    		
//	    		String[] subObjectNames = new String[limit];
//	    		
////	    		for(int j=0; j<limit; j++) {
//		    	DescribeGlobalSObjectResult objResult = describeGlobalSObjectResults[i];
//			        
//		    	String objectName = objResult.getName();
//		    	
//				if ( objectName.startsWith("REFES_") ||  objectName.startsWith("ortoo_e2a") ||  !objectName.endsWith("__c") ||  objectName.startsWith("dsfs__") ) {
//					continue;
//				}
//
//	//	    		 System.out.println("Object name: " + objectName);
//		    		logger.info("Object name: " + objectName);
//		    		subObjectNames[j] = objectName;
////	    		}
//	    		allObjectTypes.add(subObjectNames); 
//	    	}
//	    	String[] subObjectNames = new String[limit];
//	    	
//	    	for(int i=0; i<wantedAllObjectTypes.size(); i++) {
//	    		if ( j < limit ) {
//	    			subObjectNames[j++] = wantedAllObjectTypes.get(i);
//	    		} else {
//	    			allObjectTypes.add(subObjectNames);
//	    			j=0;
//	    			subObjectNames = new String[limit];
//	    		}
//	    	}
//	    	allObjectTypes.add(subObjectNames);
	    	
	    	TestBenchUtilities.say("Total wanted objects in ArrayList: " + allObjectTypes.size());
	    	
	    	for(String[] objectNames : allObjectTypes) {
	    		
		    	DescribeSObjectResult[] describeSObjectResults = null;
		    	
				try {
					describeSObjectResults = connect.getConnection().describeSObjects(objectNames);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					logger.error(objectNames.toString());
				}
			    
				if ( describeSObjectResults == null ) {
					continue;
				}
		    	
				for(DescribeSObjectResult describeSObjectResult : describeSObjectResults) {
		        // Get sObject metadata 
//		        if (describeSObjectResult != null) {
//		          System.out.println("sObject name: " + 
//		                  describeSObjectResult.getName());
//		        if (describeSObjectResult.isCreateable()) 
//		            System.out.println("Createable");              
		       
		        // Get the fields
		        Field[] fields = describeSObjectResult.getFields();
//		        System.out.println("Has " + fields.length + " fields");
		        
		        // Iterate through each field and gets its properties 
//			        logger.info("sObject name: " + describeSObjectResult.getName());
			        
			        for (int i = 0; i < fields.length; i++) {
			        	com.sforce.soap.partner.Field field = fields[i];
			        	
			        	String fieldName = field.getName();
			        	String labelName = field.getLabel();
			        	if ( fieldName.toLowerCase().contains("city") || 
			        			labelName.toLowerCase().contains("city") ) {
			        		
//			        		TestBenchUtilities.say("Field name: " + field.getName() + ", Field label: " + field.getLabel());
//	
//			        		logger.info("Field name: " + field.getName() + ", Field label: " + field.getLabel());			        		
			        		
			        		String fieldType = field.getType().toString();
			        		int length = field.getLength();
				          
			        		if ( fieldType.equals("string") && length == 60  ) {
			        			logger.info("sObject name: " + describeSObjectResult.getName());
			        			logger.info("  Field name: " + field.getName() + ", Field label: " + field.getLabel());	
			        			logger.info("        Type: "+ fieldType + "(" + length + ")");
			        		}
//			        		if ( fieldType.equals("string") ) {
//					        	  if ( length != 60 ) {			          
//						        	  logger.error("Type: "+ fieldType + "(" + length + ") NOT set to 60");
//						          } else {
//						        	  logger.info("Type: "+ fieldType + "(" + length + ")");
//						          }
//			        		}
	//			          TestBenchUtilities.say("Type: "+ fieldType + "(" + length + ")");
			        	}
			        	if ( fieldName.toLowerCase().contains("postal") || 
			        			labelName.toLowerCase().contains("postal") ) {
			        		
//			        		TestBenchUtilities.say("Field name: " + field.getName() + ", Field label: " + field.getLabel());
//	
//			        		logger.info("Field name: " + field.getName() + ", Field label: " + field.getLabel());			        		
			        		
			        		String fieldType = field.getType().toString();
			        		int length = field.getLength();
				          
			        		if ( fieldType.equals("string") && length == 40  ) {
			        			logger.info("sObject name: " + describeSObjectResult.getName());
			        			logger.info("  Field name: " + field.getName() + ", Field label: " + field.getLabel());	
			        			logger.info("        Type: "+ fieldType + "(" + length + ")");
			        		}
//			        		if ( fieldType.equals("string") ) {
//					        	  if ( length != 60 ) {			          
//						        	  logger.error("Type: "+ fieldType + "(" + length + ") NOT set to 60");
//						          } else {
//						        	  logger.info("Type: "+ fieldType + "(" + length + ")");
//						          }
//			        		}
	//			          TestBenchUtilities.say("Type: "+ fieldType + "(" + length + ")");
			        	}

			        }
				}
	    	}
//		          if ( field.isAutoNumber() ) {
//		        	  TestBenchUtilities.say("Auto Number");
//		          }
//		          
//		          if ( field.isCalculated() ) {
//		        	  TestBenchUtilities.say("Formular");
//		          }
//		          
//		          // If this is a picklist field, show the picklist values
//		          if (field.getType().equals(FieldType.picklist)) {
//		              PicklistEntry[] picklistValues = 
//		                  field.getPicklistValues();
//		              if (picklistValues != null) {
//		                System.out.println("Picklist values: ");
//		                for (int j = 0; j < picklistValues.length; j++) {
//		                  if (picklistValues[j].getLabel() != null) {
//		                    System.out.println("\tItem: " + 
//		                        picklistValues[j].getLabel()
//		                    );
//		                  }
//		                }
//		              }
//		          }
//	
//		          // If a reference field, show what it references
//		          if (field.getType().equals(FieldType.reference)) {
//		              System.out.println("Field references the " +
//		                      "following objects:");
//		              String[] referenceTos = field.getReferenceTo();              
//		              for (int j = 0; j < referenceTos.length; j++) {
//		                  System.out.println("\t" + referenceTos[j]);
//		              }
//		          }
		          
		          

	    	
		} catch (ConnectionException ce) {
		    ce.printStackTrace();
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
	
	public HashMap<String, String> describeSObject(final String objectName, final boolean apiName) {
		
		HashMap<String, String> objDesciption = new HashMap<String, String>();
		
	    try {
	        // Make the describe call
	        DescribeSObjectResult describeSObjectResult = 
	            connect.getConnection().describeSObject(objectName);
	        
	        // Get the fields
	        Field[] fields = describeSObjectResult.getFields();
//	        System.out.println("Has " + fields.length + " fields");
	        
	        // Iterate through each field and gets its properties 
	        for (int i = 0; i < fields.length; i++) {
	        	Field field = fields[i];
	        	
	        	String fieldName = field.getName(); // API name;
	        	String fieldLabel = field.getLabel(); // Label name;
	        	 
//	          System.out.println("Field name: " + field.getName());
//	          System.out.println("Field label: " + field.getLabel());
	          
	          String fieldType = field.getType().toString();
	          
//	          TestBenchUtilities.say("Type: "+ fieldType);
	          
	          if ( field.isAutoNumber() ) {
//	        	  TestBenchUtilities.say("Auto Number");
	        	  fieldType += "-AutoNumber";
	          }
	          
	          if ( field.isCalculated() ) {
//	        	  TestBenchUtilities.say("formular");
	        	  fieldType += "-Calculated";
	          } 
	          
	          if ( apiName ) {
	        	  objDesciption.put(fieldName, fieldType);
	          } else {
	        	  objDesciption.put(fieldLabel, fieldType);
	          }
	        }
		} catch (ConnectionException ce) {
		    ce.printStackTrace();
	    }
	    
	    return objDesciption;
	}
	
	public boolean updateOneSObjectRecord(final String objectName, final String colNames, final String condition, final String updateFieldName, final String updateFieldValue) {
		
		boolean succeed = false;
		
//		SObject[] sobjRecords = new SObject[1];
//		
//	    try {
//	    	SObject obj = new SObject();
//	    	
//	    	obj.setType(objectName);
//	    	obj.addField(externalIdFieldName, externalIdFieldValue);
//	    	obj.addField(updateFieldName, updateFieldValue);
//	    	sobjRecords[0] = obj;
////	        SObject[] sobjRecords = getObjectRecords(objectName, colNames, condition);
////	        String objId = getObjectRecordId(objectName, colNames, condition);
////	        sobjRecords[0].setField("Id", objId);
////	        sobjRecords[0].setField(updateFieldName, updateFieldValue);
//	        
//	        UpsertResult[] upsertResults = 
//	            connect.getConnection().upsert(externalIdFieldName, sobjRecords);
//	        
//	        succeed = upsertResults[0].isSuccess();
//
//		} catch (ConnectionException ce) {
//		    ce.printStackTrace();
//	    }
	    
		try {
			SObject updateObject = new SObject();
			
			updateObject.setType(objectName);
			updateObject.setId(getObjectRecordId(objectName, colNames, condition));
			updateObject.setField(updateFieldName, updateFieldValue);
			
			SaveResult[] saveResults = connect.getConnection().update(new SObject[] {updateObject});
   
			succeed = saveResults[0].isSuccess();
		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    return succeed;
	}

	private String getObjectRecordId(final String objName, final String colNames, final String condition) {
		
		if ( connect == null ) {
			logger.error("fail to get sfdc db connection");
			return null;
		}
		
		String qstmt = "SELECT " + colNames + " FROM " + objName;
		
		qstmt += ( condition.toUpperCase().startsWith("WHERE")  ? " " : " WHERE ")  + condition;
		
		QueryResult queryResults = getQueryResult(qstmt);
		
		if ( queryResults == null ) {
			setLastErrMsg( "fail to get record by query: " + qstmt );
			logger.error("fail to get record by query: " + qstmt);
			return null;
		}
		
		if (queryResults.getRecords().length != 1) {
			setLastErrMsg("fail to get a single record by query: " + qstmt);
			logger.error("fail to get a single record by query: " + qstmt);
			return null;
		}
			 
		String id = (String) queryResults.getRecords()[0].getField("Id");		
		
		return id;
	}

}



