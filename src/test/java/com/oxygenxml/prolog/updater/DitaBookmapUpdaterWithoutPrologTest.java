package com.oxygenxml.prolog.updater;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.text.BadLocationException;

import org.xml.sax.SAXException;

import junit.framework.TestCase;

public class DitaBookmapUpdaterWithoutPrologTest extends TestCase{

	/**
	 * Test if the bookmeta is correct added after save operation.
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
		String xmlWithoutProlog ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + 
				"<!DOCTYPE bookmap PUBLIC \"-//OASIS//DTD DITA BookMap//EN\" \"bookmap.dtd\">" + 
				"<bookmap>" + 
				"  <title>" + 
				"  </title>" + 
				"</bookmap>";
		
		//expected XML output for new input document
		String expectedNewXML = "<?xml version=\"1.0\" encoding=\"utf-8\"?><bookmap>" + 
				"  <bookmeta>" + 
				"    <author type=\"creator\">"+DitaUpdateTestUtil.AUTHOR_NAME+"</author> " + 
				"    <critdates>" + 
				"      <created date=\""+ localDate +"\" /> " + 
				"    </critdates>" + 
				"  </bookmeta>" + 
				"  <title> </title>" + 
				"</bookmap>";

		//expected XML output for old input document
		String expectedOldXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><bookmap>" + 
				"  <bookmeta>" + 
				"    <author type=\"contributor\" >"+DitaUpdateTestUtil.AUTHOR_NAME+"</author>" + 
				"    <critdates >" + 
				"      <!--"+DitaUpdateTestUtil.AUTHOR_NAME+"-->" + 
				"      <revised modified=\""+localDate+"\" />" + 
				"    </critdates>" + 
				"  </bookmeta>" + 
				"  <title > </title>" + 
				"</bookmap>" + 
				"";
		
		// test when document is new
		DitaUpdateTestUtil.testInAuthorMode(xmlWithoutProlog, true, expectedNewXML);
		
		//test when document isn't new
		DitaUpdateTestUtil.testInAuthorMode(xmlWithoutProlog, false, expectedOldXml);
		
		
	}

}
