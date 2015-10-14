/**
 * 
 */
package net.testbench.db;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import net.testbench.utility.TestBenchUtilities;
//import com.mysql.jdbc.AbandonedConnectionCleanupThread;
//import net.rba.stp.tools.TestTools;

/**
 * @author HONSHEN
 *
 */
public class TestLinkDBQuery {

	private String lastErr = "";
	
	private Properties props = null;
		
	private Connect connect;
	private Logger logger;

	public TestLinkDBQuery(final Properties props) {

		logger = Logger.getLogger(this.getClass().getName());
		
		this.props = props;
		connect = new Connect();
		connect.openDBConnection(props);
	}

	public void closeDB() {

		connect.closeDBConnection();
		
	}

	public boolean reconnectDB(final String username, final String password) {

		if ( this.props == null || connect == null) {
			logger.error("either props or connect not set");
			return false;
		}
		
		props.setProperty("user", username);
		props.setProperty("password", password);
		
		connect.closeDBConnection();
		
		connect.openDBConnection(props);
		
		return true;
	}


	public void updateRecord(final String queryStmt) {
		
		if ( queryStmt == null || queryStmt.isEmpty() ) {
			logger.error("no queryStmt ");
			return;
		}
		
		logger.info("queryStmt: " + queryStmt);
				
		if ( !connect.isConnectionOpened() ) {
			connect.openDBConnection(this.props);
			if ( !connect.isConnectionOpened() ) {
				TestBenchUtilities.sayError("failed to re-open the db connections, aborting.");
				System.exit(5);
			}
		}
		
		connect.updateTable(queryStmt);
	
		return;		
	}

	public void updateRecord(final String queryStmt, final boolean run) {
		
		if ( queryStmt == null || queryStmt.isEmpty() ) {
			logger.error("no queryStmt ");
			return;
		}
		
		logger.info("queryStmt: " + queryStmt);
				
		if ( !connect.isConnectionOpened() ) {
			connect.openDBConnection(this.props);
			if ( !connect.isConnectionOpened() ) {
				TestBenchUtilities.sayError("failed to re-open the db connections, aborting.");
				System.exit(5);
			}
		}
		
		if ( run ) {
			
			int res[] = connect.executeBatch();	
			
			for(int i : res) {
				logger.info("updated = " + i);
			}
		} else {
			connect.addToBatch(queryStmt);
		}
	
		return;		
	}
	
	
	public HashMap<String, String> getAllRecords(final String queryStmt, final String columnName) {
		
		if ( queryStmt == null || queryStmt.isEmpty() ) {
			return null;
		}
		
		logger.info("queryStmt: " + queryStmt);
		HashMap<String, String> rec = new HashMap<String, String>();
		
		try {
			
			if ( !connect.isConnectionOpened() ) {
				connect.openDBConnection(this.props);
				if ( !connect.isConnectionOpened() ) {
					TestBenchUtilities.sayError("failed to re-open the db connections, aborting.");
					System.exit(5);
				}
			}
			
			ResultSet rs = connect.queryTable(queryStmt);

			while (  rs.next() ) {				
				Object data = rs.getObject(columnName);
				
				String key = data.toString();
				rec.put(key, "1");			
			}
			
			return rec;
			
		} catch (SQLException e) {
			TestBenchUtilities.sayError("exception " + queryStmt + " rs failed.");
			e.printStackTrace();
			
		}
		
		return null;
	}

	public ArrayList<HashMap<String, String>> getAllRecords(final String queryStmt, final String[] columnNames) {
		
		if ( queryStmt == null || queryStmt.isEmpty() ) {
			return null;
		}
		
		logger.info("queryStmt: " + queryStmt);
		
		ArrayList<HashMap<String, String>> records = new ArrayList<HashMap<String, String>>();
		
		try {
			
			if ( !connect.isConnectionOpened() ) {
				connect.openDBConnection(this.props);
				if ( !connect.isConnectionOpened() ) {
					TestBenchUtilities.sayError("failed to re-open the db connections, aborting.");
					System.exit(5);
				}
			}
			
			ResultSet rs = connect.queryTable(queryStmt);

			while (  rs.next() ) {	
				HashMap<String, String> rec = new HashMap<String, String>();
				for(String colname : columnNames ) {
					String key = colname;
					Object data = rs.getObject(colname);
					
					String value = data.toString();
					rec.put(key, value);	
				}
				records.add(rec);
			}
			
//			return records;
			
		} catch (SQLException e) {
			TestBenchUtilities.sayError("exception " + queryStmt + " rs failed.");
			e.printStackTrace();
			
		}
		
		return records;
	}


