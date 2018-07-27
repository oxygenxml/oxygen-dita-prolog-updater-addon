package com.oxygenxml.prolog.updater.prolog.content;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.oxygenxml.prolog.updater.dita.editor.DocumentType;
import com.oxygenxml.prolog.updater.tags.OptionKeys;

import junit.framework.TestCase;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.options.WSOptionsStorage;

/**
 * Test the content generated for the DITA topic according to setting.
 * 
 */
@RunWith(PowerMockRunner.class)
public class PrologContentCreatorMapTest extends TestCase {
	/**
	 * The author name added in inserted content.
	 */
	private static final String AUTHOR_NAME = "name";
	/**
	 * The local date added to inserted content.
	 */
	private static final String LOCAL_DATE = "2017-12-04";
	/**
	 * The storage for options in oxygen.
	 */
	private WSOptionsStorage wsOptionsStorage;

	@Override
	protected void setUp() throws Exception {
		PluginWorkspace pluginWorkspace = Mockito.mock(PluginWorkspace.class);
		wsOptionsStorage = Mockito.mock(WSOptionsStorage.class);
		Mockito.when(pluginWorkspace.getOptionsStorage()).thenReturn(wsOptionsStorage);
		PowerMockito.mockStatic(PluginWorkspaceProvider.class);
		PowerMockito.when(PluginWorkspaceProvider.getPluginWorkspace()).thenReturn(pluginWorkspace);
	}

	/**
	 * <p>
	 * <b>Description:</b> Test the functionality when MAP_ENABLE_UPDATE_ON_SAVE
	 * option is false.
	 * </p>
	 *
	 */
	@PrepareForTest({ PluginWorkspaceProvider.class })
	@Test
	public void testUpdateDisable() {
		Mockito.when(wsOptionsStorage.getOption(OptionKeys.MAP_ENABLE_UPDATE_ON_SAVE, Boolean.TRUE.toString()))
				.thenReturn(Boolean.FALSE.toString());
		PrologContentCreator prologContentCreator = new PrologContentCreator(AUTHOR_NAME, null);

		// Get the prolog according to settings when document is new.
		String prologFragment = prologContentCreator.getPrologFragment(true, DocumentType.MAP);

		// The prolog fragment is not generated.
		assertNull("The fragment shouldn't be generated.", prologFragment);

		// Get the prolog according to settings when document isn't new.
		prologFragment = prologContentCreator.getPrologFragment(false, DocumentType.MAP);

		// The prolog fragment is not generated.
		assertNull("The fragment shouldn't be generated.", prologFragment);
	}

	/**
	 * <p>
	 * <b>Description:</b> Test the functionality when MAP_ENABLE_UPDATE_ON_SAVE
	 * option is false and the rest of options are false
	 * </p>
	 *
	 */
	@PrepareForTest({ PluginWorkspaceProvider.class })
	@Test
	public void testOptionsFalse() {
		Mockito.when(wsOptionsStorage.getOption(OptionKeys.MAP_ENABLE_UPDATE_ON_SAVE, Boolean.TRUE.toString()))
				.thenReturn(Boolean.TRUE.toString());
		Mockito.when(wsOptionsStorage.getOption(OptionKeys.MAP_SET_CREATOR, Boolean.TRUE.toString()))
				.thenReturn(Boolean.FALSE.toString());
		Mockito.when(wsOptionsStorage.getOption(OptionKeys.MAP_SET_CREATED_DATE, Boolean.TRUE.toString()))
				.thenReturn(Boolean.FALSE.toString());
		Mockito.when(wsOptionsStorage.getOption(OptionKeys.MAP_UPDATE_CONTRIBUTOR, Boolean.TRUE.toString()))
				.thenReturn(Boolean.FALSE.toString());
		Mockito.when(wsOptionsStorage.getOption(OptionKeys.MAP_UPDATE_REVISED_DATES, Boolean.TRUE.toString()))
				.thenReturn(Boolean.FALSE.toString());

		PrologContentCreator prologContentCreator = new PrologContentCreator(AUTHOR_NAME, null);

		// Get the prolog in new document.
		String prologFragment = prologContentCreator.getPrologFragment(true, DocumentType.MAP);

		// Check the generated prolog fragment.
		assertNull("The fragment shouldn't be generated.", prologFragment);

		// Get the prolog when the document isn't new.
		prologFragment = prologContentCreator.getPrologFragment(false, DocumentType.MAP);

		// Check the generated prolog fragment.
		assertNull("The fragment shouldn't be generated.", prologFragment);
	}

