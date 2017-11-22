package com.oxygenxml.prolog.updater.utils;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

public class ThreadUtils {

  /**
   * Logger for logging.
   */
  private static final Logger logger = Logger.getLogger(ThreadUtils.class.getName());
  
  /**
   * Private constructor for utilities class. Avoid instantiation.
   */
  private ThreadUtils() {
    // Nothing
  }
  
  /**
   * Invokes a runnable synchronously on the AWT thread. The current thread
   * may be AWT or not. Returns after the runnable is run.
   * 
   * @param runnable The runnable to be run.
   */
  public static void invokeSynchronously(final Runnable runnable) {
    try {
      if (SwingUtilities.isEventDispatchThread()) {
        runnable.run();
      } else {
        SwingUtilities.invokeAndWait(runnable);
      }
    } catch (InvocationTargetException ex) {
      logger.error(ex,ex);
      logger.error(ex.getCause(), ex.getCause());
    } catch (InterruptedException e) {
      logger.debug(e, e);
      Thread.currentThread().interrupt();
    }
  }

  
  
}
