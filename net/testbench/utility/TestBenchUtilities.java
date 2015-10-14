/**
 * 
 */
package net.testbench.utility;
/**
 * @author HONSHEN
 *
 */
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.codec.binary.Base64;


/**
 * @author HONSHEN
 * 
 * 
 */
public class TestBenchUtilities {
	
//	private static Logger logger = Logger.getLogger("TestBenchUtilities");
	
	private static final Pattern PAIRED_DOUBLE_QUOTE_REGEX_PATTERN =
	        Pattern.compile("\\B\"\\w*( \\w*)*\"\\B");

	public TestBenchUtilities() {
		//nothing
		
		
//		pQ = Pattern.compile("Q\\d{1,2}");
//		pZ = Pattern.compile("Z((9[1-9])|(8[3-9]))");
	}
	public static String getStmt(ArrayList<String> als) {
		String ret = "";
		for (String str : als) {
			ret += str + ", ";
		}
		ret = ret.replaceAll(", $", "");
		return ret;
	}

	public static String getTimestamp(final String option) {
		DateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd");
		
		if ( option.equals("long") ) {
			timestamp = new SimpleDateFormat("yyyyMMdd_HHmmssSS");
		} else if ( option.equals("standard") ) {
			timestamp = new SimpleDateFormat("yyyy-MM-dd");
		} else if ( option.equals("result") ) {
			timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		} else if ( option.equals("current") ) {
			timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		} else if ( option.equals("sfdc") ) {
			timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.CANADA);	
		}
		
