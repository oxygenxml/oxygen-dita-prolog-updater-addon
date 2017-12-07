package com.oxygenxml.prolog.updater.utils;

import com.oxygenxml.prolog.updater.dita.editor.DocumentType;

/**
 * XPath constants
 * 
 * @author adrian_sorop
 */
public class XPathConstants {
  
  /**
   * XPath for topic body.
   */
  public static final String TOPIC_BODY = "//*[contains(@class,' topic/body ')]";

  /**
   * XPath expression for Prolog element.
   */
  public static final String PROLOG_XPATH = "//*[contains(@class,' topic/prolog ')]";
  
  /**
   * XPath expression for topicMeta element(prolog element in DITA map).
   */
  public static final String TOPICMETA_XPATH = "//*[contains(@class,' map/topicmeta ')]";
  
  /**
   * Xpath expression for critdates element.
   */
  public static final String PROLOG_CRITDATES = PROLOG_XPATH + "/critdates";
  
  /**
   * Xpath expression for critdates element from topicmeta.
   */
  public static final String TOPICMETA_CRITDATES = TOPICMETA_XPATH + "/critdates";
  
  /**
   * XPath for last author element from prolog.
   */
  public static final String LAST_PROLOG_AUTHOR = PROLOG_XPATH+"/author[last()]";
  
  /**
   * XPath for last author element from prolog.
   */
  public static final String LAST_TOPICMETA_AUTHOR = TOPICMETA_XPATH+"/author[last()]";
  
  /**
   * Retrieve all authors from prolog element.
   */
  public static final String PROLOG_AUTHORS = PROLOG_XPATH + "/author";
  
  /**
   * Retrieve all authors from topicMeta element.
   */
  public static final String TOPICMETA_AUTHORS = TOPICMETA_XPATH + "/author";
  
  /**
   * Retrieve all "creator" authors from prolog element.
   */
  public static final String PROLOG_AUTHORS_CREATOR = PROLOG_XPATH +"/author[@type='creator']";
  
  /**
   * Retrieve all "creator" authors from topicMeta element.
   */
  public static final String TOPICMETA_AUTHORS_CREATOR = TOPICMETA_XPATH +"/author[@type='creator']";
  
  /**
   * Returns a "created" element.
   */
  public static final String PROLOG_CREATED_ELEMENT = PROLOG_CRITDATES + "/created";
  
  /**
   * Returns a "created" element from topicmeta.
   */
  public static final String TOPICMETA_CREATED_ELEMENT = TOPICMETA_CRITDATES + "/created";
  
  /**
   * XPath for map root.
   */
  public static final String ROOT_MAP_XPATH = "/*[1][contains(@class,' map/map ')]";
  
  /**
   * XPath for bookmap root.
   */
  public static final String ROOT_BOOKMAP_XPATH = "/*[1][contains(@class,' bookmap/bookmap ')]";
  
  /**
   * XPath for topic root.
   */
  public static final String ROOT_TOPIC_XPATH = "/*[1][contains(@class, ' topic/topic ')]";
  
  /**
   * XPath for root topic child.
   */
  public static final String ROOT_TOPIC_CHILD = ROOT_TOPIC_XPATH + "/*";
  
  /**
   * XPath for root map child.
   */
  public static final String ROOT_MAP_CHILD = ROOT_MAP_XPATH + "/*";
  
  /**
   * Get the XPath of prolog element.
   * @param documentType The document type( {@link DocumentType#TOPIC}, {@link DocumentType#MAP} or  {@link DocumentType#BOOKMAP}  ).
   * @return The XPath of prolog element
   */
  public static String getPrologXpath(DocumentType documentType) {
    if(documentType.equals(DocumentType.TOPIC)) {
      return PROLOG_XPATH;
    }else {
      return TOPICMETA_XPATH;
    }
  }
  
  /**
   * Get the XPath of the root element.
   * @param documentType The document type( {@link DocumentType#TOPIC}, {@link DocumentType#MAP} or  {@link DocumentType#BOOKMAP}  ).
   * @return The XPath of the root element.
   */
  public static String getRootXpath(DocumentType documentType) {
    if(documentType.equals(DocumentType.TOPIC)) {
      return ROOT_TOPIC_XPATH;
    }else {
      return ROOT_MAP_XPATH;
    }
  }
  
  /**
   * Get the XPath of the child of the root element.
   * @param documentType The document type( {@link DocumentType#TOPIC}, {@link DocumentType#MAP} or  {@link DocumentType#BOOKMAP}  ).
   * @return The XPath of the child of the root element.
   */
  public static String getRootChildXpath(DocumentType documentType) {
    if(documentType.equals(DocumentType.TOPIC)) {
      return ROOT_TOPIC_CHILD;
    }else {
      return ROOT_MAP_CHILD;
    }
  }
  
  /**
   * Get the XPath of the author element.
   * @param documentType The document type( {@link DocumentType#TOPIC}, {@link DocumentType#MAP} or  {@link DocumentType#BOOKMAP}  ).
   * @return The XPath of the author element.
   */
  public static String getAuthorXpath(DocumentType documentType) {
    if(documentType.equals(DocumentType.TOPIC)) {
      return PROLOG_AUTHORS;
    }
    else {
      return TOPICMETA_AUTHORS;
    }
  }
  
  /**
   * Get the XPath of the last author element.
   * @param documentType The document type( {@link DocumentType#TOPIC}, {@link DocumentType#MAP} or  {@link DocumentType#BOOKMAP}  ).
   * @return The XPath of last the author element.
   */
  public static String getLastAuthorXpath(DocumentType documentType) {
    if(documentType.equals(DocumentType.TOPIC)) {
      return LAST_PROLOG_AUTHOR;
    }
    else {
      return LAST_TOPICMETA_AUTHOR;
    }
  }
  
  /**
   * Get the XPath of the creator element.
   * @param documentType The document type( {@link DocumentType#TOPIC}, {@link DocumentType#MAP} or  {@link DocumentType#BOOKMAP}  ).
   * @return The XPath of last the creator element.
   */
  public static String getAuthorCreatorXpath(DocumentType documentType) {
    if(documentType.equals(DocumentType.TOPIC)) {
      return PROLOG_AUTHORS_CREATOR;
    }
    else {
      return TOPICMETA_AUTHORS_CREATOR;
    }
  }
  
  /**
   * Get the XPath of the critdates element.
   * @param documentType The document type( {@link DocumentType#TOPIC}, {@link DocumentType#MAP} or  {@link DocumentType#BOOKMAP}  ).
   * @return The XPath of last the critdates element.
   */

  public static String getCritdatesXpath(DocumentType documentType) {
    if(documentType.equals(DocumentType.TOPIC)) {
      return PROLOG_CRITDATES;
    }
    else {
      return TOPICMETA_CRITDATES;
    }
  }
  
  /**
   * Get the XPath of the created element.
   * @param documentType The document type( {@link DocumentType#TOPIC}, {@link DocumentType#MAP} or  {@link DocumentType#BOOKMAP}  ).
   * @return The XPath of last the created element.
   */

  public static String getCreatedXpath(DocumentType documentType) {
    if(documentType.equals(DocumentType.TOPIC)) {
      return PROLOG_CREATED_ELEMENT;
    }
    else {
      return TOPICMETA_CREATED_ELEMENT;
    }
  }
  
  
  /**
   * Private constructor. Avoid instantiation.
   */
  private XPathConstants() {
    // Nothing
  }
}
