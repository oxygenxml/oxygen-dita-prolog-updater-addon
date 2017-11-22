package com.oxygenxml.prolog.updater.utils;

/**
 * Utility class where xml elements fragments are generated.
 * @author adrian_sorop
 */
public class XMLStringFragmentUtils {
  /**
   * Date pattern.
   */
  public static final String DATE_PATTERN = "yyyy/MM/dd";
  
  /**
   * Private constructor. Avoid instantiation.
   */
  private XMLStringFragmentUtils() {
  }
  /**
   * Constructs an element with the date.
   * 
   * @param date The formatted date as string.
   * @return The critdates element with the date.
   */
  public static String createDateTag(String date) {
    StringBuilder sb = new StringBuilder();
    sb.append("<critdates>");
    sb.append(date);
    sb.append("</critdates>");
    return sb.toString();
  }
  
  /**
   * Creates an XML fragment an author tag with the type attribute.
   * 
   * @param authorName  The name of the author.
   * @param type        The value of type attribute.
   * 
   * @return            The author element.
   */
  public static StringBuilder createAuthorFragment(String authorName, String type) {
    StringBuilder toReturn = new StringBuilder();
    toReturn.append("<author type=\"" + type+ "\">").append(authorName).append("</author>");
    return toReturn;
  }
  
  public static StringBuilder createGeneralXmlFragment(String elementName, String attributeName, String attributeValue){
    StringBuilder toReturn = new StringBuilder();
    if (elementName != null) {
      toReturn.append("<").append(elementName).append(" ");
      if (attributeName != null) {
        toReturn.append(attributeName).append("=\"").append(attributeValue).append("\"");
      }
      toReturn.append("/>");
    } else {
      // Generate comment with the attribute value.
      toReturn.append("<!--").append(attributeValue).append("-->");
    }
    return toReturn;
  }
  
}
