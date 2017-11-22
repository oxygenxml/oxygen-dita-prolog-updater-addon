package com.oxygenxml.prolog.updater.dita.editor;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;

import com.oxygenxml.prolog.updater.PrologContentCreator;
import com.oxygenxml.prolog.updater.utils.Constants;
import com.oxygenxml.prolog.updater.utils.ThreadUtils;

import ro.sync.ecss.extensions.api.AuthorConstants;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;

/**
 * Edit DITA topic in author mode.
 * 
 * @author cosmin_duna
 */
public class DitaTopicAuthorEditor implements DitaTopicEditor{
	 
	/**
	 * Contains all elements(tags) from prolog.
	 */
	private PrologContentCreator prologContentCreater;
	
	/**
	 * Author document controller
	 */
	private AuthorDocumentController documentController;

	/**
	 * Logger
	 */
	 private static final Logger logger = Logger.getLogger(DitaTopicAuthorEditor.class);
	
	/**
	 * Constructor
	 * @param wsEditorPage workspace page editor.
	 * @param prologContentCreater Contains all elements from prolog.
	 */
	public DitaTopicAuthorEditor(WSAuthorEditorPage wsEditorPage, PrologContentCreator prologContentCreater) {
		this.documentController = wsEditorPage.getDocumentController();
		this.prologContentCreater = prologContentCreater;
	}
	
