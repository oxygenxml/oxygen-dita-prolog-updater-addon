package com.oxygenxml.prolog.updater;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.text.BadLocationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

public class DitaSubjectSchemaUpdaterAuthorModeTest extends AuthorTestCase{

	/**
	 * Test if the topicmeta is correct added after save operation on DITA Subject Schema in Author mode.
	 * @throws IOException
	 * @throws SAXException
	 * @throws BadLocationException
	 * @throws ParserConfigurationException 
	 * @throws TransformerException 
	 */
	public void testAddProlog() throws IOException, SAXException, BadLocationException, ParserConfigurationException, TransformerException {

		//Get the local date
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();
		
		final String localDate = dateFormat.format(date);
		
		//
		//Test document with topimeta element that doesn't has child.
		//
		//input document with author 
		String xmlWithEmptyProlog ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
				"<?xml-model href=\"urn:oasis:names:tc:dita:spec:classification:rng:subjectScheme.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?> \n" + 
				"<subjectScheme class = \"- map/map subjectScheme/subjectScheme \">\n" +
				"    <topicmeta class= \"- map/topicmeta \">\n" + 
				"    </topicmeta>\n" + 
				"    <subjectHead>\n" + 
				"        <subjectHeadMeta class = \"- map/topicmeta subjectScheme/subjectHeadMeta \">\n" + 
				"            <navtitle>\n" + 
				"                <!-- Enter a description of the subject scheme. -->\n" + 
				"            </navtitle>\n" + 
				"        </subjectHeadMeta>\n" + 
				"    </subjectHead>\n" + 
				"    <!-- ... -->\n" + 
				"</subjectScheme>";
				
		
		//expected XML output for new input document
		String expectedNewXMLWithEmptyProlog = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><?xml-model href=\"urn:oasis:names:tc:dita:spec:classification:rng:subjectScheme.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>" + 
				"<subjectScheme class=\"- map/map subjectScheme/subjectScheme \">" + 
				"<topicmeta class=\"- map/topicmeta \">" + 
				"<author type=\"creator\">"+AuthorTestCase.AUTHOR_NAME+"</author>" + 
				"<critdates>" + 
				"<created date=\""+localDate+"\"/>" + 
				"</critdates>" + 
				"</topicmeta>" + 
				"<subjectHead>" + 
				"<subjectHeadMeta class=\"- map/topicmeta subjectScheme/subjectHeadMeta \">" + 
				"<navtitle><!-- Enter a description of the subject scheme. --></navtitle>" + 
				"</subjectHeadMeta>" + 
				"</subjectHead><!-- ... -->" + 
				"</subjectScheme>";

		//expected XML output for old input document
		String expectedOldXmlWithEmptyProlog = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><?xml-model href=\"urn:oasis:names:tc:dita:spec:classification:rng:subjectScheme.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>" + 
				"<subjectScheme class=\"- map/map subjectScheme/subjectScheme \">" + 
				"<topicmeta class=\"- map/topicmeta \">" + 
				"<author type=\"contributor\">"+AuthorTestCase.AUTHOR_NAME+"</author>" + 
				"<critdates>" + 
				"<!--"+AuthorTestCase.AUTHOR_NAME+"-->" + 
				"<revised modified=\""+localDate+"\" />" + 
				"</critdates>" + 
				"</topicmeta>" + 
				"<subjectHead>" + 
				"<subjectHeadMeta class=\"- map/topicmeta subjectScheme/subjectHeadMeta \">" + 
				"<navtitle><!-- Enter a description of the subject scheme. --></navtitle>" + 
				"</subjectHeadMeta>" + 
				"</subjectHead><!-- ... -->" + 
				"</subjectScheme>";
		
		// test when document is new
		testInAuthorMode(xmlWithEmptyProlog, true, expectedNewXMLWithEmptyProlog);
		
		//test when document isn't new
		testInAuthorMode(xmlWithEmptyProlog, false , expectedOldXmlWithEmptyProlog);
	}

}
