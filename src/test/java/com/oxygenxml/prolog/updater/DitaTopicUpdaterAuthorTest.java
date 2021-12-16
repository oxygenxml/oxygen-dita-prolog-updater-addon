package com.oxygenxml.prolog.updater;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.text.BadLocationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

public class DitaTopicUpdaterAuthorTest extends AuthorTestCase{

	/**
	 * Test if the author tag is correct modified after save operation in DITA Topic.
	 * @throws IOException
	 * @throws SAXException
	 * @throws BadLocationException
	 * @throws ParserConfigurationException 
	 * @throws TransformerException 
	 */
	public void testUpdateAuthorInProlog() throws IOException, SAXException, BadLocationException, ParserConfigurationException, TransformerException {

		//Get the local date
		DateFormat dateFormat = new SimpleDateFormat(getDateFormat());
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
				"        <author type=\"creator\" >"+AuthorTestCase.AUTHOR_NAME+"</author> " + 
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
				"        <author type=\"creator\" >"+AuthorTestCase.AUTHOR_NAME+"</author> " + 
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
				"        <author type=\"creator\" >"+AuthorTestCase.AUTHOR_NAME+"</author>" + 
				"        <critdates>" + 
				"           <!--"+AuthorTestCase.AUTHOR_NAME+"-->" + 
				"            <revised modified=\""+localDate+"\" />" + 
				"        </critdates>" + 
				"    </prolog>" + 
				"    <body >" + 
				"        <p/>" + 
				"    </body>" + 
				"</topic>" + 
				" ";
		
		// test when document is new
		testInAuthorMode(xmlWithCreator, true,  expectedNewXMLWithCreator);
		
		//test when document isn't new
		testInAuthorMode(xmlWithCreator, false ,  expectedOldXmlWithCreator);
		
		
		
		//
		//Test document with prolog element that contains contributor author.
		//
		//input document with author 
		String xmlWithContributor ="<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
		    "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">"+
		    "<topic id=\"topic_pmy_4gd_sbb\">" + 
				"    <title > </title>" + 
				"    <prolog >" + 
				"        <author type=\"contributor\" >"+AuthorTestCase.AUTHOR_NAME+"</author> " + 
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
				"        <author type=\"creator\" >"+AuthorTestCase.AUTHOR_NAME+"</author> " + 
				"        <author type=\"contributor\" >"+AuthorTestCase.AUTHOR_NAME+"</author> " + 
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
				"        <author type=\"contributor\" >"+AuthorTestCase.AUTHOR_NAME+"</author> " + 
				"        <critdates>" + 
				"           <!--"+AuthorTestCase.AUTHOR_NAME+"-->" + 
				"            <revised modified=\""+localDate+"\" />" + 
				"        </critdates>" + 
				"    </prolog>" + 
				"    <body >" + 
				"        <p/>" + 
				"    </body>" + 
				"</topic>" + 
				" ";
		
		// test when document is new
		testInAuthorMode(xmlWithContributor, true,  expectedNewXMLWithContributor);
		
		//test when document isn't new
		testInAuthorMode(xmlWithContributor, false ,  expectedOldXmlWithContributor);
		
	
	
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
			"        <author type=\"contributor\" >"+AuthorTestCase.AUTHOR_NAME+"</author> " + 
			"        <critdates>" + 
			"           <!--nume-->" + 
			"            <revised modified=\""+localDate+"\" />" + 
			"           <!--"+AuthorTestCase.AUTHOR_NAME+"-->" + 
			"            <revised modified=\""+localDate+"\" />" + 
			"        </critdates>" + 
			"    </prolog>" + 
			"    <body >" + 
			"        <p/>" + 
			"    </body>" + 
			"</topic>" + 
			" ";
	
	// test when document isn't new
	testInAuthorMode(xmlWithDifferentContributor, false,  expectedOldXmlWithDifferentContributor);
	
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
        "        <author type=\"creator\" >"+AuthorTestCase.AUTHOR_NAME+"</author> " + 
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
    testInAuthorMode(fragment, true,  fragment);
    
  }

  /**
   * <p><b>Description:</b>Test that resusable files are not updated.</p>
   * <p><b>Bug ID:</b> EXM-49360</p>
   *
   * @author cosmin_duna
   */
  public void testReusableFileAreNotUpdated() throws IOException, SAXException, BadLocationException, ParserConfigurationException, TransformerException {
  	String xml ="<?xml version='1.0' encoding='UTF-8'?>\n" + 
  	    "<!DOCTYPE oXygen:ReusableComponent [\n" + 
  	    "<!ELEMENT oXygen:ReusableComponent (oXygen:ComponentDescription, oXygen:ComponentDefinition)>\n" + 
  	    "<!ATTLIST oXygen:ReusableComponent \n" + 
  	    "    xmlns:oXygen CDATA #FIXED \"http://www.oxygenxml.com/ns/dita/reuse\" \n" + 
  	    "    id CDATA #IMPLIED\n" + 
  	    "    xml:lang CDATA #IMPLIED\n" + 
  	    "    domains CDATA \"(topic abbrev-d)                            a(props deliveryTarget)                            (topic equation-d)                            (topic hazard-d)                            (topic hi-d)                            (topic indexing-d)                            (topic markup-d)                            (topic mathml-d)                            (topic pr-d)                            (topic relmgmt-d)                            (topic sw-d)                            (topic svg-d)                            (topic ui-d)                            (topic ut-d)                            (topic markup-d xml-d)    (topic oXygen-reuse-d) \"\n" + 
  	    "    class CDATA \"- topic/topic oXygen:ReusableComponent/oXygen:ReusableComponent \"    \n" + 
  	    "    xmlns:ditaarch CDATA #FIXED \"http://dita.oasis-open.org/architecture/2005/\"\n" + 
  	    "    ditaarch:DITAArchVersion CDATA #FIXED \"1.2\" \n" + 
  	    "    >\n" + 
  	    "<!ELEMENT oXygen:ComponentDescription (#PCDATA)>\n" + 
  	    "<!ATTLIST oXygen:ComponentDescription \n" + 
  	    " class CDATA \"- topic/title oXygen:ReusableComponent/oXygen:ComponentDescription \">\n" + 
  	    "<!ELEMENT oXygen:ComponentDefinition ANY>\n" + 
  	    "<!ATTLIST oXygen:ComponentDefinition\n" + 
  	    "    class CDATA \"- topic/body oXygen:ReusableComponent/oXygen:ComponentDefinition \">\n" + 
  	    "\n" + 
  	    "<!ENTITY % dtd PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
  	    "%dtd;\n" + 
  	    "\n" + 
  	    "]>\n" + 
  	    "<oXygen:ReusableComponent id=\"ReusableComponent_ucc_kgx_xrb\">\n" + 
  	    "    <oXygen:ComponentDescription></oXygen:ComponentDescription>\n" + 
  	    "    <oXygen:ComponentDefinition>\n" + 
  	    "        <p id=\"p_wcc_kgx_xrb\">Most of the information was taken from <xref\n" + 
  	    "                href=\"http://www.wikipedia.com\" format=\"html\" scope=\"external\">Wikipedia</xref>, the\n" + 
  	    "            free encyclopedia.</p></oXygen:ComponentDefinition>\n" + 
  	    "</oXygen:ReusableComponent>\n" + 
  	    "";
  	
  	String contentAfterUpdate = updateProlog(xml, true);
  	assertFalse(contentAfterUpdate.contains("prolog"));
  }
	
}
