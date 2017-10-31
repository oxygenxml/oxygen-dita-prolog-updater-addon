package com.oxygenxml.prolog.updater;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.swing.text.BadLocationException;
import javax.xml.transform.stream.StreamSource;

import org.mockito.Mockito;
import org.w3c.css.sac.InputSource;
import org.xml.sax.SAXException;

import junit.framework.TestCase;
import ro.sync.ecss.css.csstopdf.facade.AuthorDocumentFacade;
import ro.sync.ecss.css.csstopdf.facade.AuthorDocumentFacadeFactory;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.node.AuthorDocument;
import ro.sync.ecss.extensions.api.node.AuthorDocumentFragment;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;


/**
 * Utility class for DitaUpdateTest
 * @author intern4
 *
 */
public class DitaUpdateTestUtil extends TestCase{

	/**
	 * The name of author.
	 */
	final static String AUTHOR_NAME = "test";
	
	
	/**
	 * Test the prolog update of given input in author mode.
	 * @param inputXML The input document.
	 * @param isNewDocument <code>true</code> if document is new, <code>false</code> otherwise
	 * @param expectedXML The expected output document.
	 * @throws IOException
	 * @throws SAXException
	 * @throws BadLocationException
	 */
	public static void testInAuthorMode(String inputXML, boolean isNewDocument, String expectedXML) throws IOException, SAXException, BadLocationException{
		//
		//Create a AuthorDocumentController
		//
		AuthorDocumentFacadeFactory facadeFactory = new AuthorDocumentFacadeFactory();
		
		InputSource[] cssInputSources = new InputSource[] { new InputSource(new StringReader("* {display: block;}")) };

		AuthorDocumentFacade facade = facadeFactory.createFacade( new StreamSource(new StringReader(inputXML)),
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

		assertEquals(XmlPrettifierUtil.prettify(expectedXML).replaceAll(" ", ""),
				XmlPrettifierUtil.prettify(toXML).replaceAll(" ", ""));

	}
}
