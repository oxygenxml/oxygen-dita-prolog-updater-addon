package com.oxygenxml.prolog.updater.tags;

/**
 * Option keys for prolog updater option page.
 *
 */
public class OptionKeys {

	/**
	 * Private constructor.
	 */
	private OptionKeys() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Option for the author name in prolog updater.
	 */
	public static final String AUTHOR_NAME = "prolog.updater.author.name";

	/**
   * Option for the value of the creator author type
   */
	public static final String CUSTOM_CREATOR_TYPE_VALUE = "custom.creator.type.value";
	
	/**
   * Option for the value of the contributor author type
   */
  public static final String CUSTOM_CONTRIBUTOR_TYPE_VALUE = "custom.contributor.type.value";
	
	/**
	 * Option for the date format in prolog updater.
	 */
	public static final String DATE_FORMAT = "prolog.updater.date.format";
	
  /**
   * Option for limit the revised elements.
   */
  public static final String LIMIT_REVISED_ELEMENTS = "limit.revised.elements";
  
	/**
   * Option for the maximum number of revised elements.
   */
  public static final String MAX_REVISED_ELEMENTS = "max.revised.elements";
  
	/**
	 * Option to enable the prolog updater for topics.
	 */
	public static final String TOPIC_ENABLE_UPDATE_ON_SAVE = "prolog.updater.enable.on.save.topic";

	/**
	 * Option to set the creator in prolog for topics.
	 */
	public static final String TOPIC_SET_CREATOR = "prolog.updater.set.creator.topic";

	/**
	 * Option to set the created date in prolog for topics.
	 */
	public static final String TOPIC_SET_CREATED_DATE = "prolog.updater.set.created.date.topic";

	/**
	 * Option to set the contributor in prolog for topics.
	 */
	public static final String TOPIC_UPDATE_CONTRIBUTOR = "prolog.updater.update.contributor.topic";

	/**
	 * Option to set the revised dates in prolog for topics.
	 */
	public static final String TOPIC_UPDATE_REVISED_DATES = "prolog.updater.update.revised.dates.topic";

	/**
	 * Option to enable the prolog updater for maps.
	 */
	public static final String MAP_ENABLE_UPDATE_ON_SAVE = "prolog.updater.enable.on.save.map";

	/**
	 * Option to set the creator in prolog for maps.
	 */
	public static final String MAP_SET_CREATOR = "prolog.updater.set.creator.map";

	/**
	 * Option to set the created date in prolog for maps.
	 */
	public static final String MAP_SET_CREATED_DATE = "prolog.updater.set.created.date.map";

	/**
	 * Option to set the contributor in prolog for maps.
	 */
	public static final String MAP_UPDATE_CONTRIBUTOR = "prolog.updater.update.contributor.map";

	/**
	 * Option to set the revised dates in prolog for maps.
	 */
	public static final String MAP_UPDATE_REVISED_DATES = "prolog.updater.update.revised.dates.map";
	
	 /**
   * Option to allow adding of original author as additional one.
   */
  public static final String ALLOW_ADDING_ORIGINAL_AUTHOR_AS_ADDITIONAL = "allow.adding.original.author.as.additional";
}
