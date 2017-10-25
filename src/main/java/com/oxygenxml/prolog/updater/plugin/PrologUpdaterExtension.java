package com.oxygenxml.prolog.updater.plugin;

import java.net.URL;

import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.listeners.WSEditorChangeListener;
import ro.sync.exml.workspace.api.listeners.WSEditorListener;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

public class PrologUpdaterExtension implements WorkspaceAccessPluginExtension{

	public void applicationStarted(final StandalonePluginWorkspace workspace) {

		//create a XmlUpdater
		final DitaUpdater xmlUpdater = new DitaUpdater(workspace);
		
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

	public boolean applicationClosing() {
		return true;
	}

	
	
}
