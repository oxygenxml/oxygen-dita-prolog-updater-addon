package com.oxygenxml.prolog.updater;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.text.BadLocationException;

import org.xml.sax.SAXException;

import junit.framework.TestCase;

public class DitaUpdaterAuthorModeTest extends TestCase {

	/**
	 * Test if the document is correct modified after save operation.
	 * @throws IOException
	 * @throws SAXException
	 * @throws BadLocationException
	 */
	public void testUpdateProlog() throws IOException, SAXException, BadLocationException {

		//Get the local date
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();
		
		final String localDate = dateFormat.format(date);
		
		
	//
	//Test document with prolog element that contains created .
	//
	//input document
	String xmlWithCreated ="<?xml version=\"1.0\" encoding=\"utf-8\"?><topic xmlns:ditaarch=\"http://dita.oasis-open.org/architecture/2005/\" id=\"topic_pmy_4gd_sbb\" ditaarch:DITAArchVersion=\"1.3\" domains=\"(topic abbrev-d)                            a(props deliveryTarget)                            (topic equation-d)                            (topic hazard-d)                            (topic hi-d)                            (topic indexing-d)                            (topic markup-d)                            (topic mathml-d)                            (topic pr-d)                            (topic relmgmt-d)                            (topic sw-d)                            (topic svg-d)                            (topic ui-d)                            (topic ut-d)                            (topic markup-d xml-d)   \" class=\"- topic/topic \">" + 
			"    <title class=\"- topic/title \"> </title>" + 
			"    <prolog class=\"- topic/prolog \">" + 
			"        <critdates >" + 
			"            <created date=\""+ localDate +"\" />  " + 
			"        </critdates>" + 
			"    </prolog>" + 
			"    <body class=\"- topic/body \">" + 
			"        <p class=\"- topic/p \"/>" + 
			"    </body>" + 
			"</topic>" + 
			"";
	
	//expected XML output for new input document
	String expectedNewXMLWithCreated = "<?xml version=\"1.0\" encoding=\"utf-8\"?><topic xmlns:ditaarch=\"http://dita.oasis-open.org/architecture/2005/\" id=\"topic_pmy_4gd_sbb\" ditaarch:DITAArchVersion=\"1.3\" domains=\"(topic abbrev-d)                            a(props deliveryTarget)                            (topic equation-d)                            (topic hazard-d)                            (topic hi-d)                            (topic indexing-d)                            (topic markup-d)                            (topic mathml-d)                            (topic pr-d)                            (topic relmgmt-d)                            (topic sw-d)                            (topic svg-d)                            (topic ui-d)                            (topic ut-d)                            (topic markup-d xml-d)   \" class=\"- topic/topic \">" + 
			"    <title class=\"- topic/title \"> </title>" + 
			"    <prolog class=\"- topic/prolog \">" + 
			"        <author type=\"creator\" >"+DitaUpdateTestUtil.AUTHOR_NAME+"</author> " + 
			"        <critdates >" + 
			"            <created date=\""+ localDate +"\" />  " + 
			"        </critdates>" + 
			"    </prolog>" + 
			"    <body class=\"- topic/body \">" + 
			"        <p class=\"- topic/p \"/>" + 
			"    </body>" + 
			"</topic>" + 
			"";

	//expected XML output for old input document
	String expectedOldXmlWithCreated = "<?xml version=\"1.0\" encoding=\"utf-8\"?><topic xmlns:ditaarch=\"http://dita.oasis-open.org/architecture/2005/\" id=\"topic_pmy_4gd_sbb\" ditaarch:DITAArchVersion=\"1.3\" domains=\"(topic abbrev-d)                            a(props deliveryTarget)                            (topic equation-d)                            (topic hazard-d)                            (topic hi-d)                            (topic indexing-d)                            (topic markup-d)                            (topic mathml-d)                            (topic pr-d)                            (topic relmgmt-d)                            (topic sw-d)                            (topic svg-d)                            (topic ui-d)                            (topic ut-d)                            (topic markup-d xml-d)   \" class=\"- topic/topic \">" + 
			"    <title class=\"- topic/title \"> </title>" + 
			"    <prolog class=\"- topic/prolog \">" + 
			"        <author type=\"contributor\" >"+DitaUpdateTestUtil.AUTHOR_NAME+"</author> " + 
			"        <critdates>" + 
			"            <created date=\""+ localDate +"\" />  " + 
			"           <!--"+DitaUpdateTestUtil.AUTHOR_NAME+"-->" + 
			"            <revised modified=\""+localDate+"\" />" + 
			"        </critdates>" + 
			"    </prolog>" + 
			"    <body class=\"- topic/body \">" + 
			"        <p class=\"- topic/p \"/>" + 
			"    </body>" + 
			"</topic>" + 
			" ";
	
	// test when document is new
	DitaUpdateTestUtil.testAuthorMode(xmlWithCreated, true,  expectedNewXMLWithCreated);
	
	//test when document isn't new
	DitaUpdateTestUtil.testAuthorMode(xmlWithCreated, false ,  expectedOldXmlWithCreated);
	
	}
}
