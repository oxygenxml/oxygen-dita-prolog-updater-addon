package com.oxygenxml.prolog.updater;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.text.BadLocationException;

import org.xml.sax.SAXException;

public class DitaUpdaterWithoutPrologTest {

	/**
	 * Test if the prolog is correct added after save operation.
	 * @throws IOException
	 * @throws SAXException
	 * @throws BadLocationException
	 */
	public void testAddProlog() throws IOException, SAXException, BadLocationException {

		//Get the local date
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();
		
		final String localDate = dateFormat.format(date);
		
		//
		//Test document without prolog element.
		//
		//input document
		String xmlWithoutProlog ="<?xml version=\"1.0\" encoding=\"utf-8\"?><topic xmlns:ditaarch=\"http://dita.oasis-open.org/architecture/2005/\" id=\"topic_mfz_vwd_qbb\" ditaarch:DITAArchVersion=\"1.3\" domains=\"(topic abbrev-d)                            a(props deliveryTarget)                            (topic equation-d)                            (topic hazard-d)                            (topic hi-d)                            (topic indexing-d)                            (topic markup-d)                            (topic mathml-d)                            (topic pr-d)                            (topic relmgmt-d)                            (topic sw-d)                            (topic svg-d)                            (topic ui-d)                            (topic ut-d)                            (topic markup-d xml-d)   \" class=\"- topic/topic \">" + 
				"    <title class=\"- topic/title \">" + 
				"    </title>" + 
				"    <body class=\"- topic/body \">" + 
				"        <p class=\"- topic/p \"/>" + 
				"    </body>" + 
				"</topic>";
		
		//expected XML output for new input document
		String expectedNewXML = "<?xml version=\"1.0\" encoding=\"utf-8\"?><topic xmlns:ditaarch=\"http://dita.oasis-open.org/architecture/2005/\" id=\"topic_mfz_vwd_qbb\" ditaarch:DITAArchVersion=\"1.3\" domains=\"(topic abbrev-d)                            a(props deliveryTarget)                            (topic equation-d)                            (topic hazard-d)                            (topic hi-d)                            (topic indexing-d)                            (topic markup-d)                            (topic mathml-d)                            (topic pr-d)                            (topic relmgmt-d)                            (topic sw-d)                            (topic svg-d)                            (topic ui-d)                            (topic ut-d)                            (topic markup-d xml-d)   \" class=\"- topic/topic \">" + 
				"    <title class=\"- topic/title \"> </title>" + 
				"    <prolog>" + 
				"        <author type=\"creator\">"+DitaUpdateTestUtil.AUTHOR_NAME+"</author> " + 
				"        <critdates>" + 
				"            <created date=\""+ localDate +"\" />  " + 
				"        </critdates>" + 
				"    </prolog>" + 
				"    <body class=\"- topic/body \">" + 
				"        <p class=\"- topic/p \"/>" + 
				"    </body>" + 
				"</topic>" + 
				"";

		//expected XML output for old input document
		String expectedOldXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><topic xmlns:ditaarch=\"http://dita.oasis-open.org/architecture/2005/\" id=\"topic_mfz_vwd_qbb\" ditaarch:DITAArchVersion=\"1.3\" domains=\"(topic abbrev-d)                            a(props deliveryTarget)                            (topic equation-d)                            (topic hazard-d)                            (topic hi-d)                            (topic indexing-d)                            (topic markup-d)                            (topic mathml-d)                            (topic pr-d)                            (topic relmgmt-d)                            (topic sw-d)                            (topic svg-d)                            (topic ui-d)                            (topic ut-d)                            (topic markup-d xml-d)   \" class=\"- topic/topic \">" + 
				"    <title class=\"- topic/title \"> </title>" + 
				"    <prolog>" + 
				"        <author type=\"contributor\" >"+DitaUpdateTestUtil.AUTHOR_NAME+"</author>" + 
				"        <critdates >" + 
				"            <!--"+DitaUpdateTestUtil.AUTHOR_NAME+"-->" + 
				"            <revised modified=\""+localDate+"\" />" + 
				"        </critdates>" + 
				"    </prolog>" + 
				"    <body class=\"- topic/body \">" + 
				"        <p class=\"- topic/p \"/>" + 
				"    </body>" + 
				"</topic>" + 
				"";
		
		// test when document is new
		DitaUpdateTestUtil.testAuthorMode(xmlWithoutProlog, true, expectedNewXML);
		
		//test when document isn't new
		DitaUpdateTestUtil.testAuthorMode(xmlWithoutProlog, false, expectedOldXml);
		
	}

}