	public Map<String, Map<String, String>> getRequirements(final String queryStmt, final String mKeyName, final String[] columnNames) {
		
		if ( queryStmt == null || queryStmt.isEmpty() ) {
			return null;
		}
		
		logger.info("queryStmt: " + queryStmt);
		Map<String, Map<String, String>> records = new HashMap<String, Map<String, String>>();
		
		try {
			
			if ( !connect.isConnectionOpened() ) {
				connect.openDBConnection(this.props);
				if ( !connect.isConnectionOpened() ) {
					TestBenchUtilities.sayError("failed to re-open the db connections, aborting.");
					System.exit(5);
				}
			}
			
			ResultSet rs = connect.queryTable(queryStmt);

			while (  rs.next() ) {
				Map<String, String> rec = new HashMap<String, String>();
				Object data = rs.getObject(mKeyName);
				String mKey = data.toString();
				
				for(int i=0; i<columnNames.length; i++) {
					String key = columnNames[i];
					Object sdata = rs.getObject(key);
					
					String value = sdata.toString();
					
					rec.put(key, value);
				}
				if ( records.get(mKey) == null ) {
					records.put(mKey, rec); 
				} else {
					int cVer = TestBenchUtilities.getInt(rec.get("version"));
					int pVer = TestBenchUtilities.getInt(records.get(mKey).get("version"));
					
					if ( cVer > pVer ) {
						records.put(mKey, rec);
					}
				}
			}
			
			return records;
			
		} catch (SQLException e) {
			TestBenchUtilities.sayError("exception " + queryStmt + " rs failed");
			e.printStackTrace();
			
		}
		
		return null;
	}

	public HashMap<String, HashMap<String, String>> getRequirements(final String projectName, final String queryStmt, final String mKeyName, final String[] columnNames) {
		
		if ( queryStmt == null || queryStmt.isEmpty() ) {
			return null;
		}
		
		logger.info("queryStmt: " + queryStmt);
		HashMap<String, HashMap<String, String>> records = new HashMap<String, HashMap<String, String>>();
		
		try {
			
			if ( !connect.isConnectionOpened() ) {
				connect.openDBConnection(this.props);
				if ( !connect.isConnectionOpened() ) {
					TestBenchUtilities.sayError("failed to re-open the db connections, aborting.");
					System.exit(5);
				}
			}
			
			PreparedStatement pstmt = connect.prepareStatement(queryStmt);
			pstmt.setString(1, projectName);
			
			ResultSet rs = pstmt.executeQuery();

			while (  rs.next() ) {
				HashMap<String, String> rec = new HashMap<String, String>();
				Object data = rs.getObject(mKeyName);
				String mKey = data.toString();
				
				for(int i=0; i<columnNames.length; i++) {
					String key = columnNames[i];
					Object sdata = rs.getObject(key);
					String value = "";
					if ( sdata != null ) {
						value = sdata.toString();
					}					
					rec.put(key, value);
				}
				if ( records.get(mKey) == null ) {
					records.put(mKey, rec); 
				} else {
					int cVer = TestBenchUtilities.getInt(rec.get("version"));
					int pVer = TestBenchUtilities.getInt(records.get(mKey).get("version"));
					
					if ( cVer > pVer ) {
						records.put(mKey, rec);
					}
				}
			}
			
			return records;
			
		} catch (SQLException e) {
			TestBenchUtilities.sayError("exception " + queryStmt + " rs failed");
			e.printStackTrace();
			
		}
		
		return null;
	}


