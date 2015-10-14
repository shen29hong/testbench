/**
 * 
 */
package net.testbench.selenium;

import java.net.MalformedURLException;
//import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
//import java.util.HashMap;

//import net.rba.stp.base.WebDriverBase;
//import net.rba.stp.testdata.ExcelData;
//import net.rba.stp.testdata.TestEnvironmentData;
//import net.rba.stp.testreports.TestReport;




import net.testbench.utility.TestBenchUtilities;

import org.openqa.selenium.WebDriver;

/**
 * @author HONSHEN
 *
 */
public class WebDriverTest {

	protected WebDriver driver = null;

	public WebDriverTest(String browser) {

		WebDriverBase myDriver = new WebDriverBase();
		myDriver.setMyDriver(browser);
		
//		try {
//			myDriver.createRemoteWebDriver(browser);
//			
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			TestBenchUtilities.sayError(" can't create the webdriver, aborting");
//			System.exit(1);
//		}
		driver = myDriver.getMyDriver();
		
	}

	public WebDriverTest(String browser, String url) {

		WebDriverBase myDriver = new WebDriverBase();
		
		try {
			myDriver.createRemoteWebDriver(browser, url);
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			TestBenchUtilities.sayError(" can't create the webdriver, aborting");
			System.exit(1);
		}
		driver = myDriver.getMyDriver();
		
	}

	public String getURLPrefix(String url) {
		int endPosition = url.indexOf(".com");
		
		return( url.substring(0, endPosition) );
	}


	public void failTest() {		
		driver.quit();
		System.out.println("Test failed, check log file for details.");
		System.exit(1);
	}


	public void finishTest() {
		driver.quit();
	}

	
	public String sanitize(String val) { 

		if (val == null ) { 
			return "" ; 
		}

		return val.replaceAll( "[^\\p{L}\\p{N}\\s]" , "" ).replaceAll( "\\s+" , "-" ); 
	}

	public static String getTimestamp() {
		DateFormat timestamp = new SimpleDateFormat("yyyyMMdd_HHmmssS");
		Date date = new Date();
		return  timestamp.format(date);
	}
	

}

/**
* Copyright 2015 Hong Shen
*
*/

