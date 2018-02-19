package com.oxygenxml.prolog.updater.utils;

import com.oxygenxml.prolog.updater.dita.editor.DocumentType;

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
		return documentType.equals(DocumentType.TOPIC) ? ElementXPathConstants.PROLOG_AUTHORS_CREATOR
				: ElementXPathConstants.TOPICMETA_AUTHORS_CREATOR;
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
