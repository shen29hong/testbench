package net.testbench.sfdc;

import java.util.Properties;

import org.apache.log4j.Logger;

import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class Connect {

    private ConnectorConfig config;
    private PartnerConnection connection;
    private Logger logger;
    
    private boolean traceEnabled = false;

	public Connect() {
		
		logger = Logger.getLogger(this.getClass().getName());		

	}

//	public Connect(final String dir, final String env) {
//		
//		logger = Logger.getLogger(this.getClass().getName());
//		
//		Properties props = TestBenchUtilities.getProperties(dir + env + "_sfdc_properties.xml");
//
//		config = new ConnectorConfig();
//	    config.setUsername(props.getProperty("username"));
//	    config.setPassword(props.getProperty("password") + props.getProperty("token"));
//
//	    config.setTraceMessage(traceEnabled);
//	}

	public PartnerConnection getConnection() {
		return connection;
	}
	public void setConnection(final Properties props) {
		
		logger.info(Thread.currentThread().getStackTrace()[1].getMethodName());
		
		try {
			config = new ConnectorConfig();
			String loginurl = props.getProperty("loginurl");
			
			if ( loginurl != null ) {
				config.setAuthEndpoint(loginurl);
				config.setServiceEndpoint(loginurl);
			}
			
		    config.setUsername(props.getProperty("username"));
		    config.setPassword(props.getProperty("password") + props.getProperty("token"));

		    config.setTraceMessage(traceEnabled);

			connection = Connector.newConnection(config);
			
		} catch (ConnectionException e) {
			logger.error("ConnectionException cought when getting the db connection to sfdc", e);
			e.printStackTrace();
			
		} catch ( Exception e ) {
			logger.error("Exception cought when getting the db connection to sfdc", e);
			e.printStackTrace();
			
		} finally {
			
		}
	}

	public boolean isTraceEnabled() {
		return traceEnabled;
	}

	public void setTraceEnabled(boolean trace) {
		this.traceEnabled = trace;
	}

}
