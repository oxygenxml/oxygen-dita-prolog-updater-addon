package com.oxygenxml.prolog.updater.utils;

import com.oxygenxml.prolog.updater.dita.editor.DocumentType;
import com.oxygenxml.prolog.updater.tags.OptionKeys;

import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.options.WSOptionsStorage;

/**
 * Utility class where XPaths for elements are generated.
 *
 * @author cosmin_duna
 */
public class ElementXPathUtils {

	/**
	 * Get the XPath of prolog element.
	 * 
	 * @param documentType
	 *          The document type( {@link DocumentType#TOPIC},
	 *          {@link DocumentType#MAP}, {@link DocumentType#BOOKMAP} 
	 *          or {@link DocumentType#SUBJECT_SCHEME} ).
	 * @return The XPath of prolog element
	 */
	public static String getPrologXpath(DocumentType documentType) {
		String toRet = ElementXPathConstants.TOPICMETA_XPATH;
		
		if(DocumentType.TOPIC.equals(documentType)) {
			toRet = ElementXPathConstants.PROLOG_XPATH;
		} else if(DocumentType.SUBJECT_SCHEME.equals(documentType)) {
			toRet = ElementXPathConstants.TOPICMETA_SUBJECT_SCHEMA_XPATH;
		}

		return toRet;
	}

	/**
	 * Get the XPath of the root element.
	 * 
	 * @param documentType
	 *          The document type( {@link DocumentType#TOPIC},
	 *          {@link DocumentType#MAP}, {@link DocumentType#BOOKMAP} 
	 *          or {@link DocumentType#SUBJECT_SCHEME} ).
	 * @return The XPath of the root element.
	 */
	public static String getRootXpath(DocumentType documentType) {
		return documentType.equals(DocumentType.TOPIC) ? ElementXPathConstants.ROOT_TOPIC_XPATH
				: ElementXPathConstants.ROOT_MAP_XPATH;
	}

	/**
	 * Get the XPath of the child of the root element.
	 * 
	 * @param documentType
	 *          The document type( {@link DocumentType#TOPIC},
	 *          {@link DocumentType#MAP}, {@link DocumentType#BOOKMAP} 
	 *          or {@link DocumentType#SUBJECT_SCHEME} ).
	 * @return The XPath of the child of the root element.
	 */
	public static String getRootChildXpath(DocumentType documentType) {
		return documentType.equals(DocumentType.TOPIC) ? ElementXPathConstants.ROOT_TOPIC_CHILD
				: ElementXPathConstants.ROOT_MAP_CHILD;
	}

	/**
	 * Get the XPath of the author element.
	 * 
	 * @param documentType
	 *          The document type( {@link DocumentType#TOPIC},
	 *          {@link DocumentType#MAP}, {@link DocumentType#BOOKMAP} 
	 *          or {@link DocumentType#SUBJECT_SCHEME} ).
	 * @return The XPath of the author element.
	 */
	public static String getAuthorXpath(DocumentType documentType) {
		return documentType.equals(DocumentType.TOPIC) ? ElementXPathConstants.PROLOG_AUTHORS
				: ElementXPathConstants.TOPICMETA_AUTHORS;
	}

	/**
	 * Get the XPath of the last author element.
	 * 
	 * @param documentType
	 *          The document type( {@link DocumentType#TOPIC},
	 *          {@link DocumentType#MAP}, {@link DocumentType#BOOKMAP} 
	 *          or {@link DocumentType#SUBJECT_SCHEME} ).
	 * @return The XPath of last the author element.
	 */
	public static String getLastAuthorXpath(DocumentType documentType) {
		return documentType.equals(DocumentType.TOPIC) ? ElementXPathConstants.LAST_PROLOG_AUTHOR
				: ElementXPathConstants.LAST_TOPICMETA_AUTHOR;
	}

	/**
	 * Get the XPath of the creator element.
	 * 
	 * @param documentType
	 *          The document type( {@link DocumentType#TOPIC},
	 *          {@link DocumentType#MAP}, {@link DocumentType#BOOKMAP} 
	 *          or {@link DocumentType#SUBJECT_SCHEME} ).
	 * @return The XPath of last the creator element.
	 */
	public static String getAuthorCreatorXpath(DocumentType documentType) {
		return documentType.equals(DocumentType.TOPIC) ? getAuthorCreatorXPathInternal(ElementXPathConstants.PROLOG_XPATH)
				: getAuthorCreatorXPathInternal(ElementXPathConstants.TOPICMETA_XPATH);
	}
	
