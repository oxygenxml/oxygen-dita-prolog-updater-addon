package com.oxygenxml.prolog.updater;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.swing.text.BadLocationException;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;
import org.mockito.Mockito;
import org.w3c.css.sac.InputSource;
import org.xml.sax.SAXException;

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
 * Utility class for DitaUpdateTest
 * @author cosmin_duna
 *
 */
public class DitaUpdateTestUtil extends TestCase{

	/**
	 * The name of author.
	 */
	final static String AUTHOR_NAME = "test";
  private static final String CONFIG_FOLDER = "config";
	
  /**
   * Initializes the catalogs. Search for them in the config folder.
   * 
   * @param options The command line options.
   * @throws IOException When the installation dir does not exist.
   */
  private static void initializeCatalogs() throws IOException {
    String defaultCatalog = new File(CONFIG_FOLDER + "/catalogs/catalog.xml").toURI().toString();
    
    // Sets the catalogs
    String[] catalogURIs = new String[] {defaultCatalog};
    
    CatalogResolverFacade.setCatalogs(catalogURIs, "public");
  }
	
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    initializeCatalogs();
  }
	
	/**
	 * Test the prolog update of given input in author mode.
	 * @param inputXML The input document.
	 * @param isNewDocument <code>true</code> if document is new, <code>false</code> otherwise
	 * @param expectedXML The expected output document.
	 * @throws IOException
	 * @throws SAXException
	 * @throws BadLocationException
	 */
  @Test
	public static void testInAuthorMode(String inputXML, boolean isNewDocument, String expectedXML) throws IOException, SAXException, BadLocationException{
	  // I would say the init must be call only once.
	  initializeCatalogs();
	  
	  //
		//Create a AuthorDocumentController
		//
		AuthorDocumentFacadeFactory facadeFactory = new AuthorDocumentFacadeFactory();
		
		InputSource[] cssInputSources = new InputSource[] { new InputSource(new StringReader("* {display: block;}")) };
		
		
		
		StringReader reader = new StringReader(inputXML);
    AuthorDocumentFacade facade = facadeFactory.createFacade( new StreamSource(reader),
				cssInputSources, null, new File("."));
		AuthorDocumentController controller = facade.getController();
		
		//
		//Create mocks.
		//
		WSEditor wsEditor = Mockito.mock(WSEditor.class);
		WSAuthorEditorPage wsAuthorEditorPage = Mockito.mock(WSAuthorEditorPage.class);

		Mockito.when(wsEditor.isNewDocument()).thenReturn(isNewDocument);
		Mockito.when(wsEditor.getCurrentPage()).thenReturn(wsAuthorEditorPage);
		Mockito.when(wsAuthorEditorPage.getDocumentController()).thenReturn(controller);

		//Create the DitaUpdater
		DitaUpdater ditaUpdater = new DitaUpdater(){
			@Override
			protected String getAuthorName() {
				return AUTHOR_NAME;
			}
		};
		
		//
		//Call the updateProlog method
		//
		ditaUpdater.updateProlog(wsEditor);
		
		//
		// Check result:
		//
		AuthorDocument documentNode = controller.getAuthorDocumentNode();
		AuthorDocumentFragment fragment = controller.createDocumentFragment(documentNode, true);
		String toXML = controller.serializeFragmentToXML(fragment);

		String expected = XmlPrettifierUtil.prettify(expectedXML).replaceAll(" +", " ");
    String actual = XmlPrettifierUtil.prettify(toXML).replaceAll(" +", " ");
    
    expected = expected.replaceAll(" <", "<");
    actual = actual.replaceAll(" <", "<");
    
    expected = expected.replaceAll("> ", ">");
    actual = actual.replaceAll("> ", ">");
 
    
    assertEquals(expected, actual);
	}
}
