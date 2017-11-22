package com.oxygenxml.prolog.updater;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.mozilla.javascript.ast.XmlString;

import com.oxygenxml.prolog.updater.utils.XMLStringFragmentUtils;
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
	 * Constructor.
	 * 
	 * @param authorName The name of author
	 */
	public PrologContentCreator (String authorName) {
		this.authorName = authorName;
		
		// Creator
		creatorFragment = XMLStringFragmentUtils.createAuthorFragment(authorName, XmlElementsConstants.CREATOR_TYPE);
		// Contributor
		contributorXML = XMLStringFragmentUtils.createAuthorFragment(authorName, XmlElementsConstants.CONTRIBUTOR_TYPE);
		
		// Generate current date in a specified format.
		localDate = XMLStringFragmentUtils.SIMPLE_DATE_FORMAT.format(new Date());
		
		// Generate the created date element.
		createdDateXML = XMLStringFragmentUtils.createGeneralXmlFragment("created", "date", localDate);
		
		// Generate the revised date element.
		// Add the author name as comment.
		revisedDateFragment = XMLStringFragmentUtils.createGeneralXmlFragment(null, null, authorName);
		StringBuilder revised = XMLStringFragmentUtils.createGeneralXmlFragment("revised", "modified", localDate);
		revisedDateFragment = (revisedDateFragment == null) ? revised : revisedDateFragment.append(revised);
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
      fragment.append(XMLStringFragmentUtils.createDateTag(createdDateXML.toString()));
    } else {
      fragment.append(contributorXML);
      fragment.append(XMLStringFragmentUtils.createDateTag(revisedDateFragment.toString()));
    }
	  fragment.append("</prolog>");
	  
		return fragment.toString();
	}
	
	//Gettters
	public String getCreatorFragment() {
		return creatorFragment.toString();
	}
	
	/**
	 * @return TODO
	 */
	public String getContributorFragment() {
		return contributorXML.toString();
	}

	public String getCreatedDateFragment() {
		return createdDateXML.toString();
	}

	public String getResivedModifiedFragment() {
		return revisedDateFragment.toString();
	}

	public String getAuthor() {
		return authorName;
	}

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