	/**
	 * Update the prolog in DITA topic document(author mode) according to given flag(isNewDocument)
	 * @param isNewDocument <code>true</code> if document is new, <code>false</code> otherwise
	 * 
	 */
	public void updateProlog(boolean isNewDocument) {
		// Get the root element.
		AuthorElement rootElement = documentController.getAuthorDocumentNode().getRootElement();
		
		if (rootElement != null) {
		  // Get the prolog element.
		  AuthorElement prolog = getElementByClassName(rootElement, Constants.PROLOG_CLASS);
		  if (prolog != null) {
		    try {
		      // Updates the creators and/or contributors of document
          updateAuthorPrologElement(prolog, isNewDocument ? Constants.CREATOR_TYPE : Constants.CONTRIBUTOR_TYPE);
          
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
        String prologXMLFragment = prologContentCreater.getPrologXMLFragment(isNewDocument);
        AuthorElement bodyElement = getElementByClassName(rootElement, Constants.TOPIC_BODY_CLASS);
        if(bodyElement == null) {
          addXmlFragmentSchemaAware(prologXMLFragment, rootElement.getStartOffset() + 1);
        }
        else {
          addXmlFragmentSchemaAware(prologXMLFragment, bodyElement.getStartOffset());
        }
      }
    }
	}

	/**
	 * Search and return first element identified by class value.
	 * 
	 * @param rootElement Document root element.
	 * @return <code>null</code> or the identified element.
	 */
  private AuthorElement getElementByClassName(AuthorElement rootElement, String classValue) {
    AuthorElement toReturn = null;
    List<AuthorNode> contentNodes = rootElement.getContentNodes();
    if (contentNodes != null && !contentNodes.isEmpty()) {
      for (AuthorNode authorNode : contentNodes) {
        if (authorNode.getType() == AuthorElement.NODE_TYPE_ELEMENT) {
          AuthorElement el = (AuthorElement) authorNode;
          AttrValue clazz = el.getAttribute("class");
					if (clazz != null && clazz.getValue() != null && clazz.getValue().contains(classValue)) {
						toReturn = el;
						break;
					}
				}
      }
    }
    return toReturn;
  }

	/**
	 * Update the document adding the names of the authors.
	 * 
	 * @param prolog The prolog element.
	 * @param type The types of the authors: contributor or creator.
	 * 
	 * @throws BadLocationException
	 */
  private void updateAuthorPrologElement(AuthorElement prolog, String type ) throws BadLocationException {
	  
	  boolean creatorFound = false;
	  boolean contributorFound = false;

	  List<AuthorElement> authors = getElementsByClassName(prolog, Constants.PROLOG_AUTHOR_ELEMENT_CLASS);
    final int length = authors.size();
    
    for (int i = 0; i < length && length > 0; i++) {
      AuthorElement el = authors.get(i);
      AttrValue typeAttr = el.getAttribute("type");
      if (typeAttr != null) {
        String typeAttrvalue = typeAttr.getValue();
        if (type.equals(typeAttrvalue)) {
          if (typeAttrvalue.equals(Constants.CONTRIBUTOR_TYPE)) {
            String textContent = el.getTextContent();
            if (prologContentCreater.getAuthor().equals(textContent)) {
              contributorFound = true;
              break;
            }
          } 
          if (type.equals(Constants.CREATOR_TYPE)){
            creatorFound = true;
            break;
          }
        }
      }
    }

    if (!contributorFound && Constants.CONTRIBUTOR_TYPE.equals(type)) {
      // if wasn't found this contributor
      if(length != 0){
        AuthorElement lastAuthor = authors.get(length - 1);
        addXmlFragmentSchemaAware(prologContentCreater.getAuthorContributorFrag(), lastAuthor.getEndOffset() + 1);
      }else{
        addXmlFragmentSchemaAware(prologContentCreater.getAuthorContributorFrag(), prolog.getStartOffset() + 1);
      }
      
    } else if (!creatorFound && Constants.CREATOR_TYPE.equals(type)) {
      String frag = prologContentCreater.getAuthorCreatorFragment();
      addXmlFragmentSchemaAware(frag, prolog.getStartOffset() + 1);
    }
	}
  

  /**
   * Update the critdates element.
   * 
   * @param prolog The prolog author element.
   * @param isNewDocument <code>true</code> if document is new, <code>false</code>otherwise.
   * @throws BadLocationException
   */
  private void updateCritdates(AuthorElement prolog, boolean isNewDocument) throws BadLocationException {
    
    AuthorElement cridates = getElementByClassName(prolog, Constants.TOPIC_CRITDATES_CLASS);
    if (cridates != null) {
      if (isNewDocument) {
        AuthorElement createdElement = getElementByClassName(cridates, Constants.CREATED_DATE_ELEMENT_CLASS);
        // Was not added yet. 
        if (createdElement == null) {
          // Add it.
          addXmlFragmentSchemaAware(prologContentCreater.getCreatedDateXmlFragment(), cridates.getStartOffset() + 1);
        }
        
      } else {
        // it's not a new document
        // add revised element
        addRevisedElement(cridates);
      }
    } else {
      
      List<AuthorElement> elementsByClassName = getElementsByClassName(prolog, Constants.PROLOG_AUTHOR_ELEMENT_CLASS);
      
      // Create an element here.
      String fragment = PrologContentCreator.createDateTag(prologContentCreater.getDateFragment(isNewDocument));
      if(!elementsByClassName.isEmpty()) {
        AuthorElement lastAuthorElement = elementsByClassName.get(elementsByClassName.size()-1);
        addXmlFragmentSchemaAware(fragment, lastAuthorElement.getEndOffset() + 1);
      }else {
        addXmlFragmentSchemaAware(fragment, prolog.getEndOffset());
      }
      
    }
    
  }
	
	/**
	 * Add the revised element if it doesn't exits.
	 * @param critdatesElement critdates element(element that has revised child). 
	 */
	private void addRevisedElement(AuthorElement critdatesElement) throws BadLocationException{
		boolean localDateWithAuthorCommentExist = false;

		// get revised elements
		List<AuthorElement> revisedElements = getElementsByClassName(critdatesElement, Constants.REVISED_DATE_ELEMENT_CLASS);
		int revisedElementSize = revisedElements.size();

		// Iterate over revised elements
		for (int i = 0; i < revisedElementSize; i++) {
			AuthorElement curentRevisedElement = revisedElements.get(i);

			// check the modified value.
			AttrValue currentModifiedDate = curentRevisedElement.getAttribute(Constants.MODIFIED_ATTRIBUTE);
			if (currentModifiedDate != null && prologContentCreater.getLocalDate().equals(currentModifiedDate.getRawValue())) {
				// check the comment
				int currentElemetStartOffSet = curentRevisedElement.getStartOffset();
				AuthorNode anteriorNode;
				anteriorNode = documentController.getNodeAtOffset(currentElemetStartOffSet - 1);

				if (anteriorNode.getType() == AuthorNode.NODE_TYPE_COMMENT
						&& prologContentCreater.getAuthor().equals(anteriorNode.getTextContent())) {
					localDateWithAuthorCommentExist = true;
					break;
				}
			}
		}
		if (!localDateWithAuthorCommentExist) {
			if (revisedElementSize != 0) {
				addXmlFragmentSchemaAware(prologContentCreater.getResivedModifiedXmlFragment(),
						revisedElements.get(revisedElementSize -1 ).getEndOffset()+1);
			} else {
				addXmlFragmentSchemaAware(prologContentCreater.getResivedModifiedXmlFragment(), critdatesElement.getEndOffset());
			}
		}
	}
	
	/**
	 * Inserts an element schema aware.
	 * 
	 * @param xmlFragment The xml fragment.
	 * @param offset The offset.
	 */
	private void addXmlFragmentSchemaAware(final String xmlFragment, final int offset){
	    ThreadUtils.invokeSynchronously(new Runnable() {
        public void run() {
          try {
            documentController.insertXMLFragmentSchemaAware(xmlFragment, offset);
          } catch (AuthorOperationException e) {
            logger.debug(e.getMessage(), e);
          }
        }
      });
	}
	
	/**
	 * Create a AWT thread and insert the given fragment,
	 * 
	 * @param xmlFragment
	 *          The XML fragment.
	 * @param relativeTo
	 *          The node to insert fragment relative to.
	 * @param relativPosition
	 *          The position relative to the node. Can be one of the constants:
	 *          {@link AuthorConstants#POSITION_BEFORE}, {@link AuthorConstants#POSITION_AFTER},
	 *          {@link AuthorConstants#POSITION_INSIDE_FIRST} or
	 *          {@link AuthorConstants#POSITION_INSIDE_LAST}.
	 */
	private void addXmlFragment(final String xmlFragment, final AuthorNode relativeTo, final String relativPosition){
	  ThreadUtils.invokeSynchronously(new Runnable() {
	    public void run() {
	      try {
	        //insert create date .
	        documentController.insertXMLFragment(xmlFragment, relativeTo, relativPosition);
	      } catch (AuthorOperationException e) {
	        logger.debug(e.getMessage(), e);
	      }
	    }
	  });
	}

  /**
   * Search and return a list of elements with a specific class.
   * 
   * @param rootElement The node where the search should start.
   * @return A list with elements with a specific class. If no element is found,
   * the method returns an empty list.
   */
  private List<AuthorElement> getElementsByClassName(AuthorElement rootElement, String classValue) {
    List<AuthorElement> toReturn = new ArrayList<AuthorElement>();
    List<AuthorNode> contentNodes = rootElement.getContentNodes();
    if (contentNodes != null && !contentNodes.isEmpty()) {
      for (AuthorNode authorNode : contentNodes) {
        if (authorNode.getType() == AuthorElement.NODE_TYPE_ELEMENT) {
          AuthorElement el = (AuthorElement) authorNode;
          AttrValue clazz = el.getAttribute("class");
					if (clazz != null && clazz.getValue() != null && clazz.getValue().contains(classValue)) {
						toReturn.add(el);
					}         
        }
      }
    }
    return toReturn;
  }
}
