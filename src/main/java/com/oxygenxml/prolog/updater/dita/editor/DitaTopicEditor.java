package com.oxygenxml.prolog.updater.dita.editor;

/**
 * Editor for DITA topic.
 * @author intern4
 *
 */
public interface DitaTopicEditor {
	
	/**
	 * Class's value of prolog anterior note.
	 */
	final String ANTERIOR_NODE_CLASS_VALUE = "topic/body";
	
	/**
	 * Update the prolog in DITA topic document according to given flag(isNewDocument)
	 * @param isNewDocument <code>true</code> if document is new, <code>false</code> otherwise
	 * 
	 */
	public void updateProlog( boolean isNewDocument);
	
}