		Date date = new Date();
		return  timestamp.format(date);
	}

	public static void message(final int numberOfElement) {
		System.out.println(numberOfElement + " items are required.");
	}
	
	public static String escapeCharForXml(final String src) {
		String ret = src.replaceAll("&", "&amp;");
		ret = ret.replaceAll("'", "&apos;");
		ret = ret.replaceAll("<", "&lt;");
		ret = ret.replaceAll(">", "&gt;");
		ret = ret.replaceAll("\"", "&quot;");	
		
		return ret;
	}
	
	public static Properties getProperties(final String fileName) {
		
//		String pFolder = "properties_folder";
//		
//		pFolder += System.getProperty("file.separator");
		
		try {
			FileInputStream fis = new FileInputStream(fileName);
			Properties prop = new Properties();			
			prop.loadFromXML(fis);
			fis.close();
			return prop;
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sayError("FileNotFoundException caught, exiting");
			System.exit(2);
		} catch (IOException ex) {
	        ex.printStackTrace();
	        sayError("FileNotFoundException caught, exiting");
	        System.exit(2);
	    }
		
		return null;
	}

	public static void saveProperties(final String fileName, final Properties props) {
		
//		String pFolder = "properties_folder";
//		
//		pFolder += System.getProperty("file.separator");
		
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
			props.storeToXML(fos, fileName);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sayError("FileNotFoundException caught, exiting");
			System.exit(2);
		} catch (IOException ex) {
	        ex.printStackTrace();
	        System.exit(2);
	    }
	}

	public static String removeUnwantedUnicodes(final String line) {
		
		return line.replaceAll( "[\u0000]|"
				+ "[\u0001]|"
				+ "[\u0002]|"
				+ "[\u0003]|"
				+ "[\u0004]|"
				+ "[\u0005]|"
				+ "[\u0006]|"
				+ "[\u0007]|"
				+ "[\u0008]|"
				+ "[\u0009]|"
				+ "[\u000b]|"
				+ "[\u000e]|"
				+ "[\u000f]|"
				+ "[\u0010]|"
				+ "[\u0011]|"
				+ "[\u0012]|"
				+ "[\u0013]|"
				+ "[\u0014]|"
				+ "[\u0015]|"
				+ "[\u0016]|"
				+ "[\u0017]|"
				+ "[\u0018]|"
				+ "[\u0019]|"
				+ "[\u001a]|"
				+ "[\u001b]|"
				+ "[\u001c]|"
				+ "[\u001d]|"
				+ "[\u001e]|"
				+ "[\u001f]"," ");
	}

	public static String removeDoubleQuotes(final String line) {
		
		return line.replaceAll( "\"", "" );
	}

	public static boolean isEndWithDoubleQuote(final String line) {
		Pattern dQuoteEndPattern = Pattern.compile("\"\r*\n*$");
		Matcher dQuoteEndPatternMatcher = dQuoteEndPattern.matcher(line);
		
		return dQuoteEndPatternMatcher.find();
	}
	
	public static String getImportDate(final String src) {
		Pattern datePattern = Pattern.compile("(\\d{1,2}-){2}\\d{4}$");
		Matcher dateMatcher = datePattern.matcher(src);
		
		if (dateMatcher.find()) {
		    return dateMatcher.group();		    
		} else {
		    return src;
		}		
	}

	public static String escapeSingleQuote(final String content) {
		if ( content == null)
			return "";
		else
			return content.replaceAll("'", "''").trim();
	}

	public static String removeForwardSlash(final String content) {
		if ( content == null)
			return "";
		else
			return content.replaceAll("\\", "").trim();
	}

	public static String cleanSQLContent(final String content) {
		if ( content == null)
			return "";
		else {
			String ret = content.trim().replaceAll("\\\\", "\\\\\\\\");
			ret = ret.replaceAll("'", "''");
			return ret;
		}		
	}

	public static String cleanHTMLContent(final String content) {
		if ( content == null)
			return "";
		else {
			String ret = StringEscapeUtils.escapeHtml(content.trim());
			return ret;
		}		
	}

	public static int getInt(final String src) {
		
		try {
			if ( hasDecimal(src) ) {
				return (int) Double.parseDouble(src);
			} 
			
			if ( hasDigit(src) ) {
				return Integer.parseInt(src);
			}
			
			return 0;
			
		} catch (NumberFormatException e) {
			
			sayError("Invalid int: [" + src + "]");
			sayError("NumberFormatException caught, exiting");
//			System.exit(16);
		}
		return 0;
	}

	public static float getFloat(final String src) {
		if ( src.isEmpty() )
			return (float) 0.0;
		
		try {
			
			return Float.parseFloat(src);
			
		} catch (NumberFormatException e) {
			
			sayError("Invalid float: [" + src + "]");
			sayError("NumberFormatException caught, exiting");
//			System.exit(16);
		}
		return (float) 0.0;
	}

	public static String convertToInt(final String src) {
		
		int index = src.indexOf(".");
		
		if ( index > 0 )
			return src.substring(0,index);

		return src;
	}
	

	public static String getDir() {
		
		String dir = "";
		
		if ( System.getProperty("os.name").toLowerCase().startsWith("win") ) {
			dir = "C:\\Users\\honshen\\Documents\\tests\\tom\\";
		} else if ( System.getProperty("os.name").toLowerCase().contains("nux") ||
					System.getProperty("os.name").toLowerCase().contains("nix") ) {
			dir = "/var/lib/tomcat7/common/data/";
		}
		return dir;
	}
	
	public static String getMap(final HashMap<String, String> mp) {
		String ret = "";
		for(String key : mp.keySet() ) {
			ret += key + "=" + mp.get(key) + "; ";
		}
		return ret;
	}



	public static void showMap(final HashMap<String, String> mp) {
		for(String key : mp.keySet() ) {
			say(key + "=" + mp.get(key));
		}
	}

	public static void showMapWithInt(final HashMap<String, Integer> mp) {
		for(String key : mp.keySet() ) {
			say(key + "=" + mp.get(key));
		}
	}

	public static void showArray(ArrayList<String> al) {
		for(int i=0; i<al.size(); i++) {
			say(i + ": " + al.get(i));
		}
	}

	public static String getStringArray(String[] asl) {
		String str = "";
		for(String as : asl) {
			str += "<li>" + as + "</li>";
		}
		return str;
	}

	
	public static void say(final String word) {
		System.out.println(word);
	}

	public static void sayError(final String message) {
		System.out.println("====> Error: " + message + " <====");
	}

	public static boolean isDigit(final String src) {
		Pattern digitPattern = Pattern.compile("^\\d+$");
		Matcher digitMatch = digitPattern.matcher(src);
		
		return digitMatch.find();
	}

	
	public static boolean hasDigit(final String src) {
		Pattern digitPattern = Pattern.compile("\\d+");
		Matcher digitMatch = digitPattern.matcher(src);
		
		return digitMatch.find();
	}

	public static boolean isDecimal(final String src) {
		Pattern digitPattern = Pattern.compile("^\\d+.\\d+$");
		Matcher digitMatch = digitPattern.matcher(src);
		
		return digitMatch.find();
	}

	public static boolean hasDecimal(final String src) {
		Pattern digitPattern = Pattern.compile("\\d+.\\d+");
		Matcher digitMatch = digitPattern.matcher(src);
		
		return digitMatch.find();
	}

	public static String cleanString(final String src) {
		String ret = src.isEmpty() || src.equals("null") ? null : src;
		
		return ret;
	}

	public static String removeSpace(final String src) {
		String ret = src;
		ret = ret.replaceAll("�","");
		ret = ret.replaceAll("\\s+", "");						
		ret = ret.replaceAll("\n+", "");		
		ret = ret.replaceAll("\t+", "");
		ret = ret.replaceAll("-","");
		ret = ret.replaceAll("�","");
		return ret;
	}
	
	public static String restoreAngleBrackets(final String src) {
		String ret = src;
		
		ret = ret.replaceAll("&lt;", "<");
		ret = ret.replaceAll("&gt;", ">");
		
		return ret;
	}
	
	public static String getUserAlias(final String name) {
		
		String ret = "";
		
		String[] tmp = name.split("\\s+");
		
		String firstName = "";
		String lastName = "";
		if ( tmp.length == 2 ) {
			firstName = tmp[0];
			lastName = tmp[1];
		} else if ( tmp.length > 2) {
			firstName = tmp[0];
			lastName = tmp[tmp.length-1];			
		}
		
		if ( firstName.isEmpty() || lastName.isEmpty() ) 
			return "";
			
		if ( firstName.length() > 3 )
			ret = firstName.substring(0, 3);
		else
			ret = firstName;
		
		if ( lastName.length() > 4 ) 
			ret += lastName.substring(0,4);
		else 
			ret += lastName;
		
		return ret.toLowerCase();
	}


	public static String getMapInfo(final HashMap<String, HashMap<String,String>> srcMap, final String srcKety, final String srcContent) {
		for( String mKey : srcMap.keySet() ) {
			HashMap<String, String> map = srcMap.get(mKey);
			for( String skey : map.keySet() ) {
				if ( map.get(skey).equals(srcContent) ) 
					return mKey;
			}
		}
		
		return null;
	}

	public static boolean areTheSameTimestamps(String spTimestamp, String sfdcTimestamp) {
		try {
			DateFormat spFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.CANADA);
			
			if ( spTimestamp.matches("\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}\\s*[AaPp][Mm]")) {
				spFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.CANADA);
			} else if ( spTimestamp.matches("\\d{1,2}/\\d{1,2}/\\d{4}")) {
				spFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.CANADA);
			} else if ( spTimestamp.matches("\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}")) {
				spFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.CANADA);
			}
			
			Date spDate = spFormat.parse(spTimestamp);
			
			DateFormat sfdcFormat =new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.CANADA);			
			
			
			if ( sfdcTimestamp.matches("^\\d{4}-\\d{1,2}-\\d{1,2}T\\d{1,2}:\\d{1,2}:\\d{1,2}.\\d{1,3}Z$") ) {
				sfdcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.CANADA);
				sfdcTimestamp = sfdcTimestamp.replaceAll("Z$", "+0000");
