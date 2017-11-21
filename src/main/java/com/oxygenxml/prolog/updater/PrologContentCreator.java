package com.oxygenxml.prolog.updater;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Creates and contains all content from prolog. 
 * @author cosmin_duna
 *
 */
public class PrologContentCreator {
  
  /**
   * Date pattern.
   */
	private static final String DATE_PATTERN = "yyyy/MM/dd";
	
  /**
	 * XML fragment for author that has type creator.
	 */
	private StringBuilder creatorFragment;
	
	/**
	 * XML fragment for author that has type contributor.
	 */
	private StringBuilder contributorFragment;
	
	/**
	 * XML fragment for create tag
	 */
	private  StringBuilder createdDateFragment;
	
	/**
	 * XML fragment for revised tag.
	 */
	private  StringBuilder revisedDateFragment;
	
	/**
	 * The local date in format "yyyy/MM/dd".
	 */
	private  String localDate;
	/**
	 * The author
	 */
	private String author;
	
	/**
	 * Constructor.
	 * 
	 * @param author The name of author
	 */
	public PrologContentCreator (String author) {
		this.author = author;
		
		// Creator
		creatorFragment = new StringBuilder();
		creatorFragment.append("<author type=\"creator\">").append(author).append("</author>");
		// Contributor
		contributorFragment = new StringBuilder();
		contributorFragment.append("<author type=\"contributor\">").append(author).append("</author>");
		
		// Generate current date in a specified format.
		localDate = new SimpleDateFormat(DATE_PATTERN).format(new Date());
		
		// Generate the created date element.
		createdDateFragment = new StringBuilder();
		createdDateFragment.append("<created date=\"").append(localDate).append("\"/>");
		
		// Generate the revised date element.
		revisedDateFragment = new StringBuilder();
		// Add the author name as comment.
		revisedDateFragment.append("<!--").append(author).append("-->");
		revisedDateFragment.append("<revised modified=\"").append(localDate).append("\"/>");
	}

	/**
	 * Get all XML fragment for prolog tag, according to given state of document.
	 * @param isNewDocument <code>true</code> if document is new, <code>false</code> otherwise.
	 * @return the XML fragment in String format
	 */
	public String getPrologXMLFragment( boolean isNewDocument) { 
	  StringBuilder fragment = new StringBuilder();
	  
	  fragment.append("<prolog>");
	  if (isNewDocument) {
      fragment.append(creatorFragment);
      fragment.append(createDateTag(createdDateFragment.toString()));
    } else {
      fragment.append(contributorFragment);
      fragment.append(createDateTag(revisedDateFragment.toString()));
    }
	  fragment.append("</prolog>");
	  
		return fragment.toString();
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

	
	//Gettters
	public String getAuthorCreatorFragment() {
		return creatorFragment.toString();
	}
	
	/**
	 * @return TODO
	 */
	public String getAuthorContributorFrag() {
		return contributorFragment.toString();
	}

	public String getCreatedDateXmlFragment() {
		return createdDateFragment.toString();
	}

	public String getResivedModifiedXmlFragment() {
		return revisedDateFragment.toString();
	}

	public String getAuthor() {
		return author;
	}

	public String getLocalDate() {
		return localDate;
	}
	
	/**
	 * Get the XML fragment of author tag, according to given state of document.
	 * @param isNewDocument <code>true</code> if document is new, <code>false</code> otherwise.
	 * @return the XML fragment in String format
	 */
	public String getPrologAuthorElement(boolean isNewDocument){
		return (isNewDocument)? creatorFragment.toString() : contributorFragment.toString();
	}
	
	/**
	 * Get the XML fragment of date element, according to given state of document.
	 * @param isNewDocument <code>true</code> if document is new, <code>false</code> otherwise.
	 * @return the XML fragment in String format
	 */
	public String getDateFragment(boolean isNewDocument){
		return isNewDocument ? createdDateFragment.toString() : revisedDateFragment.toString();
	}
	
}
