package com.oxygenxml.prolog.updater.dita.editor;

import java.util.List;

import javax.swing.text.BadLocationException;
import javax.swing.text.Position;

import org.apache.log4j.Logger;

import com.oxygenxml.prolog.updater.prolog.content.PrologContentCreator;
import com.oxygenxml.prolog.updater.utils.AuthorPageDocumentUtil;
import com.oxygenxml.prolog.updater.utils.ElementXPathUtils;
import com.oxygenxml.prolog.updater.utils.XMLFragmentUtils;
import com.oxygenxml.prolog.updater.utils.XmlElementsConstants;
import com.oxygenxml.prolog.updater.utils.XmlElementsUtils;

import ro.sync.ecss.extensions.api.AuthorConstants;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;
import ro.sync.exml.workspace.api.editor.page.ditamap.WSDITAMapEditorPage;

/**
 * Edit DITA topic in author mode.
 * 
 * @author cosmin_duna
 */
public class DitaTopicAuthorEditor implements DitaEditor {

	/**
	 * Logger
	 */
	private static final Logger logger = Logger.getLogger(DitaTopicAuthorEditor.class);

	/**
	 * Contains all elements(tags) from prolog.
	 */
	private PrologContentCreator prologCreator;

	/**
	 * Author document controller
	 */
	private AuthorDocumentController documentController;

	/**
	 * The document type( {@link DocumentType#TOPIC}, {@link DocumentType#MAP},
	 *  {@link DocumentType#BOOKMAP} or {@link DocumentType#SUBJECT_SCHEME}
	 */
	private DocumentType documentType;

	/**
	 * The page from WsEditor.
	 */
	private WSEditorPage page;

	/**
	 * Constructor
	 * 
	 * @param page
	 *          The page from WSEditor.
	 * @param prologContentCreater
	 *          Contains all elements from prolog.
	 */
	public DitaTopicAuthorEditor(WSEditorPage page, PrologContentCreator prologContentCreator) {
		this.page = page;

		if (page instanceof WSAuthorEditorPage) {
			this.documentController = ((WSAuthorEditorPage) page).getDocumentController();
		} else if (page instanceof WSDITAMapEditorPage) {
			this.documentController = ((WSDITAMapEditorPage) page).getDocumentController();
		}

		if (documentController != null) {
			documentType = getDocumentType();
			prologCreator = prologContentCreator;
		}
	}


	/**
	 * Update the prolog in DITA topic document(author mode) according to given
	 * flag(isNewDocument)
	 * 
	 * @param isNewDocument
	 *          <code>true</code> if document is new, <code>false</code> otherwise
	 * 
	 * @return <code>true</code> if prolog was update, <code>false</code>
	 *         otherwise.
	 */
	public boolean updateProlog(boolean isNewDocument) {
		boolean toReturn = true;
		if (documentController != null) {
			// Get the root element.
			AuthorElement rootElement = documentController.getAuthorDocumentNode().getRootElement();

			if (rootElement != null) {
				// Get the prolog element.
				AuthorElement prolog = getPrologElement(rootElement);
				
				documentController.beginCompoundEdit();
				try {
					if (prolog != null) {
						// Prolog element exists; edit this element.
						editProlog(prolog, isNewDocument);
					} else {
						// Add the prolog element.
						addProlog(isNewDocument);
					}
				} catch (AuthorOperationException e) {
					toReturn = false;
				} finally {
				  documentController.endCompoundEdit();
        }
			}
		} else {
			toReturn = false;
		}

		return toReturn;
	}


	/**
	 * Get the prolog element from given root element. 
	 * 
	 * @param rootElement The root element where the prolog will be searched for.
	 * 
	 * @return The prolog element or <code>null</code> if it wasn't found.
	 */
	private AuthorElement getPrologElement(AuthorElement rootElement) {
		AuthorElement prolog = null;
		
		List<AuthorElement> possiblePrologElements = AuthorPageDocumentUtil.findElementsByClass(rootElement,
				XmlElementsUtils.getPrologClass(documentType));
		
		// EXM-40992 - a subject scheme map contains 2 elements with "map/topicmeta" as the class's value.
		// In this case, we should find the element having 'topicmeta' as name. 
		if(DocumentType.SUBJECT_SCHEME.equals(documentType)){
			int size = possiblePrologElements.size();
			for (int i = 0; i < size; i++) {
				AuthorElement currentElement = possiblePrologElements.get(i);
				if(XmlElementsConstants.TOPICMETA_NAME.equals(currentElement.getName())) {
					prolog = currentElement;
					break;
				}
			}
		} else {
			if(!possiblePrologElements.isEmpty()) {
				prolog = possiblePrologElements.get(0);
			}
		}
		return prolog;
	}

