package com.oxygenxml.prolog.updater.dita.editor;

import java.util.List;

import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;

import com.oxygenxml.prolog.updater.prolog.content.PrologContentCreator;
import com.oxygenxml.prolog.updater.utils.AuthorPageDocumentUtil;
import com.oxygenxml.prolog.updater.utils.XMLFragmentUtils;
import com.oxygenxml.prolog.updater.utils.XmlElementsConstants;

import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;

/**
 * Edit DITA topic in author mode.
 * 
 * @author cosmin_duna
 */
public class DitaTopicAuthorEditor implements DitaEditor{
	 
	/**
	 * Contains all elements(tags) from prolog.
	 */
	private PrologContentCreator prologCreator;
	
	/**
	 * Author document controller
	 */
	private AuthorDocumentController documentController;

	/**
	 * <code>true</code> for DITA topic, <code>false</code> for DITA map.
	 */
	private boolean isTopic = true;
	
	/**
	 * Logger
	 */
	 private static final Logger logger = Logger.getLogger(DitaTopicAuthorEditor.class);
	
	/**
	 * Constructor
	 * @param wsEditorPage workspace page editor.
	 * @param prologContentCreater Contains all elements from prolog.
	 */
	public DitaTopicAuthorEditor(WSAuthorEditorPage wsEditorPage, PrologContentCreator prologContentCreator) {
		this.documentController = wsEditorPage.getDocumentController();
		
		AuthorElement rootElement = documentController.getAuthorDocumentNode().getRootElement();
		AttrValue classValue = rootElement.getAttribute(XmlElementsConstants.CLASS);
		if (classValue != null && classValue.getValue().contains(" map/map ")) {
			isTopic = false;
		}
		
		prologCreator = prologContentCreator;
	}
	
	/**
	 * Update the prolog in DITA topic document(author mode) according to given flag(isNewDocument)
	 * @param isNewDocument <code>true</code> if document is new, <code>false</code> otherwise
	 * 
	 */
	public void updateProlog(boolean isNewDocument) {
		// Get the root element.
		AuthorElement rootElement = documentController.getAuthorDocumentNode().getRootElement();
		String type = isNewDocument ? XmlElementsConstants.CREATOR_TYPE : XmlElementsConstants.CONTRIBUTOR_TYPE;
		
		if (rootElement != null) {
		  // Get the prolog element.
		  AuthorElement prolog = AuthorPageDocumentUtil.findElementByClass(rootElement, XmlElementsConstants.getPrologClass(isTopic));
		  if (prolog != null) {
		    try {
		      // Updates the creators and/or contributors of document
          updateAuthorElements(prolog, type);
          
          // Update the critdates element .
          updateCritdates(prolog, isNewDocument);
		    } catch (BadLocationException e) {
		      if (logger.isDebugEnabled()) {
		        logger.debug(e.getMessage(), e);
          }
		    }
      } else {
        //The prolog element wasn't found.
        // Add it
        insertPrologElement(rootElement, prologCreator.getPrologFragment(isNewDocument, isTopic));
      }
    }
	}

	/**
	 * Generates and inserts a prolog element.
	 * 
	 * @param rootElement The root of the document.
	 * @param prologXML The prolog element fragment to be inserted.
	 */
  private void insertPrologElement(AuthorElement rootElement, String prologXML) {
    AuthorElement bodyElement = AuthorPageDocumentUtil.findElementByClass(rootElement, XmlElementsConstants.TOPIC_BODY_CLASS);
    int offset = rootElement.getStartOffset() + 1;
    if (bodyElement != null) {
      offset = bodyElement.getStartOffset();
    }
    // Insert Fragment
    AuthorPageDocumentUtil.insertFragmentSchemaAware(documentController, prologXML, offset);
  }

