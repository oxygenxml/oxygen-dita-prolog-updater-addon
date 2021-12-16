package com.oxygenxml.prolog.updater;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.swing.text.BadLocationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;

import org.mockito.Mockito;
import org.w3c.css.sac.InputSource;
import org.xml.sax.SAXException;

import com.oxygenxml.prolog.updater.DitaPrologUpdater;
import junit.framework.TestCase;
import ro.sync.ecss.css.csstopdf.facade.AuthorDocumentFacade;
import ro.sync.ecss.css.csstopdf.facade.AuthorDocumentFacadeFactory;
import ro.sync.ecss.css.csstopdf.facade.CatalogResolverFacade;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.node.AuthorDocument;
import ro.sync.ecss.extensions.api.node.AuthorDocumentFragment;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;

/**
 * Extension of the test case for the tests in author page.
 * 
 * @author cosmin_duna
 *
 */
public abstract class AuthorTestCase extends TestCase {

	/**
	 * The name of author.
	 */
	final static String AUTHOR_NAME = "test";

	/**
	 * The maximum number of revised elements.
	 */
	int maxNoOfRevisedElements = -1;
	
	/**
	 * The relative path to catalogs.
	 */
	private static final String CATALOG = "config/catalogs/catalog.xml";

	/**
	 * Initializes the catalogs. Search for them in the config folder.
	 * 
	 * @param options
	 *          The command line options.
	 * @throws IOException
	 *           When the installation dir does not exist.
	 */
	private static void initializeCatalogs() throws IOException {
		String defaultCatalog = new File(CATALOG).toURI().toString();

		// Sets the catalogs
		String[] catalogURIs = new String[] { defaultCatalog };

		CatalogResolverFacade.setCatalogs(catalogURIs, "public");
	}

	/**
	 * Test the prolog update of given input in author mode.
	 * 
	 * @param inputXML
	 *          The input document.
	 * @param isNewDocument
	 *          <code>true</code> if document is new, <code>false</code> otherwise
	 * @param expectedXML
	 *          The expected output document.
	 * @throws IOException
	 * @throws SAXException
	 * @throws BadLocationException
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 */
	protected void testInAuthorMode(String inputXML, boolean isNewDocument, String expectedXML)
			throws IOException, SAXException, BadLocationException, ParserConfigurationException, TransformerException {
		String toXML = updateProlog(inputXML, isNewDocument);

		String expected = XmlPrettyPrinterUtil.indent(expectedXML).replaceAll(" +", " ");
		String actual = XmlPrettyPrinterUtil.indent(toXML).replaceAll(" +", " ");

		expected = expected.replaceAll(" <", "<");
		actual = actual.replaceAll(" <", "<");

		expected = expected.replaceAll("> ", ">");
		actual = actual.replaceAll("> ", ">");

		assertEquals("The updated content is wrong", expected, actual);
	}

	/**
	 * Update the prolog into the given input xml.
	 * 
   * @param inputXML The input xml content to update.
   * @param isNewDocument <code>true</code> if document is new, <code>false</code> otherwise
	 * 
	 * @return The updated xml content.
	 */
  public String updateProlog(String inputXML, boolean isNewDocument)
      throws IOException, SAXException, BadLocationException {
    initializeCatalogs();

		//
		// Create a AuthorDocumentController
		//
		AuthorDocumentFacadeFactory facadeFactory = new AuthorDocumentFacadeFactory();
		InputSource[] cssInputSources = new InputSource[] { new InputSource(new StringReader("* {display: block;}")) };
		StringReader reader = new StringReader(inputXML);
		AuthorDocumentFacade facade = facadeFactory.createFacade(new StreamSource(reader), cssInputSources, null,
				new File("."));
		AuthorDocumentController controller = facade.getController();

		//
		// Create mocks.
		//
		WSEditor wsEditor = Mockito.mock(WSEditor.class);
		WSAuthorEditorPage wsAuthorEditorPage = Mockito.mock(WSAuthorEditorPage.class);

		Mockito.when(wsEditor.isNewDocument()).thenReturn(isNewDocument);
		Mockito.when(wsEditor.getCurrentPage()).thenReturn(wsAuthorEditorPage);
		Mockito.when(wsAuthorEditorPage.getDocumentController()).thenReturn(controller);

		final String  dateFormat = getDateFormat();
		// Create the DitaUpdater
		DitaPrologUpdater ditaUpdater = new DitaPrologUpdater() {
			@Override
			protected String getAuthorName() {
				return AUTHOR_NAME;
			}
			
			@Override
			protected String getDateFormat() {
				return dateFormat;
			}
			
			@Override
			protected int getMaxNoOfRevisedElements() {
			  return maxNoOfRevisedElements;
			}
		};

		//
		// Call the updateProlog method
		//
		ditaUpdater.updateProlog(wsEditor, isNewDocument);

		//
		// Check result:
		//
		AuthorDocument documentNode = controller.getAuthorDocumentNode();
		AuthorDocumentFragment fragment = controller.createDocumentFragment(documentNode, true);
		String toXML = controller.serializeFragmentToXML(fragment);
    return toXML;
  }

	/**
	 * Get the date format for TCs
	 * @return
	 */
	public String getDateFormat() {
		return "YYYY-MM-dd";
	}
}
