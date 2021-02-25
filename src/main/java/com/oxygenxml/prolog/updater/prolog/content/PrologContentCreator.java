package com.oxygenxml.prolog.updater.prolog.content;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.oxygenxml.prolog.updater.dita.editor.DocumentType;
import com.oxygenxml.prolog.updater.tags.OptionKeys;
import com.oxygenxml.prolog.updater.utils.XMLFragmentUtils;
import com.oxygenxml.prolog.updater.utils.XmlElementsConstants;
import com.oxygenxml.prolog.updater.utils.XmlElementsUtils;

import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.options.WSOptionsStorage;

/**
 * Creates and contains all content from prolog.
 * 
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
	private StringBuilder createdDateXML;

	/**
	 * XML fragment for revised tag.
	 */
	private StringBuilder revisedDateFragment;

	/**
	 * The local date in given format.
	 */
	private String localDate;
	
	/**
	 * The name of the author
	 */
	private String authorName;

	/**
	 * <code>true</code> if the creator must be set in DITA Topic.
	 */
	private boolean setTopicCreator = true;

	/**
	 * <code>true</code> if the creator must be set in DITA Map.
	 */
	private boolean setMapCreator = true;

	/**
	 * <code>true</code> if the contributor must be update in DITA Topic.
	 */
	private boolean updateTopicContributor = true;

	/**
	 * <code>true</code> if the contributor must be update in DITA Map.
	 */
	private boolean updateMapContributor = true;

	/**
	 * <code>true</code> if the prolog update is allowed in DITA Topic.
	 */
	private boolean isAllowedTopicUpdate = true;

	/**
	 * <code>true</code> if the prolog(topicmeta) update is allowed in DITA Map.
	 */
	private boolean isAllowedMapUpdate = true;

	/**
	 * <code>true</code> if the created date must be set in DITA Topic.
	 */
	private boolean setTopicCreatedDate = true;

	/**
	 * <code>true</code> if the created date must be set in DITA Map.
	 */
	private boolean setMapCreatedDate = true;

	/**
	 * <code>true</code> if the revised date must be update in DITA Topic.
	 */
	private boolean updateTopicRevisedDate = true;

	/**
	 * <code>true</code> if the revised date must be update in DITA Map.
	 */
	private boolean updateMapRevisedDate = true;

	/**
	 * The maximum number of revised elements
	 */
  private int maxNoOfRevised = -1;

  /**
   * The value of the type attribute for a creator
   */
  private String creatorTypeValue = XmlElementsConstants.CREATOR_TYPE;

  /**
   * The value of the type attribute for a contributor.
   */
  private String contributorTypeValue = XmlElementsConstants.CONTRIBUTOR_TYPE;

  /**
   * Constructor.
   * 
   * @param author      The name of the author.
   * @param dateFormat  The format of the date.
   */
  public PrologContentCreator(String author, String dateFormat) {
    this(author, dateFormat, -1);
  }
  
	/**
	 * Constructor.
	 * 
	 * @param author      The name of the author.
	 * @param dateFormat 	The format of the date.
	 * @param maxNoOfRevisedElements The maximum allowed number of revised elements.
	 */
	public PrologContentCreator(String author, String dateFormat, int maxNoOfRevisedElements) {
		this.authorName = author;
		this.maxNoOfRevised = maxNoOfRevisedElements;
		
		// Get the setting from option storage.
    loadOptions();
		
		if (authorName != null) {
			// Creator
			creatorFragment = XMLFragmentUtils.createAuthorFragment(authorName, creatorTypeValue);
			// Contributor
			contributorXML = XMLFragmentUtils.createAuthorFragment(authorName, contributorTypeValue);

			// Generate current date in a specified format.
			localDate = createLocalDate(dateFormat);
			// Generate the created date element.
			createdDateXML = XMLFragmentUtils.createGeneralXmlFragment("created", "date", localDate);

			// Generate the revised date element.
			// Add the author name as comment.
			revisedDateFragment = XMLFragmentUtils.createGeneralXmlFragment(null, null, authorName);
			StringBuilder revised = XMLFragmentUtils.createGeneralXmlFragment("revised", "modified", localDate);
			revisedDateFragment = (revisedDateFragment == null) ? revised : revisedDateFragment.append(revised);
		}
	}

	/**
	 * Get all XML fragment for prolog tag, according to given state of document.
	 * 
	 * @param isNewDocument
	 *          <code>true</code> if document is new.
	 * @param documentType
	 *          The document type( {@link DocumentType#TOPIC},
	 *          {@link DocumentType#MAP}, {@link DocumentType#BOOKMAP} ).
	 * @return The XML fragment in string format.
	 */
	public String getPrologFragment(boolean isNewDocument, DocumentType documentType) {
		String fragmentToReturn = null;

		boolean isUpdateAllowed = documentType.equals(DocumentType.TOPIC) ? isAllowedTopicUpdate : isAllowedMapUpdate;
		if (isUpdateAllowed) {
		  String authorElement;
		  String dateElement;
		  
      if (isNewDocument) {
        authorElement = getCreatorFragment(documentType);
        dateElement = getCreatedDateFragment(documentType);
      } else {
        authorElement = getContributorFragment(documentType);
        dateElement = getRevisedDateFragment(documentType);
      }
      
      if(authorElement != null || dateElement != null) {
        StringBuilder fragmentBuilder = new StringBuilder();
        String prologElementName = XmlElementsUtils.getPrologName(documentType);
        fragmentBuilder.append('<').append(prologElementName).append('>');
        
        if (authorElement != null) {
          fragmentBuilder.append(authorElement);
        }
        if (dateElement != null) {
          fragmentBuilder.append(XMLFragmentUtils.createCritdateTag(dateElement));
        }
        
        fragmentBuilder.append("</").append(prologElementName).append('>');
        fragmentToReturn = fragmentBuilder.toString();
      }
		}
		return fragmentToReturn;
	}

	/**
	 * @param documentType
	 *          The document type( {@link DocumentType#TOPIC},
	 *          {@link DocumentType#MAP}, {@link DocumentType#BOOKMAP} )
	 * @return The XML fragment for creator author element or <code>null</code> if
	 *         the settings doesn't accept this element.
	 */
	public String getCreatorFragment(DocumentType documentType) {
		String fragment = null;
		if ((setTopicCreator && documentType.equals(DocumentType.TOPIC))
				|| (setMapCreator && !documentType.equals(DocumentType.TOPIC))) {
			fragment = creatorFragment.toString();
		}
		return fragment;
	}

	/**
	 * @param documentType
	 *          The document type( {@link DocumentType#TOPIC},
	 *          {@link DocumentType#MAP}, {@link DocumentType#BOOKMAP} )
	 * @return The XML fragment for contributor author element or
	 *         <code>null</code> if the settings doesn't accept this element.
	 */
	public String getContributorFragment(DocumentType documentType) {
		String fragment = null;
		if ((updateTopicContributor && documentType.equals(DocumentType.TOPIC))
				|| (updateMapContributor && !documentType.equals(DocumentType.TOPIC))) {
			fragment = contributorXML.toString();
		}
		return fragment;
	}

	/**
	 * @param documentType
	 *          The document type( {@link DocumentType#TOPIC},
	 *          {@link DocumentType#MAP}, {@link DocumentType#BOOKMAP} )
	 * @return The XML fragment for create date element or <code>null</code> if
	 *         the settings doesn't accept this element.
	 */
	public String getCreatedDateFragment(DocumentType documentType) {
		String fragment = null;
		if ((setTopicCreatedDate && documentType.equals(DocumentType.TOPIC))
				|| (setMapCreatedDate && !documentType.equals(DocumentType.TOPIC))) {
			fragment = createdDateXML.toString();
		}
		return fragment;
	}

	/**
	 * @param documentType
	 *          The document type( {@link DocumentType#TOPIC},
	 *          {@link DocumentType#MAP}, {@link DocumentType#BOOKMAP} )
	 * @return The XML fragment for revised date element or <code>null</code> if
	 *         the settings doesn't accept this element.
	 */
	public String getRevisedDateFragment(DocumentType documentType) {
		String fragment = null;
		if ((updateTopicRevisedDate && documentType.equals(DocumentType.TOPIC))
				|| (updateMapRevisedDate && !documentType.equals(DocumentType.TOPIC))) {
			fragment = revisedDateFragment.toString();
		}
		return fragment;
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
	 * @param isNewDocument
	 *          <code>true</code> if document is new, <code>false</code>
	 *          otherwise.
	 * @param documentType
	 *          The document type( {@link DocumentType#TOPIC},
	 *          {@link DocumentType#MAP}, {@link DocumentType#BOOKMAP} )
	 * @return the XML fragment in String format or <code>null</code> if the
	 *         settings doesn't accept this element
	 */
	public String getPrologAuthorElement(boolean isNewDocument, DocumentType documentType) {
		return isNewDocument ? getCreatorFragment(documentType) : getContributorFragment(documentType);
	}

	/**
	 * Get the XML fragment of date element, according to given state of document.
	 * 
	 * @param isNewDocument
	 *          <code>true</code> if document is new, <code>false</code>
	 *          otherwise.
	 * @param documentType
	 *          The document type( {@link DocumentType#TOPIC},
	 *          {@link DocumentType#MAP}, {@link DocumentType#BOOKMAP} )
	 * @return the XML fragment in String format or <code>null</code> if the
	 *         settings doesn't accept this element
	 */
	public String getDateFragment(boolean isNewDocument, DocumentType documentType) {
		return isNewDocument ? getCreatedDateFragment(documentType) : getRevisedDateFragment(documentType);
	}

	/**
	 * The maximum number of allowed revised elements.
	 * @return
	 */
	public int getMaxNoOfRevisedElement() {
	  return maxNoOfRevised;
	}
	
	/**
	 * Create the local date.
	 * 
	 * @param dataFormat The format of the date.
	 * 
	 * @return The local date in String format.
	 */
	protected String createLocalDate(String dataFormat) {
		SimpleDateFormat simpleDateFormat = null;
		try {
			simpleDateFormat = new SimpleDateFormat(dataFormat);
		} catch (Exception e) {
			simpleDateFormat = new SimpleDateFormat(DateFormats.DEFAULT_DATE_PATTERN);
		}
		return simpleDateFormat.format(new Date());
	}

	/**
	 * Get the options from WSOptionsStorage.
	 */
	private void loadOptions() {
		PluginWorkspace pluginWorkspace = PluginWorkspaceProvider.getPluginWorkspace();
		if (pluginWorkspace != null) {
			WSOptionsStorage optionsStorage = pluginWorkspace.getOptionsStorage();

			String value = optionsStorage.getOption(OptionKeys.TOPIC_ENABLE_UPDATE_ON_SAVE, String.valueOf(true));
			isAllowedTopicUpdate = Boolean.parseBoolean(value);
			value = optionsStorage.getOption(OptionKeys.MAP_ENABLE_UPDATE_ON_SAVE, String.valueOf(true));
			isAllowedMapUpdate = Boolean.parseBoolean(value);

			value = optionsStorage.getOption(OptionKeys.TOPIC_SET_CREATOR, String.valueOf(true));
			setTopicCreator = Boolean.parseBoolean(value) && isAllowedTopicUpdate;
			value = optionsStorage.getOption(OptionKeys.MAP_SET_CREATOR, String.valueOf(true));
			setMapCreator = Boolean.parseBoolean(value) && isAllowedMapUpdate;
			value = optionsStorage.getOption(OptionKeys.TOPIC_UPDATE_CONTRIBUTOR, String.valueOf(true));
			updateTopicContributor = Boolean.parseBoolean(value) && isAllowedTopicUpdate;
			value = optionsStorage.getOption(OptionKeys.MAP_UPDATE_CONTRIBUTOR, String.valueOf(true));
			updateMapContributor = Boolean.parseBoolean(value) && isAllowedMapUpdate;

			value = optionsStorage.getOption(OptionKeys.TOPIC_SET_CREATED_DATE, String.valueOf(true));
			setTopicCreatedDate = Boolean.parseBoolean(value) && isAllowedTopicUpdate;
			value = optionsStorage.getOption(OptionKeys.MAP_SET_CREATED_DATE, String.valueOf(true));
			setMapCreatedDate = Boolean.parseBoolean(value) && isAllowedMapUpdate;
			value = optionsStorage.getOption(OptionKeys.TOPIC_UPDATE_REVISED_DATES, String.valueOf(true));
			updateTopicRevisedDate = Boolean.parseBoolean(value) && isAllowedTopicUpdate;
			value = optionsStorage.getOption(OptionKeys.MAP_UPDATE_REVISED_DATES, String.valueOf(true));
			updateMapRevisedDate = Boolean.parseBoolean(value) && isAllowedMapUpdate;
			
			creatorTypeValue = optionsStorage.getOption(OptionKeys.CREATOR_TYPE_VALUE, XmlElementsConstants.CREATOR_TYPE);
			contributorTypeValue = optionsStorage.getOption(OptionKeys.CONTRIBUTOR_TYPE_VALUE, XmlElementsConstants.CONTRIBUTOR_TYPE);
		}
	}
	
	
	public String getCreatorTypeValue() {
    return creatorTypeValue;
  }
	
	public String getContributorTypeValue() {
    return contributorTypeValue;
  }
}
