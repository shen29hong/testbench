/**
 * 
 */
package net.testbench.selenium;

//import java.util.ArrayList;
//import java.util.HashMap;
import java.util.List;

//import net.rba.test.util.TestTools;



import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
//import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
//import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
//import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;


public abstract class Page {

	public final String linefeed = "<br>\n";
	public final String trStart = "[START] TR##";
	public final String trEnd = "[END] TR##";

//	private final int waitInterval = 500; //ms
		
	protected WebDriver driver;
	protected WebDriverWait wait;
	
	
	protected String homeUrl = "";
	
	private String errorMsg = "";
	private int timeout = 120; // seconds

//	private int tr = 0;
	
	private Logger logger;
	
	private boolean debug = false;
	
	public Page(WebDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, timeout);
		
		logger = Logger.getLogger(this.getClass().getName());
	}

	public final String getCurrentUrl() {
		return driver.getCurrentUrl();
	}
	
	/**
	 * @return the driver
	 */
	public final WebDriver getDriver() {
		return driver;
	}


	public final String getTitle() {
		return driver.getTitle();
	}
	
	public final void setHomeUrl(String url) {
		this.homeUrl = url;
	}

	public final String getHomeUrl() {
		return homeUrl;
	}
		
	public final void myDelay(final long ls) {
		try {
			Thread.sleep(ls);
		} catch (InterruptedException ie) {
			logger.error("Got exception: InterruptedException");
			ie.printStackTrace();
		}
		
	}
	
	public final void goToHome() {
		driver.get(homeUrl);
	}
	
	public final void jiraGoTo(final String id) {
		driver.get(homeUrl + "browse/"+ id);
	}
	
 	public final void refresh() {
		driver.navigate().refresh();
		driver.switchTo().defaultContent();
	}

 	public final void switchToDefaultContent() {

		driver.switchTo().defaultContent();
	}
 
 	public final void switchToFrame(final String frameName) {

		driver.switchTo().frame(frameName);
	}
 	
	public final void back() {
		driver.navigate().back();
		driver.switchTo().defaultContent();
	}
	
	public final void back(String msg) {
		driver.navigate().back();
		driver.switchTo().defaultContent();
		checkPage(msg);
	}

	
	
	public final void setErrorMsg(Exception e) {
		errorMsg += "Exception: class [" + e.getStackTrace()[1].getClassName() + "]" + linefeed +
				   "method [" + e.getStackTrace()[1].getMethodName() + "]" + linefeed + 
				   "line [" + e.getStackTrace()[1].getLineNumber() + "]" + linefeed;
	}
	
	public final void setErrorMsg(String s) {
		errorMsg += "[" + s + "]"+ linefeed;
	}

	public final void clearErrorMsg() {
		errorMsg = "";
	}

	/**
	 * @return the errorMsg
	 */
	public final String getErrorMsg() {
		return errorMsg;
	}

	private final boolean waitForPageLoaded(final String originPage) {
		wait.until(ExpectedConditions.elementToBeClickable(By.id("rba-copyright")));
		String landingPage = driver.getCurrentUrl();
		
		if ( debug ) logger.info(trEnd + " [" + landingPage + "] page loaded for origin page [" + originPage + "]");

		if ( isIE8() ||  isIE10() ) { // skipping for IEs
			myDelay(3000);
			return true;
		}

//		try {
//
//			TestMeasurements tm = TestMeasurements.getInstance();
//			tm.initMapMeasurements();
//			logger.info("[TIMING] TR"+ tr + " ####for [" + landingPage+ "####" + originPage + "]" );			
//			tm.putMapMeasurements(landingPage+ "####" + originPage, getWebTimings());
//
//		} catch (org.openqa.selenium.WebDriverException wde) {
//			
//			wde.printStackTrace();
//			
//		} catch (InterruptedException ie) {
//			
//			// TODO Auto-generated catch block
//			ie.printStackTrace();
//			
//		} catch (Exception e) {
//			
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			
//		}
		
		return true;	
	}
	