	/**
	 * <p>
	 * <b>Description:</b> Test the functionality when MAP_SET_CREATOR option is
	 * set true
	 * </p>
	 *
	 */
	@PrepareForTest({ PluginWorkspaceProvider.class })
	@Test
	public void testSetCreator() {
		Mockito.when(wsOptionsStorage.getOption(OptionKeys.MAP_ENABLE_UPDATE_ON_SAVE, Boolean.TRUE.toString()))
				.thenReturn(Boolean.TRUE.toString());
		Mockito.when(wsOptionsStorage.getOption(OptionKeys.MAP_SET_CREATOR, Boolean.TRUE.toString()))
				.thenReturn(Boolean.TRUE.toString());
		Mockito.when(wsOptionsStorage.getOption(OptionKeys.MAP_SET_CREATED_DATE, Boolean.TRUE.toString()))
				.thenReturn(Boolean.FALSE.toString());
		Mockito.when(wsOptionsStorage.getOption(OptionKeys.MAP_UPDATE_CONTRIBUTOR, Boolean.TRUE.toString()))
				.thenReturn(Boolean.FALSE.toString());
		Mockito.when(wsOptionsStorage.getOption(OptionKeys.MAP_UPDATE_REVISED_DATES, Boolean.TRUE.toString()))
				.thenReturn(Boolean.FALSE.toString());

		// When MAP_SET_CREATOR is false
		PrologContentCreator prologContentCreator = new PrologContentCreator(AUTHOR_NAME, null);

		// Get the prolog in new document.
		String prologFragment = prologContentCreator.getPrologFragment(true, DocumentType.MAP);

		// Check the generated prolog fragment.
		assertEquals("The fragment is not generated according to options.",
				"<topicmeta><author type=\"creator\">name</author></topicmeta>", prologFragment);

		// Get the prolog when document isn't new.
		prologFragment = prologContentCreator.getPrologFragment(false, DocumentType.MAP);

		assertNull("The fragment shouldn't be generated.", prologFragment);
	}

	/**
	 * <p>
	 * <b>Description:</b> Test the functionality when MAP_SET_CREATED_DATE
	 * options is set true
	 * </p>
	 *
	 */
	@PrepareForTest({ PluginWorkspaceProvider.class })
	@Test
	public void testSetCreatedDate() {
		Mockito.when(wsOptionsStorage.getOption(OptionKeys.MAP_ENABLE_UPDATE_ON_SAVE, Boolean.TRUE.toString()))
				.thenReturn(Boolean.TRUE.toString());
		Mockito.when(wsOptionsStorage.getOption(OptionKeys.MAP_SET_CREATOR, Boolean.TRUE.toString()))
				.thenReturn(Boolean.FALSE.toString());
		Mockito.when(wsOptionsStorage.getOption(OptionKeys.MAP_SET_CREATED_DATE, Boolean.TRUE.toString()))
				.thenReturn(Boolean.TRUE.toString());
		Mockito.when(wsOptionsStorage.getOption(OptionKeys.MAP_UPDATE_CONTRIBUTOR, Boolean.TRUE.toString()))
				.thenReturn(Boolean.FALSE.toString());
		Mockito.when(wsOptionsStorage.getOption(OptionKeys.MAP_UPDATE_REVISED_DATES, Boolean.TRUE.toString()))
				.thenReturn(Boolean.FALSE.toString());

		// When MAP_SET_CREATOR is false
		PrologContentCreator prologContentCreator = new PrologContentCreator(AUTHOR_NAME, null) {
			@Override
			protected String createLocalDate(String dateFormat) {
				return LOCAL_DATE;
			}
		};

		// Get the prolog when document is new.
		String prologFragment = prologContentCreator.getPrologFragment(true, DocumentType.MAP);

		// Check the generated prolog fragment.
		assertEquals("The fragment is not generated according to options.",
				"<topicmeta><critdates><created date=\"2017-12-04\"/></critdates></topicmeta>", prologFragment);

		// Get the prolog when document isn't new.
		prologFragment = prologContentCreator.getPrologFragment(false, DocumentType.MAP);

		// Check the generated prolog fragment.
		assertNull("The fragment shouldn't be generated.", prologFragment);
	}