	/**
	 * Add the prolog element.
	 * 
	 * @param isNewDocument
	 *          <code>true</code> if document is new, <code>false</code>otherwise.
	 * @throws AuthorOperationException
	 *           If the prolog could not be added.
	 */
	private void addProlog(boolean isNewDocument) throws AuthorOperationException {
		
		String prologFragment = prologCreator.getPrologFragment(isNewDocument, documentType);
		if (prologFragment != null) {
		  AuthorElement rootElement = documentController.getAuthorDocumentNode().getRootElement();
		  String prologXpath = AuthorPageDocumentUtil.findElementXPath(
		      documentController,
		      XmlElementsUtils.getPrologName(documentType),
		      ElementXPathUtils.getRootXpath(documentType),
		      rootElement
		      );

		  if (prologXpath != null) {
		    AuthorPageDocumentUtil.insertFragmentSchemaAware(page, documentController, prologFragment, prologXpath,
		        AuthorConstants.POSITION_AFTER);
		  } else {
		    AuthorPageDocumentUtil.insertFragmentSchemaAware(page, documentController, prologFragment,
		        ElementXPathUtils.getRootXpath(documentType), AuthorConstants.POSITION_INSIDE_FIRST);
		  }
		}
	}

	/**
	 * Edit the given prolog element.
	 * 
	 * @param critdates
	 *          The element to be edited. <code>Not null</code>
	 * @param isNewDocument
	 *          <code>true</code> if document is new, <code>false</code>otherwise.
	 * @throws AuthorOperationException
	 *           If the prolog could not be edited.
	 */
	private void editProlog(AuthorElement prolog, boolean isNewDocument) throws AuthorOperationException {
		// Updates the creators and/or contributors of document
		updateAuthorElements(prolog, isNewDocument);

		// Update the critdates element .
		updateCritdates(prolog, isNewDocument);
	}

	/**
	 * Update the critdates element.
	 * 
	 * @param prolog
	 *          The prolog author element. Not <code>null</code>.
	 * @param isNewDocument
	 *          <code>true</code> if document is new, <code>false</code>otherwise.
	 * @throws AuthorOperationException
	 *           If the element could not be update.
	 */
	private void updateCritdates(AuthorElement prolog, boolean isNewDocument) throws AuthorOperationException {
		// Where to insert
		AuthorElement cridates = AuthorPageDocumentUtil.findElementByClass(prolog,
				XmlElementsConstants.TOPIC_CRITDATES_CLASS);
		if (cridates != null) {
			// The critdates element exists, edit the content of this element.
			editCritdates(cridates, isNewDocument);
		} else {
			// The critdates element doesn't exist, add this element.
			addCritdates(prolog, isNewDocument);
		}
	}

	/**
	 * Add the critdates element into the given prolog element.
	 * 
	 * @param prolog
	 *          The prolog element where the critdates is add.
	 *          <code>Not null</code>
	 * @param isNewDocument
	 *          <code>true</code> if document is new, <code>false</code>otherwise.
	 * @throws AuthorOperationException
	 *           If the element could not be added.
	 */
	private void addCritdates(AuthorElement prolog, boolean isNewDocument) throws AuthorOperationException {
		String fragment = null;
		
		// Search for a valid position to insert critdates element.
		String xPath = AuthorPageDocumentUtil.findElementXPath(documentController,
				XmlElementsConstants.CRITDATES_NAME,
				ElementXPathUtils.getPrologXpath(documentType),
				prolog);

		fragment = XMLFragmentUtils.createCritdateTag(prologCreator.getDateFragment(isNewDocument, documentType));
		
		if(xPath != null) {
			AuthorPageDocumentUtil.insertFragmentSchemaAware(page, documentController,
					fragment, xPath, AuthorConstants.POSITION_AFTER);
		} else {
			int offset = -1;
			List<AuthorElement> authors = AuthorPageDocumentUtil.findElementsByClass(prolog,
					XmlElementsConstants.PROLOG_AUTHOR_ELEMENT_CLASS);
			if (authors.isEmpty()) {
				offset = prolog.getEndOffset();
			} else {
				AuthorElement lastAuthorElement = authors.get(authors.size() - 1);
				offset = lastAuthorElement.getEndOffset() + 1;
			}
			// Create an element here.
			AuthorPageDocumentUtil.insertFragmentSchemaAware(page, documentController, fragment, offset);
		}
	}

