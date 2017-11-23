package com.oxygenxml.prolog.updater.dita.editor;

import org.apache.log4j.Logger;

import com.oxygenxml.prolog.updater.PrologContentCreator;
import com.oxygenxml.prolog.updater.utils.ThreadUtils;
import com.oxygenxml.prolog.updater.utils.XMLFragmentUtils;
import com.oxygenxml.prolog.updater.utils.XPathConstants;

import ro.sync.exml.editor.xmleditor.operations.context.RelativeInsertPosition;
import ro.sync.exml.workspace.api.editor.page.text.xml.TextDocumentController;
import ro.sync.exml.workspace.api.editor.page.text.xml.TextOperationException;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextNodeRange;
import ro.sync.exml.workspace.api.editor.page.text.xml.XPathException;

/**
 * Edit DITA topic in text mode.
 * 
 * @author cosmin_duna
 *
 */
public class DitaTopicTextEditor implements DitaTopicEditor {
	
	/**
	 * Contains all elements from prolog.
	 */
	private PrologContentCreator prologCreator;
	/**
	 * Text document controller
	 */
	TextDocumentController documentController;
	
	/**
	 * WsTextEditorPage
	 */
	private WSXMLTextEditorPage wsTextEditorPage;

	/**
	 * Logger
	 */
	 private static final Logger logger = Logger.getLogger(DitaTopicTextEditor.class);

	/**
	 * Constructor
	 * @param wsEditorPage workspace page editor.
	 * @param prologCreator Content of prolog.
	 */
	public DitaTopicTextEditor(WSXMLTextEditorPage wsEditorPage) {
		this.wsTextEditorPage = wsEditorPage;
		this.documentController = wsTextEditorPage.getDocumentController();
		prologCreator = PrologContentCreator.getInstance();
	}
	
	/**
	 * Update the prolog element text page.
	 * 
	 * @param isNewDocument <code>true</code> if document is new
	 */
	public void updateProlog(boolean isNewDocument) {
	  try {
	    // get the prolog element
	    WSXMLTextNodeRange[] prologs = wsTextEditorPage.findElementsByXPath(XPathConstants.PROLOG_XPATH);
	    // The document doesn't has a prolog element
	    if (prologs.length == 0) {
	      // add the prolog xml fragment
	      insertXmlFragment(
	          prologCreator.getPrologFragment(isNewDocument), 
	          XPathConstants.TOPIC_BODY,
	          RelativeInsertPosition.INSERT_LOCATION_BEFORE);
	    } else {
	      // the prolog element exists
	      updateAuthor(isNewDocument);
	      
	      //update the critdates element
	      updateCritdates(isNewDocument);
	    }
	  } catch (XPathException e) {
	    logger.debug(e.getMessage(), e);
	  }
	}
	
	/**
	 * Update the critdates element of prolog.
	 * 
	 * @param isNewDocument <code>true</code> if document is new, <code>false</code> otherwise
	 * @throws XPathException 
	 */
	private void updateCritdates(boolean isNewDocument) throws XPathException {
	  // get the critdates element
	  WSXMLTextNodeRange[] critdates = wsTextEditorPage.findElementsByXPath(XPathConstants.PROLOG_CRITDATES);

	  // cridates xml fragment
	  String fragment = XMLFragmentUtils.createDateTag(prologCreator.getDateFragment(isNewDocument));
	  String xPath = XPathConstants.LAST_PROLOG_AUTHOR;
	  RelativeInsertPosition insertPosition = RelativeInsertPosition.INSERT_LOCATION_AFTER;

	  if (critdates.length > 0) {
	    if (isNewDocument) {
	      // search for "created" node.
	      Object[] createdElements = wsTextEditorPage.evaluateXPath(XPathConstants.PROLOG_CREATED_ELEMENT);
	      if (createdElements.length == 0) {
	        fragment = prologCreator.getPrologAuthorElement(isNewDocument);
	        xPath = XPathConstants.PROLOG_CRITDATES;
	        insertPosition = RelativeInsertPosition.INSERT_LOCATION_AS_FIRST_CHILD;
	      } 
	    } else {
	      //it's not a new document
	      //search for revised elements that have local date as modified and have contributor as comment
	      Object[] revisedElements = wsTextEditorPage.findElementsByXPath(
	          XPathConstants.PROLOG_CRITDATES + "/revised[@modified = '"+ prologCreator.getLocalDate() + "']/"
	              + "preceding-sibling::node()[2][.='"+prologCreator.getAuthor()+"']"); 

	      //if the element wasn't found
	      if (revisedElements.length == 0) {
	        //add revised xml fragament
	        fragment = prologCreator.getRevisedDateFragment();
	        xPath = XPathConstants.PROLOG_CRITDATES;
	        insertPosition = RelativeInsertPosition.INSERT_LOCATION_AS_LAST_CHILD;
	      }
	    }
	  }
	  
	  // Insert the fragment.
	  insertXmlFragment(fragment, xPath, insertPosition);
	}
	
