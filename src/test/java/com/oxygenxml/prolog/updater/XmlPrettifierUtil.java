package com.oxygenxml.prolog.updater;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

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
		System.err.println("PRETTYFI " + xmlContent);
		InputSource inputSource = new InputSource(new StringReader(xmlContent));

		StringWriter sw = new StringWriter();
		StreamResult res = new StreamResult(sw);

		try {
			transformer = transformerFactory.newTransformer();

			// set the output properties
//			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
//			transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "");
//			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "");
			
			
			
			// prettify and print
			SAXSource saxSource = new SAXSource(inputSource);
			XMLReader reader = createXMLReader();
//			reader.setEntityResolver(null);
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
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (SAXNotSupportedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (SAXException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
		prettifiedContent = sw.toString();

		sw.close();
		System.err.println("RET " + sw.toString());
		return prettifiedContent;
	}
	
	public static String prettyPrint(String xmlFragment) {
	  
    StringReader reader = new StringReader(xmlFragment);
    Source xmlInput = new StreamSource(reader);
    StreamResult output = new StreamResult(new StringWriter());

    Transformer transformer;
    try {
      TransformerFactory factory = TransformerFactory.newInstance();

//      factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
//      factory.setFeature("http://xml.org/sax/features/validation", false);
      
      transformer = factory.newTransformer();
      transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "topic.dtd");
      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty(OutputKeys.METHOD, "xml");
      transformer.transform(xmlInput, output);
    } catch (Exception e) {
      e.printStackTrace();
    } // An identity transformer

    String indentedOutput = output.getWriter().toString();
	  System.out.println("out:" + indentedOutput);
    return indentedOutput;
	}
	
	public static XMLReader createXMLReader()
      throws SAXNotRecognizedException, SAXNotSupportedException, ParserConfigurationException, SAXException {
    
    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setNamespaceAware(true);
    factory.setValidating(false);
//    factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
//    factory.setFeature("http://xml.org/sax/features/validation", false);
    SAXParser saxParser = factory.newSAXParser();
    
    return saxParser.getXMLReader();
  }
	
}
