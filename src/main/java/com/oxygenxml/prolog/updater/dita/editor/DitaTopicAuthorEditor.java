package com.oxygenxml.prolog.updater.dita.editor;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;

import com.oxygenxml.prolog.updater.PrologContentCreator;

import ro.sync.ecss.extensions.api.AuthorConstants;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;

/**
 * Edit DITA topic in author mode.
 * @author intern4
 *
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

		//get the root element
		AuthorElement rootElement = documentController.getAuthorDocumentNode().getRootElement();
		
		//get the prolog element
		AuthorElement[] prologElement = rootElement.getElementsByLocalName("prolog");
		int prologElementSize = prologElement.length;

		if (prologElementSize == 0) {
			// prolog node doesn't exist
			//get the anterior node of prolog
			final AuthorElement bodyAuthorElement = getPrologAnteriorAuthorElement(rootElement);

			if (bodyAuthorElement != null) {
				// add the prolog node.
				addXmlFragment(prologContentCreater.getPrologXMLFragment(isNewDocument), bodyAuthorElement.getStartOffset());
			}

		} else {
			// prolog node exists;
			try {
				// update the author node
				updateAuthorElements(prologElement[0], isNewDocument);

				// update the critdates node
				updateCritdatesElements(prologElement[0], isNewDocument);
				
			} catch (BadLocationException e) {
				logger.debug(e.getMessage(), e);
			}
		}

	}
	
	
	
	/**
	 * Update the author elements of prolog.
	 * @param authorPrologElement The prolog element.
	 * @param isNewDocument <code>true</code> if document is new, <code>false</code> otherwise
	 * @throws BadLocationException 
	 */
	private void updateAuthorElements(final AuthorElement authorPrologElement, final boolean isNewDocument) throws BadLocationException {
		boolean foundCreator = false;
		boolean foundContributor = false;

		//get the author elements with name author"
		final AuthorElement[] authorElements = authorPrologElement.getElementsByLocalName("author");
		final int length = authorElements.length;
		
		//the elements with author name exist
		if (length != 0) {
			
			if (isNewDocument) {
				//it's a new document
				//check if it's already set a creator
				for (int i = 0; i < length; i++) {
					AuthorElement authorElement = authorElements[i];
					if (authorElement.getAttribute("type").getRawValue().equals("creator")) {
						foundCreator = true;
						break;
					}
				}
				//if wasn't found a creator
				if (!foundCreator) {
					addXmlFragment(prologContentCreater.getAuthorCreatorXmlFragment(), authorElements[0],
							AuthorConstants.POSITION_BEFORE);
				}
				
				
			} else {
				//it's not a new document
				//check it's already set this contributor
				for (int i = 0; i < length; i++) {
					AuthorElement authorElement = authorElements[i];
					String textContent = "";

					//get the text content of node
						textContent = authorElement.getTextContent();
					if (authorElement.getAttribute("type").getRawValue().equals("contributor") && 
							prologContentCreater.getAuthor().equals(textContent)) {
						foundContributor = true;
						break;
					}
				}
				
				//if wasn't found this contributor
				if (!foundContributor) {
					addXmlFragment(prologContentCreater.getAuthorContributorXmlFragment(), authorElements[length-1],
							AuthorConstants.POSITION_AFTER);
				}
			}

		}
		else{
			//It's not found author elements
				addXmlFragment(prologContentCreater.getAuthorXmlFragment(isNewDocument), authorPrologElement.getStartOffset()+1);
		}
	}

	
	
	/**
	 * Update the critdates element of prolog.
	 * @param authorPrologElement The prolog element.
	 * @param isNewDocument <code>true</code> if document is new, <code>false</code> otherwise
	 * @throws BadLocationException 
	 */
	private void updateCritdatesElements(AuthorElement authorPrologElement, boolean isNewDocument) throws BadLocationException {
		//get the critdates author elements
		final AuthorElement[] critdatesElements = authorPrologElement.getElementsByLocalName("critdates");
		int length = critdatesElements.length;
		
		if(length != 0){
			//was found critdates element
			if(isNewDocument){
				//is a new document
				//get the created elements
				 AuthorElement[] creatDateElements = critdatesElements[0].getElementsByLocalName("created");
		
				 //if wasn't found a created element.
				 if(creatDateElements.length == 0){
					 //add a created element.
					 addXmlFragment(prologContentCreater.getCreatedDateXmlFragment(), critdatesElements[0].getStartOffset() + 1);
					 
				 }
			}
			else {
				// it's not a new document
				AuthorElement[] reviDateElements = critdatesElements[0].getElementsByLocalName("revised");
				int reviDateElementsLength = reviDateElements.length;

				if (reviDateElementsLength == 0) {
					addXmlFragment(prologContentCreater.getResivedModifiedXmlFragment(), critdatesElements[0].getEndOffset());
				
				} else {
					boolean localDateWithAuthorExist = false;
					// Iterate over revised elements
					for (int i = 0; i < reviDateElementsLength; i++) {
						AuthorElement curentRevisedElement = reviDateElements[i];

						// check the modified value.
						String currentModifiedDate = curentRevisedElement.getAttribute("modified").getRawValue();
						if (prologContentCreater.getLocalDate().equals(currentModifiedDate)) {

							// check the comment
							int currentElemetStartOffSet = curentRevisedElement.getStartOffset();
							AuthorNode anteriorNode;
							anteriorNode = documentController.getNodeAtOffset(currentElemetStartOffSet - 1);

							if (anteriorNode.getType() == AuthorNode.NODE_TYPE_COMMENT) {
								if (prologContentCreater.getAuthor().equals(anteriorNode.getTextContent())) {
									localDateWithAuthorExist = true;
									break;
								}
							}

						}
					}
					if (!localDateWithAuthorExist) {
						addXmlFragment(prologContentCreater.getResivedModifiedXmlFragment(),
								reviDateElements[reviDateElementsLength - 1], AuthorConstants.POSITION_AFTER);
					}

				}
			}
			
		}else{
			//wasn't found critdates element
			final String toAdd = "<critdates>\n" + prologContentCreater.getDateXmlFragment(isNewDocument) + "\n</critdates>";
			final AuthorElement[] authorElements = authorPrologElement.getElementsByLocalName("author");
			
			addXmlFragment(toAdd, authorElements[authorElements.length-1], AuthorConstants.POSITION_AFTER);
		}
	}
	
	
	
	/**
	 * Create a AWT thread and insert the given fragment at given offset.
	 * @param xmlFragment The xml fragment.
	 * @param offset The offset.
	 */
	private void addXmlFragment(final String xmlFragment, final int offset){
		 try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						try {
							//insert create date .
							documentController.insertXMLFragment(xmlFragment, offset );
						} catch (AuthorOperationException e) {
							logger.debug(e.getMessage(), e);
						}
					}
				});
			} catch (InvocationTargetException e) {
				logger.debug(e.getMessage(), e);
			} catch (InterruptedException e) {
				logger.debug(e.getMessage(), e);
			}
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
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					try {
						//insert create date .
						documentController.insertXMLFragment(xmlFragment, relativeTo, relativPosition);
					} catch (AuthorOperationException e) {
						logger.debug(e.getMessage(), e);
					}
				}
			});
		} catch (InvocationTargetException e) {
			logger.debug(e.getMessage(), e);
		} catch (InterruptedException e) {
			logger.debug(e.getMessage(), e);
		}
	}


	/**
	 * Get the anterior node of prolog node.
	 * 
	 * @param rootElement The root element.
	 * @return The anterior node of prolog node.
	 */
	private AuthorElement getPrologAnteriorAuthorElement(AuthorElement rootElement) {
		//get all nodes
		List<AuthorNode> contentNodes = rootElement.getContentNodes();

		//Iterate over nodes
		for (Iterator<AuthorNode> iterator = contentNodes.iterator(); iterator.hasNext();) {
			AuthorNode authorNode = (AuthorNode) iterator.next();
			if (authorNode.getType() == AuthorNode.NODE_TYPE_ELEMENT) {
				AuthorElement authorElement = (AuthorElement) authorNode;
				//get the value of attribute class.
				AttrValue attribute = authorElement.getAttribute("class");
				
				//if value of attribute contains ANTERIOR_NODE_CLASS_VALUE
				if (attribute.toString().contains(FOLLOWING_NODE_CLASS_VALUE)) {
					// anterior node was found
					return authorElement;
				}
			}

		}
		return null;
	}
}
