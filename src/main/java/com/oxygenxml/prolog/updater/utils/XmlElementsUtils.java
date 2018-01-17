package com.oxygenxml.prolog.updater.utils;

import com.oxygenxml.prolog.updater.dita.editor.DocumentType;

/**
 * Utility class where xml elements are generated.
 * @author cosmin_duna
 */
public class XmlElementsUtils {
	 /**
   * Get the class's value of prolog element.( topic/prolog or map/topicmeta)
   * @param documentType The document type( {@link DocumentType#TOPIC}, {@link DocumentType#MAP} or {@link DocumentType#BOOKMAP}  ).
   * @return The class's value of prolog element
   */
  public static String getPrologClass(DocumentType documentType) {
    return documentType.equals(DocumentType.TOPIC) ? XmlElementsConstants.PROLOG_CLASS : XmlElementsConstants.TOPICMETA_CLASS;
  }
  
  /**
   * Get the name of prolog element. (prolog, topicmeta or bookmeta).
   * @param documentType The document type( {@link DocumentType#TOPIC}, {@link DocumentType#MAP} or {@link DocumentType#BOOKMAP}  ).
   * @return The name of prolog element
   */
  public static String getPrologName(DocumentType documentType) {
    String name = XmlElementsConstants.PROLOG_NAME;
    if(documentType.equals(DocumentType.MAP)) {
      name = XmlElementsConstants.TOPICMETA_NAME;
    }else if (documentType.equals(DocumentType.BOOKMAP)){
      name = XmlElementsConstants.BOOKMETA_NAME;
    }
    return name;
  }
  
  /**
   * public constructor.
   */
  private XmlElementsUtils() {
    // Avoid instantiation.
  }
}
