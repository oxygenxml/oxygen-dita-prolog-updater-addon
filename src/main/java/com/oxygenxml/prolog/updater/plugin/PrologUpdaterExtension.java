package com.oxygenxml.prolog.updater.plugin;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.oxygenxml.prolog.updater.DitaPrologUpdater;
import com.oxygenxml.prolog.updater.utils.FileUtil;

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
public class PrologUpdaterExtension implements WorkspaceAccessPluginExtension {
	/**
	 * The name of the DITA type.
	 */
	private static final String DITA_TYPE_NAME = "dita";
	
	/**
	 * The new state of files opened in the main editor.
	 */
	private Map<String, Boolean> stateOfMainEditors = new HashMap<String, Boolean>(); 
	
	/**
	 * The new state of files opened in the DMM editor.
	 */
	private Map<String, Boolean> stateOfDmmEditors = new HashMap<String, Boolean>(); 
	
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
				final WSEditor editor = workspace.getEditorAccess(editorLocation, PluginWorkspace.MAIN_EDITING_AREA);
				if(editor != null) {
				  boolean isNewDocument = editor.isNewDocument();
				  if (!isNewDocument) {
				    isNewDocument = FileUtil.checkCurrentNewDocumentState(editorLocation);
				  }
				  stateOfMainEditors.put(editorLocation.toExternalForm(), isNewDocument);

				  // Check the document type name.
				  String docTypeName = getDocumentTypeName(editor);
				  if (docTypeName != null && docTypeName.toLowerCase().contains(DITA_TYPE_NAME)) {
				    // Add an WSEditorListener
				    editor.addEditorListener(new WSEditorListener() {
				      private boolean wasSaved = false;
				      boolean wasSavedAnUntitle = false;
				      String oldUntitleLocation = "";

				      @Override
				      public boolean editorAboutToBeSavedVeto(int operationType) {
				        if (editor.isNewDocument() && !editor.isModified()) {
				          editor.setModified(true);
				        }
				        wasSavedAnUntitle = editor.isNewDocument();
				        oldUntitleLocation = editor.getEditorLocation().toExternalForm();
				        return true;
				      }

				      @Override
				      public void editorSaved(int operationType) {
				        String editorLocation = editor.getEditorLocation().toExternalForm();
				        if (!wasSaved) {
				          wasSaved = true;
				          if (wasSavedAnUntitle) {
				            stateOfMainEditors.remove(oldUntitleLocation);
				            stateOfMainEditors.put(editorLocation, Boolean.TRUE);
				          }

				          Boolean isNew = stateOfMainEditors.get(editorLocation);
				          xmlUpdater.updateProlog(
				              editor,
				              isNew != null ? isNew : Boolean.FALSE);
				          editor.save();
				          wasSaved = false;
				        }
				      }
				    });
				  }
				}
			}
			
			@Override
			public void editorClosed(URL editorLocation) {
			  stateOfMainEditors.remove(editorLocation.toExternalForm());
			}
		}, PluginWorkspace.MAIN_EDITING_AREA);

		// Add a WSEditorChangeListener on the DITA Maps Manager
		workspace.addEditorChangeListener(new WSEditorChangeListener() {
			@Override
			public void editorOpened(final URL editorLocation) {
			  final WSEditor editor = workspace.getEditorAccess(editorLocation, PluginWorkspace.DITA_MAPS_EDITING_AREA);
			  if(editor != null) {
			    boolean isNewDocument = editor.isNewDocument();
			    if (!isNewDocument) {
			      isNewDocument = FileUtil.checkCurrentNewDocumentState(editorLocation);
			    }
			    stateOfDmmEditors.put(editorLocation.toExternalForm(), isNewDocument);

			    // Check the document type name.
			    String docTypeName = getDocumentTypeName(editor);
			    if (docTypeName != null && docTypeName.toLowerCase().contains(DITA_TYPE_NAME)) {
			      // Add an WSEditorListener
			      editor.addEditorListener(new WSEditorListener() {
			        private boolean wasSaved = false;
			        boolean wasSavedAnUntitle = false;
			        String oldUntitleLocation = "";

			        @Override
			        public boolean editorAboutToBeSavedVeto(int operationType) {
			          wasSavedAnUntitle = editor.isNewDocument();
			          oldUntitleLocation = editor.getEditorLocation().toExternalForm();
			          return true;
			        }

			        @Override
			        public void editorSaved(int operationType) {
			          String editorLocation = editor.getEditorLocation().toExternalForm();
			          if (!wasSaved) {
			            wasSaved = true;
			            if (wasSavedAnUntitle) {
			              stateOfDmmEditors.remove(oldUntitleLocation);
			              stateOfDmmEditors.put(editorLocation, Boolean.TRUE);
			            }

			            Boolean isNew = stateOfDmmEditors.get(editorLocation);
			            xmlUpdater.updateProlog(
			                editor,
			                isNew != null ? isNew : Boolean.FALSE);
			            editor.save();
			            wasSaved = false;
			          }
			          super.editorSaved(operationType);
			        }
			      });
			    }
			  }
			}
			
			@Override
			public void editorClosed(URL editorLocation) {
			  stateOfDmmEditors.remove(editorLocation.toExternalForm());
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
}