	/**
	 * Edit the given critdate element.
	 * 
	 * @param critdates
	 *          The element to be edited. <code>Not null</code>
	 * @param isNewDocument
	 *          <code>true</code> if document is new, <code>false</code>otherwise.
	 * @throws AuthorOperationException
	 *           If the element could not be edited.
	 */
	private void editCritdates(AuthorElement critdates, boolean isNewDocument) throws AuthorOperationException {
		if (isNewDocument) {
			AuthorElement createdElement = AuthorPageDocumentUtil.findElementByClass(critdates,
					XmlElementsConstants.CREATED_DATE_ELEMENT_CLASS);
			// Was not added yet.
			if (createdElement == null) {
				// Add it.
				AuthorPageDocumentUtil.insertFragmentSchemaAware(page, documentController,
						prologCreator.getCreatedDateFragment(documentType), critdates.getStartOffset() + 1);
			}
		} else {
			// it's not a new document
			// add revised element
			addRevisedElement(critdates);
		}

	}

	/**
	 * Add the revised element if it doesn't exits.
	 * 
	 * @param critdatesElement
	 *          critdates element(element that has revised child).
	 *          <code>Not null</code>
	 * @throws AuthorOperationException
	 *           If the element could not be added.
	 */
	private void addRevisedElement(AuthorElement critdatesElement) throws AuthorOperationException {
		boolean localDateWithAuthorCommentExist = false;

		// get revised elements
		List<AuthorElement> revisedElements = AuthorPageDocumentUtil.findElementsByClass(critdatesElement,
				XmlElementsConstants.REVISED_DATE_ELEMENT_CLASS);
		int revisedElementSize = revisedElements.size();

		// Iterate over revised elements
		for (AuthorElement current : revisedElements) {
			// check the modified value.
			AttrValue modifiedDate = current.getAttribute(XmlElementsConstants.MODIFIED_ATTRIBUTE);
			if (modifiedDate != null && prologCreator.getLocalDate().equals(modifiedDate.getRawValue())) {
				try {
					// Get the previous node
					AuthorNode previousSibling = documentController.getNodeAtOffset(current.getStartOffset() - 1);
					// and check if it's a comment.
					if (previousSibling.getType() == AuthorNode.NODE_TYPE_COMMENT
							&& prologCreator.getAuthor().equals(previousSibling.getTextContent())) {
						localDateWithAuthorCommentExist = true;
						break;
					}
				} catch (BadLocationException e) {
					logger.debug(e.getMessage(), e);
				}
			}
		}
		if (!localDateWithAuthorCommentExist) {
			int offset = critdatesElement.getEndOffset();
			String fragment = prologCreator.getRevisedDateFragment(documentType);
			if (revisedElementSize != 0) {
				offset = revisedElements.get(revisedElementSize - 1).getEndOffset() + 1;
			}
			// Now insert it.
			AuthorPageDocumentUtil.insertFragmentSchemaAware(page, documentController, fragment, offset);
			deleteExtraRevisedElements(critdatesElement);
		}
	}

