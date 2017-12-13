package com.oxygenxml.prolog.updater.plugin;

import java.net.URL;

import javax.swing.JComponent;

import com.oxygenxml.prolog.updater.DitaUpdater;
import com.oxygenxml.prolog.updater.view.PrologOptionPage;

import ro.sync.exml.plugin.option.OptionPagePluginExtension;
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
public class PrologUpdaterExtension extends OptionPagePluginExtension implements WorkspaceAccessPluginExtension{
  
  private PrologOptionPage prologOptionPage = null;

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
            private boolean wasNew = false;
            private boolean wasSave = false;
            @Override
						public boolean editorAboutToBeSavedVeto(int operationType) {
            	System.out.println("aboutToBESave");
              wasNew  = editorAccess.isNewDocument();
							return true;
						}
						@Override
						public void editorSaved(int operationType) {
						  if(!wasSave) {
						    wasSave = true;
						    xmlUpdater.updateProlog(editorAccess, wasNew);
                editorAccess.save();                    
						    wasSave = false;
						  }
						  super.editorSaved(operationType);
						}
						 
					});
					
			}
		}, PluginWorkspace.MAIN_EDITING_AREA);
		
    workspace.addEditorChangeListener(new WSEditorChangeListener() {

      @Override
      public void editorOpened(URL editorLocation) {
        final WSEditor editorAccess = workspace.getEditorAccess(editorLocation, PluginWorkspace.DITA_MAPS_EDITING_AREA);
        // add an WSEditorListener
        editorAccess.addEditorListener(new WSEditorListener() {
          private boolean wasNew = false;
          private boolean wasSave = false;
          @Override
          public boolean editorAboutToBeSavedVeto(int operationType) {
            wasNew  = editorAccess.isNewDocument();
            return true;
          }
          @Override
          public void editorSaved(int operationType) {
            if(!wasSave) {
              wasSave = true;
              xmlUpdater.updateProlog(editorAccess, wasNew);
              editorAccess.save();
              wasSave = false;
            }
            super.editorSaved(operationType);
          }
        });
      }
    }, PluginWorkspace.DITA_MAPS_EDITING_AREA);
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

  @Override
  public void apply(PluginWorkspace pluginWorkspace) {
   if(prologOptionPage != null) {
     prologOptionPage.savePageState();
   }
  }

  @Override
  public void restoreDefaults() {
    if(prologOptionPage != null) {
      prologOptionPage.restoreDefault();
    }
  }

  @Override
  public String getTitle() {
    return PrologUpdaterPlugin.getInstance().getDescriptor().getName();
  }

  @Override
  public JComponent init(PluginWorkspace pluginWorkspace) {
    prologOptionPage = new PrologOptionPage();
    return prologOptionPage;
  }
	
}
