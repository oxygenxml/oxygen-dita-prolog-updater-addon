package com.oxygenxml.prolog.updater.utils;
/**
 * Constant class where classes and attributes of XML elements are defined.
 */
public class XmlElementsConstants {
  /**
   * Class's value of prolog element.
   */
  public static final String PROLOG_CLASS = " topic/prolog ";

  /**
   * Name of prolog element.
   */
  public static final String PROLOG_NAME = "prolog";

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
   * public constructor.
   */
  private XmlElementsConstants() {
    // Avoid instantiation.
  }
}