	/**
	 * Update the author elements of prolog.
	 * @param isNewDocument <code>true</code> if document is new, <code>false</code> otherwise
	 * @throws XPathException 
	 */
	private void updateAuthor( boolean isNewDocument) throws XPathException {
	  String fragment = prologCreator.getPrologAuthorElement(isNewDocument);
	  String xPath = XPathConstants.PROLOG_XPATH;
	  RelativeInsertPosition insertPosition = RelativeInsertPosition.INSERT_LOCATION_AS_FIRST_CHILD;

	  // get the author elements
	  Object[] authorElements = wsTextEditorPage.findElementsByXPath(XPathConstants.PROLOG_AUTHORS);
	  if (authorElements.length > 0) {
	    // prolog contains author elements
	    if (isNewDocument) {
	      // search for a author with value of attribute type equal with creator
	      Object[] creators = wsTextEditorPage.evaluateXPath(XPathConstants.PROLOG_AUTHORS_CREATOR);
	      if (creators.length == 0) {
	        // No creator. Prepare one.
	        fragment = prologCreator.getCreatorFragment();
	        insertPosition = RelativeInsertPosition.INSERT_LOCATION_AS_FIRST_CHILD;
	      }
	    } else {
	      // the document isn't new
	      // search for a contributor author that has local author name as text
	      String xp = XPathConstants.PROLOG_XPATH + "/author[@type='contributor' and text()= '" + prologCreator.getAuthor() + "']";
	      Object[] contributors = wsTextEditorPage.evaluateXPath(xp);
	      if (contributors.length == 0) {
	        // No contributors. Add one.
	        fragment = prologCreator.getContributorFragment();
	        xPath = XPathConstants.LAST_PROLOG_AUTHOR;
	        insertPosition = RelativeInsertPosition.INSERT_LOCATION_AFTER;
	      }
	    }
	  }

	  // Insert the fragment
	  insertXmlFragment(fragment, xPath, insertPosition);
	}

	/**
	 * Create a AWT thread and insert the given fragment,
	 * 
	 * @param xmlFragment
	 *          The XML fragment.
	 * @param xPath
	 *          The xPath to insert fragment relative to.
	 * @param position
	 *          The position relative to the node. Can be one of the constants:
	 *          {@link RelativeInsertPosition#INSERT_LOCATION_AFTER}, {@link RelativeInsertPosition#INSERT_LOCATION_AS_FIRST_CHILD},
	 *          {@link RelativeInsertPosition#INSERT_LOCATION_AS_LAST_CHILD} or
	 *          {@link RelativeInsertPosition#INSERT_LOCATION_BEFORE}.
	 */
	private void insertXmlFragment(final String xmlFragment, final String xPath, final RelativeInsertPosition position) {
	  if (xmlFragment != null && xPath != null && position != null) {
	    ThreadUtils.invokeSynchronously(new Runnable() {
	      public void run() {
	        try {
	          documentController.insertXMLFragment(xmlFragment, xPath, position);
	        } catch (TextOperationException e) {
	          logger.debug(e.getMessage(), e);
	        }
	      }
	    });
	  }
	}
}
