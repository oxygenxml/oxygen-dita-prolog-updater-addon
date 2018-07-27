package com.oxygenxml.prolog.updater;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.text.BadLocationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import com.oxygenxml.prolog.updater.prolog.content.DateFormats;

/**
 * Test if the default format is used if the given date format is invalid.
 * 
 * @author cosmin_duna
 *
 */
public class DitaUpdaterCritdatesInvalidDateFormatTest extends AuthorTestCase {

	@Override
	public String getDateFormat() {
		// Set a invalid date format
		return "invalidDateFormat";
	}
	
	/**
	 * Test if the critdates tag is correct modified after save operation.
	 * @throws IOException
	 * @throws SAXException
	 * @throws BadLocationException
	 * @throws ParserConfigurationException 
	 * @throws TransformerException 
	 */
	public void testUpdateProlog() throws IOException, SAXException, BadLocationException, ParserConfigurationException, TransformerException {

		//Get the local date, using the default date format.
		DateFormat dateFormat = new SimpleDateFormat(DateFormats.DEFAULT_DATE_PATTERN);
		Date date = new Date();
		
		final String localDate = dateFormat.format(date);
		
		
	//
	//Test document with critdates element that contains created .
	//
	//input document
	String xmlWithCreated ="<?xml version=\"1.0\" encoding=\"utf-8\"?>"+
	    "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">"+
	    "<topic id=\"topic_pmy_4gd_sbb\"  >" + 
			"    <title> </title>" + 
			"    <prolog>" + 
			"        <critdates >" + 
			"            <created date=\""+ localDate +"\" />  " + 
			"        </critdates>" + 
			"    </prolog>" + 
			"    <body>" + 
			"        <p/>" + 
			"    </body>" + 
			"</topic>" + 
			"";
	
	//expected XML output for new input document
	String expectedNewXMLWithCreated = "<?xml version=\"1.0\" encoding=\"utf-8\"?><topic id=\"topic_pmy_4gd_sbb\"  >" + 
			"    <title> </title>" + 
			"    <prolog>" + 
			"        <author type=\"creator\" >"+AuthorTestCase.AUTHOR_NAME+"</author> " + 
			"        <critdates >" + 
			"            <created date=\""+ localDate +"\" />  " + 
			"        </critdates>" + 
			"    </prolog>" + 
			"    <body>" + 
			"        <p/>" + 
			"    </body>" + 
			"</topic>" + 
			"";

	//expected XML output for old input document
	String expectedOldXmlWithCreated = "<?xml version=\"1.0\" encoding=\"utf-8\"?><topic id=\"topic_pmy_4gd_sbb\"  >" + 
			"    <title> </title>" + 
			"    <prolog>" + 
			"        <author type=\"contributor\" >"+AuthorTestCase.AUTHOR_NAME+"</author> " + 
			"        <critdates>" + 
			"            <created date=\""+ localDate +"\" />  " + 
			"           <!--"+AuthorTestCase.AUTHOR_NAME+"-->" + 
			"            <revised modified=\""+localDate+"\" />" + 
			"        </critdates>" + 
			"    </prolog>" + 
			"    <body>" + 
			"        <p/>" + 
			"    </body>" + 
			"</topic>" + 
			" ";
	
	// test when document is new
	testInAuthorMode(xmlWithCreated, true,  expectedNewXMLWithCreated);
	
	//test when document isn't new
	testInAuthorMode(xmlWithCreated, false ,  expectedOldXmlWithCreated);
	
	
	
	//
	//Test document with critdates element that contains revised .
	//
	//input document
	String xmlWithRevised ="<?xml version=\"1.0\" encoding=\"utf-8\"?>"+
	    "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">"+
	    "<topic id=\"topic_pmy_4gd_sbb\"  >" + 
			"    <title> </title>" + 
			"    <prolog>" + 
			"        <critdates >" + 
			"           <!--"+AuthorTestCase.AUTHOR_NAME+"-->" + 
			"            <revised modified=\""+ localDate +"\" />  " + 
			"        </critdates>" + 
			"    </prolog>" + 
			"    <body>" + 
			"        <p/>" + 
			"    </body>" + 
			"</topic>" + 
			"";
	
	//expected XML output for new input document
	String expectedNewXMLWithRevised = "<?xml version=\"1.0\" encoding=\"utf-8\"?><topic id=\"topic_pmy_4gd_sbb\"  >" + 
			"    <title> </title>" + 
			"    <prolog>" + 
			"        <author type=\"creator\" >"+AuthorTestCase.AUTHOR_NAME+"</author> " + 
			"        <critdates >" + 
			"            <created date=\""+ localDate +"\" />  " + 
			"           <!--"+AuthorTestCase.AUTHOR_NAME+"-->" + 
			"            <revised modified=\""+ localDate +"\" />  " + 
			"        </critdates>" + 
			"    </prolog>" + 
			"    <body>" + 
			"        <p/>" + 
			"    </body>" + 
			"</topic>" + 
			"";

	//expected XML output for old input document
	String expectedOldXmlWithRevised = "<?xml version=\"1.0\" encoding=\"utf-8\"?><topic id=\"topic_pmy_4gd_sbb\"  >" + 
			"    <title> </title>" + 
			"    <prolog>" + 
			"        <author type=\"contributor\" >"+AuthorTestCase.AUTHOR_NAME+"</author> " + 
			"        <critdates>" + 
			"           <!--"+AuthorTestCase.AUTHOR_NAME+"-->" + 
			"            <revised modified=\""+ localDate +"\" />  " + 
			"        </critdates>" + 
			"    </prolog>" + 
			"    <body>" + 
			"        <p/>" + 
			"    </body>" + 
			"</topic>" + 
			" ";
	
	// test when document is new
	testInAuthorMode(xmlWithRevised, true,  expectedNewXMLWithRevised);
	
	//test when document isn't new
	testInAuthorMode(xmlWithRevised, false ,  expectedOldXmlWithRevised);
	
	
	//
	//Test document with critdates element that contains revised with another modified date.
	//
	//input document
	String xmlWithOldRevised ="<?xml version=\"1.0\" encoding=\"utf-8\"?>"+
	    "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">"+
	    "<topic id=\"topic_pmy_4gd_sbb\"  >" + 
			"    <title> </title>" + 
			"    <prolog>" + 
			"		<author type=\"contributor\" >"+AuthorTestCase.AUTHOR_NAME+"</author> "+
			"        <critdates >" + 
			"           <!--"+AuthorTestCase.AUTHOR_NAME+"-->" + 
			"            <revised modified=\"10.10.2017\" />  " + 
			"        </critdates>" + 
			"    </prolog>" + 
			"    <body>" + 
			"        <p/>" + 
			"    </body>" + 
			"</topic>" + 
			"";
	

	//expected XML output for old input document
	String expectedWithOldRevised = "<?xml version=\"1.0\" encoding=\"utf-8\"?><topic id=\"topic_pmy_4gd_sbb\"  >" + 
			"    <title> </title>" + 
			"    <prolog>" + 
			"        <author type=\"contributor\" >"+AuthorTestCase.AUTHOR_NAME+"</author> " + 
			"        <critdates>" + 
			"           <!--"+AuthorTestCase.AUTHOR_NAME+"-->" + 
			"            <revised modified=\"10.10.2017\" />  " + 
			"           <!--"+AuthorTestCase.AUTHOR_NAME+"-->" + 
			"            <revised modified=\""+ localDate +"\" />  " + 
			"        </critdates>" + 
			"    </prolog>" + 
			"    <body>" + 
			"        <p/>" + 
			"    </body>" + 
			"</topic>" + 
			" ";
	
	// test when document isn't new
	testInAuthorMode(xmlWithOldRevised, false,  expectedWithOldRevised);
	
	
	
	//
	//Test document with critdates element that contains revised with another contributor comment.
	//
	//input document
	String xmlWithContributorComment ="<?xml version=\"1.0\" encoding=\"utf-8\"?>"+
	    "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">"+
	    "<topic id=\"topic_pmy_4gd_sbb\"  >" + 
			"    <title> </title>" + 
			"    <prolog>" + 
			"				 <author type=\"contributor\" >anotherAuthor</author> "+
			"        <critdates >" + 
			"           <!--anotherAuthor-->" + 
			"            <revised modified=\""+ localDate +"\" />  " + 
			"        </critdates>" + 
			"    </prolog>" + 
			"    <body>" + 
			"        <p/>" + 
			"    </body>" + 
			"</topic>" + 
			"";
	

	//expected XML output for old input document
	String expectedWithContributorComment= "<?xml version=\"1.0\" encoding=\"utf-8\"?><topic id=\"topic_pmy_4gd_sbb\"  >" + 
			"    <title> </title>" + 
			"    <prolog>" + 
			"        <author type=\"contributor\" >anotherAuthor</author> "+
			"        <author type=\"contributor\" >"+AuthorTestCase.AUTHOR_NAME+"</author> " + 
			"        <critdates>" + 
			"           <!--anotherAuthor-->" + 
			"            <revised modified=\""+ localDate +"\" />  " + 
			"           <!--"+AuthorTestCase.AUTHOR_NAME+"-->" + 
			"            <revised modified=\""+ localDate +"\" />  " + 
			"        </critdates>" + 
			"    </prolog>" + 
			"    <body>" + 
			"        <p/>" + 
			"    </body>" + 
			"</topic>" + 
			" ";
	
	// test when document isn't new
	testInAuthorMode(xmlWithContributorComment, false,  expectedWithContributorComment);
	
	}
}
