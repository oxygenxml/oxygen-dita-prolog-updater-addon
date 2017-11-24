package com.oxygenxml.prolog.updater.utils;

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
  public static final String PROLOG_XPATH = "//*[contains(@class,'topic/prolog')]";
  
  /**
   * Xpath expression for critdates element.
   */
  public static final String PROLOG_CRITDATES = PROLOG_XPATH + "/critdates";
  
  /**
   * XPath for last author element from prolog.
   */
  public static final String LAST_PROLOG_AUTHOR = PROLOG_XPATH+"/author[last()]";
  
  /**
   * Retrieve all authors from prolog element.
   */
  public static final String PROLOG_AUTHORS = PROLOG_XPATH + "/author";
  
  /**
   * Retrieve all "creator" authors from prolog element.
   */
  public static final String PROLOG_AUTHORS_CREATOR = PROLOG_XPATH +"/author[@type='creator']";
  
  /**
   * Returns a "created" element.
   */
  public static final String PROLOG_CREATED_ELEMENT = PROLOG_CRITDATES + "/created";
  
  /**
   * 
   */
  public static final String FIRST_BODY_IN_ROOT_TOPIC = "/*[1][contains(@class, ' topic/topic ')]/*[contains(@class, ' topic/body ')]";
  
  public static final String ROOT_TOPIC = "/*[1][contains(@class, ' topic/topic ')]";
  
  public static final String ROOT_TOPIC_CHILD = ROOT_TOPIC + "/*";
  
  /**
   * Private constructor. Avoid instantiation.
   */
  private XPathConstants() {
    // Nothing
  }
}