	public Map<String, Map<String, String>> getTCRecords(final String queryStmt, final String[] columnNames) {
		
		if ( queryStmt == null || queryStmt.isEmpty() ) {
			return null;
		}
		
		Map<String, Map<String, String>> records = new HashMap<String, Map<String, String>>();		
		
		Map<String, Map<String, String>> tmps = new HashMap<String, Map<String, String>>();
		try {
			
			if ( !connect.isConnectionOpened() ) {
				connect.openDBConnection(this.props);
				if ( !connect.isConnectionOpened() ) {
					TestBenchUtilities.sayError("failed to re-open the db connections, aborting.");
					System.exit(5);
				}
			}
			
			ResultSet rs = connect.queryTable(queryStmt);

			while (  rs.next() ) {

				HashMap<String, String> tmp = new HashMap<String, String>();

				for(int i=0; i<columnNames.length; i++) {
					String colname = columnNames[i];
					
					Object obj = rs.getObject(colname);
					
					String value = obj.toString();
					tmp.put(colname, value);
				}
				String tc_external_id = tmp.get("tc_external_id");
				
				boolean save = false;
				/**
				 * need to be saved?
				 */				
				if ( tmps.get(tc_external_id) == null ) {
					tmps.put(tc_external_id, tmp); 
					save = true;
				} else {
					int cVer = TestBenchUtilities.getInt(tmp.get("version"));
					int pVer = TestBenchUtilities.getInt(tmps.get(tc_external_id).get("version"));
					
					if ( cVer > pVer ) {						
						/**
						 * remove previous saved record from records
						 */
						records.remove(tmps.get(tc_external_id).get("cf_node_id"));
						tmps.put(tc_external_id, tmp); 
						save = true;
//						/**
//						 * save the current record to records
//						 */
//						data.put("summary", tmp.get("summary"));
//						records.put(tmp.get("cf_node_id"), data);
					}
				}
				
				if ( save ) {					
//					data.put("summary", tmp.get("summary"));
					records.put(tmp.get("cf_node_id"), tmp);
				}
			}
			
		} catch (SQLException e) {
			logger.error("SQL exception " + queryStmt + " rs failed", e);
			e.printStackTrace();
			return null;
			
		} catch (Exception e) {
			logger.error("exception " + queryStmt + " failed", e);
			e.printStackTrace();
			return null;
			
		}
		
		return records;
	}