/**
	private final HashMap<String, Long> getWebTimings() throws InterruptedException {
		String[] timingNames = {
								"navigationStart",
		                        "unloadEventStart",
		                        "unloadEventEnd",
		                        "redirectStart",
		                        "redirectEnd",
		                        "fetchStart",
		                        "domainLookupStart",
		                        "domainLookupEnd",
		                        "connectStart",
		                        "connectEnd",
		                        "secureConnectionStart",
		                        "requestStart",
		                        "responseStart",
		                        "responseEnd",
		                        "domLoading",
		                        "domInteractive",
		                        "domContentLoadedEventStart",
		                        "domContentLoadedEventEnd",
		                        "domComplete",
		                        "loadEventStart",
		                        "loadEventEnd"
		                        };
		
		HashMap<String, Long> mapTimings = new HashMap<String, Long>();
		
	    JavascriptExecutor js = (JavascriptExecutor) driver;
	    
	    String jsCmd = "return window.performance.timing.loadEventEnd;";
		
	    Long loadEventEnd = getTiming(js, jsCmd);

	    while ( loadEventEnd == 0 ) { // not finish loading yet.
	    	myDelay(10); 
	    	loadEventEnd = getTiming(js, jsCmd);
	    }	    
	    
	    for(int idx=0; idx<timingNames.length; idx++) {
	    	String jsCmd1 = "return window.performance.timing." + timingNames[idx] + ";";
	    	Long tmp = getTiming(js, jsCmd1);
	    	mapTimings.put(timingNames[idx], tmp);
	    	if ( debug ) logger.info("[TIMING] TR"+ tr + timingNames[idx] +" ##for [" + tmp + "]" );
	    }
	    
	    tr++;
	    
	    return mapTimings;
	    
	}
*/
	
