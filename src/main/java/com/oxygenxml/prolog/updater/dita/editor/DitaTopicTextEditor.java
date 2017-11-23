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
	    System.out.println("prolog length: "+ prologs.length);
	    if (prologs.length == 0) {
	      //TODO poate ma raportez si la altceva decat la body
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
	  WSXMLTextNodeRange[] critdateElements = wsTextEditorPage.findElementsByXPath(XPathConstants.PROLOG_CRITDATES);

	  // the critdates doesn't exist
	  if (critdateElements.length == 0) {
	    // add the cridates xml fragment
	    String toAdd = "<critdates>\n" + prologCreator.getDateFragment(isNewDocument) + "\n</critdates>";
	    insertXmlFragment(toAdd, XPathConstants.PROLOG_XPATH+"/author[last()]",
	        RelativeInsertPosition.INSERT_LOCATION_AFTER);

	  } else {
	    // the critdates element exists
	    if (isNewDocument) {
	      // document is new
	      // search for created element.
	      Object[] createdElements = wsTextEditorPage
	          .evaluateXPath(XPathConstants.PROLOG_CRITDATES + "/created");

	      // created element doesn't exist
	      if (createdElements.length == 0) {
	        // add the created xml fragment
	        insertXmlFragment(prologCreator.getPrologAuthorElement(isNewDocument), XPathConstants.PROLOG_CRITDATES,
	            RelativeInsertPosition.INSERT_LOCATION_AS_FIRST_CHILD);
	      } 
	    } else {
	      //it's not a new document
	      //search for revised elements that have local date as modified and have contributor as comment
	      Object[] revisedElements = wsTextEditorPage
	          .findElementsByXPath(XPathConstants.PROLOG_CRITDATES + "/revised[@modified = '"
	              + prologCreator.getLocalDate() + "']/"
	              + "preceding-sibling::node()[2][.='"+prologCreator.getAuthor()+"']"); 

	      //if the element wasn't found
	      if (revisedElements.length == 0) {
	        //add revised xml fragament
	        insertXmlFragment(prologCreator.getRevisedDateFragment(),
	            XPathConstants.PROLOG_CRITDATES, RelativeInsertPosition.INSERT_LOCATION_AS_LAST_CHILD);
	      }
	    }
	  }
	}
	
	/**
	 * Update the author elements of prolog.
	 * @param isNewDocument <code>true</code> if document is new, <code>false</code> otherwise
	 * @throws XPathException 
	 */
	private void updateAuthor( boolean isNewDocument) throws XPathException {
	  // get the author elements
	  Object[] authorElements = wsTextEditorPage.findElementsByXPath(XPathConstants.PROLOG_XPATH + "/author");
	  int authorElementSize = authorElements.length;

	  if (authorElementSize == 0) {
	    // if the author elements doesn't exist
	    // add author xml fragment
	    insertXmlFragment(prologCreator.getPrologAuthorElement(isNewDocument), XPathConstants.PROLOG_XPATH, RelativeInsertPosition.INSERT_LOCATION_AS_FIRST_CHILD);
	  }else {
	    // prolog contains author elements
	    if (isNewDocument) {
	      // the document is new
	      // search for a author with value of attribute type equal with creator
	      Object[] creatorAuthorElements = wsTextEditorPage.evaluateXPath(XPathConstants.PROLOG_XPATH + "/author[@type='creator']");
	      int creatorElementSize = creatorAuthorElements.length;

	      // check if creator author was found
	      if (creatorElementSize == 0) {
	        // there aren't creator author elements in prolog
	        // add the creator author xml fragment
	        insertXmlFragment(prologCreator.getCreatorFragment(), XPathConstants.PROLOG_XPATH,RelativeInsertPosition.INSERT_LOCATION_AS_FIRST_CHILD);
	      }

	    } else {
	      // the document isn't new
	      // search for a contributor author that has local author name as text
	      Object[] contributorAuthorElements = wsTextEditorPage.evaluateXPath(XPathConstants.PROLOG_XPATH + "/author[@type='contributor' and text()= '" + prologCreator.getAuthor() + "']");
	      int contributorElementSize = contributorAuthorElements.length;

	      if (contributorElementSize == 0) {
	        // there aren't contributor author elements in prolog
	        // add the contributor author xml content
	        insertXmlFragment(prologCreator.getContributorFragment(), XPathConstants.PROLOG_XPATH + "/author[last()]", RelativeInsertPosition.INSERT_LOCATION_AFTER);
	      }
	    }
	  }
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