//				Calendar cal = Calendar.getInstance();
//				cal.setTime(spDate);
//				if ( cal.getTimeZone().inDaylightTime(spDate) ) {
//					sfdcTimestamp = sfdcTimestamp.replaceAll("Z$", "-0700");
//				} else {
//					sfdcTimestamp = sfdcTimestamp.replaceAll("Z$", "-0800");
//				}
//				if ( spDate.toString().contains("PDT") ) {
//					sfdcTimestamp = sfdcTimestamp.replaceAll("Z$", "-0700");
//				} else if ( spDate.toString().contains("PST") ) {
//					sfdcTimestamp = sfdcTimestamp.replaceAll("Z$", "-0800");
//				}
				sfdcTimestamp = sfdcTimestamp.replaceAll("Z$", "+0000");
			} else if ( sfdcTimestamp.matches("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{2}:\\d{2}:\\d{2}.\\d{2}$") ) {
				sfdcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS", Locale.CANADA);
			} else if ( sfdcTimestamp.matches("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{2}:\\d{2}:\\d{2}.\\d{3}$") ) {
				sfdcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CANADA);
			} else if ( sfdcTimestamp.matches("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{2}:\\d{2}:\\d{2}$") ) {
				sfdcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);
			} else if ( sfdcTimestamp.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$") ) {
				sfdcFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
			} 
			
			Date sfdcDate = sfdcFormat.parse(sfdcTimestamp);
			
			if ( spDate.equals(sfdcDate) )
				return true;
			else 
				return false;
	
		} catch(ParseException ex){
			sayError(" failed to parse [" + spTimestamp + "] or [" + sfdcTimestamp );
			ex.printStackTrace();
		}
		
		return false;
	}
	
	public static int compareTimestamps(final String fromTimestamp, final String toTimestamp) {
		
		if ( fromTimestamp.isEmpty() || toTimestamp.isEmpty() ) {
			return 9;
		}
		
		try {
			DateFormat fromFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.CANADA);
			
			String fromTimestampTmp = fromTimestamp;
			
			if ( fromTimestamp.matches("\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}\\s*[AaPp][Mm]")) {
				fromFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.CANADA);
			} else if ( fromTimestamp.matches("\\d{1,2}/\\d{1,2}/\\d{4}")) {
				fromFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.CANADA);
			} else if ( fromTimestamp.matches("\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}")) {
				fromFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.CANADA);
			} else if ( fromTimestamp.matches("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{2}:\\d{2}:\\d{2}.\\d{1}$") ) {
				fromFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.CANADA);
			} else if ( fromTimestamp.matches("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{2}:\\d{2}:\\d{2}.\\d{2}$") ) {
				fromFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS", Locale.CANADA);
			} else if ( fromTimestamp.matches("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{2}:\\d{2}:\\d{2}.\\d{3}$") ) {
				fromFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CANADA);
			} else if ( fromTimestamp.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$") ) {
				fromFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
			} else if ( fromTimestamp.matches("^\\d{4}-\\d{1,2}-\\d{1,2}T\\d{1,2}:\\d{1,2}:\\d{1,2}.\\d{1,3}Z$") ) {
				fromFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.CANADA);
				fromTimestampTmp = fromTimestampTmp.replaceAll("Z$", "+0000");
			} 
			
			Date fromDate = fromFormat.parse(fromTimestampTmp);
			
			DateFormat toFormat =new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.CANADA);			
			
			String toTimestampTmp = toTimestamp;
			if ( toTimestamp.matches("^\\d{4}-\\d{1,2}-\\d{1,2}T\\d{1,2}:\\d{1,2}:\\d{1,2}.\\d{1,3}Z$") ) {
				toFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.CANADA);
				toTimestampTmp = toTimestampTmp.replaceAll("Z$", "+0000");

			} else if ( toTimestamp.matches("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{2}:\\d{2}:\\d{2}.\\d{2}$") ) {
				toFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS", Locale.CANADA);
			} else if ( toTimestamp.matches("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{2}:\\d{2}:\\d{2}.\\d{3}$") ) {
				toFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CANADA);
			} else if ( toTimestamp.matches("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{2}:\\d{2}:\\d{2}$") ) {
				toFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);
			} else if ( toTimestamp.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$") ) {
				toFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
			} 
			
			Date toDate = toFormat.parse(toTimestampTmp);
			
			if ( fromDate.equals(toDate) ) {
				return 0;
			} else if ( fromDate.after(toDate) ) {
				return 1;
			} else 
				return -1;
	
		} catch(ParseException ex){
			
			if ( fromTimestamp.equals("-- No Call Back") && toTimestamp.equals("") ) {
				return 0;
			}
			
			sayError("failed to parse [" + fromTimestamp + "] or [" + toTimestamp + "]");
			
			sayError("ParseException caught");
			ex.printStackTrace();
		}
		
		return -3;
	}

	public static String getDateAndTimeFromTimestamp(final String timestamp) {
		DateFormat toFormat =new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.CANADA);			
	
		Date toDate = null;
		try {
			toDate = toFormat.parse(timestamp);
			return toDate.toString();
		} catch (ParseException e) {
			
			sayError("ParseException caught");
			e.printStackTrace();
		}
		
		return "";		
	}
	
	public static int getTimestampsDiffInMinute(final String fromTimestamp, final String toTimestamp) {
		
		if ( fromTimestamp.isEmpty() || toTimestamp.isEmpty() ) {
			return 50;
		}
		
		try {
			DateFormat dateFormat =new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.CANADA);	
			
			Date fromDate = dateFormat.parse(fromTimestamp.replaceAll("Z$", "+0000"));
			long lFromDate = fromDate.getTime();			
			
			Date toDate = dateFormat.parse(toTimestamp.replaceAll("Z$", "+0000"));
			
			long lToDate = toDate.getTime();
			
			long diffMins = Math.abs(lToDate - lFromDate ); // milliseconds
			
			diffMins /= 1000; // seconds
			diffMins /= 60; // mins
//			diffMins /= 60; // hours
//			diffMins /= 24; // days
			
			return (int) diffMins;
			
		} catch(ParseException ex){
			
			sayError("failed to parse [" + fromTimestamp + "] or [" + toTimestamp + "]");
			
			sayError("ParseException caught");
			ex.printStackTrace();
		}
		
		return 30;
	}

	public static String convertTimestamps(String spTimestamp) {
		
		String ret = "";
		
		try {
			DateFormat spFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.CANADA);
			
			if ( spTimestamp.matches("\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}\\s*[AaPp][Mm]")) {
				spFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.CANADA);
			} else if ( spTimestamp.matches("\\d{1,2}/\\d{1,2}/\\d{4}")) {
				spFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.CANADA);
			} else if ( spTimestamp.matches("\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}")) {
				spFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.CANADA);
			}
			
			Date spDate = spFormat.parse(spTimestamp);
			
			DateFormat sfdcFormat =new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);			
			
			ret = sfdcFormat.format(spDate);
	
		} catch(ParseException ex){
			
			sayError("failed to parse [" + spTimestamp + "]");
			
			sayError("ParseException caught");
			ex.printStackTrace();
		}
		
		return ret;
	}

	public static String getDateFromDateTime(final String src) {
		
		if ( src.isEmpty() )
			return "";
		
		String[] tmp = null;
		
		if ( src.matches("^\\d{4}-\\d{1,2}-\\d{1,2}T\\d{1,2}:\\d{1,2}:\\d{1,2}.\\d{1,3}Z$") ) {
			tmp = src.split("T");
		}
		
		if ( src.matches("^\\d{4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2}.{0,1}\\d{0,3}$") ) {
			tmp = src.split("\\s+");
		}
		
		if ( tmp == null ) {
			return src;
		}
		return tmp[0].trim();
	}

	public static String decodeBase64(final String encodedContent) {
		
		boolean isBase64 = Base64.isBase64(encodedContent);
		
		if ( isBase64 ) {		
		
			byte[] byteArray = Base64.decodeBase64(encodedContent.getBytes());	
			String decodedString = new String(byteArray);
			
			return decodedString;
		}
		
		return encodedContent;

	}
	
	public static boolean validateUnids(final String src) {
		  if ( src.matches("^('\\w+',\\s*)|('\\w+'\\s*)$") ) {
			  return true;
		  }
		  return false;
	  }
	  
	public static boolean hasExcludedOwnerCode(final String src) {
		Pattern pQ = Pattern.compile("Q\\d{1,2}");
		Pattern pZ = Pattern.compile("Z((9[1-9])|(8[3-9]))");

		Matcher mPQ = pQ.matcher(src);
		
		if ( mPQ.matches() ) 
			return true;
		
		Matcher mPZ = pZ.matcher(src);
		
		if ( mPZ.matches() ) {
			
			return true;
		}

		return false;
	}
	  
	public static String extractJiraId(final String url) {
		int start = url.lastIndexOf("/");
		
		if ( start != -1 ) {
			String tmp = url.substring(start+1);
			return tmp;
		}
		return url;
	}

	public static String conformXmlChar(final String text) {
		String ret = text.replaceAll("&", "&amp;");
		ret = ret.replaceAll("<", "&lt;");
		ret = ret.replaceAll(">", "&gt;");
		return ret;
	}
