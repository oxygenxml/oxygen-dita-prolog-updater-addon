package com.oxygenxml.prolog.updater.dita.editor;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;

import com.oxygenxml.prolog.updater.PrologContentCreator;
import com.oxygenxml.prolog.updater.utils.ThreadUtils;
import com.oxygenxml.prolog.updater.utils.XMLStringFragmentUtils;
import com.oxygenxml.prolog.updater.utils.XmlElementsConstants;

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
		String type = isNewDocument ? XmlElementsConstants.CREATOR_TYPE : XmlElementsConstants.CONTRIBUTOR_TYPE;
		
		if (rootElement != null) {
		  // Get the prolog element.
		  AuthorElement prolog = getElementByClassName(rootElement, XmlElementsConstants.PROLOG_CLASS);
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
        insertPrologElement(rootElement, prologContentCreater.getPrologXMLFragment(isNewDocument));
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
    AuthorElement bodyElement = getElementByClassName(rootElement, XmlElementsConstants.TOPIC_BODY_CLASS);
    if(bodyElement == null) {
      insertFragmentSchemaAware(prologXML, rootElement.getStartOffset() + 1);
    }else {
      insertFragmentSchemaAware(prologXML, bodyElement.getStartOffset());
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
   * Update the critdates element.
   * 
   * @param prolog The prolog author element.
   * @param isNewDocument <code>true</code> if document is new, <code>false</code>otherwise.
   * @throws BadLocationException
   */
  private void updateCritdates(AuthorElement prolog, boolean isNewDocument) throws BadLocationException {
    
    AuthorElement cridates = getElementByClassName(prolog, XmlElementsConstants.TOPIC_CRITDATES_CLASS);
    if (cridates != null) {
      if (isNewDocument) {
        AuthorElement createdElement = getElementByClassName(cridates, XmlElementsConstants.CREATED_DATE_ELEMENT_CLASS);
        // Was not added yet. 
        if (createdElement == null) {
          // Add it.
          insertFragmentSchemaAware(prologContentCreater.getCreatedDateFragment(), cridates.getStartOffset() + 1);
        }
      } else {
        // it's not a new document
        // add revised element
        addRevisedElement(cridates);
      }
    } else {
      List<AuthorElement> elementsByClassName = getElementsByClassName(prolog, XmlElementsConstants.PROLOG_AUTHOR_ELEMENT_CLASS);
      
      // Create an element here.
      String fragment = XMLStringFragmentUtils.createDateTag(prologContentCreater.getDateFragment(isNewDocument));
      if(!elementsByClassName.isEmpty()) {
        AuthorElement lastAuthorElement = elementsByClassName.get(elementsByClassName.size()-1);
        insertFragmentSchemaAware(fragment, lastAuthorElement.getEndOffset() + 1);
      }else {
        insertFragmentSchemaAware(fragment, prolog.getEndOffset());
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
		List<AuthorElement> revisedElements = getElementsByClassName(critdatesElement, XmlElementsConstants.REVISED_DATE_ELEMENT_CLASS);
		int revisedElementSize = revisedElements.size();

		// Iterate over revised elements
		for (int i = 0; i < revisedElementSize; i++) {
			AuthorElement curentRevisedElement = revisedElements.get(i);

			// check the modified value.
			AttrValue currentModifiedDate = curentRevisedElement.getAttribute(XmlElementsConstants.MODIFIED_ATTRIBUTE);
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
				insertFragmentSchemaAware(prologContentCreater.getRevisedDateFragment(),
						revisedElements.get(revisedElementSize -1 ).getEndOffset()+1);
			} else {
				insertFragmentSchemaAware(prologContentCreater.getRevisedDateFragment(), critdatesElement.getEndOffset());
			}
		}
	}
	
	/**
	 * Inserts an element schema aware.
	 * 
	 * @param xmlFragment The xml fragment.
	 * @param offset The offset.
	 */
	private void insertFragmentSchemaAware(final String xmlFragment, final int offset){
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

  /**
   * Update the document adding the names of the authors.
   * 
   * @param prolog The prolog element.
   * @param type The types of the authors: contributor or creator.
   * 
   * @throws BadLocationException
   */
  private void updateAuthorElements(AuthorElement prolog, String type ) throws BadLocationException {
    List<AuthorElement> authors = getElementsByClassName(prolog, XmlElementsConstants.PROLOG_AUTHOR_ELEMENT_CLASS);
    final int length = authors.size();
    
    // Search for author with given type.
    boolean foundAuthor = searchAuthor(authors, type);
   
    if (!foundAuthor && XmlElementsConstants.CONTRIBUTOR_TYPE.equals(type)) {
      // if wasn't found this contributor
      if(length != 0){
        AuthorElement lastAuthor = authors.get(length - 1);
        insertFragmentSchemaAware(prologContentCreater.getContributorFragment(), lastAuthor.getEndOffset() + 1);
      }else{
        insertFragmentSchemaAware(prologContentCreater.getContributorFragment(), prolog.getStartOffset() + 1);
      }
    } else if (!foundAuthor && XmlElementsConstants.CREATOR_TYPE.equals(type)) {
      String frag = prologContentCreater.getCreatorFragment();
      insertFragmentSchemaAware(frag, prolog.getStartOffset() + 1);
    }
  }
  
  /**
   * Search for author according to given type.
   * 
   * @param authors The list with authors.
   * @param type The searched author type ( {@link XmlElementsConstants#CREATOR_TYPE} or {@link XmlElementsConstants#CONTRIBUTOR_TYPE})
   * @return <code>true</code> if was found a author with given type, <code>false</code> otherwise.
   * @throws BadLocationException
   */
  private boolean searchAuthor(List<AuthorElement>authors, String type ) throws BadLocationException {
    boolean foundAuthor = false;
    
    int length = authors.size();
    // Iterate over authors.
    for (int i = 0; i < length ; i++) {
      AuthorElement el = authors.get(i);
      // Get the type's value,
      AttrValue typeAttr = el.getAttribute("type");
      if (typeAttr != null) {
        String typeAttrValue = typeAttr.getValue();
        if (type.equals(typeAttrValue)) {
          // Was found a creator.
          foundAuthor = typeAttrValue.equals(XmlElementsConstants.CREATOR_TYPE);
          
          if (typeAttrValue.equals(XmlElementsConstants.CONTRIBUTOR_TYPE)) {
            // Check the content of contributor element.
            String textContent = el.getTextContent();
            // Was found a valid contributor.
            foundAuthor = prologContentCreater.getAuthor().equals(textContent);
          } 
          
          if (foundAuthor) {
            break;
          }
        }
      }
    }
    return foundAuthor;
  }
}
