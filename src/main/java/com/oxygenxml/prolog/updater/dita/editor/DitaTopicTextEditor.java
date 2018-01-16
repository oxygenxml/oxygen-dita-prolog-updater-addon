package com.oxygenxml.prolog.updater.dita.editor;

import org.apache.log4j.Logger;

import com.oxygenxml.prolog.updater.prolog.content.PrologContentCreator;
import com.oxygenxml.prolog.updater.utils.TextPageDocumentUtil;
import com.oxygenxml.prolog.updater.utils.XMLFragmentUtils;
import com.oxygenxml.prolog.updater.utils.XPathConstants;

import ro.sync.exml.editor.xmleditor.operations.context.RelativeInsertPosition;
import ro.sync.exml.workspace.api.editor.page.text.xml.TextDocumentController;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextNodeRange;
import ro.sync.exml.workspace.api.editor.page.text.xml.XPathException;

/**
 * Edit DITA topic in text mode.
 * 
 * @author cosmin_duna
 *
 */
public class DitaTopicTextEditor implements DitaEditor {

	/**
	 * Logger
	 */
	private static final Logger logger = Logger.getLogger(DitaTopicTextEditor.class);
	
	/**
	 * Contains all elements from prolog.
	 */
	private PrologContentCreator prologCreator;
	/**
	 * Text document controller
	 */
	TextDocumentController documentController;
	
	/**
	 * The page from Workspace text editor.
	 */
	private WSXMLTextEditorPage wsTextEditorPage;

	/**
	 * The type of the document( {@link DocumentType#TOPIC}, {@link DocumentType#MAP} or {@link DocumentType#BOOKMAP}  ).. 
	 */
	private DocumentType documentType = DocumentType.TOPIC;
	

	/**
	 * Constructor
	 * @param wsEditorPage workspace page editor.
	 * @param prologCreator Content of prolog.
	 */
	public DitaTopicTextEditor(WSXMLTextEditorPage wsEditorPage, PrologContentCreator prologCreator) {
		this.wsTextEditorPage = wsEditorPage;
		this.documentController = wsTextEditorPage.getDocumentController();
		
		try {
      WSXMLTextNodeRange[] mapRoot = wsTextEditorPage.findElementsByXPath(XPathConstants.ROOT_MAP_XPATH);
      if (mapRoot.length != 0) {
        documentType = DocumentType.MAP;
      }
      WSXMLTextNodeRange[] bookmapRoot = wsTextEditorPage.findElementsByXPath(XPathConstants.ROOT_BOOKMAP_XPATH);
      if (bookmapRoot.length != 0) {
        documentType = DocumentType.BOOKMAP;
      }
		} catch (XPathException e) {
      logger.debug(e, e.getCause());
    }
		
		this.prologCreator = prologCreator;
	}
	
	/**
	 * Update the prolog element in text page.
	 * 
	 * @param isNewDocument <code>true</code> if document is new
	 */
	public void updateProlog(boolean isNewDocument) {
		try {
	    // get the prolog element
	    WSXMLTextNodeRange[] prologs = wsTextEditorPage.findElementsByXPath(XPathConstants.getPrologXpath(documentType));
	    // The document doesn't have a prolog element
	    if (prologs.length == 0) {
	    	// the prolog element doesn't exist
	      addProlog(isNewDocument);
	    } else {
	      // the prolog element exists.
	    	// update the author element.
	      updateAuthor(isNewDocument);
	      
	      //update the critdates element
	      updateCritdates(isNewDocument);
	    }
		} catch (XPathException e) {
	    logger.debug(e.getMessage(), e);
	  }
	}

