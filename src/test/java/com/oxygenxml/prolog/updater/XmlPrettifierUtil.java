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
 * XML prettifier used in JUnits
 * 
 * @author cosmin_duna
 *
 */
public class XmlPrettifierUtil {

	public static String prettify(String xmlContent) throws IOException {
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
		} catch (TransformerException e) {
		  e.printStackTrace();
		} catch (SAXNotRecognizedException e) {
      e.printStackTrace();
    } catch (SAXNotSupportedException e) {
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
		prettifiedContent = sw.toString();

		sw.close();
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
