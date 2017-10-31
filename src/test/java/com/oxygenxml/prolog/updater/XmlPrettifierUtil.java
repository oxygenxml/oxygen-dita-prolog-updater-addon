package com.oxygenxml.prolog.updater;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.InputSource;

/**
 * XML prettifier used in JUnits
 * 
 * @author intern4
 *
 */
public class XmlPrettifierUtil {

	public static String prettify(String xmlContent) {
		String prettifiedContent;

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		// create the transformer
		Transformer transformer;

		// get the input source
		InputSource inputSource = new InputSource(new StringReader(xmlContent));

		StringWriter sw = new StringWriter();
		StreamResult res = new StreamResult(sw);

		try {
			transformer = transformerFactory.newTransformer();

			// set the output properties
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			// prettify and print
			transformer.transform(new SAXSource(inputSource), res);
		} catch (TransformerException e) {
		}
		prettifiedContent = sw.toString();

		try {
			sw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return prettifiedContent;
	}
	
}