//	public static String CleanInvalidXmlChars(final String text) 
//	{ 
//	    // From xml spec valid chars: 
//	    // #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]     
//	    // any Unicode character, excluding the surrogate blocks, FFFE, and FFFF. 
////	    String re = "[^\x09\x0A\x0D\x20-\uD7FF\uE000-\uFFFD\u10000-u10FFFF]"; 
////	    return Regex.Replace(text, re, ""); 
//	}
	
	
	/**
	 * 
	 * @param futureDays (in the future)
	 * @param eventDays (number of event days)
	 * @return
	 */
	public static Map<String, String> getEventDate(final String country, final int futureDays, final int eventDays) {
		
		Map<String, String> eventDayInfo = new HashMap<String, String>();
		
		DateFormat nameDF = new SimpleDateFormat("MMM dd, yyyy");
		String localeFormat = "dd/MM/yyyy"; // USA format
		
		if ( country.equals("Canada") ) {
			localeFormat = "MM/dd/yyyy";
		}
		
		DateFormat startDF = new SimpleDateFormat(localeFormat);
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, futureDays); 
		Date startDate = c.getTime();
		Date endDate = c.getTime();
		if ( eventDays > 1 ) {
			c.add(Calendar.DATE, eventDays);
			endDate = c.getTime();
		}
		eventDayInfo.put("eventDate", nameDF.format(startDate));
		eventDayInfo.put("startDate", startDF.format(startDate));
		eventDayInfo.put("endDate", startDF.format(endDate));
		
		
		return  eventDayInfo;
	}

	public static HashMap<String, HashMap<String,String>> getEventDate(final int futureDays, final int eventDays) {
		
		HashMap<String, HashMap<String,String>> eventDayInfo = new HashMap<String, HashMap<String, String>>();
		
		DateFormat nameDF = new SimpleDateFormat("MMM dd, yyyy");
//		String localeFormat = "dd/MM/yyyy"; // USA format
		
//		DateFormat startDF = new SimpleDateFormat(localeFormat);
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, futureDays); 
		eventDayInfo.put("startDate", getDateInfo(c));
		Date startDate = c.getTime();

		if ( eventDays > 1 ) {
			c.add(Calendar.DATE, eventDays);
		}
		eventDayInfo.put("endDate", getDateInfo(c));
		
		HashMap<String,String> tmp = new HashMap<String, String>();
		tmp.put("eventDate", nameDF.format(startDate));
		eventDayInfo.put("eventDate", tmp);		
		
		return  eventDayInfo;
	}
	
	private static HashMap<String, String> getDateInfo(final Calendar c) {
		HashMap<String, String> dinfo = new HashMap<String, String>();
		
		dinfo.put("year", "" + c.get(Calendar.YEAR));
		dinfo.put("month", "" + c.get(Calendar.MONTH));
		dinfo.put("dayOfMonth", "" + c.get(Calendar.DAY_OF_MONTH));
		
		return dinfo;
	}

	public static String getRandomAlphabeticString(final int length) {
		String randomStr = RandomStringUtils.randomAlphabetic(length);		
		return cleanRepetedChar(randomStr);
	}
	
	public static String getRandomNumbericString(final int length) {
		return RandomStringUtils.randomNumeric(length);
	}

	private static String cleanRepetedChar(final String src){
//	    String s = src;
	    String mod = src;
	    int repeated = 0;
	    for (int i = 0; i < src.length(); i++) {

	        for (int j = 0; j < mod.length(); j++) {

	            if(src.charAt(i)==mod.charAt(j))
	            {            	
	            	repeated++;
//	            	if ( distinct > 1 ) {
//	            		String d=String.valueOf(mod.charAt(j)).trim();
//	            		mod = mod.replace(d, "");
//	            	}
	            } else {
	            	repeated = 0;
	            }	            
	        }
	        if ( repeated > 2 ) {
	        	String d=String.valueOf(src.charAt(i)).trim();
	        	mod = mod.replaceAll(d+d, d);
	        }
//	        distinct = 0;
	    }
	    return mod;
	}
	public static String replaceNotPairedDoubleQuote(String input) {
	    StringBuffer sb = new StringBuffer();
	    Matcher matcher = PAIRED_DOUBLE_QUOTE_REGEX_PATTERN.matcher(input);
	    int start = 0;
	    int last = 0;
	    while (matcher.find()) {
	        start = matcher.start();
	        sb.append(input.substring(last, start).replace("\"", ""));
	        last = matcher.end();
	        sb.append(matcher.group());
	    }
	    sb.append(input.substring(last).replace("\"", ""));
	    return sb.toString();
	}
	
	public static String getIndustry(final String industry) {
		
		String ret = industry;
		
		int start = industry.indexOf("-");
		if ( start != -1 ) {
			ret = industry.substring(start+1).trim();
		}
		
		ret = ret.replaceAll("Marine, Aircraft and Rail", "Marine, Aircraft & Rail");
		
		return ret;
	}

	public static void writeToFile(final String filename, final String data) {
		BufferedWriter bw = null;
		
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename, false), "UTF8") ) ;
			bw.write(data);
			
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
		} finally {
			if (bw != null) try{
				bw.close();
			}catch(IOException e2){
				//ignore
			}
		}
	}

	public static int getRandomInt(final int end) {
		Random generator = new Random();
		int i = generator.nextInt(end);
		return i;
	}
	
	/**
	 * 
	 * @param args
	 */
//	public static void main (String[] args) {
////		TestTools.areTheSameTimestamps("7/25/2012 11:08:07 AM", "2012-07-25T18:08:07.000+0000");
////		System.out.println(TestTools.cleanContent("home address\\"));
//		
////		if ( TestTools.validateUnids("'3B495FEFB4C0395688257B5200762D50', '3B495FEFB4C0395688257B5200762D50'")) {
////			say("OK");
////		} else 
////		sayError("NG");
//		
////		TestBenchUtilities tt = new TestBenchUtilities();
//		
//		TestBenchUtilities.getEventDate(35, 1);
//		System.out.println(TestBenchUtilities.getTimestamp("sfdc"));
//		
//	}

}
