package net.testbench.tests;

import java.util.HashMap;

import net.testbench.selenium.WebDriverTest;
import net.testbench.sfdc.SFDCPartnerQuery;
import net.testbench.utility.Test;
import net.testbench.utility.TestBenchUtilities;

public class UnitTest extends Test {

	private SFDCPartnerQuery sfdcpq;
	
	private String[] tmp = {"", ""};
	public UnitTest() {
		// TODO Auto-generated constructor stub
		
		setUp("test03f_sfdc_properties.xml");
	}

	public void showApple() {
		System.out.println("-+++++++++++++++++++++++++`");
		System.out.println("/++++++++++++++++++++++++/`");
		System.out.println("");
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		
		
	
	}
	public void runSFDCTest() {
		sfdcpq = new SFDCPartnerQuery(this.props, false);
		
//		sfdcpq.describeSObjectSample("REFES_Attribute_Choice__c");
		
		HashMap<String, String> objDescription = sfdcpq.describeSObject("REFES_Attribute_Choice__c", false);
		
		TestBenchUtilities.showMap(objDescription);
	}

	
	public void testWebDriver() {
		
		WebDriverTest wdtest = new WebDriverTest("chrome");
		
	}
	  public static void main(String[] args) {

		  TestBenchUtilities.say(TestBenchUtilities.getTimestamp("current"));
		  UnitTest unitTest = new UnitTest();
		  unitTest.testWebDriver();
//		  unitTest.runSFDCTest();
//		  System.out.println(System.getProperty("os.name"));
//		  unitTest.showApple();
		  TestBenchUtilities.say(TestBenchUtilities.getTimestamp("current"));
		  System.exit(0);
	  }
}