  /**
   * Update the critdates element.
   * 
   * @param prolog The prolog author element.
   * @param isNewDocument <code>true</code> if document is new, <code>false</code>otherwise.
   * @throws BadLocationException
   */
  private void updateCritdates(AuthorElement prolog, boolean isNewDocument) throws BadLocationException {
    // Where to insert
    AuthorElement cridates = AuthorPageDocumentUtil.findElementByClass(prolog, XmlElementsConstants.TOPIC_CRITDATES_CLASS);
    int offset = -1;
    String fragment = null;
    if (cridates != null) {
      if (isNewDocument) {
        AuthorElement createdElement = AuthorPageDocumentUtil.findElementByClass(cridates, XmlElementsConstants.CREATED_DATE_ELEMENT_CLASS);
        // Was not added yet. 
        if (createdElement == null) {
          // Add it.
          offset = cridates.getStartOffset() + 1;
          fragment = prologCreator.getCreatedDateFragment(isTopic);
        }
      } else {
        // it's not a new document
        // add revised element
        addRevisedElement(cridates);
      }
    } else {
      List<AuthorElement> authors = AuthorPageDocumentUtil.findElementsByClass(prolog, XmlElementsConstants.PROLOG_AUTHOR_ELEMENT_CLASS);
      // Create an element here.
      fragment = XMLFragmentUtils.createCritdateTag(prologCreator.getDateFragment(isNewDocument, isTopic));
      if(authors.isEmpty()) {
        offset = prolog.getEndOffset();
      } else {
        AuthorElement lastAuthorElement = authors.get(authors.size()-1);
        offset = lastAuthorElement.getEndOffset() + 1;
      }
    }
    
    AuthorPageDocumentUtil.insertFragmentSchemaAware(documentController, fragment, offset);
  }
	
	/**
	 * Add the revised element if it doesn't exits.
	 * @param critdatesElement critdates element(element that has revised child). <code>Not null</code>
	 */
	private void addRevisedElement(AuthorElement critdatesElement) throws BadLocationException{
		boolean localDateWithAuthorCommentExist = false;

		// get revised elements
		List<AuthorElement> revisedElements = AuthorPageDocumentUtil.findElementsByClass(critdatesElement, XmlElementsConstants.REVISED_DATE_ELEMENT_CLASS);
		int revisedElementSize = revisedElements.size();

		// Iterate over revised elements
		for (AuthorElement current : revisedElements) {
		  // check the modified value.
		  AttrValue modifiedDate = current.getAttribute(XmlElementsConstants.MODIFIED_ATTRIBUTE);
		  if (modifiedDate != null && prologCreator.getLocalDate().equals(modifiedDate.getRawValue())) {
		    // Get the previous node
		    AuthorNode previousSibling = documentController.getNodeAtOffset(current.getStartOffset() - 1);
		    //and check if it's a comment.
		    if (previousSibling.getType() == AuthorNode.NODE_TYPE_COMMENT
		        && prologCreator.getAuthor().equals(previousSibling.getTextContent())) {
		      localDateWithAuthorCommentExist = true;
		      break;
		    }
		  }
		}
		if (!localDateWithAuthorCommentExist) {
		  int offset = critdatesElement.getEndOffset();
		  String fragment = prologCreator.getRevisedDateFragment(isTopic);
			if (revisedElementSize != 0) {
        offset = revisedElements.get(revisedElementSize -1).getEndOffset()+1;
			}
			// Now insert it.
			AuthorPageDocumentUtil.insertFragmentSchemaAware(documentController, fragment, offset);
		}
	}

	/**
   * Update the document adding the names of the authors.
   * 
   * @param prolog The prolog element.
   * @param type The types of the authors: contributor or creator.
   * 
   * @throws BadLocationException
   */
  private void updateAuthorElements(AuthorElement prolog, String type ) throws BadLocationException {
    List<AuthorElement> authors = AuthorPageDocumentUtil.findElementsByClass(prolog, XmlElementsConstants.PROLOG_AUTHOR_ELEMENT_CLASS);
    final int length = authors.size();
    
    // Search for author with given type.
    boolean hasAuthor = AuthorPageDocumentUtil.hasAuthor(authors, type, prologCreator.getAuthor());
    
    String fragment = null;
    int offset = prolog.getStartOffset() + 1;
    
    if (!hasAuthor && XmlElementsConstants.CONTRIBUTOR_TYPE.equals(type)) {
      // if wasn't found this contributor
      fragment = prologCreator.getContributorFragment(isTopic);
      if(length > 0){
        AuthorElement lastAuthor = authors.get(length - 1);
        offset = lastAuthor.getEndOffset() + 1;
      }
    } else if (!hasAuthor && XmlElementsConstants.CREATOR_TYPE.equals(type)) {
      fragment = prologCreator.getCreatorFragment(isTopic);
    }
    
    AuthorPageDocumentUtil.insertFragmentSchemaAware(documentController, fragment, offset);
  }
}
