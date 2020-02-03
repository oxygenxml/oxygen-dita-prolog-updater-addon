package com.oxygenxml.prolog.updater;

import java.net.URL;

import com.oxygenxml.prolog.updater.dita.editor.DitaEditor;
import com.oxygenxml.prolog.updater.dita.editor.DitaTopicAuthorEditor;
import com.oxygenxml.prolog.updater.dita.editor.DitaTopicTextEditor;
import com.oxygenxml.prolog.updater.plugin.PrologUpdaterPlugin;
import com.oxygenxml.prolog.updater.prolog.content.PrologContentCreator;
import com.oxygenxml.prolog.updater.tags.OptionKeys;
import com.oxygenxml.prolog.updater.tags.Tags;
import com.oxygenxml.prolog.updater.utils.AWTUtil;

import ro.sync.document.DocumentPositionedInfo;
import ro.sync.exml.workspace.api.PluginResourceBundle;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;
import ro.sync.exml.workspace.api.editor.page.ditamap.WSDITAMapEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextEditorPage;
import ro.sync.exml.workspace.api.options.WSOptionsStorage;
import ro.sync.exml.workspace.api.results.ResultsManager;
import ro.sync.exml.workspace.api.results.ResultsManager.ResultType;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * Update the prolog in DITA topics.
 * 
 * @author cosmin_duna
 */
public class DitaPrologUpdater {
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
	 * @param wsEditor
	 *          Workspace editor.
	 */
	public void updateProlog(final WSEditor wsEditor, final boolean isNewDocument) {
		// get the currentPage
		WSEditorPage currentPage = wsEditor.getCurrentPage();

		// create a PrologContentCreator
		PrologContentCreator prologContentCreater = new PrologContentCreator(getAuthorName(), getDateFormat(), getMaxNoOfRevisedElements());

		final DitaEditor[] ditaEditor = new DitaEditor[1];
		if (currentPage instanceof WSAuthorEditorPage) {
			// Author page
			WSAuthorEditorPage authorPage = (WSAuthorEditorPage) currentPage;
			ditaEditor[0] = new DitaTopicAuthorEditor(authorPage, prologContentCreater);

		} else if (currentPage instanceof WSXMLTextEditorPage) {
			// Text page
			WSXMLTextEditorPage textPage = (WSXMLTextEditorPage) currentPage;
			ditaEditor[0] = new DitaTopicTextEditor(textPage, prologContentCreater);

		} else if (currentPage instanceof WSDITAMapEditorPage) {
			// DMM
			WSDITAMapEditorPage mapEditorPage = (WSDITAMapEditorPage) currentPage;
			ditaEditor[0] = new DitaTopicAuthorEditor(mapEditorPage, prologContentCreater);
		}

		// Update prolog.
		AWTUtil.invokeSynchronously(new Runnable() {
			public void run() {
				if (ditaEditor[0] != null) {
					boolean wasUpdated = ditaEditor[0].updateProlog(isNewDocument);
					if (!wasUpdated) {
						showWarnMessage(wsEditor);
					}
				}
			}
		});
	}

	/**
	 * @return The author's name. Never <code>null</code>.
	 */
	protected String getAuthorName() {
		String toReturn = UNKNOWN;

		String name = System.getProperty(USER_NAME_PROPERTY);
		if (name != null) {
			toReturn = name;
		}

		WSOptionsStorage optionsStorage = PluginWorkspaceProvider.getPluginWorkspace().getOptionsStorage();
		if (optionsStorage != null) {
			toReturn = optionsStorage.getOption(OptionKeys.AUTHOR_NAME, toReturn);
		}

		return toReturn;
	}

	/**
	 * @return The date format.
	 */
	protected String getDateFormat() {
		String toReturn = null;

		PluginWorkspace pluginWorkspace = PluginWorkspaceProvider.getPluginWorkspace();
		if(pluginWorkspace != null) {
			WSOptionsStorage optionsStorage = pluginWorkspace.getOptionsStorage();
			toReturn = optionsStorage.getOption(OptionKeys.DATE_FORMAT, toReturn);
		}
		return toReturn;
	}
	
	/**
	 * Get the maximum number of allowed revised elements.
	 * @return
	 */
	protected int getMaxNoOfRevisedElements() {
    int max = -1;
    PluginWorkspace pluginWorkspace = PluginWorkspaceProvider.getPluginWorkspace();
    if(pluginWorkspace != null) {
      WSOptionsStorage optionsStorage = pluginWorkspace.getOptionsStorage();
      String shouldLimit =  optionsStorage.getOption(OptionKeys.LIMIT_REVISED_ELEMENTS, String.valueOf(false));
      if(Boolean.TRUE.equals(Boolean.valueOf(shouldLimit))) {
        String value = optionsStorage.getOption(OptionKeys.MAX_REVISED_ELEMENTS, String.valueOf(-1));
        max = Integer.valueOf(value);
      }
    }
    return max;
  }

  /**
	 * Show a warning message("The prolog wasn't updated") in results manager.
	 * @param wsEditor The workspace editor access.
	 */
	private void showWarnMessage(WSEditor wsEditor) {
		StandalonePluginWorkspace pluginWorkspace = (StandalonePluginWorkspace) PluginWorkspaceProvider
				.getPluginWorkspace();
		if (pluginWorkspace != null) {

			final ResultsManager resultsManager = pluginWorkspace.getResultsManager();
			PluginResourceBundle messages = pluginWorkspace.getResourceBundle();
			if(resultsManager != null && messages != null) {
				// Get the message that will be showed.
				final String message = messages.getMessage(Tags.ERROR_MESSAGE);
				final URL editorLocation = wsEditor.getEditorLocation();
				
				// Add the message in results manager.
				DocumentPositionedInfo result = null;
				if (editorLocation != null) {
					result = new DocumentPositionedInfo(DocumentPositionedInfo.SEVERITY_WARN, message, editorLocation.toString());
				} else {
					result = new DocumentPositionedInfo(DocumentPositionedInfo.SEVERITY_WARN, message);
				}

				resultsManager.addResult(PrologUpdaterPlugin.getInstance().getDescriptor().getName(),
						result,
						ResultType.PROBLEM,
						false,
						false);
			}
		}
	}
}