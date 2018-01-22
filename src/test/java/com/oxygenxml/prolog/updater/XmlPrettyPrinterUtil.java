package com.oxygenxml.prolog.updater;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

/**
 * XML pretty printer used in JUnits
 * 
 * @author cosmin_duna
 *
 */
public class XmlPrettyPrinterUtil {

	/**
	 * Indent the given XML content.
	 * 
	 * @param xmlContent
	 *          The content to indent.
	 * @return The indented content.
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws SAXNotSupportedException
	 * @throws SAXNotRecognizedException
	 * @throws TransformerException
	 */
	public static String indent(String xmlContent) throws IOException, SAXNotRecognizedException,
			SAXNotSupportedException, ParserConfigurationException, SAXException, TransformerException {
		String prettifiedContent = null;

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		// create the transformer
		Transformer transformer;

		// get the input source
		InputSource inputSource = new InputSource(new StringReader(xmlContent));
		StringWriter sw = new StringWriter();

		try {
			StreamResult res = new StreamResult(sw);
			transformer = transformerFactory.newTransformer();
			// set the output properties
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			// prettify and print
			SAXSource saxSource = new SAXSource(inputSource);
			XMLReader reader = createXMLReader();
			reader.setEntityResolver(new EntityResolver() {
				public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
					return new InputSource(new StringReader(""));
				}
			});
			saxSource.setXMLReader(reader);
			transformer.transform(saxSource, res);
			prettifiedContent = sw.toString();
		} finally {
			sw.close();
		}
		return prettifiedContent;
	}

	public static XMLReader createXMLReader()
			throws SAXNotRecognizedException, SAXNotSupportedException, ParserConfigurationException, SAXException {

		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setValidating(false);
		SAXParser saxParser = factory.newSAXParser();

		return saxParser.getXMLReader();
	}

}
