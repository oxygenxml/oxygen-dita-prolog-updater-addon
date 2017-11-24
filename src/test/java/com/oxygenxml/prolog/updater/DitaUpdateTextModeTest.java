package com.oxygenxml.prolog.updater;

import java.io.IOException;

import javax.swing.text.BadLocationException;

import org.junit.Test;
import org.mockito.Mockito;
import org.xml.sax.SAXException;

import com.oxygenxml.prolog.updater.dita.editor.DitaTopicTextEditor;
import com.oxygenxml.prolog.updater.utils.XPathConstants;

import junit.framework.TestCase;
import ro.sync.exml.editor.xmleditor.operations.context.RelativeInsertPosition;
import ro.sync.exml.workspace.api.editor.page.text.xml.TextDocumentController;
import ro.sync.exml.workspace.api.editor.page.text.xml.TextOperationException;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextNodeRange;
import ro.sync.exml.workspace.api.editor.page.text.xml.XPathException;

public class DitaUpdateTextModeTest extends TestCase{

	static final String AUTHOR_NAME = "test";
  private WSXMLTextEditorPage wsTextEditorPage;
  private TextDocumentController textDocumentController;
  private PrologContentCreator prologContentCreater;
  private DitaTopicTextEditor ditaTopicTextEditor;
	
  @Override
  protected void setUp() throws Exception {
    //Mock the WSXMLTextEditorPage
    wsTextEditorPage = Mockito.mock(WSXMLTextEditorPage.class);

    //Mock the TextDocumentController
    textDocumentController = Mockito.mock(TextDocumentController.class);
    Mockito.when(wsTextEditorPage.getDocumentController()).thenReturn(textDocumentController);

    //Create prolog content creator
    prologContentCreater = PrologContentCreator.getInstance();
    prologContentCreater.setAuthor(AUTHOR_NAME);

    //Create ditaTopicTextEditor
    ditaTopicTextEditor = new DitaTopicTextEditor(wsTextEditorPage);
  }
	
	/**
	 *  <p><b>Description:</b>Check if insertXMLFragment is call when the prolog tag is not found.</p>
	 *  
	 * @throws IOException
	 * @throws SAXException
	 * @throws BadLocationException
	 */
	@Test
	public void testUpdateInexistentProlog() throws TextOperationException, XPathException {
		Mockito.when(wsTextEditorPage.findElementsByXPath("//*[contains(@class,'topic/prolog')]")).
		thenReturn(new WSXMLTextNodeRange[0]);
		
		Mockito.when(wsTextEditorPage.findElementsByXPath(XPathConstants.FIRST_BODY_IN_ROOT_TOPIC)).
    thenReturn(new WSXMLTextNodeRange[1]);
		
		ditaTopicTextEditor.updateProlog(true);
		
		Mockito.verify(textDocumentController, 
		    Mockito.times(1)).insertXMLFragment(Mockito.eq(prologContentCreater.getPrologFragment(true)), 
		        Mockito.anyString(), Mockito.any(RelativeInsertPosition.class));
		Mockito.reset(textDocumentController);
	}
	
	 /**
   *  <p><b>Description:</b>Check if insertXMLFragment is call when the prolog element is found, but it's empty(doesn't contain author or critdates).</p>
   * 
   * @throws IOException
   * @throws SAXException
   * @throws BadLocationException
   */
  @Test
  public void testUpdateProlog() throws TextOperationException, XPathException {
		Mockito.when(wsTextEditorPage.findElementsByXPath("//*[contains(@class,'topic/prolog')]")).
		thenReturn(new WSXMLTextNodeRange[1]);
		
		Mockito.when(wsTextEditorPage.findElementsByXPath("//*[contains(@class,'topic/prolog')]/author")).
		thenReturn(new WSXMLTextNodeRange[0]);
		
		Mockito.when(wsTextEditorPage.findElementsByXPath("//*[contains(@class,'topic/prolog')]/critdates")).
		thenReturn(new WSXMLTextNodeRange[0]);
		
		
		ditaTopicTextEditor.updateProlog(true);
		
		Mockito.verify(textDocumentController, Mockito.times(2)).insertXMLFragment(Mockito.anyString(), Mockito.anyString(), Mockito.any(RelativeInsertPosition.class));
		Mockito.reset(textDocumentController);
	}
	
