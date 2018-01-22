package com.oxygenxml.prolog.updater.utils;

/**
 * Utility class where xml elements fragments are generated.
 * 
 * @author adrian_sorop
 */
public class XMLFragmentUtils {
	/**
	 * Date pattern.
	 */
	public static final String DATE_PATTERN = "yyyy/MM/dd";

	/**
	 * Private constructor. Avoid instantiation.
	 */
	private XMLFragmentUtils() {
	}

	/**
	 * Constructs an element with the date.
	 * 
	 * @param date
	 *          The formatted date as string.
	 * @return The critdates element with the date.
	 */
	public static String createCritdateTag(String date) {
		if (date == null) {
			return null;
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append("<critdates>");
			sb.append(date);
			sb.append("</critdates>");
			return sb.toString();
		}
	}

	/**
	 * Creates an XML fragment an author tag with the type attribute.
	 * 
	 * @param authorName
	 *          The name of the author.
	 * @param type
	 *          The value of type attribute.
	 * 
	 * @return The author element.
	 */
	public static StringBuilder createAuthorFragment(String authorName, String type) {
		StringBuilder toReturn = new StringBuilder();
		toReturn.append("<author type=\"").append(type).append("\">");
		toReturn.append(authorName);
		toReturn.append("</author>");

		return toReturn;
	}

	/**
	 * Creates a XML element: elementName attributeName="attributeValue". If the
	 * name of the element is null it will generate a comment with the attribute
	 * value.
	 * 
	 * @param elementName
	 *          Name of the element. If <code>null</code> a comment with the
	 *          attribute value is generated.
	 * @param attributeName
	 *          Name of the attribute.
	 * @param attributeValue
	 *          Value of the attribute.
	 * @return
	 */
	public static StringBuilder createGeneralXmlFragment(String elementName, String attributeName,
			String attributeValue) {
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