  /**
   * Get the xpath for retrieving all creator authors from prolog/topicmeta element.
   * 
   * @param prologTypeXpath The xpath to prolog/topicmeta element.
   *  
   * @return The xpath for retrieving all creator authors from prolog/topicmeta element.
   */
  private static String getAuthorCreatorXPathInternal(String prologTypeXpath) {
    StringBuilder xpath = new StringBuilder();
    xpath.append(prologTypeXpath);
    xpath.append("/author[@type='");
    
    String creatorTypeValue = XmlElementsConstants.CREATOR_TYPE;
    PluginWorkspace pluginWorkspace = PluginWorkspaceProvider.getPluginWorkspace();
    if (pluginWorkspace != null) {
      WSOptionsStorage optionsStorage = pluginWorkspace.getOptionsStorage();
      String valueFromOptions = optionsStorage.getOption(OptionKeys.CUSTOM_CREATOR_TYPE_VALUE, "");
      if (!valueFromOptions.isEmpty()) {
        creatorTypeValue = valueFromOptions;
      }
    }
    xpath.append(creatorTypeValue).append("']");

    return xpath.toString();
  }
  
  /**
   * Get the XPath of the contributor element with the given author name.
   * 
   * @param documentType
   *          The document type( {@link DocumentType#TOPIC},
   *          {@link DocumentType#MAP}, {@link DocumentType#BOOKMAP} 
   *          or {@link DocumentType#SUBJECT_SCHEME} ).
   * @param authorName The name of the author.         
   *        
   * @return The XPath of last the creator element.
   */
  public static String getAuthorContributorXpath(DocumentType documentType, String authorName) {
    return documentType.equals(DocumentType.TOPIC) 
        ? getAuthorContributorXPathInternal(ElementXPathConstants.PROLOG_XPATH, authorName)
        : getAuthorContributorXPathInternal(ElementXPathConstants.TOPICMETA_XPATH, authorName);
  }
  
  /**
   * Get the xpath for retrieving the contributor elements with the given author name.
   * 
   * @param prologTypeXpath The xpath to prolog/topicmeta element.
   * @param authorName The name of the author.   
   * 
   * @return The xpath for retrieving all contributor elements with the given author name.
   */
  private static String getAuthorContributorXPathInternal(String prologTypeXpath, String authorName) {
    StringBuilder xpath = new StringBuilder();
    xpath.append(prologTypeXpath);
    xpath.append("/author[@type='");
    
    String creatorTypeValue = XmlElementsConstants.CONTRIBUTOR_TYPE;
    PluginWorkspace pluginWorkspace = PluginWorkspaceProvider.getPluginWorkspace();
    if (pluginWorkspace != null) {
      WSOptionsStorage optionsStorage = pluginWorkspace.getOptionsStorage();
      String valueFromOptions = optionsStorage.getOption(OptionKeys.CUSTOM_CONTRIBUTOR_TYPE_VALUE, "");
      if (!valueFromOptions.isEmpty()) {
        creatorTypeValue = valueFromOptions;
      }
    }
    xpath.append(creatorTypeValue);
    xpath.append("' and text()= '").append(authorName).append("']");

    return xpath.toString();
  }

	/**
	 * Get the XPath of the critdates element.
	 * 
	 * @param documentType
	 *          The document type( {@link DocumentType#TOPIC},
	 *          {@link DocumentType#MAP}, {@link DocumentType#BOOKMAP} 
	 *          or {@link DocumentType#SUBJECT_SCHEME} ).
	 * @return The XPath of last the critdates element.
	 */

	public static String getCritdatesXpath(DocumentType documentType) {
		return documentType.equals(DocumentType.TOPIC) ? ElementXPathConstants.PROLOG_CRITDATES
				: ElementXPathConstants.TOPICMETA_CRITDATES;
	}

	/**
	 * Get the XPath of the created element.
	 * 
	 * @param documentType
	 *          The document type( {@link DocumentType#TOPIC},
	 *          {@link DocumentType#MAP}, {@link DocumentType#BOOKMAP} 
	 *          or {@link DocumentType#SUBJECT_SCHEME} ).
	 * @return The XPath of last the created element.
	 */

	public static String getCreatedXpath(DocumentType documentType) {
		return documentType.equals(DocumentType.TOPIC) ? ElementXPathConstants.PROLOG_CREATED_ELEMENT
				: ElementXPathConstants.TOPICMETA_CREATED_ELEMENT;
	}

	/**
	 * Private constructor. Avoid instantiation.
	 */
	private ElementXPathUtils() {
		// Nothing
	}
}
