package net.testbench.utility;


import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;
//import javax.activation.*;

import org.apache.log4j.Logger;

public class Email {
	
	private String to = "";
	private String cc = "";
	
	private String from = "stp_qa_no_reply@stp.rba.net";
	
	private String host = "smtprelay2.rbauction.com";
	
	private String subject = "";
	private String body = "";

	private Properties props = null;
	private boolean failed = false;
	
	private Logger logger;
	
	public Email() {
		logger = Logger.getLogger(this.getClass().getName());
		setFailed(false);
	}

	public Email(Properties props) {

		logger = Logger.getLogger(this.getClass().getName());
		
		this.props = props;		
		
		this.from = props.getProperty("stpFrom");
		this.host = props.getProperty("mailserver");
	}

	public boolean send(final String projectName, final boolean trial) {
		
	      Properties sendProps = System.getProperties();

	      sendProps.setProperty("mail.smtp.host", host);

	      this.to = props.getProperty("stpTo");
	      
	      Session session = Session.getDefaultInstance(sendProps);

	      try{
	         MimeMessage message = new MimeMessage(session);

	         message.setFrom(new InternetAddress(from));
	         
	         if ( !failed ) {
		         String projTo = props.getProperty("stpTo_"+projectName);
		         
		         if ( projTo != null && !projTo.isEmpty() ) {
		        	 this.to += "," + projTo;
		         }
	         }
	         
	         String[] toEmails = to.split(",");
	         InternetAddress toDests[] = new InternetAddress[toEmails.length];
	         for (int i = 0; i < toEmails.length; i++) {
	        	 if ( toEmails[i].trim().isEmpty() ) {
	        		 continue;
	        	 }
	             toDests[i] = new InternetAddress(toEmails[i].trim().toLowerCase());
	         }
	         message.setRecipients(Message.RecipientType.TO, toDests);	         

	         this.cc = props.getProperty("stpCC");
	         
	         if ( !failed ) {
		         if ( cc != null && !cc.isEmpty() ) {	         
			         String[] ccEmails = cc.split(",");
			         InternetAddress ccDests[] = new InternetAddress[ccEmails.length];
			         for (int i = 0; i < ccEmails.length; i++) {
			        	 if ( ccEmails[i].trim().isEmpty() ) {
			        		 continue;
			        	 }
			        	 ccDests[i] = new InternetAddress(ccEmails[i].trim().toLowerCase());
			         }
			         message.setRecipients(Message.RecipientType.CC, ccDests);
		         }
	         }

	         message.setSubject( (failed ? "Failed " : "") + subject);

	         String content = props.getProperty("htmlstart") + "\n" + body + "\n" + props.getProperty("htmlend");	         
	        
	         message.setText(content, "utf-8", "html");
	         logger.info("mailserver: " + host);
	         logger.info("To: " + to);
	         logger.info("CC: " + cc);
	         logger.info("From: " + from);
	         logger.info("Subject: " + subject);
	         logger.info("Content: " + content);
	         
	         message.setHeader("X-Priority", "High") ;

	         if ( ! trial ) {
	        	 Transport.send(message);
	         }
	         
	         System.out.println("Sent message successfully....");
	         
	         return true;
	         
	      }catch (MessagingException mex) {
	         mex.printStackTrace();
	      }
	      
	      return false;
	}

	public boolean sendErrorReport(final String projectName) {
		
	      Properties sendProps = System.getProperties();

	      sendProps.setProperty("mail.smtp.host", host);

	      this.to = props.getProperty("stpCC");
	      
	      Session session = Session.getDefaultInstance(sendProps);

	      try{
	         MimeMessage message = new MimeMessage(session);

	         message.setFrom(new InternetAddress(from));
	         
	         String[] toEmails = to.split(",");
	         InternetAddress toDests[] = new InternetAddress[toEmails.length];
	         for (int i = 0; i < toEmails.length; i++) {
	        	 if ( toEmails[i].trim().isEmpty() ) {
	        		 continue;
	        	 }
	             toDests[i] = new InternetAddress(toEmails[i].trim().toLowerCase());
	         }
	         message.setRecipients(Message.RecipientType.TO, toDests);	         

	         message.setSubject("Error: " + subject);

	         String content = props.getProperty("htmlstart") + "\n" + body + "\n" + props.getProperty("htmlend");	         
	        
	         message.setText(content, "utf-8", "html");
	         logger.info("mailserver: " + host);
	         logger.info("To: " + to);
	         logger.info("CC: " + cc);
	         logger.info("From: " + from);
	         logger.info("Subject: " + subject);
	         logger.info("Content: " + content);
	         
	         message.setHeader("X-Priority", "High") ;

	         Transport.send(message);
	         
	         System.out.println("Sent message successfully....");
	         return true;
	      }catch (MessagingException mex) {
	         mex.printStackTrace();
	      }
	      
	      return false;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public boolean isFailed() {
		return failed;
	}

	public void setFailed(boolean failed) {
		this.failed = failed;
	}


}
