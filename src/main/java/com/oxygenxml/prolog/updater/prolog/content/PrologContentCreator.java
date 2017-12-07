package com.oxygenxml.prolog.updater.prolog.content;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.oxygenxml.prolog.updater.tags.OptionKeys;
import com.oxygenxml.prolog.updater.utils.XMLFragmentUtils;
import com.oxygenxml.prolog.updater.utils.XmlElementsConstants;

import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.options.WSOptionsStorage;

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

  private boolean updateTopicCreator = true;

  private boolean updateMapCreator = true;

  private boolean updateTopicContributor = true;

  private boolean updateMapContributor = true;

  private boolean updateTopicProlog = true;

  private boolean updateMapProlog = true;

  private boolean updateTopicCreated = true;

  private boolean updateMapCreated = true;

  private boolean updateTopicRevised = true;

  private boolean updateMapRevised = true;
	

	/**
	 * Constructor.
	 * @param author The name of the author.
	 */
	public PrologContentCreator(String author) {
	  this.authorName = author;
    if (authorName != null) {
      // Creator
      creatorFragment = XMLFragmentUtils.createAuthorFragment(authorName, XmlElementsConstants.CREATOR_TYPE);
      // Contributor
      contributorXML = XMLFragmentUtils.createAuthorFragment(authorName, XmlElementsConstants.CONTRIBUTOR_TYPE);

      // Generate current date in a specified format.
      localDate = new SimpleDateFormat(XMLFragmentUtils.DATE_PATTERN).format(new Date());
      // Generate the created date element.
      createdDateXML = XMLFragmentUtils.createGeneralXmlFragment("created", "date", localDate);

      // Generate the revised date element.
      // Add the author name as comment.
      revisedDateFragment = XMLFragmentUtils.createGeneralXmlFragment(null, null, authorName);
      StringBuilder revised = XMLFragmentUtils.createGeneralXmlFragment("revised", "modified", localDate);
      revisedDateFragment = (revisedDateFragment == null) ? revised : revisedDateFragment.append(revised);
    }
	  
	  //Get the setting from option storage.
	  loadOptions();
	  
	}
	
	/**
	 * Get all XML fragment for prolog tag, according to given state of document.
	 * @param isNewDocument <code>true</code> if document is new, <code>false</code> otherwise.
	 * @param isTopic <code>true</code> if the document is a DITA Topic, <code>false</code> if it's a DITA Map.
	 * @return the XML fragment in String format
	 */
	public String getPrologFragment( boolean isNewDocument, boolean isTopic) { 
	  StringBuilder fragment = new StringBuilder();
	  
    if ((updateTopicProlog && isTopic) || (updateMapProlog && !isTopic)) {
      fragment.append('<');
      fragment.append(XmlElementsConstants.getPrologName(isTopic));
      fragment.append('>');
      if (isNewDocument) {
        if ((updateTopicCreator && isTopic) || (updateMapCreator && !isTopic)) {
        fragment.append(creatorFragment);
        }
        if ((updateTopicCreated && isTopic) || (updateMapCreated && !isTopic)) {
          fragment.append(XMLFragmentUtils.createCritdateTag(createdDateXML.toString()));
        }
      } else { 
        if ((updateTopicContributor && isTopic) || (updateMapContributor && !isTopic)) {
          fragment.append(contributorXML);
        }
        if ((updateTopicRevised && isTopic) || (updateMapRevised && !isTopic)) {
          fragment.append(XMLFragmentUtils.createCritdateTag(revisedDateFragment.toString()));
        }
      }
      fragment.append("</");
      fragment.append(XmlElementsConstants.getPrologName(isTopic));
      fragment.append('>');
    }
    String toReturn = fragment.toString();
    return toReturn.isEmpty() ? null : toReturn;
	}
	
	//Gettters
	/**
   * @param isTopic <code>true</code> if the document is a DITA Topic, <code>false</code> if it's a DITA Map.
   * @return The XML fragment for creator author element or <code>null</code> if the settings doesn't accept this element.
   */
	public String getCreatorFragment(boolean isTopic) {
	  if ((updateTopicCreator && isTopic) || (updateMapCreator && !isTopic)) {
	    return creatorFragment.toString();
	  }else {
	    return null; 
	  }
	}
	
	/**
	 * @param isTopic <code>true</code> if the document is a DITA Topic, <code>false</code> if it's a DITA Map.
	 * @return The XML fragment for contributor author element or <code>null</code> if the settings doesn't accept this element.
	 */
	public String getContributorFragment(boolean isTopic) {
		if ((updateTopicContributor && isTopic) || (updateMapContributor && !isTopic)) {
		  return contributorXML.toString();
		}else {
		  return null;
		}
	}
	
	/**
	 * @param isTopic <code>true</code> if the document is a DITA Topic, <code>false</code> if it's a DITA Map.
   * @return The XML fragment for create date element or <code>null</code> if the settings doesn't accept this element.
   */
	public String getCreatedDateFragment(boolean isTopic) {
	  if ((updateTopicCreated && isTopic) || (updateMapCreated && !isTopic)) {
	    return createdDateXML.toString();
	  }else {
	    return null;
	  }
	}

	 /**
	 * @param isTopic <code>true</code> if the document is a DITA Topic, <code>false</code> if it's a DITA Map.
   * @return The XML fragment for revised date element or <code>null</code> if the settings doesn't accept this element.
   */
	public String getRevisedDateFragment(boolean isTopic) {
    if ((updateTopicRevised && isTopic) || (updateMapRevised && !isTopic)) {
      return revisedDateFragment.toString();
    }else {
      return null;
    }
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
	 * @param isTopic <code>true</code> if the document is a DITA Topic, <code>false</code> if it's a DITA Map.
	 * @return the XML fragment in String format or <code>null</code> if the settings doesn't accept this element
	 */
	public String getPrologAuthorElement(boolean isNewDocument , boolean isTopic){
		return isNewDocument ? getCreatorFragment(isTopic) : getContributorFragment(isTopic);
	}
	
	/**
	 * Get the XML fragment of date element, according to given state of document.
	 * 
	 * @param isNewDocument <code>true</code> if document is new, <code>false</code> otherwise.
   * @param isTopic <code>true</code> if the document is a DITA Topic, <code>false</code> if it's a DITA Map.
	 * @return the XML fragment in String format or <code>null</code> if the settings doesn't accept this element
	 */
	public String getDateFragment(boolean isNewDocument, boolean isTopic){
		return isNewDocument ? getCreatedDateFragment(isTopic) : getRevisedDateFragment(isTopic);
	}
	
	/**
	 * Get the options from WSOptionsStorage.
	 */
	private void loadOptions() {
	  PluginWorkspace pluginWorkspace = PluginWorkspaceProvider.getPluginWorkspace();
	  if(pluginWorkspace != null) {
	    WSOptionsStorage optionsStorage = pluginWorkspace.getOptionsStorage();
	    
	    String value = optionsStorage.getOption(OptionKeys.TOPIC_ENABLE_UPDATE_ON_SAVE, String.valueOf(true));
	    updateTopicProlog = Boolean.parseBoolean(value);
	    value = optionsStorage.getOption(OptionKeys.MAP_ENABLE_UPDATE_ON_SAVE, String.valueOf(true));
	    updateMapProlog = Boolean.parseBoolean(value);
	    
	    value = optionsStorage.getOption(OptionKeys.TOPIC_SET_CREATOR, String.valueOf(true));
	    updateTopicCreator = Boolean.parseBoolean(value);
	    value = optionsStorage.getOption(OptionKeys.MAP_SET_CREATOR, String.valueOf(true));
	    updateMapCreator = Boolean.parseBoolean(value);
	    value = optionsStorage.getOption(OptionKeys.TOPIC_UPDATE_CONTRIBUTOR, String.valueOf(true));
	    updateTopicContributor = Boolean.parseBoolean(value);
	    value = optionsStorage.getOption(OptionKeys.MAP_UPDATE_CONTRIBUTOR, String.valueOf(true));
	    updateMapContributor = Boolean.parseBoolean(value);
	    
	    value = optionsStorage.getOption(OptionKeys.TOPIC_SET_CREATED_DATE, String.valueOf(true));
	    updateTopicCreated = Boolean.parseBoolean(value);
	    value = optionsStorage.getOption(OptionKeys.MAP_SET_CREATED_DATE, String.valueOf(true));
	    updateMapCreated = Boolean.parseBoolean(value);
	    value = optionsStorage.getOption(OptionKeys.TOPIC_UPDATE_REVISED_DATES, String.valueOf(true));
	    updateTopicRevised = Boolean.parseBoolean(value);
	    value = optionsStorage.getOption(OptionKeys.MAP_UPDATE_REVISED_DATES, String.valueOf(true));
	    updateMapRevised = Boolean.parseBoolean(value);
	  }
	}
}