	/**
	 * Add the prolog element.
	 * @param isNewDocument <code>true</code> if document is new
	 *
	 */
	private void addProlog(boolean isNewDocument) {
		// Search for a possible prolog xpath.
		String xp = TextPageDocumentUtil.findPrologXPath(wsTextEditorPage, documentType);
		if (xp != null) {
		  TextPageDocumentUtil.insertXmlFragment(
		  		wsTextEditorPage, 
		      prologCreator.getPrologFragment(isNewDocument, documentType), 
		      xp,
		      RelativeInsertPosition.INSERT_LOCATION_AFTER);
		}else {
		  TextPageDocumentUtil.insertXmlFragment(
		      wsTextEditorPage, 
		      prologCreator.getPrologFragment(isNewDocument, documentType), 
		      XPathConstants.getRootXpath(documentType),
		      RelativeInsertPosition.INSERT_LOCATION_AS_FIRST_CHILD);
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
		WSXMLTextNodeRange[] critdateElements = wsTextEditorPage.findElementsByXPath(XPathConstants.getCritdatesXpath(documentType));

		if (critdateElements.length == 0) {
			// The critdates doesn't exist.
	    // Add the cridates xml fragment
	    String dateFragment = prologCreator.getDateFragment(isNewDocument, documentType);
	    String toAdd = XMLFragmentUtils.createCritdateTag(dateFragment);
	    TextPageDocumentUtil.insertXmlFragment(wsTextEditorPage, toAdd, XPathConstants.getLastAuthorXpath(documentType),
	        RelativeInsertPosition.INSERT_LOCATION_AFTER);

	  } else {
	  	// The critdates element exists.
	  	// Edit the element.
	    editCritdates(isNewDocument);
	  }
	}

	/**
	 * Edit the existing critdates element.
	 * @param isNewDocument <code>true</code> if document is new, <code>false</code> otherwise
	 * @throws XPathException
	 */
	private void editCritdates(boolean isNewDocument) throws XPathException {
		// the critdates element exists
		if (isNewDocument) {
		  // document is new
		  // search for created element.
		  Object[] createdElements = wsTextEditorPage
		      .evaluateXPath(XPathConstants.getCreatedXpath(documentType));

		  // created element doesn't exist
		  if (createdElements.length == 0) {
		    // add the created xml fragment
		    TextPageDocumentUtil.insertXmlFragment(wsTextEditorPage, prologCreator.getPrologAuthorElement(isNewDocument, documentType),
		        XPathConstants.getCritdatesXpath(documentType),
		        RelativeInsertPosition.INSERT_LOCATION_AS_FIRST_CHILD);
		  } 
		} else {
		  //it's not a new document
		  //search for revised elements that have local date as modified and have contributor as comment
		  Object[] revisedElements = wsTextEditorPage
		      .findElementsByXPath(XPathConstants.getCritdatesXpath(documentType) + "/revised[@modified = '"
		          + prologCreator.getLocalDate() + "']/"
		          + "preceding-sibling::node()[2][.='"+prologCreator.getAuthor()+"']"); 

		  //if the element wasn't found
		  if (revisedElements.length == 0) {
		    //add revised xml fragament
		    TextPageDocumentUtil.insertXmlFragment(wsTextEditorPage, prologCreator.getRevisedDateFragment(documentType),
		        XPathConstants.getCritdatesXpath(documentType), RelativeInsertPosition.INSERT_LOCATION_AS_LAST_CHILD);
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
		Object[] authorElements = wsTextEditorPage.findElementsByXPath(XPathConstants.getAuthorXpath(documentType));
		int authorElementSize = authorElements.length;

		if (authorElementSize == 0) {
	    // if the author elements doesn't exist
	    // add author xml fragment
	    TextPageDocumentUtil.insertXmlFragment(wsTextEditorPage, prologCreator.getPrologAuthorElement(isNewDocument, documentType), XPathConstants.getPrologXpath(documentType), RelativeInsertPosition.INSERT_LOCATION_AS_FIRST_CHILD);
	  } else {
	  	// The author element exists.
	  	// Edit the author element.
	    editAuthor(isNewDocument);
	  }
	}

	/**
	 * Edit the author elements of prolog element.
	 * @param isNewDocument <code>true</code> if document is new, <code>false</code> otherwise
	 * @throws XPathException 
	 */
	private void editAuthor(boolean isNewDocument) throws XPathException {
		// prolog contains author elements
		if (isNewDocument) {
		  // the document is new
		  // search for a author with value of attribute type equal with creator
		  Object[] creatorAuthorElements = wsTextEditorPage.evaluateXPath(XPathConstants.getAuthorCreatorXpath(documentType));
		  int creatorElementSize = creatorAuthorElements.length;

		  // check if creator author was found
		  if (creatorElementSize == 0) {
		    // there aren't creator author elements in prolog
		    // add the creator author xml fragment
		    TextPageDocumentUtil.insertXmlFragment(wsTextEditorPage, prologCreator.getCreatorFragment(documentType), 
		        XPathConstants.getPrologXpath(documentType), RelativeInsertPosition.INSERT_LOCATION_AS_FIRST_CHILD);
		  }

		} else {
		  // the document isn't new
		  // search for a contributor author that has local author name as text
		  Object[] contributorAuthorElements = wsTextEditorPage.evaluateXPath(XPathConstants.getPrologXpath(documentType) + "/author[@type='contributor' and text()= '" + prologCreator.getAuthor() + "']");
		  int contributorElementSize = contributorAuthorElements.length;

		  if (contributorElementSize == 0) {
		    // there aren't contributor author elements in prolog
		    // add the contributor author xml content
		    TextPageDocumentUtil.insertXmlFragment(wsTextEditorPage, prologCreator.getContributorFragment(documentType),
		        XPathConstants.getLastAuthorXpath(documentType), RelativeInsertPosition.INSERT_LOCATION_AFTER);
		  }
		}
	}

	
}
