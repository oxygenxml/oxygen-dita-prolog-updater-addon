package com.oxygenxml.prolog.updater.tags;

/**
 * Tags used for translation.
 * 
 * @author cosmin_duna
 *
 */
public class Tags {

	/**
	 * Private constructor.
	 */
	private Tags() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * en: Author
	 */
	public static final String AUTHOR = "prolog.updater.author.name";

	/**
	 * en: DITA topic
	 */
	public static final String DITA_TOPIC = "prolog.updater.dita.topic";

	/**
	 * en: DITA map
	 */
	public static final String DITA_MAP = "prolog.updater.dita.map";

	/**
	 * en: Enable automatic prolog update on save
	 */
	public static final String ENABLE_UPDATE_ON_SAVE = "prolog.updater.enable.on.save";

	/**
	 * en: Set creator name
	 */
	public static final String SET_CREATOR = "prolog.updater.set.creator";

	/**
	 * en: Set created date
	 */
	public static final String SET_CREATED_DATE = "prolog.updater.set.created.date";

	/**
	 * en: Update contributor names
	 */
	public static final String UPDATE_CONTRIBUTOR = "prolog.updater.update.contributor";

	/**
	 * en: Update revised dates
	 */
	public static final String UPDATE_REVISED_DATES = "prolog.updater.update.revised.dates";

	/**
	 * en: The prolog wasn't updated.
	 */
	public static final String ERROR_MESSAGE = "prolog.updater.error.message";

}
