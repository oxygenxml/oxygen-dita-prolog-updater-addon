package com.oxygenxml.prolog.updater.prolog.content;
/**
 * Contains all used date patterns. 
 * 
 * @author cosmin_duna
 *
 */
public class DateFormats {
	
	/**
	 * Private constructor.
	 */
	private DateFormats() {
    throw new IllegalStateException("Utility class");
  }
	
	/**
	 * Patterns for date formats.
	 */
	public static final String[] DATE_PATTERNS = {
			"yyyy/MM/dd",
			"YYYY-MM-dd",
			"yyyy.MMMMM.dd",
			"dd MMMMM yyyy",
			"dd.MM.yy",
			"d. MMM. yyyy",
			"MM/dd/yy",
			"MM/dd/yyyy",
			"MMMM d, yyyy",
			"EEE, MMM d, yy",
			"EEE dd/MMM yy",
			"EEE, MMMM d, yyyy"
	};
	
	/**
	 * The default date pattern.
	 */
	public static final String DEFAULT_DATE_PATTERN = DATE_PATTERNS[0];
	
}
