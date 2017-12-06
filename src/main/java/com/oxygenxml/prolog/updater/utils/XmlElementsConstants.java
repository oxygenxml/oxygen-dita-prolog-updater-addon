package com.oxygenxml.prolog.updater.utils;
/**
 * Constant class where classes and attributes of XML elements are defined.
 */
public class XmlElementsConstants {
  /**
   * The class attribute.
   */
  public static final String CLASS = "class";
  
  /**
   * Class's value of prolog element.
   */
  public static final String PROLOG_CLASS = " topic/prolog ";

  /**
   * Class's value of topicMeta element(prolog element from map).
   */
  public static final String TOPICMETA_CLASS = " map/topicmeta ";
  
  /**
   * Name of prolog element.
   */
  public static final String PROLOG_NAME = "prolog";

  /**
   * Name of topicMeta element.
   */
  public static final String TOPICMETA_NAME = "topicmeta";

  
  /**
   * Class's value of body element.
   */
  public static final String TOPIC_BODY_CLASS = " topic/body ";

  /**
   * Class's value of critdates element.
   */
  public static final String TOPIC_CRITDATES_CLASS = " topic/critdates ";
  /**
   * Class's value of created element.
   */
  public static final String CREATED_DATE_ELEMENT_CLASS = " topic/created ";
  /**
   * Class's value of revised element.
   */
  public static final String REVISED_DATE_ELEMENT_CLASS = " topic/revised ";
 
  /**
   * Modified attribute from revised element.
   */
  public static final String MODIFIED_ATTRIBUTE = "modified";
  
  /**
   * Type's value of the contributor author.
   */
  public static final String CONTRIBUTOR_TYPE = "contributor";

  /**
   * Type's value of the creator author.
   */
  public static final String CREATOR_TYPE = "creator";

  /**
   * Class's value of author element.
   */
  public static final String PROLOG_AUTHOR_ELEMENT_CLASS = " topic/author ";

  
  /**
   * Get the class's value of prolog element.( topic/prolog or map/topicmeta)
   * @param isTopic <code>true</code> if it's topic, <code>false</code> if it's a map.
   * @return The class's value of prolog element
   */
  public static String getPrologClass(boolean isTopic) {
    if(isTopic) {
      return PROLOG_CLASS;
    }else {
      return TOPICMETA_CLASS;
    }
  }
  
  /**
   * Get the name of prolog element. (prolog or topicmeta)
   * @param isTopic <code>true</code> if it's topic, <code>false</code> if it's a map.
   * @return The name of prolog element
   */
  public static String getPrologName(boolean isTopic) {
    if(isTopic) {
      return PROLOG_NAME;
    }else {
      return TOPICMETA_NAME;
    }
  }
  
  /**
   * public constructor.
   */
  private XmlElementsConstants() {
    // Avoid instantiation.
  }
}
