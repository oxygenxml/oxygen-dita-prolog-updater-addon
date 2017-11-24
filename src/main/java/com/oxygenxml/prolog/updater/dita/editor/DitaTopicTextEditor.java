package com.oxygenxml.prolog.updater.dita.editor;

import java.io.StringReader;
import java.net.URL;
import java.util.List;

import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;

import com.oxygenxml.prolog.updater.PrologContentCreator;
import com.oxygenxml.prolog.updater.utils.ThreadUtils;
import com.oxygenxml.prolog.updater.utils.XPathConstants;
import com.oxygenxml.prolog.updater.utils.XmlElementsConstants;

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
      if (prologs.length == 0) {
        // Search for body element
        WSXMLTextNodeRange[] body = wsTextEditorPage.findElementsByXPath(XPathConstants.FIRST_BODY_IN_ROOT_TOPIC);
        if (body.length != 0) {
          insertXmlFragment(prologCreator.getPrologFragment(isNewDocument), XPathConstants.FIRST_BODY_IN_ROOT_TOPIC,
              RelativeInsertPosition.INSERT_LOCATION_BEFORE);
        } else {
          String xp = findPrologXPath(wsTextEditorPage);
          if (xp != null) {
            insertXmlFragment(prologCreator.getPrologFragment(isNewDocument), xp,
                RelativeInsertPosition.INSERT_LOCATION_AFTER);
          }
        }
	      
	    
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
	    insertXmlFragment(toAdd, XPathConstants.LAST_PROLOG_AUTHOR,
	        RelativeInsertPosition.INSERT_LOCATION_AFTER);

	  } else {
	    // the critdates element exists
	    if (isNewDocument) {
	      // document is new
	      // search for created element.
	      Object[] createdElements = wsTextEditorPage
	          .evaluateXPath(XPathConstants.PROLOG_CREATED_ELEMENT);

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
	  Object[] authorElements = wsTextEditorPage.findElementsByXPath(XPathConstants.PROLOG_AUTHORS);
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
	      Object[] creatorAuthorElements = wsTextEditorPage.evaluateXPath(XPathConstants.PROLOG_AUTHORS_CREATOR);
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
	        insertXmlFragment(prologCreator.getContributorFragment(), XPathConstants.LAST_PROLOG_AUTHOR, RelativeInsertPosition.INSERT_LOCATION_AFTER);
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
	            documentController.insertXMLFragment(
	                prettyPrintFragment(xmlFragment), 
	                xPath, 
	                position);
	          } catch (TextOperationException e) {
	            logger.debug(e.getMessage(), e);
	          }
	        }

	      });
	    }
	  }
	  
	  /**
	   * Pretty print the given fragment.
	   * 
	   * @param fragment The fragment.
	   * @return The pretty printed content.
	   */
	  private String prettyPrintFragment(String fragment) {
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
	  private URL getCurrentEditorLocation(WSXMLTextEditorPage textPage) {
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
	   * @return A xPath where to insert the prolog node or <code>null</code>.
	   * 
	   * @throws BadLocationException
	   * @throws XPathException
	   */
  private String findPrologXPath(WSXMLTextEditorPage page) {
    String toReturn = null;
    ContextElement nodeToInsertAfter = null;
    
    // Find the context where prolog element can be inserted.
    WhatElementsCanGoHereContext context;
    try {
      context = findPrologContext(page);
      if (context != null) {
        List<ContextElement> previous = context.getPreviousSiblingElements();
        if (previous != null && !previous.isEmpty()) {
          // Get the previous sibling.
          nodeToInsertAfter = previous.get(previous.size() - 1);
          // Generate the XPath.
          toReturn = XPathConstants.ROOT_TOPIC + "/" + nodeToInsertAfter.getQName();
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
     * @return A context where prolog element can go or <code>null</code>.
     * @throws BadLocationException 
     * @throws XPathException 
     */
  private WhatElementsCanGoHereContext findPrologContext(WSXMLTextEditorPage page) throws BadLocationException, XPathException {
    WhatElementsCanGoHereContext toReturn = null;

    // Get the XmlSchemaManager.
    WSTextXMLSchemaManager schemaManager = page.getXMLSchemaManager();

      // Get all child of root topic.
      WSXMLTextNodeRange[] topicChild = page.findElementsByXPath(XPathConstants.ROOT_TOPIC_CHILD);
      
      int childNo = topicChild.length;
    // Iterate over topic child
    loop: for (int j = 0; j < childNo; j++) {
      WSXMLTextNodeRange currentNode = topicChild[j];
      // Get the offset of next line.
      int offset = wsTextEditorPage.getOffsetOfLineStart(currentNode.getStartLine() + 1);
      WhatElementsCanGoHereContext currentContext = schemaManager.createWhatElementsCanGoHereContext(offset);
      if (currentContext != null) {
        // Analyze if current context can contain the prolog element.
        List<CIElement> possible = schemaManager.whatElementsCanGoHere(currentContext);
        if (possible != null) {
          // Iterate over possible elements.
          int size = possible.size();
          for (int i = 0; i < size; i++) {
            CIElement ciElement = possible.get(i);
            if (ciElement.getName().equals(XmlElementsConstants.PROLOG_NAME)) {
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
