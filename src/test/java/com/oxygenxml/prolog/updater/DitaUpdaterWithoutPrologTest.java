package com.oxygenxml.prolog.updater;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.text.BadLocationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

public class DitaUpdaterWithoutPrologTest extends AuthorTestCase{

	/**
	 * Test if the prolog is correct added after save operation.
	 * @throws IOException
	 * @throws SAXException
	 * @throws BadLocationException
	 * @throws ParserConfigurationException 
	 * @throws TransformerException 
	 */
	public void testAddProlog() throws IOException, SAXException, BadLocationException, ParserConfigurationException, TransformerException {

		//Get the local date
		DateFormat dateFormat = new SimpleDateFormat(getDateFormat());
		Date date = new Date();
		
		final String localDate = dateFormat.format(date);
		
		//
		//Test document without prolog element.
		//
		//input document
		String xmlWithoutProlog =
		  "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + 
		    "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">" + 
		    "<topic id=\"topic_mfz_vwd_qbb\" >" + 
				"  <title>" + 
				"  </title>" + 
				"  <body>" + 
				"    <p/>" + 
				"  </body>" + 
				"</topic>";
		
		//expected XML output for new input document
		String expectedNewXML = "<?xml version=\"1.0\" encoding=\"utf-8\"?><topic id=\"topic_mfz_vwd_qbb\">" + 
				"  <title> </title>" + 
				"  <prolog>" + 
				"    <author type=\"creator\">"+AuthorTestCase.AUTHOR_NAME+"</author> " + 
				"    <critdates>" + 
				"      <created date=\""+ localDate +"\" /> " + 
				"    </critdates>" + 
				"  </prolog>" + 
				"  <body>" + 
				"    <p/>" + 
				"  </body>" + 
				"</topic>" + 
				"";

		//expected XML output for old input document
		String expectedOldXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><topic id=\"topic_mfz_vwd_qbb\" >" + 
				"  <title > </title>" + 
				"  <prolog>" + 
				"    <author type=\"contributor\" >"+AuthorTestCase.AUTHOR_NAME+"</author>" + 
				"    <critdates >" + 
				"      <!--"+AuthorTestCase.AUTHOR_NAME+"-->" + 
				"      <revised modified=\""+localDate+"\" />" + 
				"    </critdates>" + 
				"  </prolog>" + 
				"  <body >" + 
				"    <p/>" + 
				"  </body>" + 
				"</topic>" + 
				"";
		
		// test when document is new 
		//testInAuthorMode(xmlWithoutProlog, true, expectedNewXML);
		
		//test when document isn't new
		//testInAuthorMode(xmlWithoutProlog, false, expectedOldXml);
		
		
		//
		//Test document with prolog element that doesn't has child.
		//
		//input document with author 
		String xmlWithEmptyProlog ="<?xml version=\"1.0\" encoding=\"utf-8\"?>"+
		    "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">" + 
		    "<topic id=\"topic_pmy_4gd_sbb\" >" + 
				"  <title > </title>" + 
				"  <prolog >" + 
				"  </prolog>" + 
				"  <body >" + 
				"    <p/>" + 
				"  </body>" + 
				"</topic>" + 
				"";
		
		//expected XML output for new input document
		String expectedNewXMLWithEmptyProlog = "<?xml version=\"1.0\" encoding=\"utf-8\"?><topic id=\"topic_pmy_4gd_sbb\" >" + 
				"  <title> </title>" + 
				"  <prolog>" + 
				"    <author type=\"creator\" >"+AuthorTestCase.AUTHOR_NAME+"</author> " + 
				"    <critdates >" + 
				"      <created date=\""+ localDate +"\" /> " + 
				"    </critdates>" + 
				"  </prolog>" + 
				"  <body>" + 
				"    <p/>" + 
				"  </body>" + 
				"</topic>" + 
				"";

		//expected XML output for old input document
		String expectedOldXmlWithEmptyProlog = "<?xml version=\"1.0\" encoding=\"utf-8\"?><topic id=\"topic_pmy_4gd_sbb\" >" + 
				"  <title> </title>" + 
				"  <prolog>" + 
				"    <author type=\"contributor\" >"+AuthorTestCase.AUTHOR_NAME+"</author>" + 
				"    <critdates>" + 
				"      <!--"+AuthorTestCase.AUTHOR_NAME+"-->" + 
				"      <revised modified=\""+localDate+"\" />" + 
				"    </critdates>" + 
				"  </prolog>" + 
				"  <body>" + 
				"    <p/>" + 
				"  </body>" + 
				"</topic>" + 
				" ";
		
		// test when document is new
		testInAuthorMode(xmlWithEmptyProlog, true, expectedNewXMLWithEmptyProlog);
		
		//test when document isn't new
		testInAuthorMode(xmlWithEmptyProlog, false , expectedOldXmlWithEmptyProlog);
	}

}
