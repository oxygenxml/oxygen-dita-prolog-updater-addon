package com.oxygenxml.prolog.updater.plugin;

import ro.sync.exml.plugin.Plugin;
import ro.sync.exml.plugin.PluginDescriptor;

public class PrologUpdaterPlugin extends Plugin {
  /**
   * The static plugin instance.
   */
  private static PrologUpdaterPlugin instance = null;

  /**
   * Constructs the plugin.
   * 
   * @param descriptor The plugin descriptor
   */
  public PrologUpdaterPlugin(PluginDescriptor descriptor) {
    super(descriptor);

    if (instance != null) {
      throw new IllegalStateException("Already instantiated!");
    }
    instance = this;
  }
  
  /**
   * Get the plugin instance.
   * 
   * @return the shared plugin instance.
   */
  public static PrologUpdaterPlugin getInstance() {
    return instance;
  }

}
