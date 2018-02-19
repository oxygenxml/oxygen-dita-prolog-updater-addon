package com.oxygenxml.prolog.updater;

import org.junit.Test;
import org.mockito.Mockito;

import com.oxygenxml.prolog.updater.dita.editor.DitaTopicTextEditor;
import com.oxygenxml.prolog.updater.dita.editor.DocumentType;
import com.oxygenxml.prolog.updater.prolog.content.PrologContentCreator;
import com.oxygenxml.prolog.updater.utils.ElementXPathConstants;
import com.oxygenxml.prolog.updater.utils.ElementXPathUtils;

import junit.framework.TestCase;
import ro.sync.exml.editor.xmleditor.operations.context.RelativeInsertPosition;
import ro.sync.exml.workspace.api.editor.page.text.xml.TextDocumentController;
import ro.sync.exml.workspace.api.editor.page.text.xml.TextOperationException;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextNodeRange;
import ro.sync.exml.workspace.api.editor.page.text.xml.XPathException;

/**
 * Test the topic meta-information update in the Text mode for DITA maps.
 */
public class DitaSubjectSchemaUpdateTextModeTest extends TestCase {

	/**
	 * The name of the author.
	 */
	private static final String AUTHOR_NAME = "test";
	/**
	 * The page from wsEditor
	 */
	private WSXMLTextEditorPage wsTextEditorPage;
	/**
	 * Document controller
	 */
	private TextDocumentController textDocumentController;
	/**
	 * Creates the content that is inserted.
	 */
	private PrologContentCreator prologContentCreater;
	/**
	 * Text editor for prolog in dita.
	 */
	private DitaTopicTextEditor ditaTopicTextEditor;

	@Override
	protected void setUp() throws Exception {
		// Mock the WSXMLTextEditorPage
		wsTextEditorPage = Mockito.mock(WSXMLTextEditorPage.class);

		Mockito.when(wsTextEditorPage.findElementsByXPath(ElementXPathConstants.ROOT_MAP_XPATH))
				.thenReturn(new WSXMLTextNodeRange[1]);
		Mockito.when(wsTextEditorPage.findElementsByXPath(ElementXPathConstants.ROOT_BOOKMAP_XPATH))
				.thenReturn(new WSXMLTextNodeRange[1]);

		// Mock the TextDocumentController
		textDocumentController = Mockito.mock(TextDocumentController.class);
		Mockito.when(wsTextEditorPage.getDocumentController()).thenReturn(textDocumentController);

		// Create prolog content creator
		prologContentCreater = new PrologContentCreator(AUTHOR_NAME);

		// Create ditaTopicTextEditor
		ditaTopicTextEditor = new DitaTopicTextEditor(wsTextEditorPage, prologContentCreater);
	}

	/**
	 * <p>
	 * <b>Description:</b> Check if insertXMLFragment() is called when the prolog
	 * element is found but it's empty (doesn't contain "author" or "critdates").
	 * </p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUpdateProlog() throws Exception {

		Mockito.when(wsTextEditorPage.findElementsByXPath(ElementXPathConstants.TOPICMETA_XPATH))
				.thenReturn(new WSXMLTextNodeRange[1]);

		Mockito.when(wsTextEditorPage.findElementsByXPath(ElementXPathConstants.TOPICMETA_AUTHORS))
				.thenReturn(new WSXMLTextNodeRange[0]);

		Mockito.when(wsTextEditorPage.findElementsByXPath(ElementXPathConstants.TOPICMETA_CRITDATES))
				.thenReturn(new WSXMLTextNodeRange[0]);

		ditaTopicTextEditor.updateProlog(true);

		// Verify if the insertXMLFragment was called for 2 times.
		Mockito.verify(textDocumentController, Mockito.times(2)).insertXMLFragment(Mockito.anyString(), Mockito.anyString(),
				Mockito.any(RelativeInsertPosition.class));
		Mockito.reset(textDocumentController);
	}

	/**
	 * <p>
	 * <b>Description:</b> Check if "insertXMLFragment()" is called when the prolog tag
	 * is found and it contains the creator.
	 * </p>
	 *
	 * @throws Exception
	 */
	@Test
	public void testUpdateAuthor() throws Exception {

		Mockito.when(wsTextEditorPage.findElementsByXPath(ElementXPathConstants.TOPICMETA_XPATH))
				.thenReturn(new WSXMLTextNodeRange[1]);

		Mockito.when(wsTextEditorPage.findElementsByXPath(ElementXPathConstants.TOPICMETA_AUTHORS))
				.thenReturn(new WSXMLTextNodeRange[1]);

		Mockito.when(wsTextEditorPage.evaluateXPath(ElementXPathConstants.TOPICMETA_AUTHORS_CREATOR))
				.thenReturn(new WSXMLTextNodeRange[1]);

		Mockito.when(wsTextEditorPage.findElementsByXPath(ElementXPathConstants.TOPICMETA_CRITDATES))
				.thenReturn(new WSXMLTextNodeRange[0]);

		ditaTopicTextEditor.updateProlog(true);

		// Verify if the insertXMLFragment was called 1 time.
		Mockito.verify(textDocumentController, Mockito.times(1)).insertXMLFragment(Mockito.anyString(), Mockito.anyString(),
				Mockito.any(RelativeInsertPosition.class));
		Mockito.reset(textDocumentController);
	}

