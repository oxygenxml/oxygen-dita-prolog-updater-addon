package com.oxygenxml.prolog.updater.prolog.content;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.oxygenxml.prolog.updater.utils.XMLFragmentUtils;
import com.oxygenxml.prolog.updater.utils.XmlElementsConstants;

/**
 * Creates and contains all content from prolog. 
 * @author cosmin_duna
 *
 */
public class PrologContentCreator {

  /**
	 * XML fragment for author that has type creator.
	 */
	private StringBuilder creatorFragment;
	
	/**
	 * XML fragment for author that has type contributor.
	 */
	private StringBuilder contributorXML;
	
	/**
	 * XML fragment for create tag
	 */
	private  StringBuilder createdDateXML;
	
	/**
	 * XML fragment for revised tag.
	 */
	private  StringBuilder revisedDateFragment;
	
	/**
	 * The local date in format "yyyy/MM/dd".
	 */
	private  String localDate;
	/**
	 * The name of the author
	 */
	private String authorName;
	
	/**
	 * Singleton instance.
	 */
	private static PrologContentCreator instance = null;
	
	/**
	 * @return Returns the singleton instance.
	 */
	public static PrologContentCreator getInstance() {
	  if (instance == null) {
	    instance = new PrologContentCreator();
	  }
	  return instance;
	}

	/**
	 * Constructor.
	 */
	private PrologContentCreator() {
		// Generate current date in a specified format.
		localDate = new SimpleDateFormat(XMLFragmentUtils.DATE_PATTERN).format(new Date());
		// Generate the created date element.
		createdDateXML = XMLFragmentUtils.createGeneralXmlFragment("created", "date", localDate);
	}
	
	/**
	 * Sets the author name.
	 * 
	 * @param author The name of the author.
	 */
	public void setAuthor(String author) {
	  this.authorName = author;
	  if (authorName != null) {
	    // Creator
	    creatorFragment = XMLFragmentUtils.createAuthorFragment(authorName, XmlElementsConstants.CREATOR_TYPE);
	    // Contributor
	    contributorXML = XMLFragmentUtils.createAuthorFragment(authorName, XmlElementsConstants.CONTRIBUTOR_TYPE);

	    // Generate the revised date element.
	    // Add the author name as comment.
	    revisedDateFragment = XMLFragmentUtils.createGeneralXmlFragment(null, null, authorName);
	    StringBuilder revised = XMLFragmentUtils.createGeneralXmlFragment("revised", "modified", localDate);
	    revisedDateFragment = (revisedDateFragment == null) ? revised : revisedDateFragment.append(revised);
	  }
	}
	
	/**
	 * Get all XML fragment for prolog tag, according to given state of document.
	 * @param isNewDocument <code>true</code> if document is new, <code>false</code> otherwise.
	 * @return the XML fragment in String format
	 */
	public String getPrologFragment( boolean isNewDocument, boolean isTopic) { 
	  StringBuilder fragment = new StringBuilder();
	  fragment.append(ElementGetter.getPrologStartElement(isTopic));
	  if (isNewDocument) {
      fragment.append(creatorFragment);
      fragment.append(XMLFragmentUtils.createDateTag(createdDateXML.toString()));
    } else {
      fragment.append(contributorXML);
      fragment.append(XMLFragmentUtils.createDateTag(revisedDateFragment.toString()));
    }
	  fragment.append(ElementGetter.getPrologEndElement(isTopic));
	  
		return fragment.toString();
	}
	
	//Gettters
	public String getCreatorFragment() {
		return creatorFragment.toString();
	}
	
	/**
	 * @return The XML fragment for contributor author element.
	 */
	public String getContributorFragment() {
		return contributorXML.toString();
	}
	
	/**
   * @return The XML fragment for create date element.
   */
	public String getCreatedDateFragment() {
		return createdDateXML.toString();
	}

	 /**
   * @return The XML fragment for revised date element.
   */
	public String getRevisedDateFragment() {
		return revisedDateFragment.toString();
	}

	/**
	 * @return The name of author.
	 */
	public String getAuthor() {
		return authorName;
	}
	
	/**
	 * @return The actual local date.
	 */
	public String getLocalDate() {
		return localDate;
	}
	
	/**
	 * Get the XML fragment of author tag, according to given state of document.
	 * 
	 * @param isNewDocument <code>true</code> if document is new, <code>false</code> otherwise.
	 * @return the XML fragment in String format
	 */
	public String getPrologAuthorElement(boolean isNewDocument){
		return isNewDocument ? creatorFragment.toString() : contributorXML.toString();
	}
	
	/**
	 * Get the XML fragment of date element, according to given state of document.
	 * 
	 * @param isNewDocument <code>true</code> if document is new, <code>false</code> otherwise.
	 * @return the XML fragment in String format
	 */
	public String getDateFragment(boolean isNewDocument){
		return isNewDocument ? createdDateXML.toString() : revisedDateFragment.toString();
	}
	
}
