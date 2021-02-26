package com.oxygenxml.prolog.updater;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.text.BadLocationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

public class DitaMapUpdaterAuthorTest extends AuthorTestCase{

	/**
	 * Test if the author tag is correct modified after save operation in DITA map.
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
		    "<!DOCTYPE map PUBLIC \"-//OASIS//DTD DITA Map//EN\" \"map.dtd\">"+
		    "<map>" + 
				"    <topicmeta >" + 
				"        <author type=\"creator\" >"+AuthorTestCase.AUTHOR_NAME+"</author> " + 
				"    </topicmeta>" + 
			  "    <title > </title>" + 
				"</map>" + 
				"";
		
		//expected XML output for new input document
		String expectedNewXMLWithCreator = "<?xml version=\"1.0\" encoding=\"utf-8\"?><map>" + 
				"    <topicmeta >" + 
				"        <author type=\"creator\" >"+AuthorTestCase.AUTHOR_NAME+"</author> " + 
				"        <critdates >" + 
				"            <created date=\""+ localDate +"\" />  " + 
				"        </critdates>" + 
				"    </topicmeta>" + 
				"    <title > </title>" + 
				"</map>" + 
				"";

		//expected XML output for old input document
		String expectedOldXmlWithCreator = "<?xml version=\"1.0\" encoding=\"utf-8\"?><map>" + 
				"    <topicmeta >" + 
				"        <author type=\"creator\" >"+AuthorTestCase.AUTHOR_NAME+"</author>" + 
				"        <critdates>" + 
				"           <!--"+AuthorTestCase.AUTHOR_NAME+"-->" + 
				"            <revised modified=\""+localDate+"\" />" + 
				"        </critdates>" + 
				"    </topicmeta>" + 
				"    <title > </title>" + 
				"</map>" + 
				" ";
		
		// test when document is new
		testInAuthorMode(xmlWithCreator, true,  expectedNewXMLWithCreator);
		
		//test when document isn't new
		testInAuthorMode(xmlWithCreator, false ,  expectedOldXmlWithCreator);
		
		
		
		//
		//Test document with topicmeta element that contains contributor author.
		//
		//input document with author 
		String xmlWithContributor ="<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
		    "<!DOCTYPE map PUBLIC \"-//OASIS//DTD DITA Map//EN\" \"map.dtd\">"+
		    "<map>" + 
				"    <title > </title>" + 
				"    <topicmeta >" + 
				"        <author type=\"contributor\" >"+AuthorTestCase.AUTHOR_NAME+"</author> " + 
				"    </topicmeta>" + 
				"    <body >" + 
				"        <p/>" + 
				"    </body>" + 
				"</map>" + 
				"";
		
		//expected XML output for new input document
		String expectedNewXMLWithContributor = "<?xml version=\"1.0\" encoding=\"utf-8\"?><map>" + 
				"    <title > </title>" + 
				"    <topicmeta >" + 
				"        <author type=\"creator\" >"+AuthorTestCase.AUTHOR_NAME+"</author> " + 
				"        <author type=\"contributor\" >"+AuthorTestCase.AUTHOR_NAME+"</author> " + 
				"        <critdates >" + 
				"            <created date=\""+ localDate +"\" />  " + 
				"        </critdates>" + 
				"    </topicmeta>" + 
				"    <body >" + 
				"        <p/>" + 
				"    </body>" + 
				"</map>" + 
				"";

		//expected XML output for old input document
		String expectedOldXmlWithContributor = "<?xml version=\"1.0\" encoding=\"utf-8\"?><map>" + 
				"    <title > </title>" + 
				"    <topicmeta >" + 
				"        <author type=\"contributor\" >"+AuthorTestCase.AUTHOR_NAME+"</author> " + 
				"        <critdates>" + 
				"           <!--"+AuthorTestCase.AUTHOR_NAME+"-->" + 
				"            <revised modified=\""+localDate+"\" />" + 
				"        </critdates>" + 
				"    </topicmeta>" + 
				"    <body >" + 
				"        <p/>" + 
				"    </body>" + 
				"</map>" + 
				" ";
		
		// test when document is new
		testInAuthorMode(xmlWithContributor, true,  expectedNewXMLWithContributor);
		
		//test when document isn't new
		testInAuthorMode(xmlWithContributor, false ,  expectedOldXmlWithContributor);
		
	
	
	//
	//Test document with topicmeta element that contains a different contributor.
	//
	//input document
	String xmlWithDifferentContributor ="<?xml version=\"1.0\" encoding=\"utf-8\"?>"+
	    "<!DOCTYPE map PUBLIC \"-//OASIS//DTD DITA Map//EN\" \"map.dtd\">"+
	    "<map>" + 
			"    <title > </title>" + 
			"    <topicmeta >" + 
			"        <author type=\"contributor\" >nume</author> " + 
			"        <critdates>" + 
			"           <!--nume-->" + 
			"            <revised modified=\""+localDate+"\" />" + 
			"        </critdates>" + 
			"    </topicmeta>" + 
			"    <body >" + 
			"        <p/>" + 
			"    </body>" + 
			"</map>" + 
			"";
	

	//expected XML output for old input document
	String expectedOldXmlWithDifferentContributor = "<?xml version=\"1.0\" encoding=\"utf-8\"?><map>" + 
			"    <title > </title>" + 
			"    <topicmeta >" + 
			"        <author type=\"contributor\" >nume</author> " + 
			"        <author type=\"contributor\" >"+AuthorTestCase.AUTHOR_NAME+"</author> " + 
			"        <critdates>" + 
			"           <!--nume-->" + 
			"            <revised modified=\""+localDate+"\" />" + 
			"           <!--"+AuthorTestCase.AUTHOR_NAME+"-->" + 
			"            <revised modified=\""+localDate+"\" />" + 
			"        </critdates>" + 
			"    </topicmeta>" + 
			"    <body >" + 
			"        <p/>" + 
			"    </body>" + 
			"</map>" + 
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
        "<!DOCTYPE map PUBLIC \"-//OASIS//DTD DITA Map//EN\" \"map.dtd\">"+
        "<map>" + 
        "    <title> </title>" + 
        "    <topicmeta>" + 
        "        <author type=\"creator\" >"+AuthorTestCase.AUTHOR_NAME+"</author> " + 
        "        <critdates >" + 
        "            <created date=\""+ localDate +"\" />  " + 
        "        </critdates>" + 
        "    </topicmeta>" + 
        "    <body >" + 
        "        <p/>" + 
        "    </body>" + 
        "</map>" + 
        "";
    
    
    
    
    // test when document is new
    testInAuthorMode(fragment, true,  fragment);
  }
	
}
