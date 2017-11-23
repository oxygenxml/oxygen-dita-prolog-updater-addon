package com.oxygenxml.prolog.updater;

import java.io.IOException;

import javax.swing.text.BadLocationException;

import org.junit.Test;
import org.mockito.Mockito;
import org.xml.sax.SAXException;

import com.oxygenxml.prolog.updater.dita.editor.DitaTopicTextEditor;

import ro.sync.exml.editor.xmleditor.operations.context.RelativeInsertPosition;
import ro.sync.exml.workspace.api.editor.page.text.xml.TextDocumentController;
import ro.sync.exml.workspace.api.editor.page.text.xml.TextOperationException;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextNodeRange;
import ro.sync.exml.workspace.api.editor.page.text.xml.XPathException;

public class DitaUpdateTextModeTest {

	static final String AUTHOR_NAME = "test";
	
	/**
	 * Test if the prolog is correct added in text mode.
	 * @throws IOException
	 * @throws SAXException
	 * @throws BadLocationException
	 */
	@Test
	public void testUpdateInTextMode() throws TextOperationException, XPathException {
		
		//Mock the WSXMLTextEditorPage
		WSXMLTextEditorPage wsTextEditorPage = Mockito.mock(WSXMLTextEditorPage.class);

		//Mock the TextDocumentController
		TextDocumentController textDocumentController = Mockito.mock(TextDocumentController.class);
		Mockito.when(wsTextEditorPage.getDocumentController()).thenReturn(textDocumentController);
	
		//Create prolog content creator
		PrologContentCreator prologContentCreater = PrologContentCreator.getInstance();
		prologContentCreater.setAuthor(AUTHOR_NAME);
		
		//Create ditaTopicTextEditor
		DitaTopicTextEditor ditaTopicTextEditor = new DitaTopicTextEditor(wsTextEditorPage);
		
		
		//
		//Check if insertXMLFragment is call when the prolog tag is not found.
		//
		Mockito.when(wsTextEditorPage.findElementsByXPath("//*[contains(@class,'topic/prolog')]")).
		thenReturn(new WSXMLTextNodeRange[0]);
		
		ditaTopicTextEditor.updateProlog(true);
		
		Mockito.verify(textDocumentController, Mockito.times(1)).insertXMLFragment(Mockito.eq(prologContentCreater.getPrologFragment(true)), Mockito.anyString(), Mockito.any(RelativeInsertPosition.class));
		Mockito.reset(textDocumentController);
		
		
		//
		//Check if insertXMLFragment is call when the prolog tag is found.
		//
		Mockito.when(wsTextEditorPage.findElementsByXPath("//*[contains(@class,'topic/prolog')]")).
		thenReturn(new WSXMLTextNodeRange[1]);
		
		Mockito.when(wsTextEditorPage.findElementsByXPath("//*[contains(@class,'topic/prolog')]/author")).
		thenReturn(new WSXMLTextNodeRange[0]);
		
		Mockito.when(wsTextEditorPage.findElementsByXPath("//*[contains(@class,'topic/prolog')]/critdates")).
		thenReturn(new WSXMLTextNodeRange[0]);
		
		
		ditaTopicTextEditor.updateProlog(true);
		
		Mockito.verify(textDocumentController, Mockito.times(2)).insertXMLFragment(Mockito.anyString(), Mockito.anyString(), Mockito.any(RelativeInsertPosition.class));

	}

}
