package com.oxygenxml.prolog.updater.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import junit.framework.TestCase;

/**
 * Tests for the FileUtil class.
 * 
 * @author cosmin_duna
 */
public class FileUtilTest extends TestCase{

  /**
   * <p><b>Description:</b> Test the 'checkCurrentNewDocumentState' method works properly.</p>
   * <p><b>Bug ID:</b> EXM-44798</p>
   *
   * @author cosmin_duna
   * @throws IOException 
   *
   * @throws Exception If it fails.
   */ 
  public void testCheckCurrentNewDocumentState() throws Exception {
    File testFile = new File("test/test.dita");
    testFile.getParentFile().mkdir();
    try {
    	testFile.createNewFile();
    	URL fileURL = testFile.toURI().toURL();
    	assertTrue(FileUtil.checkCurrentNewDocumentState(fileURL));

    	// Sleep half second
    	Thread.sleep(500); // NOSONAR
    	assertTrue("We consider that file is new when "
    			+ "creation time and last modification time differ with one second.", FileUtil.checkCurrentNewDocumentState(fileURL));

     URL oldFile = ro.sync.util.URLUtil.correct(new File("test/EXM-47809/oldFile.dita"));
    	assertFalse("The file isn't new.", FileUtil.checkCurrentNewDocumentState(oldFile));
    } finally {
      testFile.delete();
      testFile.getParentFile().delete();
    }
  }

  /**
   * <p><b>Description:</b> Test the 'checkCurrentNewDocumentState' method works properly when file has white-spaces in path.</p>
   * <p><b>Bug ID:</b> EXM-47809</p>
   *
   * @author cosmin_duna
   * @throws IOException 
   *
   * @throws Exception If it fails.
   */ 
  public void testCheckCurrentNewDocumentState_EXM_47809() throws Exception {
    File testFile = new File("test/test with white spaces.dita");
    testFile.getParentFile().mkdir();
    try {
    	testFile.createNewFile();
    	Thread.sleep(100);
    	URL fileURL = testFile.toURI().toURL();
    	assertTrue(FileUtil.checkCurrentNewDocumentState(fileURL));
    } finally {
      testFile.delete();
      testFile.getParentFile().delete();
    }
  }
}
