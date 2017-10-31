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
 * @author intern4
 *
 */
public class PrologUpdaterExtension implements WorkspaceAccessPluginExtension{

	/**
	 * Main plugin method. Notified when the application is started. 
	 */
	public void applicationStarted(final StandalonePluginWorkspace workspace) {

//TODO delete		
//		String name = (String)workspace.getGlobalObjectProperty("change.tracking.author");
//		System.out.println("name: " + name);
		
		
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
							
							return super.editorAboutToBeSavedVeto(operationType);
						}
					});
					
					super.editorOpened(editorLocation);
			}
			
		}, PluginWorkspace.MAIN_EDITING_AREA);
		
	}

	/**
	 * Creates the udpater.
	 * 
	 * @return a new instance.
	 */
	protected DitaUpdater createDitaUpdater() {
		return new DitaUpdater();
	}

	/**
	 * Notified before the editors are closed and the application exits
	 */
	public boolean applicationClosing() {
		return true;
	}

	
	
}
