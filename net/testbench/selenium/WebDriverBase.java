/**
 * support WebDriverBase
 * firefox: Windows, Linux (latest version), 
 * chrome:  Windows, Linux (latest version),
 * ie:      Windows IE8, 
 * safari:  Max OS X, Windows (safari 6)
 *
 */
package net.testbench.selenium;

/**
 * @author HONSHEN
 *
 */

//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
//import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;

import static org.junit.Assume.assumeTrue;

public final class WebDriverBase {
	private WebDriver myDriver;
	private String webBrowserName = "";
//	private String fileSeparator = "";
	
	public WebDriverBase() {
		super();
		
//		fileSeparator = System.getProperty("file.separator");
	}
	/**
	 * @param args
	 */
	public WebDriver getMyDriver() {
		return myDriver;
	}
	
	public void setMyDriver(String browser) {
		String webBrowserName = browser;

		String fileSeparator = System.getProperty("file.separator");
		String userHomeLibDir = System.getProperty("user.home") + fileSeparator + "lib" + fileSeparator;

		if (webBrowserName.equals("firefox")){

			FirefoxProfile profile = new FirefoxProfile();	
			profile.setEnableNativeEvents(true);

			myDriver = new FirefoxDriver(profile);
			
		} else if (webBrowserName.equals("ie")){
			
			System.setProperty("webdriver.ie.driver", userHomeLibDir + "IEDriverServer.exe");
			myDriver = new InternetExplorerDriver();
			
		} else if (webBrowserName.equals("ie32")){
			
			System.setProperty("webdriver.ie.driver", userHomeLibDir + "IEDriverServer32.exe");
			myDriver = new InternetExplorerDriver();
			
		} else if (webBrowserName.equals("ie64")){
			
			System.setProperty("webdriver.ie.driver", userHomeLibDir + "IEDriverServer64.exe");
			myDriver = new InternetExplorerDriver();
			
		} else if (browser.equals("chrome")){
			
			final String chromeDriverExecutable = userHomeLibDir + "chromedriver.exe";
			
			if ( System.getProperty("os.name").toLowerCase().startsWith("win") ) {
				System.setProperty("webdriver.chrome.driver", chromeDriverExecutable);
			} else if ( System.getProperty("os.name").toLowerCase().contains("nux") ||
						System.getProperty("os.name").toLowerCase().contains("nix") ) {
				System.setProperty("webdriver.chrome.driver", chromeDriverExecutable);
			}	
			
			myDriver = new ChromeDriver();
			
			/**
			 * default startup - myDriver = new ChromeDriver();	
			 */
			/**
			 * turn off the warning of ignore-certificate-errors
			 */
//			ChromeOptions chromeOptions = new ChromeOptions();			
//			List<String> options = new ArrayList<String>();
//			options.add("ignore-certificate-errors");
//			chromeOptions.setExperimentalOption("excludeSwitches", options);			
//			chromeOptions.addArguments("--start-maximized");
//			DesiredCapabilities capabilities = DesiredCapabilities.chrome();			
//			capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
//			myDriver = new ChromeDriver(capabilities);		
			
		} else if (webBrowserName.equals("safari")){
			
			if (isSupportedPlatform() ) {
				
				myDriver = new SafariDriver();
				
			} else {
				
				System.out.println("safari webdriver is not supported for this platform [" + Platform.getCurrent() + "]." );
				System.exit(1);
				
			}
		}
	}

/*	private void setMyChromeDriver() {
		
		String chromeDriverExecutable = this.fileSeparator + "lib"+ this.fileSeparator + "chromedriver.exe";

		if ( System.getProperty("os.name").toLowerCase().startsWith("win") ) {
			System.setProperty("webdriver.chrome.driver", ".." + chromeDriverExecutable);
		} else if ( System.getProperty("os.name").toLowerCase().contains("nux") ||
					System.getProperty("os.name").toLowerCase().contains("nix") ) {
			System.setProperty("webdriver.chrome.driver", "." + chromeDriverExecutable);
		}	
		/**
		 * default startup - myDriver = new ChromeDriver();	
		 *
		/**
		 * turn off the warning of ignore-certificate-errors
		 *
		ChromeOptions chromeOptions = new ChromeOptions();			
		List<String> options = new ArrayList<String>();
		options.add("ignore-certificate-errors");
		chromeOptions.setExperimentalOption("excludeSwitches", options);			
		chromeOptions.addArguments("--start-maximized");
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();			
		capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
		
		myDriver = new ChromeDriver(capabilities);		
	} */

	public void setMyDriver(String browser, String useragent) {
		String webBrowserName = browser;
	
		if (webBrowserName.equals("firefox")){
			
			FirefoxProfile profile = new FirefoxProfile();	
			profile.setPreference("general.useragent.override", useragent);
			
			myDriver = new FirefoxDriver(profile);
			
		}else {
			
			System.out.println("Browser User-Agent is not supported for [" + browser + "].");
			System.exit(1);
			
		}		

	}
	
	private static boolean isSupportedPlatform() {
		Platform current = Platform.getCurrent();
		return Platform.MAC.is(current) || Platform.WINDOWS.is(current);
	}
	/**
	 * @return the webBrowserName
	 */
	public String getWebBrowserName() {
		return webBrowserName;
	}

	public void createRemoteWebDriver(final String browserName, final String myUrl) throws MalformedURLException {
		this.webBrowserName = browserName;
		
		Platform current = Platform.getCurrent();
		
		URL url = new URL(myUrl);//("http://10.2.17.53:5555/wd/hub/");
		
		if (browserName.equals("ie") || browserName.equals("internet explorer") ) {
			assumeTrue(Platform.WINDOWS.is(current));
		}

		if (browserName.equals("safari") ) {
			assumeTrue(Platform.MAC.is(current));
		}

		DesiredCapabilities dCap = new DesiredCapabilities();
		dCap.setBrowserName(webBrowserName);
		dCap.setPlatform(current);
		dCap.setJavascriptEnabled(true);
		
		myDriver = new RemoteWebDriver(url, dCap);
	}
}

/**
 * Copyright 2015 Hong Shen
 *
 */