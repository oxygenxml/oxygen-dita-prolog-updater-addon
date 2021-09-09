package com.oxygenxml.prolog.updater.plugin;

import javax.swing.JComponent;

import com.oxygenxml.prolog.updater.tags.OptionKeys;
import com.oxygenxml.prolog.updater.view.PrologOptionPanel;

import ro.sync.exml.plugin.option.OptionPagePluginExtension;
import ro.sync.exml.workspace.api.PluginWorkspace;

/**
 * Option page of the prolog updater plugin.
 * 
 * @author cosmin_duna
 */
public class PrologOptionPageExtension extends OptionPagePluginExtension {
  /**
   * The page with option for this plugin.
   */
  private PrologOptionPanel prologOptionPage = null;

  /**
   * @see OptionPagePluginExtension#init(PluginWorkspace)
   */
  @Override
  public JComponent init(PluginWorkspace pluginWorkspace) {
    prologOptionPage = new PrologOptionPanel();
    return prologOptionPage;
  }
  
  /**
   * @see OptionPagePluginExtension#apply(PluginWorkspace)
   */
  @Override
  public void apply(PluginWorkspace pluginWorkspace) {
    if (prologOptionPage != null) {
      prologOptionPage.savePageState();
    }
  }

  /**
   * @see OptionPagePluginExtension#restoreDefaults()
   */
  @Override
  public void restoreDefaults() {
    if (prologOptionPage != null) {
      prologOptionPage.restoreDefault();
    }
  }

  /**
   * @see OptionPagePluginExtension#getTitle()
   */
  @Override
  public String getTitle() {
    return PrologUpdaterPlugin.getInstance().getDescriptor().getName();
  }
 
  
  /**
   * Get the options for the plugin that can be saved at project level.
   * 
   * @return The plugin options that can be saved at project level
   * 
   * @since 24.0
   */
  public String[] getProjectLevelOptionKeys() {
    return  new String[] {
        OptionKeys.AUTHOR_NAME,
        OptionKeys.CUSTOM_CREATOR_TYPE_VALUE,
        OptionKeys.CUSTOM_CONTRIBUTOR_TYPE_VALUE,
        OptionKeys.DATE_FORMAT,
        OptionKeys.LIMIT_REVISED_ELEMENTS,
        OptionKeys.MAX_REVISED_ELEMENTS,
        OptionKeys.TOPIC_ENABLE_UPDATE_ON_SAVE,
        OptionKeys.TOPIC_SET_CREATOR,
        OptionKeys.TOPIC_SET_CREATED_DATE,
        OptionKeys.TOPIC_UPDATE_CONTRIBUTOR,
        OptionKeys.TOPIC_UPDATE_REVISED_DATES,
        OptionKeys.MAP_ENABLE_UPDATE_ON_SAVE,
        OptionKeys.MAP_SET_CREATOR,
        OptionKeys.MAP_SET_CREATED_DATE,
        OptionKeys.MAP_UPDATE_CONTRIBUTOR,
        OptionKeys.MAP_UPDATE_REVISED_DATES };
  }
}
