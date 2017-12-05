package com.oxygenxml.prolog.updater;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.text.BadLocationException;

import org.xml.sax.SAXException;

import junit.framework.TestCase;

public class DitaTopicUpdaterAuthorTest extends TestCase{

	/**
	 * Test if the author tag is correct modified after save operation in DITA Topic.
	 * @throws IOException
	 * @throws SAXException
	 * @throws BadLocationException
	 */
	public void testUpdateAuthorInProlog() throws IOException, SAXException, BadLocationException {

		//Get the local date
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();
		
		final String localDate = dateFormat.format(date);
		
		
		//
		//Test document with prolog element that contains creator author.
		//
		//input document with author 
		String xmlWithCreator ="<?xml version=\"1.0\" encoding=\"utf-8\"?>" + 
		    "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">"+
		    "<topic id=\"topic_pmy_4gd_sbb\">" + 
				"    <title > </title>" + 
				"    <prolog >" + 
				"        <author type=\"creator\" >"+DitaUpdateTestUtil.AUTHOR_NAME+"</author> " + 
				"    </prolog>" + 
				"    <body >" + 
				"        <p/>" + 
				"    </body>" + 
				"</topic>" + 
				"";
		
		//expected XML output for new input document
		String expectedNewXMLWithCreator = "<?xml version=\"1.0\" encoding=\"utf-8\"?><topic id=\"topic_pmy_4gd_sbb\">" + 
				"    <title > </title>" + 
				"    <prolog >" + 
				"        <author type=\"creator\" >"+DitaUpdateTestUtil.AUTHOR_NAME+"</author> " + 
				"        <critdates >" + 
				"            <created date=\""+ localDate +"\" />  " + 
				"        </critdates>" + 
				"    </prolog>" + 
				"    <body >" + 
				"        <p/>" + 
				"    </body>" + 
				"</topic>" + 
				"";

		//expected XML output for old input document
		String expectedOldXmlWithCreator = "<?xml version=\"1.0\" encoding=\"utf-8\"?><topic id=\"topic_pmy_4gd_sbb\">" + 
				"    <title > </title>" + 
				"    <prolog >" + 
				"        <author type=\"creator\" >"+DitaUpdateTestUtil.AUTHOR_NAME+"</author>" + 
				"        <author type=\"contributor\" >"+DitaUpdateTestUtil.AUTHOR_NAME+"</author>" + 
				"        <critdates>" + 
				"           <!--"+DitaUpdateTestUtil.AUTHOR_NAME+"-->" + 
				"            <revised modified=\""+localDate+"\" />" + 
				"        </critdates>" + 
				"    </prolog>" + 
				"    <body >" + 
				"        <p/>" + 
				"    </body>" + 
				"</topic>" + 
				" ";
		
		// test when document is new
		DitaUpdateTestUtil.testInAuthorMode(xmlWithCreator, true,  expectedNewXMLWithCreator);
		
		//test when document isn't new
		DitaUpdateTestUtil.testInAuthorMode(xmlWithCreator, false ,  expectedOldXmlWithCreator);
		
		
		
		//
		//Test document with prolog element that contains contributor author.
		//
		//input document with author 
		String xmlWithContributor ="<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
		    "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">"+
		    "<topic id=\"topic_pmy_4gd_sbb\">" + 
				"    <title > </title>" + 
				"    <prolog >" + 
				"        <author type=\"contributor\" >"+DitaUpdateTestUtil.AUTHOR_NAME+"</author> " + 
				"    </prolog>" + 
				"    <body >" + 
				"        <p/>" + 
				"    </body>" + 
				"</topic>" + 
				"";
		
		//expected XML output for new input document
		String expectedNewXMLWithContributor = "<?xml version=\"1.0\" encoding=\"utf-8\"?><topic id=\"topic_pmy_4gd_sbb\">" + 
				"    <title > </title>" + 
				"    <prolog >" + 
				"        <author type=\"creator\" >"+DitaUpdateTestUtil.AUTHOR_NAME+"</author> " + 
				"        <author type=\"contributor\" >"+DitaUpdateTestUtil.AUTHOR_NAME+"</author> " + 
				"        <critdates >" + 
				"            <created date=\""+ localDate +"\" />  " + 
				"        </critdates>" + 
				"    </prolog>" + 
				"    <body >" + 
				"        <p/>" + 
				"    </body>" + 
				"</topic>" + 
				"";

		//expected XML output for old input document
		String expectedOldXmlWithContributor = "<?xml version=\"1.0\" encoding=\"utf-8\"?><topic id=\"topic_pmy_4gd_sbb\">" + 
				"    <title > </title>" + 
				"    <prolog >" + 
				"        <author type=\"contributor\" >"+DitaUpdateTestUtil.AUTHOR_NAME+"</author> " + 
				"        <critdates>" + 
				"           <!--"+DitaUpdateTestUtil.AUTHOR_NAME+"-->" + 
				"            <revised modified=\""+localDate+"\" />" + 
				"        </critdates>" + 
				"    </prolog>" + 
				"    <body >" + 
				"        <p/>" + 
				"    </body>" + 
				"</topic>" + 
				" ";
		
		// test when document is new
		DitaUpdateTestUtil.testInAuthorMode(xmlWithContributor, true,  expectedNewXMLWithContributor);
		
		//test when document isn't new
		DitaUpdateTestUtil.testInAuthorMode(xmlWithContributor, false ,  expectedOldXmlWithContributor);
		
	
	
	//
	//Test document with prolog element that contains a different contributor.
	//
	//input document
	String xmlWithDifferentContributor ="<?xml version=\"1.0\" encoding=\"utf-8\"?>"+
	    "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">"+
	    "<topic id=\"topic_pmy_4gd_sbb\">" + 
			"    <title > </title>" + 
			"    <prolog >" + 
			"        <author type=\"contributor\" >nume</author> " + 
			"        <critdates>" + 
			"           <!--nume-->" + 
			"            <revised modified=\""+localDate+"\" />" + 
			"        </critdates>" + 
			"    </prolog>" + 
			"    <body >" + 
			"        <p/>" + 
			"    </body>" + 
			"</topic>" + 
			"";
	

	//expected XML output for old input document
	String expectedOldXmlWithDifferentContributor = "<?xml version=\"1.0\" encoding=\"utf-8\"?><topic id=\"topic_pmy_4gd_sbb\">" + 
			"    <title > </title>" + 
			"    <prolog >" + 
			"        <author type=\"contributor\" >nume</author> " + 
			"        <author type=\"contributor\" >"+DitaUpdateTestUtil.AUTHOR_NAME+"</author> " + 
			"        <critdates>" + 
			"           <!--nume-->" + 
			"            <revised modified=\""+localDate+"\" />" + 
			"           <!--"+DitaUpdateTestUtil.AUTHOR_NAME+"-->" + 
			"            <revised modified=\""+localDate+"\" />" + 
			"        </critdates>" + 
			"    </prolog>" + 
			"    <body >" + 
			"        <p/>" + 
			"    </body>" + 
			"</topic>" + 
			" ";
	
	// test when document isn't new
	DitaUpdateTestUtil.testInAuthorMode(xmlWithDifferentContributor, false,  expectedOldXmlWithDifferentContributor);
	
	}

	/**
   * <p><b>Description:</b> Test if the content isn't modified after save.</p>
   * @throws Exception
   */
  public void testSaveTwice() throws Exception {
    //Get the local date
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    Date date = new Date();
    
    final String localDate = dateFormat.format(date);
    
  //expected XML output for new input document
    String fragment = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"+
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">"+
        "<topic id=\"topic_pmy_4gd_sb\">" + 
        "    <title> </title>" + 
        "    <prolog>" + 
        "        <author type=\"creator\" >"+DitaUpdateTestUtil.AUTHOR_NAME+"</author> " + 
        "        <critdates >" + 
        "            <created date=\""+ localDate +"\" />  " + 
        "        </critdates>" + 
        "    </prolog>" + 
        "    <body >" + 
        "        <p/>" + 
        "    </body>" + 
        "</topic>" + 
        "";
    
    
    
    
    // test when document is new
    DitaUpdateTestUtil.testInAuthorMode(fragment, true,  fragment);
    
  }

	
}
