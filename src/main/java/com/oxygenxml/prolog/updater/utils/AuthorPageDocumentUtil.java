package com.oxygenxml.prolog.updater.utils;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.swing.text.Position;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.sync.contentcompletion.xml.CIElement;
import ro.sync.contentcompletion.xml.ContextElement;
import ro.sync.contentcompletion.xml.WhatElementsCanGoHereContext;
import ro.sync.ecss.extensions.api.AuthorConstants;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.AuthorSchemaManager;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.XPathException;

/**
 * A collection of utility methods to be used in the author page.
 * 
 * @author adrian_sorop
 */
public class AuthorPageDocumentUtil {

	/**
	 * Logger for logging.
	 */
	private static final Logger logger = LoggerFactory.getLogger(AuthorPageDocumentUtil.class.getName());

	/**
	 * Private constructor. Avoid instantiation.
	 */
	private AuthorPageDocumentUtil() {
		// Nothing
	}

	/**
	 * Search and return a list of elements with a specific class.
	 * 
	 * @param rootElement
	 *          The node where the search should start.
	 * @param classValue
	 *          The class value used to identify the element.
	 * @return A list with elements with a specific class. If no element is found,
	 *         the method returns an empty list.
	 */
	public static List<AuthorElement> findElementsByClass(AuthorElement rootElement, String classValue) {
		List<AuthorElement> toReturn = new ArrayList<AuthorElement>();
		List<AuthorNode> contentNodes = rootElement.getContentNodes();
		if (contentNodes != null && !contentNodes.isEmpty()) {
			for (AuthorNode authorNode : contentNodes) {
				if (authorNode.getType() == AuthorNode.NODE_TYPE_ELEMENT) {
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
	 * Search and return first element identified by given class value.
	 * 
	 * @param rootElement
	 *          Document root element.
	 * @param classValue
	 *          The class value used to identify the element.
	 * @return <code>null</code> or the identified element.
	 */
	public static AuthorElement findElementByClass(AuthorElement rootElement, String classValue) {
		AuthorElement toReturn = null;
		List<AuthorNode> contentNodes = rootElement.getContentNodes();
		if (contentNodes != null && !contentNodes.isEmpty()) {
			for (AuthorNode authorNode : contentNodes) {
				if (authorNode.getType() == AuthorNode.NODE_TYPE_ELEMENT) {
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
   * Search for creator author with the given name
   * 
   * @param authors              The list with authors.
   * @param contributorTypeValue The value of type attribute for a creator.
   * 
   * @return <code>true</code> if was found an author with given type.
   */
	public static boolean hasCreator(List<AuthorElement> authors, String creatorTypeValue) {
    boolean foundCreator = false;
    // Iterate over authors.
    for (AuthorElement el : authors) {
      // Get the type's value,
      AttrValue typeAttr = el.getAttribute("type");
      if (typeAttr != null && creatorTypeValue.equals(typeAttr.getValue())) {
        foundCreator= true;
        break;
      }
    }
    return foundCreator;
  
	}
	
	
	/**
	 * Search for author with the type attribute and the given name
	 * 
	 * @param authors              The list with authors.
	 * @param attributeType        The value of type attribute.
	 * @param authorName           The name of the author.
	 * 
	 * @return <code>true</code> if was found an author with given type.
	 */
	public static boolean hasAuthorWithTypeAndName(List<AuthorElement> authors, String attributeType, String authorName) {
		boolean foundContributor = false;

		// Iterate over authors.
		for (AuthorElement el : authors) {
			// Get the type's value,
			AttrValue typeAttr = el.getAttribute("type");
			if (typeAttr != null && attributeType.equals(typeAttr.getValue())) {
			  try {
			    // Check the content of contributor element.
			    String textContent = el.getTextContent();
			    // Was found a valid contributor.
			    if (authorName.equals(textContent)) {
			      foundContributor = true;
			      break;
			    }
			  } catch (BadLocationException e) {
			    logger.debug(e.getMessage(), e);
			  }
			}
		}
		return foundContributor;
	}

	/**
	 * Inserts an element schema aware and restore the caret position.
	 * 
	 * @param page
	 *          The current page opened in editor.
	 * @param controller
	 *          The document controller.
	 * @param xmlFragment
	 *          The xml fragment to insert.
	 * @param offset
	 *          The insert position.
	 * @throws AuthorOperationException
	 *           If the fragment could not be inserted.
	 */
	public static void insertFragmentSchemaAware(final WSEditorPage page, final AuthorDocumentController controller,
			final String xmlFragment, final int offset) throws AuthorOperationException {

		if (controller != null && xmlFragment != null && offset != -1) {
			Position position = null;
			// Get the initial caret offset.
			int caretOffset = -1;
			if (page instanceof WSAuthorEditorPage) {
				caretOffset = ((WSAuthorEditorPage) page).getCaretOffset();
			}

			try {
				position = controller.createPositionInContent(caretOffset);
			} catch (BadLocationException e) {
				logger.debug(e.getMessage(), e);
			}

			// Insert the fragment.
			controller.insertXMLFragmentSchemaAware(xmlFragment, offset);

			// Restore the caret position.
			if (position != null) {
				((WSAuthorEditorPage) page).setCaretPosition(position.getOffset());
			}

		}
	}

	/**
	 * Inserts the given fragment schema aware and restore the caret position.
	 * 
	 * @param page
	 *          The current page opened in editor.
	 * @param documentController
	 *          The author document controller.
	 * @param xmlFragment
	 *          The xml fragment that will be inserted.
	 * @param xPath
	 *          The xPath to insert fragment relative to.
	 * @param position
	 *          The position relative to the node identified by the XPath
	 *          location. Can be one of the constants:
	 *          {@link AuthorConstants#POSITION_BEFORE},
	 *          {@link AuthorConstants#POSITION_AFTER},
	 *          {@link AuthorConstants#POSITION_INSIDE_FIRST} or
	 *          {@link AuthorConstants#POSITION_INSIDE_LAST}.
	 * @throws AuthorOperationException
	 *           If the fragment could not be inserted.
	 */
	public static void insertFragmentSchemaAware(final WSEditorPage page,
			final AuthorDocumentController documentController, final String xmlFragment, final String xPath,
			final String position) throws AuthorOperationException {

		if (xmlFragment != null && xPath != null && position != null) {
			Position pos = null;

			int caretOffset = -1;
			if (page instanceof WSAuthorEditorPage) {
				caretOffset = ((WSAuthorEditorPage) page).getCaretOffset();
			}
			try {
				pos = documentController.createPositionInContent(caretOffset);
			} catch (BadLocationException e) {
				logger.debug(e.getMessage(), e);
			}

			// Insert the fragment.
			documentController.insertXMLFragmentSchemaAware(xmlFragment, xPath, position);

			// Restore the caret position.
			if (pos != null) {
				((WSAuthorEditorPage) page).setCaretPosition(pos.getOffset());
			}
		}

	}

	/**
	 * Find a possible xPath where given element can be inserted after.
	 * 
	 * @param documentController
	 *          The author document controller.
	 * @param parentXpath The parent xPath of the element.  
	 * @param parentElement The parent of the element to find context. 
	 * @return A xPath where to insert the given element node or 
	 * <code>null</code> if the element should be inserted as first child in parrentXpath.
	 * 
	 * @throws BadLocationException
	 * @throws XPathException
	 */
	public static String findElementXPath(AuthorDocumentController controller, String elementName,
			String parentXpath, AuthorElement parentElement) {
		String toReturn = null;
		ContextElement nodeToInsertAfter = null;

		// Find the context where prolog element can be inserted.
		WhatElementsCanGoHereContext context;
		context = findElementContext(controller, elementName, parentElement);
		if (context != null) {
			List<ContextElement> previous = context.getPreviousSiblingElements();
			if (previous != null && !previous.isEmpty()) {
				// Get the previous sibling.
				nodeToInsertAfter = previous.get(previous.size() - 1);
				// Generate the XPath.
				toReturn = parentXpath + "/" + nodeToInsertAfter.getQName();
			}
		}
		return toReturn;
	}

	/**
	 * Find a possible context where prolog element can be inserted.
	 * 
	 * @param controller
	 *          The controller for author document.
	 * @param elementName The name of the element to find context.
	 * @param parentElement The parent of the element to find context. 
	 * @return A context where given element can go or <code>null</code>.
	 */
	private static WhatElementsCanGoHereContext findElementContext(AuthorDocumentController controller,
			String elementName, AuthorElement parentElement) {
		WhatElementsCanGoHereContext toReturn = null;

		// Get the AuthorSchemaManager.
		AuthorSchemaManager schemaManager = null;
		try {
			schemaManager = controller.getAuthorSchemaManager();
		} catch (Exception e) {
			//Do nothing. (for TC) 
		}
		
		if (schemaManager != null) {
			List<AuthorNode> childNodes = parentElement.getContentNodes();
			int nodesSize = childNodes.size();
			
			for (int i = 0; i < nodesSize; i++) {
				AuthorNode authorNode = childNodes.get(i);
				
				int offset = authorNode.getEndOffset();
				
				WhatElementsCanGoHereContext currentContext = null;
				try {
					currentContext = schemaManager.createWhatElementsCanGoHereContext(offset + 1);
				} catch (BadLocationException e) {
					logger.warn(String.valueOf(e), e.getCause());
				}
				
				Boolean isContextForProlog = analyzeContextForElement(
						elementName,
						currentContext,
						schemaManager);
				if (isContextForProlog != null) {
					toReturn = currentContext;
					if (Boolean.TRUE.equals(isContextForProlog)) {
						break;
					}
				}
			}
		}
		return toReturn;
	}

	/**
	 * Analyze if the given context can contains the given element.
	 * @param element The name of the element to search.
	 * @param context Context to analyze
	 * @param schemaManager The schema manager from author mode.
	 * @return <code>null</code> If the context can't contain the given element, 
	 * 					<code>false</code> if the context can contain the given element, but it's not the best position.
	 * 					<code>true</code> if the context can contains the given element and this is the best position.
	 */
	private static Boolean analyzeContextForElement(String element, WhatElementsCanGoHereContext context, AuthorSchemaManager schemaManager) {
		Boolean toReturn = null;
		if (context != null) {
			// Analyze if current context can contain the prolog element.
			List<CIElement> possible = schemaManager.whatElementsCanGoHere(context);
			if (possible != null) {
				// Iterate over possible elements.
				int size = possible.size();
				for (int j = 0; j < size; j++) {
					CIElement ciElement = possible.get(j);
					if (ciElement.getName().equals(element)) {
						// the context can contain the given element.
						toReturn = false;
						if (j == 0) {
							// if given element is first in possible elements list(This is the best position).
							toReturn = true;
						}
					}
				}
			}
		
		}
		return toReturn;
	}
}
