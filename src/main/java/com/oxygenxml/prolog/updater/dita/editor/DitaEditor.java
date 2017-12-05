package com.oxygenxml.prolog.updater.dita.editor;

/**
 * Editor for DITA topic.
 * 
 * @author cosmin_duna
 */
public interface DitaEditor {
	
	/**
	 * Update the prolog in DITA topic document according to given flag(isNewDocument)
	 * @param isNewDocument <code>true</code> if document is new, <code>false</code> otherwise
	 * 
	 */
	void updateProlog( boolean isNewDocument);
	
}
