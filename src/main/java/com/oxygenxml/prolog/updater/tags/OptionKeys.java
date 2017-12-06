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
}