	//final String queryStmt, final String[] columnNames
	public HashMap<String, HashMap<String, String>> getAllQaUserRecords() {
		String[] columnNames = {"summary", "username", "password", "version"};
		
		String dbPrefix = props.getProperty("testlink database prefix");
		
		if ( dbPrefix == null ) {
			dbPrefix = "stp";
		}
		
		String projectPrefix = props.getProperty("testlink project prefix");
		
		if ( projectPrefix == null ) {
			projectPrefix = "QA";
		}
		String queryStmt = "SELECT tcv.id as cf_node_id, tc_external_id, version, summary, uv.value as username, pv.value as password, su.login as author, creation_ts, su1.login as updater, modification_ts FROM " + dbPrefix + "tcversions tcv "
				+ "LEFT JOIN " + dbPrefix + "users su ON tcv.author_id = su.id "
				+ "LEFT JOIN " + dbPrefix + "users su1 ON tcv.updater_id = su1.id "
				+ "LEFT JOIN ( "
				+ "SELECT node_id, name, value, label FROM " + dbPrefix + "cfield_design_values cfdv LEFT JOIN " + dbPrefix + "custom_fields cfs ON cfdv.field_id = cfs.id "
				+ "WHERE name='username' ) uv ON uv.node_id = tcv.id "
				+ "LEFT JOIN ( "
				+ "SELECT node_id, name, value, label FROM " + dbPrefix + "cfield_design_values cfdvp LEFT JOIN " + dbPrefix + "custom_fields cfsp ON cfdvp.field_id = cfsp.id "
				+ "WHERE name='password' ) pv ON pv.node_id = tcv.id "
				+ "WHERE tcv.id IN ( SELECT id FROM " + dbPrefix + "nodes_hierarchy pnode "
				+ "WHERE pnode.parent_id IN ( SELECT id FROM " + dbPrefix + "nodes_hierarchy snode "
				+ "WHERE snode.parent_id IN ( SELECT id FROM " + dbPrefix + "nodes_hierarchy snode " 
				+ "WHERE snode.parent_id IN ( SELECT id FROM " + dbPrefix + "nodes_hierarchy "
				+ "WHERE  id in ( SELECT id FROM " + dbPrefix + "testprojects proj WHERE proj.prefix='"+ projectPrefix +"' ) ) ) ) ) ORDER BY tcv.modification_ts desc";

		if ( queryStmt == null || queryStmt.isEmpty() ) {
			return null;
		}
		
		HashMap<String, HashMap<String, String>> records = new HashMap<String, HashMap<String, String>>();		
		
		try {
			
			if ( !connect.isConnectionOpened() ) {
				connect.openDBConnection(this.props);
				if ( !connect.isConnectionOpened() ) {
					TestBenchUtilities.sayError("failed to re-open the db connections, aborting.");
					System.exit(5);
				}
			}
			
			ResultSet rs = connect.queryTable(queryStmt);

			while (  rs.next() ) {
				HashMap<String, String> data = new HashMap<String, String>();

				for(int i=0; i<columnNames.length; i++) {
					String colname = columnNames[i];
					
					Object obj = rs.getObject(colname);
					
					String value = obj.toString();
					data.put(colname, value);
				}
				String username = data.get("username");
				/**
				 * need to be saved?
				 */				
				if ( records.get(username) == null ) {
					records.put(username, data);
				} else {
					int cVer = TestBenchUtilities.getInt(data.get("version"));
					int pVer = TestBenchUtilities.getInt(records.get(username).get("version"));
				
					if ( cVer > pVer ) {						
						/**
						 * overwrite the saved record from records
						 */
						records.put(username, data);
					}
				}
			}
			
		} catch (SQLException e) {
			logger.error("SQL exception " + queryStmt + " not queried", e);
			e.printStackTrace();
			return null;
			
		} catch (Exception e) {
			logger.error("exception " + queryStmt + " failed", e);
			e.printStackTrace();
			return null;			
		}
		
		return records;
	}



	public String getSingleRecordValue(final String queryStmt, final String colNmae) {
		
		if ( queryStmt == null || queryStmt.isEmpty() ) {
			return null;
		}
		
		try {
			
			if ( !connect.isConnectionOpened() ) {
				connect.openDBConnection(this.props);
				if ( !connect.isConnectionOpened() ) {
					TestBenchUtilities.sayError("failed to re-open the db connections, aborting.");
					System.exit(5);
				}
			}
			
			ResultSet rs = connect.queryTable(queryStmt);

			if (  rs.next() ) {
				Object data = rs.getObject(colNmae);
				
				String value = data.toString();
				
				return value;
			}
			
		} catch (SQLException e) {
			TestBenchUtilities.sayError("exception " + queryStmt + " not inserted");
			e.printStackTrace();
			
		}
		
		return null;
	}

	public Map<String, String> getCustomeFieldsRecords(final String queryStmt, final String keyName, final String valueName) {
		
		if ( queryStmt == null || queryStmt.isEmpty() ) {
			return null;
		}
		
		logger.info("queryStmt: " + queryStmt);
		Map<String, String> rec = new HashMap<String, String>();
		
		try {
			
			if ( !connect.isConnectionOpened() ) {
				connect.openDBConnection(this.props);
				if ( !connect.isConnectionOpened() ) {
					TestBenchUtilities.sayError("failed to re-open the db connections, aborting.");
					System.exit(5);
				}
			}
			
			ResultSet rs = connect.queryTable(queryStmt);

			while (  rs.next() ) {
				Object data = rs.getObject(keyName);
				String key = data.toString();
				data = rs.getObject(valueName);
				String value = data.toString();
				rec.put(key, value);
			}
			
			return rec;
			
		} catch (SQLException e) {
			TestBenchUtilities.sayError("exception " + queryStmt + " not inserted");
			e.printStackTrace();			
		}
		
		return null;
	}
	
	public String getLastErr() {
		return lastErr;
	}

	public void setLastErr(String lastErr) {
		this.lastErr = lastErr;
	}

}
