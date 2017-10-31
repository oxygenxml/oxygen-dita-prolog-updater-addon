package com.oxygenxml.prolog.updater;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.text.BadLocationException;

import org.xml.sax.SAXException;

import junit.framework.TestCase;

public class DitaUpdaterCritdatesTest extends TestCase {

	/**
	 * Test if the critdates tag is correct modified after save operation.
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
	//Test document with critdates element that contains created .
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
	DitaUpdateTestUtil.testInAuthorMode(xmlWithCreated, true,  expectedNewXMLWithCreated);
	
	//test when document isn't new
	DitaUpdateTestUtil.testInAuthorMode(xmlWithCreated, false ,  expectedOldXmlWithCreated);
	
	
	
	//
	//Test document with critdates element that contains revised .
	//
	//input document
	String xmlWithRevised ="<?xml version=\"1.0\" encoding=\"utf-8\"?><topic xmlns:ditaarch=\"http://dita.oasis-open.org/architecture/2005/\" id=\"topic_pmy_4gd_sbb\" ditaarch:DITAArchVersion=\"1.3\" domains=\"(topic abbrev-d)                            a(props deliveryTarget)                            (topic equation-d)                            (topic hazard-d)                            (topic hi-d)                            (topic indexing-d)                            (topic markup-d)                            (topic mathml-d)                            (topic pr-d)                            (topic relmgmt-d)                            (topic sw-d)                            (topic svg-d)                            (topic ui-d)                            (topic ut-d)                            (topic markup-d xml-d)   \" class=\"- topic/topic \">" + 
			"    <title class=\"- topic/title \"> </title>" + 
			"    <prolog class=\"- topic/prolog \">" + 
			"        <critdates >" + 
			"           <!--"+DitaUpdateTestUtil.AUTHOR_NAME+"-->" + 
			"            <revised modified=\""+ localDate +"\" />  " + 
			"        </critdates>" + 
			"    </prolog>" + 
			"    <body class=\"- topic/body \">" + 
			"        <p class=\"- topic/p \"/>" + 
			"    </body>" + 
			"</topic>" + 
			"";
	
	//expected XML output for new input document
	String expectedNewXMLWithRevised = "<?xml version=\"1.0\" encoding=\"utf-8\"?><topic xmlns:ditaarch=\"http://dita.oasis-open.org/architecture/2005/\" id=\"topic_pmy_4gd_sbb\" ditaarch:DITAArchVersion=\"1.3\" domains=\"(topic abbrev-d)                            a(props deliveryTarget)                            (topic equation-d)                            (topic hazard-d)                            (topic hi-d)                            (topic indexing-d)                            (topic markup-d)                            (topic mathml-d)                            (topic pr-d)                            (topic relmgmt-d)                            (topic sw-d)                            (topic svg-d)                            (topic ui-d)                            (topic ut-d)                            (topic markup-d xml-d)   \" class=\"- topic/topic \">" + 
			"    <title class=\"- topic/title \"> </title>" + 
			"    <prolog class=\"- topic/prolog \">" + 
			"        <author type=\"creator\" >"+DitaUpdateTestUtil.AUTHOR_NAME+"</author> " + 
			"        <critdates >" + 
			"            <created date=\""+ localDate +"\" />  " + 
			"           <!--"+DitaUpdateTestUtil.AUTHOR_NAME+"-->" + 
			"            <revised modified=\""+ localDate +"\" />  " + 
			"        </critdates>" + 
			"    </prolog>" + 
			"    <body class=\"- topic/body \">" + 
			"        <p class=\"- topic/p \"/>" + 
			"    </body>" + 
			"</topic>" + 
			"";

	//expected XML output for old input document
	String expectedOldXmlWithRevised = "<?xml version=\"1.0\" encoding=\"utf-8\"?><topic xmlns:ditaarch=\"http://dita.oasis-open.org/architecture/2005/\" id=\"topic_pmy_4gd_sbb\" ditaarch:DITAArchVersion=\"1.3\" domains=\"(topic abbrev-d)                            a(props deliveryTarget)                            (topic equation-d)                            (topic hazard-d)                            (topic hi-d)                            (topic indexing-d)                            (topic markup-d)                            (topic mathml-d)                            (topic pr-d)                            (topic relmgmt-d)                            (topic sw-d)                            (topic svg-d)                            (topic ui-d)                            (topic ut-d)                            (topic markup-d xml-d)   \" class=\"- topic/topic \">" + 
			"    <title class=\"- topic/title \"> </title>" + 
			"    <prolog class=\"- topic/prolog \">" + 
			"        <author type=\"contributor\" >"+DitaUpdateTestUtil.AUTHOR_NAME+"</author> " + 
			"        <critdates>" + 
			"           <!--"+DitaUpdateTestUtil.AUTHOR_NAME+"-->" + 
			"            <revised modified=\""+ localDate +"\" />  " + 
			"        </critdates>" + 
			"    </prolog>" + 
			"    <body class=\"- topic/body \">" + 
			"        <p class=\"- topic/p \"/>" + 
			"    </body>" + 
			"</topic>" + 
			" ";
	
	// test when document is new
	DitaUpdateTestUtil.testInAuthorMode(xmlWithRevised, true,  expectedNewXMLWithRevised);
	
	//test when document isn't new
	DitaUpdateTestUtil.testInAuthorMode(xmlWithRevised, false ,  expectedOldXmlWithRevised);
	
	
	//
	//Test document with critdates element that contains revised with another modified date.
	//
	//input document
	String xmlWithOldRevised ="<?xml version=\"1.0\" encoding=\"utf-8\"?><topic xmlns:ditaarch=\"http://dita.oasis-open.org/architecture/2005/\" id=\"topic_pmy_4gd_sbb\" ditaarch:DITAArchVersion=\"1.3\" domains=\"(topic abbrev-d)                            a(props deliveryTarget)                            (topic equation-d)                            (topic hazard-d)                            (topic hi-d)                            (topic indexing-d)                            (topic markup-d)                            (topic mathml-d)                            (topic pr-d)                            (topic relmgmt-d)                            (topic sw-d)                            (topic svg-d)                            (topic ui-d)                            (topic ut-d)                            (topic markup-d xml-d)   \" class=\"- topic/topic \">" + 
			"    <title class=\"- topic/title \"> </title>" + 
			"    <prolog class=\"- topic/prolog \">" + 
			"		<author type=\"contributor\" >"+DitaUpdateTestUtil.AUTHOR_NAME+"</author> "+
			"        <critdates >" + 
			"           <!--"+DitaUpdateTestUtil.AUTHOR_NAME+"-->" + 
			"            <revised modified=\"10.10.2017\" />  " + 
			"        </critdates>" + 
			"    </prolog>" + 
			"    <body class=\"- topic/body \">" + 
			"        <p class=\"- topic/p \"/>" + 
			"    </body>" + 
			"</topic>" + 
			"";
	

	//expected XML output for old input document
	String expectedWithOldRevised = "<?xml version=\"1.0\" encoding=\"utf-8\"?><topic xmlns:ditaarch=\"http://dita.oasis-open.org/architecture/2005/\" id=\"topic_pmy_4gd_sbb\" ditaarch:DITAArchVersion=\"1.3\" domains=\"(topic abbrev-d)                            a(props deliveryTarget)                            (topic equation-d)                            (topic hazard-d)                            (topic hi-d)                            (topic indexing-d)                            (topic markup-d)                            (topic mathml-d)                            (topic pr-d)                            (topic relmgmt-d)                            (topic sw-d)                            (topic svg-d)                            (topic ui-d)                            (topic ut-d)                            (topic markup-d xml-d)   \" class=\"- topic/topic \">" + 
			"    <title class=\"- topic/title \"> </title>" + 
			"    <prolog class=\"- topic/prolog \">" + 
			"        <author type=\"contributor\" >"+DitaUpdateTestUtil.AUTHOR_NAME+"</author> " + 
			"        <critdates>" + 
			"           <!--"+DitaUpdateTestUtil.AUTHOR_NAME+"-->" + 
			"            <revised modified=\"10.10.2017\" />  " + 
			"           <!--"+DitaUpdateTestUtil.AUTHOR_NAME+"-->" + 
			"            <revised modified=\""+ localDate +"\" />  " + 
			"        </critdates>" + 
			"    </prolog>" + 
			"    <body class=\"- topic/body \">" + 
			"        <p class=\"- topic/p \"/>" + 
			"    </body>" + 
			"</topic>" + 
			" ";
	
	// test when document isn't new
	DitaUpdateTestUtil.testInAuthorMode(xmlWithOldRevised, false,  expectedWithOldRevised);
	
	
	
	//
	//Test document with critdates element that contains revised with another contributor comment.
	//
	//input document
	String xmlWithContributorComment ="<?xml version=\"1.0\" encoding=\"utf-8\"?><topic xmlns:ditaarch=\"http://dita.oasis-open.org/architecture/2005/\" id=\"topic_pmy_4gd_sbb\" ditaarch:DITAArchVersion=\"1.3\" domains=\"(topic abbrev-d)                            a(props deliveryTarget)                            (topic equation-d)                            (topic hazard-d)                            (topic hi-d)                            (topic indexing-d)                            (topic markup-d)                            (topic mathml-d)                            (topic pr-d)                            (topic relmgmt-d)                            (topic sw-d)                            (topic svg-d)                            (topic ui-d)                            (topic ut-d)                            (topic markup-d xml-d)   \" class=\"- topic/topic \">" + 
			"    <title class=\"- topic/title \"> </title>" + 
			"    <prolog class=\"- topic/prolog \">" + 
			"				 <author type=\"contributor\" >anotherAuthor</author> "+
			"        <critdates >" + 
			"           <!--anotherAuthor-->" + 
			"            <revised modified=\""+ localDate +"\" />  " + 
			"        </critdates>" + 
			"    </prolog>" + 
			"    <body class=\"- topic/body \">" + 
			"        <p class=\"- topic/p \"/>" + 
			"    </body>" + 
			"</topic>" + 
			"";
	

	//expected XML output for old input document
	String expectedWithContributorComment= "<?xml version=\"1.0\" encoding=\"utf-8\"?><topic xmlns:ditaarch=\"http://dita.oasis-open.org/architecture/2005/\" id=\"topic_pmy_4gd_sbb\" ditaarch:DITAArchVersion=\"1.3\" domains=\"(topic abbrev-d)                            a(props deliveryTarget)                            (topic equation-d)                            (topic hazard-d)                            (topic hi-d)                            (topic indexing-d)                            (topic markup-d)                            (topic mathml-d)                            (topic pr-d)                            (topic relmgmt-d)                            (topic sw-d)                            (topic svg-d)                            (topic ui-d)                            (topic ut-d)                            (topic markup-d xml-d)   \" class=\"- topic/topic \">" + 
			"    <title class=\"- topic/title \"> </title>" + 
			"    <prolog class=\"- topic/prolog \">" + 
			"        <author type=\"contributor\" >anotherAuthor</author> "+
			"        <author type=\"contributor\" >"+DitaUpdateTestUtil.AUTHOR_NAME+"</author> " + 
			"        <critdates>" + 
			"           <!--anotherAuthor-->" + 
			"            <revised modified=\""+ localDate +"\" />  " + 
			"           <!--"+DitaUpdateTestUtil.AUTHOR_NAME+"-->" + 
			"            <revised modified=\""+ localDate +"\" />  " + 
			"        </critdates>" + 
			"    </prolog>" + 
			"    <body class=\"- topic/body \">" + 
			"        <p class=\"- topic/p \"/>" + 
			"    </body>" + 
			"</topic>" + 
			" ";
	
	// test when document isn't new
	DitaUpdateTestUtil.testInAuthorMode(xmlWithContributorComment, false,  expectedWithContributorComment);
	
	
	}
}
