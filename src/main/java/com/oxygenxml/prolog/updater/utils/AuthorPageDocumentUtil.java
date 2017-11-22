package com.oxygenxml.prolog.updater.utils;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;

import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;

/**
 * A collection of utility methods to be used in the author page.
 *  
 * @author adrian_sorop
 */
public class AuthorPageDocumentUtil {
  
  /**
   * Logger for logging.
   */
  private static final Logger logger = Logger.getLogger(AuthorPageDocumentUtil.class.getName());
  
  /**
   * Private constructor. Avoid instantiation.
   */
  private AuthorPageDocumentUtil() {
    // Nothing
  }
  
  /**
   * Search and return a list of elements with a specific class.
   * 
   * @param rootElement The node where the search should start.
   * @return A list with elements with a specific class. If no element is found,
   * the method returns an empty list.
   */
  public static List<AuthorElement> findElementsByClass(AuthorElement rootElement, String classValue) {
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
   * Search and return first element identified by class value.
   * 
   * @param rootElement Document root element.
   * @return <code>null</code> or the identified element.
   */
  public static AuthorElement findElementByClass(AuthorElement rootElement, String classValue) {
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
   * Search for author according to given type.
   * 
   * @param authors The list with authors.
   * @param type The searched author type ( {@link XmlElementsConstants#CREATOR_TYPE} or {@link XmlElementsConstants#CONTRIBUTOR_TYPE})
   * @param authorName The name of the author.
   * 
   * @return <code>true</code> if was found a author with given type, <code>false</code> otherwise.
   * @throws BadLocationException
   */
  public static boolean hasAuthor(List<AuthorElement>authors, String type, String authorName) throws BadLocationException {
    boolean foundAuthor = false;
    // Iterate over authors.
    for (AuthorElement el : authors) {
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
            foundAuthor = authorName.equals(textContent);
          } 

          if (foundAuthor) {
            break;
          }
        }
      }
    }
    return foundAuthor;
  }
  
  /**
   * Inserts an element schema aware.
   * 
   * @param controller The document controller.
   * @param xmlFragment The xml fragment.
   * @param offset The offset.
   */
  public static void insertFragmentSchemaAware(final AuthorDocumentController controller, final String xmlFragment, final int offset){
    if (controller != null && xmlFragment != null && offset != -1) {
      ThreadUtils.invokeSynchronously(new Runnable() {
        public void run() {
          try {
            controller.insertXMLFragmentSchemaAware(xmlFragment, offset);
          } catch (AuthorOperationException e) {
            logger.debug(e.getMessage(), e);
          }
        }
      });
    }
  }
  
}
