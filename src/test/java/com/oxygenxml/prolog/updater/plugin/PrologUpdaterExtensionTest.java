package com.oxygenxml.prolog.updater.plugin;

import java.io.File;
import java.net.URL;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.oxygenxml.prolog.updater.DitaUpdater;

import junit.framework.TestCase;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.listeners.WSEditorChangeListener;
import ro.sync.exml.workspace.api.listeners.WSEditorListener;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

public class PrologUpdaterExtensionTest extends TestCase {

	/**
	 * We are testing that a listener is installed on the editor and it receives
	 * save callbacks.
	 */
	@Test
	public void testInstallingListener() throws Exception {

		// Mock the XML Updater.
		final boolean[] doUpdateProlog = new boolean[1];
		final DitaUpdater ditaUpdater = Mockito.mock(DitaUpdater.class);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				doUpdateProlog[0] = true;
				return null;
			}
		}).when(ditaUpdater).updateProlog((WSEditor) Mockito.anyObject(), Mockito.anyBoolean());

		//
		// The plugin
		//
		PrologUpdaterExtension extension = new PrologUpdaterExtension() {

			@Override
			protected DitaUpdater createDitaUpdater() {
				return ditaUpdater;
			}
		};

		// Mock the editor access.
		final WSEditorListener[] editorListeners = new WSEditorListener[1];
		final WSEditor editorAccess = Mockito.mock(WSEditor.class);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				editorListeners[0] = (WSEditorListener) invocation.getArguments()[0];
				return null;
			}
		}).when(editorAccess).addEditorListener((WSEditorListener) Mockito.anyObject());

		// Mock the workspace.
		final WSEditorChangeListener[] workspaceListeners = new WSEditorChangeListener[1];
		StandalonePluginWorkspace workspace = Mockito.mock(StandalonePluginWorkspace.class);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				workspaceListeners[0] = (WSEditorChangeListener) invocation.getArguments()[0];
				return null;
			}
		}).when(workspace).addEditorChangeListener((WSEditorChangeListener) Mockito.anyObject(), Mockito.anyInt());

		Mockito.when(workspace.getEditorAccess((URL) Mockito.anyObject(), Mockito.anyInt())).then(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				return editorAccess;
			}
		});

		assertNull(workspaceListeners[0]);
		extension.applicationStarted(workspace);
		assertNotNull(" 'applicationStarted' method should be called.", workspaceListeners[0]);

		// Check that the listener reacts to editorOpened event.
		assertNull(editorListeners[0]);
		workspaceListeners[0].editorOpened(new File("test/dummy.xml").toURI().toURL());
		assertNotNull(" 'editorOpened' method should be called.", editorListeners[0]);

		assertFalse(doUpdateProlog[0]);
		editorListeners[0].editorAboutToBeSavedVeto(0);
		editorListeners[0].editorSaved(0);
		assertTrue(" 'editorSaved' method should be called.", doUpdateProlog[0]);

	}

}
