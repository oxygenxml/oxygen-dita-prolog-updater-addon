package com.oxygenxml.prolog.updater.plugin;

import java.net.URL;

import javax.swing.JComponent;

import com.oxygenxml.prolog.updater.DitaPrologUpdater;
import com.oxygenxml.prolog.updater.view.PrologOptionPage;

import ro.sync.exml.plugin.option.OptionPagePluginExtension;
import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.documenttype.DocumentTypeInformation;
import ro.sync.exml.workspace.api.listeners.WSEditorChangeListener;
import ro.sync.exml.workspace.api.listeners.WSEditorListener;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * Prolog updater plugin extension.
 * 
 * @author cosmin_duna
 */
public class PrologUpdaterExtension extends OptionPagePluginExtension implements WorkspaceAccessPluginExtension {

	/**
	 * The page with option for this plugin.
	 */
	private PrologOptionPage prologOptionPage = null;

	/**
	 * The name of the DITA type.
	 */
	private static final String DITA_TYPE_NAME = "dita";
	
	/**
	 * @see ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension#applicationStarted(ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace)
	 */
	public void applicationStarted(final StandalonePluginWorkspace workspace) {
		// Create a XmlUpdater
		final DitaPrologUpdater xmlUpdater = createDitaUpdater();

		// Add n WSEditorChangeListener on the main editing area
		workspace.addEditorChangeListener(new WSEditorChangeListener() {
			@Override
			public void editorOpened(URL editorLocation) {
				final WSEditor editorAccess = workspace.getEditorAccess(editorLocation, PluginWorkspace.MAIN_EDITING_AREA);
				
				// Check the document type name.
				String docTypeName = getDocumentTypeName(editorAccess);
				if (docTypeName != null && docTypeName.toLowerCase().contains(DITA_TYPE_NAME)) {
					// Add an WSEditorListener
					editorAccess.addEditorListener(new WSEditorListener() {
						private boolean wasNew = false;
						private boolean wasSave = false;

						@Override
						public boolean editorAboutToBeSavedVeto(int operationType) {
							if (editorAccess.isNewDocument() && !editorAccess.isModified()) {
								editorAccess.setModified(true);
							}
							wasNew = editorAccess.isNewDocument();
							return true;
						}

						@Override
						public void editorSaved(int operationType) {
							if (!wasSave) {
								wasSave = true;
								xmlUpdater.updateProlog(editorAccess, wasNew);
								editorAccess.save();
								wasSave = false;
							}
						}
					});
				}
			}
		}, PluginWorkspace.MAIN_EDITING_AREA);

		// Add a WSEditorChangeListener on the DITA Maps Manager
		workspace.addEditorChangeListener(new WSEditorChangeListener() {
			@Override
			public void editorOpened(URL editorLocation) {
				final WSEditor editorAccess = workspace.getEditorAccess(editorLocation, PluginWorkspace.DITA_MAPS_EDITING_AREA);
				
				// Check the document type name.
				String docTypeName = getDocumentTypeName(editorAccess);
				if (docTypeName != null && docTypeName.toLowerCase().contains(DITA_TYPE_NAME)) {
					// Add an WSEditorListener
					editorAccess.addEditorListener(new WSEditorListener() {
						private boolean wasNew = false;
						private boolean wasSave = false;

						@Override
						public boolean editorAboutToBeSavedVeto(int operationType) {
							wasNew = editorAccess.isNewDocument();
							return true;
						}

						@Override
						public void editorSaved(int operationType) {
							if (!wasSave) {
								wasSave = true;
								xmlUpdater.updateProlog(editorAccess, wasNew);
								editorAccess.save();
								wasSave = false;
							}
							super.editorSaved(operationType);
						}
					});
				}
			}
		}, PluginWorkspace.DITA_MAPS_EDITING_AREA);
	}

	/**
	 * Creates the updater.
	 * 
	 * @return a new instance.
	 */
	protected DitaPrologUpdater createDitaUpdater() {
		return new DitaPrologUpdater();
	}

	/**
	 * Get the name of the given editor's document type.
	 * 
	 * @param wsEditor Access to workspace editor.
	 * 
	 * @return The name of the given editor's document type, or <code>null</code>
	 *  if the editor does not have an XML content type.
	 */
	protected String getDocumentTypeName(WSEditor wsEditor) {
		String toReturn = null;
		DocumentTypeInformation documentTypeInformation = wsEditor.getDocumentTypeInformation();
		if(documentTypeInformation != null) {
			toReturn = documentTypeInformation.getName();
		}
		return toReturn;
	}
	
	/**
	 * Notified before the editors are closed and the application exits
	 * 
	 * @return <code>True</code> application can close, <code>false</code>, if
	 *         vetoed
	 */
	public boolean applicationClosing() {
		return true;
	}

	@Override
	public void apply(PluginWorkspace pluginWorkspace) {
		if (prologOptionPage != null) {
			prologOptionPage.savePageState();
		}
	}

	@Override
	public void restoreDefaults() {
		if (prologOptionPage != null) {
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
