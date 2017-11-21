package com.oxygenxml.prolog.updater.plugin;

import java.net.URL;

import com.oxygenxml.prolog.updater.DitaUpdater;

import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.listeners.WSEditorChangeListener;
import ro.sync.exml.workspace.api.listeners.WSEditorListener;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * Prolog updater plugin extension.
 * 
 * @author cosmin_duna
 */
public class PrologUpdaterExtension implements WorkspaceAccessPluginExtension{


  /**
   * @see ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension#applicationStarted(ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace)
   */
	public void applicationStarted(final StandalonePluginWorkspace workspace) {

		//create a XmlUpdater
		final DitaUpdater xmlUpdater = createDitaUpdater();
		
		//add an WSEditorChangeListener
		workspace.addEditorChangeListener(new WSEditorChangeListener(){
			@Override
			public void editorOpened(URL editorLocation) {
					final WSEditor editorAccess = workspace.getEditorAccess(editorLocation, PluginWorkspace.MAIN_EDITING_AREA);
					//add an WSEditorListener
					editorAccess.addEditorListener(new WSEditorListener(){
						@Override
						public boolean editorAboutToBeSavedVeto(int operationType) {
							xmlUpdater.updateProlog(editorAccess);
							return true;
						}
					});
			}
		}, PluginWorkspace.MAIN_EDITING_AREA);
		
	}

	/**
	 * Creates the updater.
	 * 
	 * @return a new instance.
	 */
	protected DitaUpdater createDitaUpdater() {
		return new DitaUpdater();
	}

	/**
	 * Notified before the editors are closed and the application exits
	 * @return <code>True</code> application can close, <code>false</code>, if vetoed
	 */
	public boolean applicationClosing() {
		return true;
	}
	
}
