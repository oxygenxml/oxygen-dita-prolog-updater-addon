package com.oxygenxml.prolog.updater.utils;

import java.io.StringReader;
import java.net.URL;
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;

import org.apache.log4j.Logger;

import com.oxygenxml.prolog.updater.dita.editor.DocumentType;

import ro.sync.contentcompletion.xml.CIElement;
import ro.sync.contentcompletion.xml.ContextElement;
import ro.sync.contentcompletion.xml.WhatElementsCanGoHereContext;
import ro.sync.exml.editor.xmleditor.operations.context.RelativeInsertPosition;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.text.WSTextXMLSchemaManager;
import ro.sync.exml.workspace.api.editor.page.text.xml.TextDocumentController;
import ro.sync.exml.workspace.api.editor.page.text.xml.TextOperationException;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextNodeRange;
import ro.sync.exml.workspace.api.editor.page.text.xml.XPathException;
import ro.sync.exml.workspace.api.util.PrettyPrintException;

/**
 * A collection of utility methods to be used in the text page.
 * 
 */
public class TextPageDocumentUtil {

	/**
	 * Logger
	 */
	private static final Logger logger = Logger.getLogger(TextPageDocumentUtil.class);

	/**
	 * Private constructor.
	 */
	private TextPageDocumentUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Create a AWT thread and insert the given fragment,
	 * 
	 * @param page
	 *          The text editor.
	 * @param xmlFragment
	 *          The XML fragment to insert.
	 * @param xPath
	 *          The xPath to insert fragment relative to.
	 * @param position
	 *          The position relative to the node. Can be one of the constants:
	 *          {@link RelativeInsertPosition#INSERT_LOCATION_AFTER},
	 *          {@link RelativeInsertPosition#INSERT_LOCATION_AS_FIRST_CHILD},
	 *          {@link RelativeInsertPosition#INSERT_LOCATION_AS_LAST_CHILD} or
	 *          {@link RelativeInsertPosition#INSERT_LOCATION_BEFORE}.
	 * @throws TextOperationException
	 *           If the fragment could not be inserted.
	 */
	public static void insertXmlFragment(final WSXMLTextEditorPage page, final String xmlFragment, final String xPath,
			final RelativeInsertPosition position) throws TextOperationException {
		if (xmlFragment != null && xPath != null && position != null) {
			TextDocumentController controller = page.getDocumentController();

			// Get the offset of caret.
			int initialOffset = page.getCaretOffset();
			// Create a position of caret.
			Position pos = null;
			if (initialOffset != -1) {
				Document document = page.getDocument();
				if (document != null) {
					try {
						pos = document.createPosition(initialOffset);
					} catch (BadLocationException e) {
						logger.debug(e.getMessage(), e);
					}
				}
			}
			controller.insertXMLFragment(prettyPrintFragment(page, xmlFragment), xPath, position);

			// Restore the position of caret.
			if (pos != null) {
				page.setCaretPosition(pos.getOffset());
			}

		}
	}

	/**
	 * Pretty print the given fragment.
	 * 
	 * @param wsTextEditorPage
	 *          The text editor.
	 * @param fragment
	 *          The fragment.
	 * @return The pretty printed content.
	 */
	private static String prettyPrintFragment(WSXMLTextEditorPage wsTextEditorPage, String fragment) {
		PluginWorkspace pluginWorkspace = PluginWorkspaceProvider.getPluginWorkspace();
		if (pluginWorkspace != null) {
			URL location = getCurrentEditorLocation(wsTextEditorPage);
			if (location != null) {
				try {
					fragment = pluginWorkspace.getXMLUtilAccess().prettyPrint(new StringReader(fragment),
							location.toExternalForm());
					// Pretty print moves to next line. We don't want that.
					if (fragment.endsWith("\r\n")) {
						fragment = fragment.substring(0, fragment.length() - 2);
					} else if (fragment.endsWith("\n") || fragment.endsWith("\r")) {
						fragment = fragment.substring(0, fragment.length() - 1);
					}
				} catch (PrettyPrintException e) {
					logger.debug(e, e);
				}
			}
		}
		return fragment;
	}

	/**
	 * @param textPage
	 *          The text page.
	 * @return The URL of the current edited page or <code>null</code>.
	 */
	private static URL getCurrentEditorLocation(WSXMLTextEditorPage textPage) {
		URL url = null;
		if (textPage != null) {
			WSEditor parentEditor = textPage.getParentEditor();
			if (parentEditor != null) {
				url = parentEditor.getEditorLocation();
			}
		}
		return url;
	}

	/**
	 * Find a possible xPath where given element can be inserted after. 
	 * 
	 * @param page
	 *          The page from the WsEditor.
	 * @param elementName The name of element to find xPath.         
	 * @param documentType
	 *          The type of the document ( {@link DocumentType#TOPIC},
	 *          {@link DocumentType#MAP} or {@link DocumentType#BOOKMAP} ).
	 * @param  parrentXpath The xPath of parent of the element.       
	 * @return A xPath where to insert the element node or 
	 * <code>null</code> if the element should be inserted as first child in parrentXpath.
	 * 
	 */
	public static String findElementXPath(WSXMLTextEditorPage page, String elementName, String parrentXpath, DocumentType documentType) {
		String toReturn = null;
		ContextElement nodeToInsertAfter = null;

		// Find the context where prolog element can be inserted.
		try {
			WhatElementsCanGoHereContext context = findElementContext(page, elementName, parrentXpath, documentType);
			
			if (context != null) {
				List<ContextElement> previous = context.getPreviousSiblingElements();
				if (previous != null && !previous.isEmpty()) {
					// Get the previous sibling.
					nodeToInsertAfter = previous.get(previous.size() - 1);
					// Generate the XPath.
					toReturn = parrentXpath + "/" + nodeToInsertAfter.getQName();
				}
			}
		} catch (XPathException e) {
			logger.warn(e, e.getCause());
		}
		
		return toReturn;
	}

	/**
	 * Find a possible context where given element can be inserted.
	 * 
	 * @param page
	 *          Workspace text editor page.
	 * @param elementName The name of the element to find context.
	 * @param  parrentXpath The xPath of parent of the element.       
	 * @param documentType
	 *          The type of the document ( {@link DocumentType#TOPIC},
	 *          {@link DocumentType#MAP} or {@link DocumentType#BOOKMAP} ).
	 * @return A context where prolog element can go or <code>null</code>.
	 * @throws XPathException
	 *           If prolog context can't be found.
	 */
	private static WhatElementsCanGoHereContext findElementContext(WSXMLTextEditorPage page, String elementName, String parrentXpath, DocumentType documentType)
			throws XPathException {
		WhatElementsCanGoHereContext toReturn = null;

		// Get the XmlSchemaManager.
		WSTextXMLSchemaManager schemaManager = page.getXMLSchemaManager();
		if (schemaManager != null) {
			
			// Get all child of root topic.
			WSXMLTextNodeRange[] topicChild = page.findElementsByXPath(parrentXpath + "/*" ); 
			int childNo = topicChild.length;
			
			// Iterate over topic child
			for (int j = 0; j < childNo; j++) {
				WSXMLTextNodeRange currentNode = topicChild[j];
				
				// Get the offset of next line.
				WhatElementsCanGoHereContext currentContext = null;
				try {
					int offset = page.getOffsetOfLineEnd(currentNode.getEndLine());
					currentContext = schemaManager.createWhatElementsCanGoHereContext(offset);
				} catch (BadLocationException e) {
					logger.debug(e.getMessage(), e);
				}

				Boolean contextForElement = analyzeContextForElement(
						elementName,
						currentContext,
						schemaManager);
				if (contextForElement != null) {
					toReturn = currentContext;
					if (contextForElement) {
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
	private static Boolean analyzeContextForElement(String element, WhatElementsCanGoHereContext context, WSTextXMLSchemaManager schemaManager) {
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
							// if prolog element is first in possible elements list(This is the best position).
							toReturn = true;
						}
					}
				}
			}
		
		}
		return toReturn;
	}
}
