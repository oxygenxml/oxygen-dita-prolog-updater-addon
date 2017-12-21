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
   * @param page The text editor.
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
  public static void insertXmlFragment(final WSXMLTextEditorPage page,  final String xmlFragment, final String xPath, final RelativeInsertPosition position) {
    if (xmlFragment != null && xPath != null && position != null) {
      ThreadUtils.invokeSynchronously(new Runnable() {
        public void run() {
          try {
            TextDocumentController controller = page.getDocumentController();
            
            // Get the offset of caret.
            int initialOffset = page.getCaretOffset();
            // Create a position of caret.
            Position pos = null;
            if (initialOffset != -1) {
              Document document = page.getDocument();
              if(document != null) {
                pos = document.createPosition(initialOffset);
              }
            }
            controller.insertXMLFragment(
                prettyPrintFragment(page, xmlFragment), 
                xPath, 
                position);

            // Restore the position of caret.
            if (pos != null) {
              page.setCaretPosition(pos.getOffset());
            }

          } catch (TextOperationException e) {
            logger.debug(e.getMessage(), e);
          } catch (BadLocationException e) {
            if (logger.isDebugEnabled()) {
              logger.debug(e, e);
            }
          }
        }
      });
    }
  }
  
  /**
   * Pretty print the given fragment.
   * 
   * @param wsTextEditorPage The text editor.
   * @param fragment The fragment.
   * @return The pretty printed content.
   */
  private static String prettyPrintFragment(WSXMLTextEditorPage wsTextEditorPage, String fragment) {
    PluginWorkspace pluginWorkspace = PluginWorkspaceProvider.getPluginWorkspace();
    if (pluginWorkspace != null) {
      URL location = getCurrentEditorLocation(wsTextEditorPage);
      if (location != null) {
        try {
          fragment = pluginWorkspace.getXMLUtilAccess().prettyPrint(new StringReader(fragment), location.toExternalForm());
          // Pretty print moves to next line. We don't want that.
          if (fragment.endsWith("\n") || fragment.endsWith("\r\n") || fragment.endsWith("\r")) {
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
   * @param textPage The text page.
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
   * Find a possible xPath where prolog element can be inserted.
   *  
   * @param page WSXMLTextEditorPage.
   * @param documentType The type of the document ( {@link DocumentType#TOPIC}, {@link DocumentType#MAP} or  {@link DocumentType#BOOKMAP}  ).
   * @return A xPath where to insert the prolog node or <code>null</code>.
   * 
   * @throws BadLocationException
   * @throws XPathException
   */
public static String findPrologXPath(WSXMLTextEditorPage page, DocumentType documentType) {
  String toReturn = null;
  ContextElement nodeToInsertAfter = null;
  
  // Find the context where prolog element can be inserted.
  WhatElementsCanGoHereContext context;
  try {
    context = findPrologContext(page, documentType);
    if (context != null) {
      List<ContextElement> previous = context.getPreviousSiblingElements();
      if (previous != null && !previous.isEmpty()) {
        // Get the previous sibling.
        nodeToInsertAfter = previous.get(previous.size() - 1);
        // Generate the XPath.
        toReturn = XPathConstants.getRootXpath(documentType) + "/" + nodeToInsertAfter.getQName();
      }
    }
  } catch (BadLocationException e) {
    logger.warn(e, e.getCause());
  } catch (XPathException e) {
    logger.warn(e, e.getCause());
  }
  return toReturn;
}
  
  /**
   * Find a possible context where prolog element can be inserted.
   * 
   * @param page WSXMLTextEditorPage
   * @param documentType The type of the document ( {@link DocumentType#TOPIC}, {@link DocumentType#MAP} or  {@link DocumentType#BOOKMAP}  ).
   * @return A context where prolog element can go or <code>null</code>.
   * @throws BadLocationException 
   * @throws ro.sync.exml.workspace.api.editor.page.text.xml.XPathException 
   */
private static WhatElementsCanGoHereContext findPrologContext(WSXMLTextEditorPage page, DocumentType documentType) throws BadLocationException, XPathException {
  WhatElementsCanGoHereContext toReturn = null;

  // Get the XmlSchemaManager.
  WSTextXMLSchemaManager schemaManager = page.getXMLSchemaManager();

    // Get all child of root topic.
    WSXMLTextNodeRange[] topicChild = page.findElementsByXPath(XPathConstants.getRootChildXpath(documentType));
    
    int childNo = topicChild.length;
  // Iterate over topic child
  loop: for (int j = 0; j < childNo; j++) {
    WSXMLTextNodeRange currentNode = topicChild[j];
    // Get the offset of next line.
    int offset = page.getOffsetOfLineEnd(currentNode.getEndLine());
    WhatElementsCanGoHereContext currentContext = schemaManager.createWhatElementsCanGoHereContext(offset);
    if (currentContext != null) {
      // Analyze if current context can contain the prolog element.
      List<CIElement> possible = schemaManager.whatElementsCanGoHere(currentContext);
      if (possible != null) {
        // Iterate over possible elements.
        int size = possible.size();
        for (int i = 0; i < size; i++) {
          CIElement ciElement = possible.get(i);
          if (ciElement.getName().equals(XmlElementsConstants.getPrologName(documentType))) {
            toReturn = currentContext;
            if (i == 0) {
              // if prolog element is first in possible elements list. STOP.
              break loop;
            }
          }
        }
      }
    }
  }
  return toReturn;
}
  
}