/**	
	private final Long getTiming(final JavascriptExecutor js, final String jsCmd) {
		Long value = 0l;
		
	    if ( !isIE10() ) { // since IE10 return double instead of long
	    	value = (Long) js.executeScript(jsCmd);
	    }
	    
	    return value;
	}
	
*/	
	public final Boolean checkPage(final String originPage){
		if ( !this.isIE8() ) { 
			waitForPageLoaded(originPage);
		}
		return getTitle().trim().endsWith("| Ritchie Bros. Auctioneers");
	}
	

	
	public final boolean clickOnLink(String hrefStr) {
		
		String origin = driver.getCurrentUrl();
		List<WebElement> linkElmList = driver.findElements(By.tagName("a"));		
		
		for (WebElement linkElm : linkElmList){
			
			if ( linkElm.getAttribute("href").endsWith(hrefStr) ) {				
				if ( debug ) logger.info("Access [" + hrefStr + "] from: " + origin);				
				linkElm.click();				
				checkPage(origin);				
				return true;				
			}
		}
		setErrorMsg("Not found [" + hrefStr + "] from [" + origin + "]" + linefeed);
		return false;		
		
	}
	
	/**
	 * @return the isFirefox
	 */
	public final boolean isFirefox() {
		Capabilities cp = ((RemoteWebDriver) driver).getCapabilities();	
		String browserName = cp.getBrowserName();

		if ( browserName.equalsIgnoreCase("firefox") ) {
			return true; 
		}
		return false;
	}
	
	/**
	 * @return the isFirefox
	 */
	public final boolean isIE8() {
		Capabilities cp = ((RemoteWebDriver) driver).getCapabilities();	
		String browserName = cp.getBrowserName();
		
		if ( browserName.equalsIgnoreCase("internet explorer") && cp.getVersion().equals("8") ) {
			return true; 
		}
		return false;
	}
	
	public final boolean isIE10() {
		Capabilities cp = ((RemoteWebDriver) driver).getCapabilities();	
		String browserName = cp.getBrowserName();
		
		if ( browserName.equalsIgnoreCase("internet explorer") && cp.getVersion().equals("10") ) {
			return true; 
		}
		return false;
	}
	
	/*
	 * supported By
	 * id, className, name, tagName, xpath
	 */
	public final void waitForVisible(final String type, final String name) {
		if ( debug ) logger.info("action waiting for [" + type + "] [" + name + "] to be visible.");
		
		try {
			if ( type.equals("id") ) {
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(name)));
			} else if ( type.equals("class") ) {
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.className(name)));
			} else if ( type.equals("tag") ) {
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName(name)));			
			} else if ( type.equals("name") ) {
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(name)));			
			} else if ( type.equals("xpath") ) {
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(name)));			
			}
		} catch (org.openqa.selenium.TimeoutException te) {
			
			if ( debug ) logger.info("Skipping this action, due to element [" + type + "] [" + name + "] time out for not visible");
			
		} catch (Exception e) {
			
			e.printStackTrace();
			System.out.println("nfg, aborting ..");
			System.exit(5);
		} 
	}

	/*
	 * supported By
	 * id, className, name, tagName, xpath
	 */
	public final void waitForClickable(final String type, final String name) {
//		wait.until(ExpectedConditions.elementToBeClickable(By.className("rba-sprite rba-dialog-close")));
		if ( debug ) logger.info("action waiting for [" + type + "] [" + name + "] to be clickable.");
		
		try {
			if ( type.equals("id") ) {
				wait.until(ExpectedConditions.elementToBeClickable(By.id(name)));
			} else if ( type.equals("class") ) {
				wait.until(ExpectedConditions.elementToBeClickable(By.className(name)));
			} else if ( type.equals("tag") ) {
				wait.until(ExpectedConditions.elementToBeClickable(By.tagName(name)));			
			} else if ( type.equals("name") ) {
				wait.until(ExpectedConditions.elementToBeClickable(By.name(name)));			
			} else if ( type.equals("xpath") ) {
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath(name)));			
			} else if ( type.equals("text") ) {
				wait.until(ExpectedConditions.elementToBeClickable(By.linkText(name)));			
			}
		} catch (org.openqa.selenium.TimeoutException te) {
			
			if (debug ) logger.info("Skipping waiting for clickable, due to element [" + type + "] [" + name + "] due to time out for not clickable.");
			
		} catch (Exception e) {
			
			e.printStackTrace();
			System.out.println("nfg, aborting ..");
			System.exit(5);
		} 
	}

	public final Integer getCount(final String src) {
		Integer count = 0;
		
		int start = src.lastIndexOf("(");
		if ( start != -1 ) {
			int end = src.lastIndexOf(")");
			
			String txtCount = src.substring(start+1, end);
			if ( debug ) logger.info("Count: " + txtCount);
			try {
				count = Integer.parseInt(txtCount);
			} catch(Exception e) {
				logger.error("Exception: " + e.getMessage());
				System.out.println("Exception: " + e.getMessage());
			}
		}
		return count;
	}
	/*
	 * supported By
	 * id, className, name, tagName, xpath
	 */
	public final WebElement getWebElement(
			final String type, 
			final String name) {		
	
		long end = System.currentTimeMillis() + 15000; 
		
		String msg = "";
		
		WebElement rtElm = null;
		
		while (System.currentTimeMillis() < end) {
			
			myDelay(200);
	
			try {
				if ( type.toLowerCase().equals("id") ) {
					rtElm = driver.findElement(By.id(name));
				} else if ( type.toLowerCase().equals("class") ) {
					rtElm = driver.findElement(By.className(name));
				} else if ( type.toLowerCase().equals("name") ) {
					rtElm = driver.findElement(By.name(name));
				} else if ( type.toLowerCase().equals("tag") ) {
					rtElm = driver.findElement(By.tagName(name));
				} else if ( type.toLowerCase().equals("xpath") ) {
					rtElm = driver.findElement(By.xpath(name));
				} else if ( type.toLowerCase().equals("text") ) {
					rtElm = driver.findElement(By.linkText(name));
				}				
//				rtElm = checkNotNull(rtElm);				
				if (rtElm.isDisplayed()) {
					if ( debug ) logger.info(msg + " " + name + " displayed");
					break;
				} else {
					if ( debug ) logger.error(msg + " " + name + " not displayed");
				}
				
			} catch (org.openqa.selenium.NoSuchElementException nsee) {				
				msg += "_";
			} catch (org.openqa.selenium.StaleElementReferenceException sere) {				
				msg += "*";
				logger.error( "Exception: " + sere.getMessage() );
				refresh();
			} catch (Exception e) {						
				e.printStackTrace();
				System.out.println(msg + " nfg, abort 2!");
				System.exit(2);
			} 	

		}
		
		return rtElm;		
		
	}

	public final List<WebElement> getWebElements(
			final String type, 
			final String name) {		
	
		
		List<WebElement> rtElmList = null;
		

		if ( type.toLowerCase().equals("id") ) {
			rtElmList = driver.findElements(By.id(name));
		} else if ( type.toLowerCase().equals("class") ) {
			rtElmList = driver.findElements(By.className(name));
		} else if ( type.toLowerCase().equals("name") ) {
			rtElmList = driver.findElements(By.name(name));
		} else if ( type.toLowerCase().equals("tag") ) {
			rtElmList = driver.findElements(By.tagName(name));
		} else if ( type.toLowerCase().equals("xpath") ) {
			rtElmList = driver.findElements(By.xpath(name));
		} else if ( type.toLowerCase().equals("text") ) {
			rtElmList = driver.findElements(By.linkText(name));
		}				
		
		return rtElmList;		
		
	}

	/*
	 * supported By
	 * id, className, name, tagName, xpath
	 */
	public final WebElement getWebElement(
			final WebElement elm, 
			final String type, 
			final String name) {
		
		long end = System.currentTimeMillis() + 15000; 
		
		String msg = "";
		
		WebElement rtElm = null;
		
		while (System.currentTimeMillis() < end) {
			
			myDelay(200);
			try {
				if ( type.toLowerCase().equals("id") ) {					
					rtElm = elm.findElement(By.id(name));					
				} else if ( type.toLowerCase().equals("class") ) {					
					rtElm = elm.findElement(By.className(name));
					
				} else if ( type.toLowerCase().equals("name") ) {
					rtElm = elm.findElement(By.name(name));
				} else if ( type.toLowerCase().equals("tag") ) {
					rtElm = elm.findElement(By.tagName(name));
				} else if ( type.toLowerCase().equals("xpath") ) {
					rtElm = elm.findElement(By.xpath(name));
				}
				
//				rtElm = checkNotNull(rtElm);
				
				if (rtElm.isDisplayed()) {
					if ( debug ) logger.info(msg + " " + name + " displayed");
					break;
				} else {
					if ( debug ) logger.info(msg + " " + name + " not displayed");
				}
				
			} catch (org.openqa.selenium.NoSuchElementException nsee) {
				
				msg += "_";
			} catch (org.openqa.selenium.StaleElementReferenceException sere) {
				
				msg += "*";
				logger.error( "Exception: " + sere.getMessage() );
				refresh();
			} catch (Exception e) {
						
				e.printStackTrace();
				System.out.println(msg + " nfg, abort 3!");
				System.exit(3);
			} 
		
		}
		return rtElm;
	}

	/*
	 * supported By
	 * find a web element by tag name, attribute name, and attribute value
	 */
	public final WebElement getWebElementFromTag(
			final String tagName, 
			final String attributeName, 
			final String expectedValue) {		
	
//		WebElement rtElm = null;
		
//		driver.switchTo().defaultContent();
		
		List<WebElement> WebElmList = driver.findElements(By.tagName(tagName));
		
		for(WebElement elm : WebElmList) {
			String attributeValue = elm.getAttribute(attributeName);
			
			if ( attributeValue == null )
				continue;
			
			if ( attributeValue.equals(expectedValue) ||
				 attributeValue.startsWith(expectedValue) ||
				 attributeValue.endsWith(expectedValue) ) {
				
				return elm;
//				break;
			}			
		}		
		
//		rtElm = checkNotNull(rtElm);
		
//		if (rtElm.isDisplayed()) {
////			logger.info(msg + " " + name + " displayed");
//			return rtElm;
//		} else {
////			logger.info(msg + " " + name + " not displayed");
//			System.exit(5);
//		}

		return null;		
		
	}

	/*
	 * supported By
	 * find a web element by tag name, tag text value
	 */
	public final WebElement getWebElementFromTagAndText(
			final String tagName, 
			final String expectedTextValue) {		
	
//		WebElement rtElm = null;
		
//		driver.switchTo().defaultContent();
		driver.navigate().refresh();
		
		List<WebElement> WebElmList = driver.findElements(By.tagName(tagName));
		
		for(WebElement elm : WebElmList) {
			String textValue = elm.getText().trim();
			
			if ( textValue == null || textValue.isEmpty() )
				continue;
			
			if ( textValue.equals(expectedTextValue) ) {
				return elm;
//				rtElm = elm;
//				break;
			}	
			
			if ( textValue.contains(expectedTextValue) ) {
				return elm;
//				rtElm = elm;
//				break;
			}			

		}		
		
//		rtElm = checkNotNull(rtElm);
		
//		if (rtElm.isDisplayed()) {
//			return rtElm;
//		} 
		
		return null;		
		
	}

	/*
	 * supported By
	 * find a web element by tag name, tag text value
	 */
	public final WebElement getWebElementFromTagAndText(
			final WebElement startElm,
			final String tagName, 
			final String expectedTextValue) {		
	
//		WebElement rtElm = null;
		
//		driver.switchTo().defaultContent();
//		driver.navigate().refresh();
		
		List<WebElement> WebElmList = startElm.findElements(By.tagName(tagName));
		
		for(WebElement elm : WebElmList) {
			String textValue = elm.getText().trim();
			
			if ( textValue == null )
				continue;
			
			if ( textValue.equals(expectedTextValue) ) {
				return elm;
//				rtElm = elm;
//				break;
			}			
		}		
		
//		rtElm = checkNotNull(rtElm);
		
//		if (rtElm.isDisplayed()) {
//			return rtElm;
//		} 
		
		return null;		
		
	}

	/*
	 * supported By
	 * find a web element by tag name, attribute name, and attribute value
	 */
	public final WebElement getWebElementFromTag(
			final WebElement startElm, 
			final String tagName, 
			final String attributeName, 
			final String expectedValue) {		
	
		WebElement rtElm = null;
		String msg = "";
		
		List<WebElement> WebElmList;
		
		if ( startElm == null ) {
		
			WebElmList = driver.findElements(By.tagName(tagName));
			msg += " driver - findElements - " + tagName + " - ";
		} else {
		
			WebElmList = startElm.findElements(By.tagName(tagName));
			msg += startElm.toString() + " - findElements - " + tagName + " - ";
		}
		
		for(WebElement elm : WebElmList) {
			String attributeValue = elm.getText();
			
			if ( attributeName == null || attributeName.isEmpty() ) {
				msg += attributeName;
				attributeValue = elm.getAttribute(attributeName);
			
				if ( attributeValue == null )
					continue;
			}
			
			
			if ( attributeValue.equals(expectedValue) ||
				 attributeValue.startsWith(expectedValue) ||
				 attributeValue.endsWith(expectedValue) ) {
				
				rtElm = elm;
				break;
			}			
		}		
		
		if (rtElm == null || !rtElm.isDisplayed()) {
			logger.error("Not found: " + msg + " - " + expectedValue);
			return null;
		} 

		if ( debug ) logger.info("Found: " + msg + " - " + expectedValue);
		
		return rtElm;		
		
	}

	/*
	 * supported By
	 * find a web element by tag name, attribute name, and attribute value
	 */
	public final WebElement getWebElementFromTag(
			final WebElement startElm, 
			final String tagName, 
			final String expectedValue) {		
	
		WebElement rtElm = null;
		
		List<WebElement> WebElmList = startElm.findElements(By.tagName(tagName));
		
		for(WebElement elm : WebElmList) {
			String txt = elm.getText().trim();
			
//			TestTools.say("text" + txt);
			if ( txt == null )
				continue;
			
			if ( txt.equals(expectedValue) ||
				 txt.startsWith(expectedValue) ||
				 txt.endsWith(expectedValue) ) {
				
				rtElm = elm;
				break;
			}			
		}		
		
//		if ( rtElm != null ) { // make sure it is displayed
//		
//			if (rtElm.isDisplayed()) {
//	//			logger.info(msg + " " + name + " displayed");
//				return rtElm;
//			} else {
//	//			logger.info(msg + " " + name + " not displayed");
//				System.exit(5);
//			}
//		}

		return rtElm;		
		
	}

