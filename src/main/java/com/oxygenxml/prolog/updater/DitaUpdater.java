package com.oxygenxml.prolog.updater;

import com.oxygenxml.prolog.updater.dita.editor.DitaEditor;
import com.oxygenxml.prolog.updater.dita.editor.DitaTopicAuthorEditor;
import com.oxygenxml.prolog.updater.dita.editor.DitaTopicTextEditor;
import com.oxygenxml.prolog.updater.prolog.content.PrologContentCreator;
import com.oxygenxml.prolog.updater.tags.OptionKeys;
import com.oxygenxml.prolog.updater.tags.Tags;
import com.oxygenxml.prolog.updater.utils.AWTUtil;

import ro.sync.exml.workspace.api.PluginResourceBundle;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;
import ro.sync.exml.workspace.api.editor.page.ditamap.WSDITAMapEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextEditorPage;
import ro.sync.exml.workspace.api.options.WSOptionsStorage;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * Update the prolog in DITA topics.
 * 
 * @author cosmin_duna
 */
public class DitaUpdater {
  /**
   * Unknown user name value.
   */
  private static final String UNKNOWN = "unknown";
  /**
   * System property of user name.
   */
  private static final String USER_NAME_PROPERTY = "user.name";
  
  /**
   * Update the prolog of the current page from given wsEditor.
   * 
   * @param wsEditor Workspace editor.
   */
  public void updateProlog(WSEditor wsEditor , final boolean isNewDocument) {
    //get the currentPage
    WSEditorPage currentPage = wsEditor.getCurrentPage();

    //create a PrologContentCreator
    PrologContentCreator prologContentCreater = new PrologContentCreator(getAuthorName());
    
    final DitaEditor[] ditaEditor = new DitaEditor[1];
    if (currentPage instanceof WSAuthorEditorPage) {
      //Author page
      WSAuthorEditorPage authorPage = (WSAuthorEditorPage)currentPage;
      ditaEditor[0] = new DitaTopicAuthorEditor(authorPage, prologContentCreater);
    
    } else if (currentPage instanceof WSXMLTextEditorPage) {
      //Text page
      WSXMLTextEditorPage textPage = (WSXMLTextEditorPage)currentPage;
      ditaEditor[0] = new DitaTopicTextEditor(textPage, prologContentCreater);
      
    } else if (currentPage instanceof WSDITAMapEditorPage) {
      //DMM
      WSDITAMapEditorPage mapEditorPage = (WSDITAMapEditorPage)currentPage;
      ditaEditor[0] = new DitaTopicAuthorEditor(mapEditorPage, prologContentCreater);
    }
    
    // Update prolog.
    AWTUtil.invokeSynchronously(new Runnable() {
			public void run() {
				if (ditaEditor[0] != null) {
					boolean wasUpdated = ditaEditor[0].updateProlog(isNewDocument);
					if (!wasUpdated) {
						showErrorMessage();
					}
				}
			}
		});
  }

  /**
   * @return The author's name. Never <code>null</code>.
   */
  protected String getAuthorName(){
    String toReturn = UNKNOWN;
    
    String name = System.getProperty(USER_NAME_PROPERTY);
    if(name != null) {
    	toReturn = name;
    }
    
    WSOptionsStorage optionsStorage = PluginWorkspaceProvider.getPluginWorkspace().getOptionsStorage();
    if(optionsStorage != null) {
      toReturn = optionsStorage.getOption(OptionKeys.AUTHOR_NAME, toReturn);
    }
    
    return toReturn;
  }
  
  private void showErrorMessage() {
  	StandalonePluginWorkspace pluginWorkspace = (StandalonePluginWorkspace) PluginWorkspaceProvider.getPluginWorkspace();
  	if(pluginWorkspace != null) {
  		PluginResourceBundle messages = pluginWorkspace.getResourceBundle();
  		pluginWorkspace.showErrorMessage(messages.getMessage(Tags.ERROR_MESSAGE));
  	}
  }
}