package com.oxygenxml.prolog.updater.utils;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A collection of utility methods used in AWT invocations.
 * 
 * @author cosmin_duna
 *
 */
public class AWTUtil {

	/**
	 * Logger for logging.
	 */
	private static final Logger logger = LoggerFactory.getLogger(AWTUtil.class.getName());

	/**
	 * Private constructor for utilities class. Avoid instantiation.
	 */
	private AWTUtil() {
		// Nothing
	}

	/**
	 * Invokes a runnable synchronously on the AWT thread. The current thread may
	 * be AWT or not. Returns after the runnable is run.
	 * 
	 * @param runnable
	 *          The runnable to be run.
	 */
	public static void invokeSynchronously(final Runnable runnable) {
		try {
			if (SwingUtilities.isEventDispatchThread()) {
				runnable.run();
			} else {
				SwingUtilities.invokeAndWait(runnable);
			}
		} catch (InvocationTargetException ex) {
			logger.error(String.valueOf(ex), ex);
		} catch (InterruptedException e) {
			logger.debug(String.valueOf(e), e);
			Thread.currentThread().interrupt();
		}
	}
}