//	public final <T> T checkNotNull(final T e) {
//		if (e == null) {
//			throw new MyException();
//		}
//		
//		return e;
//	}
	public final WebElement getWebElementFromTagAndAttribute(
			final String tagName,
			final String attributeName,
			final String expectedValue) {		
	
		WebElement rtElm = null;
		
		List<WebElement> WebElmList = driver.findElements(By.tagName(tagName));
		
		for(WebElement elm : WebElmList) {
WebElement imgElm = elm.findElement(By.tagName("img"));
			
			String txt = imgElm.getAttribute(attributeName);
			
			if ( txt == null || txt.isEmpty() ) {
				continue;
			}
			
			if ( txt.equals(expectedValue) ||
				 txt.startsWith(expectedValue) ||
				 txt.endsWith(expectedValue) ) {
				
				rtElm = elm;
				break;
			}			
		}		
		


		return rtElm;		
		
	}

	public final WebElement getWebElementFromTagAndAttribute(
			final WebElement startElm, 
			final String tagName,
			final String attributeName,
			final String expectedValue) {		
	
		WebElement rtElm = null;
		
		List<WebElement> WebElmList = startElm.findElements(By.tagName(tagName));
		
		for(WebElement elm : WebElmList) {
			
			WebElement imgElm = elm.findElement(By.tagName("img"));
			
			String txt = imgElm.getAttribute(attributeName);
			
			if ( txt == null || txt.isEmpty() ) {
				continue;
			}
			
			if ( txt.equals(expectedValue) ||
				 txt.startsWith(expectedValue) ||
				 txt.endsWith(expectedValue) ) {
				
				rtElm = elm;
				break;
			}			
		}

		return rtElm;		
		
	}
	
	
	public void clearBrowserCache() {
		driver.manage().deleteAllCookies();
		myDelay(6000);
	}

	public void select(final WebElement elm, final String type, final String value) {
		Select mySel = new Select(elm);	
		
		if ( type.equals("value")) {
			mySel.selectByValue(value);
		}
		if ( type.equals("text")) {
			mySel.selectByVisibleText(value);
		}

	}

	public void select(final WebElement elm, final String type, final int value) {
		Select mySel = new Select(elm);	
		
		mySel.selectByIndex(value);

	}

//	public void click(final String attrName, final String attrValue) {
//		
//		
//	}
//	
//	public void click(final String tagName, final String attrName, final String attrValue) {
//		
//		
//	}
//	
//	public void click(final WebElement startElm, final String tagName, final String attrName, final String attrValue) {
//		
//		
//	}
	
	


}


/**
 * Copyright 2015 Hong Shen
 *
 */

