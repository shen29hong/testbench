package net.testbench.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
//import java.util.Vector;



import net.testbench.utility.TestBenchUtilities;

import org.apache.log4j.Logger;

public class Connect {

	private Connection connection;
	private Statement stmt;
	private DatabaseMetaData meta;
	private ResultSet rs;
	
	private boolean connectionOpened = false;
	
	private boolean scrolling;

//	private Vector<String> tableNames;
//	private int numberOfTables = 0;
//	private Vector<String> columnNames;
	
//	private String lastErr = "";
	
//	private Properties props = null;
//	private String driverClassName = "";
//	private String url = "";
	
	
	private Logger logger;

	public Connect() {
		
		logger = Logger.getLogger(this.getClass().getName());
		
	}

//	public DBConnect(final Properties props) {
//		
//		logger = Logger.getLogger(this.getClass().getName());
//		
//		this.props = props;
//
//		this.driverClassName = props.getProperty("driver");
//		this.url = props.getProperty("url");
//	}

	public void openDBConnection(final Properties props) {
		
		try {
		
			Class.forName(props.getProperty("driver")).newInstance();
			
			connection = DriverManager.getConnection(props.getProperty("url"), props);			
			
			if ( connection == null ) {
				TestBenchUtilities.sayError("Failed to make a connection to DB");
				return;
			} else {
//				con.setAutoCommit(false); 
				
				stmt = connection.createStatement();
				if ( stmt != null ) {
					TestBenchUtilities.say("make a connection to DB successfully.");
					logger.info("DB connected");
					this.setConnectionOpened(true);
//					return true;
				}
			}
		} 		
		catch (SQLException e) {

			TestBenchUtilities.sayError(e.getStackTrace()[1].getClassName() + " - " + e.getStackTrace()[1].getMethodName());
		}
		catch (Exception e) {

			TestBenchUtilities.sayError(e.getStackTrace()[1].getClassName() + " - " + e.getStackTrace()[1].getMethodName());
		}

		return;
	}

	public void closeDBConnection() {
		try {
			
			if ( connection != null ) {
				connection.close();	
				
				if ( connection.isClosed() ) {
					this.setConnectionOpened(false);
					this.stmt = null;
					this.connection = null;
				}
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
			TestBenchUtilities.sayError("Failed to close database.");
		}
		catch (Exception e) {
			e.printStackTrace();
			TestBenchUtilities.sayError("Failed to close database.");
		}
	}

	   public void createStatement() throws SQLException
	   {
	      if (meta.supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE)) {
	    	  
	         stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	         setScrolling(true);
	      } else {
	         stmt = connection.createStatement();
	         setScrolling(false);
	      }

		  return;
	   }

		public PreparedStatement prepareStatement(String s) {
			
			logger.info("prepare stmt " + s);
			try {
				
				return connection.prepareStatement(s);
				
			} catch (SQLException e) {
				
				e.printStackTrace();
				TestBenchUtilities.sayError("Failed to get prepareStatement for " + s);
			}
			
			return null;
		}

	   public void setScrolling(boolean b) {
		   this.scrolling = b;
	   }
	   
	   public boolean getScrolling()
	   {
		   return scrolling;
	   }
	   
		public ResultSet queryTable( String s)
		{
			logger.info("query stmt "+ s);
			try
			{
				rs = stmt.executeQuery(s);
			}
			catch (SQLException sqle)
			{
				TestBenchUtilities.sayError("sql exception " + s + " query failed");
				sqle.printStackTrace();
			}
			catch (Exception e)
			{
				TestBenchUtilities.sayError("exception " + s + " query failed");
				e.printStackTrace();
			}

			return rs;
		}

		public int updateTable( String s)
		{
			logger.info("udpate stmt "+ s);
			
			int c = 0;
			try
			{
				c = stmt.executeUpdate(s);
			}
			catch (SQLException sqle)
			{
				TestBenchUtilities.sayError("sql exception " + s + " query failed");
				sqle.printStackTrace();
			}
			catch (Exception e)
			{
				TestBenchUtilities.sayError("exception " + s + " query failed");
				e.printStackTrace();
			}

			return c;
		}

		public void addToBatch( String s)
		{
			try
			{
				stmt.addBatch(s);
			}
			catch (SQLException sqle)
			{
				TestBenchUtilities.sayError("addBatch exception " + s + " query failed");
				sqle.printStackTrace();
			}
			catch (Exception e)
			{
				TestBenchUtilities.sayError("addBatch exception " + s + " query failed");
				e.printStackTrace();
			}
		}

		public int[] executeBatch()
		{
			try
			{
				return stmt.executeBatch();
				
			}
			catch (SQLException sqle)
			{
				TestBenchUtilities.sayError("batch execute failed");
				sqle.printStackTrace();
			}
			catch (Exception e)
			{
				TestBenchUtilities.sayError("exception batch execute failed");
				e.printStackTrace();
			}
			
			return null;
		}

		public boolean isConnectionOpened() {
			return connectionOpened;
		}

		public void setConnectionOpened(boolean connectionOpened) {
			this.connectionOpened = connectionOpened;
		}

}