	 /**
   * Delete the first revised elements if the number of them is greater than allowed number.
   * 
   * @param critdatesElement The parent of revised elements.
   */
  private void deleteExtraRevisedElements(AuthorElement critdatesElement) {
    int noOfAllowedElements = prologCreator.getMaxNoOfRevisedElement();
    if(noOfAllowedElements != -1) {
      List<AuthorElement> allRevisedElements = AuthorPageDocumentUtil.findElementsByClass(
          critdatesElement, XmlElementsConstants.REVISED_DATE_ELEMENT_CLASS);
      int noOfElements = allRevisedElements.size();
    
      // Get the caret position
      Position caretPosition = null;
      if (page instanceof WSAuthorEditorPage) {
        try {
          caretPosition = documentController.createPositionInContent(
              ((WSAuthorEditorPage)page).getCaretOffset());
        } catch (BadLocationException e) {
          logger.error(e, e);
        }
      }
      
      if(noOfElements > noOfAllowedElements) {
        int nuOfElementToDelete = noOfElements - noOfAllowedElements;
        for (int i = 0; i < nuOfElementToDelete; i++) {
          AuthorElement currentRevised = allRevisedElements.get(i);
          // Get the previous node
          try {
            AuthorNode previousSibling = documentController.getNodeAtOffset(currentRevised.getStartOffset() - 1);
            // We will delete the previous sibling if it's a comment
            if (previousSibling.getType() == AuthorNode.NODE_TYPE_COMMENT) {
              documentController.deleteNode(previousSibling);
            }
          } catch (BadLocationException e) {
            logger.debug(e.getMessage(), e);
          }
          // Delete the revised element.
          documentController.deleteNode(currentRevised);
        }
        
        // Restore the caret position
        if(caretPosition != null) {
          ((WSAuthorEditorPage)page).setCaretPosition(caretPosition.getOffset());
        }
      }
    }
  }

	/**
	 * Update the document adding the names of the authors.
	 * 
	 * @param prolog
	 *          The prolog element. Never <code>null</code>.
	 * @param isNewDocument
	 *          <code>true</code> if document is new, <code>false</code>otherwise.
	 * 
	 * @throws AuthorOperationException
	 *           If the element could not be update.
	 */
	private void updateAuthorElements(AuthorElement prolog, boolean isNewDocument) throws AuthorOperationException {
		List<AuthorElement> existentAuthorsElements = AuthorPageDocumentUtil.findElementsByClass(prolog,
				XmlElementsConstants.PROLOG_AUTHOR_ELEMENT_CLASS);

		String fragment = null;
		int offset = prolog.getStartOffset() + 1;
		if (isNewDocument) {
		  if (!AuthorPageDocumentUtil.hasCreator(existentAuthorsElements,
		      prologCreator.getCreatorTypeValue())) {
		    fragment = prologCreator.getCreatorFragment(documentType);
		  }
		} else {
		  if (!AuthorPageDocumentUtil.hasContributor(
		      existentAuthorsElements,
		      prologCreator.getContributorTypeValue(),
		      prologCreator.getAuthor())) {
		    // if wasn't found this contributor
		    fragment = prologCreator.getContributorFragment(documentType);
		    int nuOfAuthors = existentAuthorsElements.size();
		    if (nuOfAuthors > 0) {
		      AuthorElement lastAuthor = existentAuthorsElements.get(nuOfAuthors - 1);
		      offset = lastAuthor.getEndOffset() + 1;
		    }
		  }
		}
		
		if (fragment != null) {
		  // Search for a xPath to insert the author element (in DMM the fragment is not inserted schema aware)
		  String xPath = AuthorPageDocumentUtil.findElementXPath(documentController,
		      XmlElementsConstants.AUTHOR_NAME,
		      ElementXPathUtils.getPrologXpath(documentType),
	        prolog);
		  if (xPath != null) {
		    AuthorPageDocumentUtil.insertFragmentSchemaAware(page, documentController,
		        fragment,
		        xPath,
		        AuthorConstants.POSITION_AFTER);
		  } else {
		    AuthorPageDocumentUtil.insertFragmentSchemaAware(page, documentController, fragment, offset);
		  }
		}
	}

	/**
	 * Get the document type of the current document.
	 * @return The document type of the current document 
	 * ( {@link DocumentType#TOPIC}, {@link DocumentType#MAP}, 
	 * {@link DocumentType#BOOKMAP} or {@link DocumentType#SUBJECT_SCHEME} ).
	 */
	private DocumentType getDocumentType() {
		DocumentType docType = DocumentType.TOPIC;
		AuthorElement rootElement = documentController.getAuthorDocumentNode().getRootElement();
		AttrValue classValue = rootElement.getAttribute(XmlElementsConstants.CLASS);

		if(classValue != null) {
			String value = classValue.getValue();
			if (value.contains(" map/map ")) {
				docType = DocumentType.MAP;
			}
			if (value.contains(" bookmap/bookmap ")) {
				docType = DocumentType.BOOKMAP;
			} else if (value.contains(" subjectScheme/subjectScheme ")) {
				docType = DocumentType.SUBJECT_SCHEME;
			}
		}
		return docType;
	}
}