	/**
	 * <p>
	 * <b>Description:</b> Check if "insertXMLFragment()" is called when the prolog tag
	 * is found and it contains "critdates".
	 * </p>
	 *
	 * @throws Exception
	 */
	@Test
	public void testUpdateCritdates() throws XPathException, TextOperationException {
		Mockito.when(wsTextEditorPage.findElementsByXPath(ElementXPathConstants.TOPICMETA_XPATH))
				.thenReturn(new WSXMLTextNodeRange[1]);

		Mockito.when(wsTextEditorPage.findElementsByXPath(ElementXPathConstants.TOPICMETA_AUTHORS))
				.thenReturn(new WSXMLTextNodeRange[0]);

		Mockito.when(wsTextEditorPage.evaluateXPath(ElementXPathConstants.TOPICMETA_AUTHORS_CREATOR))
				.thenReturn(new WSXMLTextNodeRange[0]);

		Mockito.when(wsTextEditorPage.findElementsByXPath(ElementXPathConstants.TOPICMETA_CRITDATES))
				.thenReturn(new WSXMLTextNodeRange[1]);

		Mockito.when(wsTextEditorPage.findElementsByXPath(ElementXPathConstants.TOPICMETA_CRITDATES))
				.thenReturn(new WSXMLTextNodeRange[1]);

		Mockito.when(wsTextEditorPage.evaluateXPath(ElementXPathUtils.getCreatedXpath(DocumentType.MAP)))
				.thenReturn(new WSXMLTextNodeRange[1]);

		ditaTopicTextEditor.updateProlog(true);

		// Verify if the insertXMLFragment was called one time.
		Mockito.verify(textDocumentController, Mockito.times(1)).insertXMLFragment(Mockito.anyString(), Mockito.anyString(),
				Mockito.any(RelativeInsertPosition.class));
		Mockito.reset(textDocumentController);
	}

	/**
	 * <p>
	 * <b>Description:</b> Check if "insertXMLFragment()" is not called when the prolog
	 * contains the "creator" and "critdates" elements.
	 * </p>
	 *
	 * @throws Exception
	 */
	@Test
	public void testUpdateWhenContainsContent() throws XPathException, TextOperationException {
		Mockito.when(wsTextEditorPage.findElementsByXPath(ElementXPathConstants.TOPICMETA_XPATH))
				.thenReturn(new WSXMLTextNodeRange[1]);

		Mockito.when(wsTextEditorPage.findElementsByXPath(ElementXPathConstants.TOPICMETA_AUTHORS))
				.thenReturn(new WSXMLTextNodeRange[1]);

		Mockito.when(wsTextEditorPage.evaluateXPath(ElementXPathConstants.TOPICMETA_AUTHORS_CREATOR))
				.thenReturn(new WSXMLTextNodeRange[1]);

		Mockito.when(wsTextEditorPage.findElementsByXPath(ElementXPathConstants.TOPICMETA_CRITDATES))
				.thenReturn(new WSXMLTextNodeRange[1]);

		Mockito.when(wsTextEditorPage.evaluateXPath(ElementXPathConstants.TOPICMETA_CREATED_ELEMENT))
				.thenReturn(new WSXMLTextNodeRange[1]);

		ditaTopicTextEditor.updateProlog(true);

		// Verify if the insertXMLFragment wasn't called.
		Mockito.verify(textDocumentController, Mockito.times(0)).insertXMLFragment(Mockito.anyString(), Mockito.anyString(),
				Mockito.any(RelativeInsertPosition.class));
		Mockito.reset(textDocumentController);
	}

}