	/**
   * <p><b>Description:</b> Check if insertXMLFragment is call when the prolog tag is found and it contains the creator.</p>
   *
   * @throws Exception
   */
	@Test
	public void testUpdateAuthor() throws XPathException, TextOperationException {
    Mockito.when(wsTextEditorPage.findElementsByXPath("//*[contains(@class,'topic/prolog')]"))
        .thenReturn(new WSXMLTextNodeRange[1]);

    Mockito.when(wsTextEditorPage.findElementsByXPath("//*[contains(@class,'topic/prolog')]/author")).
    thenReturn(new WSXMLTextNodeRange[1]);
    
    Mockito.when(wsTextEditorPage.evaluateXPath(XPathConstants.PROLOG_AUTHORS_CREATOR))
        .thenReturn(new WSXMLTextNodeRange[1]);

    Mockito.when(wsTextEditorPage.findElementsByXPath("//*[contains(@class,'topic/prolog')]/critdates"))
        .thenReturn(new WSXMLTextNodeRange[0]);

    ditaTopicTextEditor.updateProlog(true);

    Mockito.verify(textDocumentController, Mockito.times(1)).insertXMLFragment(Mockito.anyString(), Mockito.anyString(),
        Mockito.any(RelativeInsertPosition.class));
    Mockito.reset(textDocumentController);
  }
	
	/**
   * <p><b>Description:</b> Check if insertXMLFragment is call when the prolog tag is found and it contains the critdates.</p>
   *
   * @throws Exception
   */
	@Test
  public void testUpdateCritdates() throws XPathException, TextOperationException {
    Mockito.when(wsTextEditorPage.findElementsByXPath("//*[contains(@class,'topic/prolog')]"))
        .thenReturn(new WSXMLTextNodeRange[1]);

    Mockito.when(wsTextEditorPage.findElementsByXPath("//*[contains(@class,'topic/prolog')]/author")).
    thenReturn(new WSXMLTextNodeRange[0]);
    
    Mockito.when(wsTextEditorPage.evaluateXPath(XPathConstants.PROLOG_AUTHORS_CREATOR))
        .thenReturn(new WSXMLTextNodeRange[0]);

    Mockito.when(wsTextEditorPage.findElementsByXPath("//*[contains(@class,'topic/prolog')]/critdates"))
        .thenReturn(new WSXMLTextNodeRange[1]);
    
    Mockito.when(wsTextEditorPage.evaluateXPath(XPathConstants.PROLOG_CREATED_ELEMENT))
    .thenReturn(new WSXMLTextNodeRange[1]);

    ditaTopicTextEditor.updateProlog(true);

    Mockito.verify(textDocumentController, Mockito.times(1)).insertXMLFragment(Mockito.anyString(), Mockito.anyString(),
        Mockito.any(RelativeInsertPosition.class));
    Mockito.reset(textDocumentController);
  }
	
	/**
   * <p><b>Description:</b> Check if insertXMLFragment is not call when the prolog contains the creator and critdates</p>
   *
   * @throws Exception
   */
  @Test
  public void testUpdateWhenContainsContent() throws XPathException, TextOperationException {
    Mockito.when(wsTextEditorPage.findElementsByXPath("//*[contains(@class,'topic/prolog')]"))
        .thenReturn(new WSXMLTextNodeRange[1]);

    Mockito.when(wsTextEditorPage.findElementsByXPath("//*[contains(@class,'topic/prolog')]/author")).
    thenReturn(new WSXMLTextNodeRange[1]);
    
    Mockito.when(wsTextEditorPage.evaluateXPath(XPathConstants.PROLOG_AUTHORS_CREATOR))
    .thenReturn(new WSXMLTextNodeRange[1]);

    Mockito.when(wsTextEditorPage.findElementsByXPath("//*[contains(@class,'topic/prolog')]/critdates"))
    .thenReturn(new WSXMLTextNodeRange[1]);

    Mockito.when(wsTextEditorPage.evaluateXPath(XPathConstants.PROLOG_CREATED_ELEMENT))
        .thenReturn(new WSXMLTextNodeRange[1]);

    ditaTopicTextEditor.updateProlog(true);

    Mockito.verify(textDocumentController, Mockito.times(0)).insertXMLFragment(Mockito.anyString(), Mockito.anyString(),
        Mockito.any(RelativeInsertPosition.class));
    Mockito.reset(textDocumentController);
  }

}