	/**
	 * <p>
	 * <b>Description:</b> Test the functionality when MAP_UPDATE_CONTRIBUTOR
	 * options is set true
	 * </p>
	 *
	 */
	@PrepareForTest({ PluginWorkspaceProvider.class })
	@Test
	public void testUpdateContributor() {
		Mockito.when(wsOptionsStorage.getOption(OptionKeys.MAP_ENABLE_UPDATE_ON_SAVE, Boolean.TRUE.toString()))
				.thenReturn(Boolean.TRUE.toString());
		Mockito.when(wsOptionsStorage.getOption(OptionKeys.MAP_SET_CREATOR, Boolean.TRUE.toString()))
				.thenReturn(Boolean.FALSE.toString());
		Mockito.when(wsOptionsStorage.getOption(OptionKeys.MAP_SET_CREATED_DATE, Boolean.TRUE.toString()))
				.thenReturn(Boolean.FALSE.toString());
		Mockito.when(wsOptionsStorage.getOption(OptionKeys.MAP_UPDATE_CONTRIBUTOR, Boolean.TRUE.toString()))
				.thenReturn(Boolean.TRUE.toString());
		Mockito.when(wsOptionsStorage.getOption(OptionKeys.MAP_UPDATE_REVISED_DATES, Boolean.TRUE.toString()))
				.thenReturn(Boolean.FALSE.toString());

		// When MAP_SET_CREATOR is false
		PrologContentCreator prologContentCreator = new PrologContentCreator(AUTHOR_NAME, null);

		// Get the prolog when document is new.
		String prologFragment = prologContentCreator.getPrologFragment(true, DocumentType.MAP);

		// Check the generated prolog fragment.
		assertNull("The fragment shouldn't be generated.", prologFragment);

		// Get the prolog when document isn't new.
		prologFragment = prologContentCreator.getPrologFragment(false, DocumentType.MAP);

		// Check the generated prolog fragment.
		assertEquals("The fragment is not generated according to options.",
				"<topicmeta><author type=\"contributor\">name</author></topicmeta>", prologFragment);

	}

	/**
	 * <p>
	 * <b>Description:</b> Test the functionality when MAP_UPDATE_REVISED_DATES
	 * options is set true
	 * </p>
	 *
	 */
	@PrepareForTest({ PluginWorkspaceProvider.class })
	@Test
	public void testUpdateRevised() {
		Mockito.when(wsOptionsStorage.getOption(OptionKeys.MAP_ENABLE_UPDATE_ON_SAVE, Boolean.TRUE.toString()))
				.thenReturn(Boolean.TRUE.toString());
		Mockito.when(wsOptionsStorage.getOption(OptionKeys.MAP_SET_CREATOR, Boolean.TRUE.toString()))
				.thenReturn(Boolean.FALSE.toString());
		Mockito.when(wsOptionsStorage.getOption(OptionKeys.MAP_SET_CREATED_DATE, Boolean.TRUE.toString()))
				.thenReturn(Boolean.FALSE.toString());
		Mockito.when(wsOptionsStorage.getOption(OptionKeys.MAP_UPDATE_CONTRIBUTOR, Boolean.TRUE.toString()))
				.thenReturn(Boolean.FALSE.toString());
		Mockito.when(wsOptionsStorage.getOption(OptionKeys.MAP_UPDATE_REVISED_DATES, Boolean.TRUE.toString()))
				.thenReturn(Boolean.TRUE.toString());

		// When MAP_SET_CREATOR is false
		PrologContentCreator prologContentCreator = new PrologContentCreator(AUTHOR_NAME, null) {
			@Override
			protected String createLocalDate(String dateFormat) {
				return LOCAL_DATE;
			}
		};

		// Get the prolog when document is new.
		String prologFragment = prologContentCreator.getPrologFragment(true, DocumentType.MAP);

		// Check the generated prolog fragment.
		assertNull("The fragment shouldn't be generated.", prologFragment);

		// Get the prolog when document isn't new.
		prologFragment = prologContentCreator.getPrologFragment(false, DocumentType.MAP);
		assertEquals("The fragment is not generated according to options.",
				"<topicmeta><critdates><!--name--><revised modified=\"2017-12-04\"/></critdates></topicmeta>", prologFragment);

	}
}
