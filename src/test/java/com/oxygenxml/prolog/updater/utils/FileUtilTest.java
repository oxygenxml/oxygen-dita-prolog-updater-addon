package com.oxygenxml.prolog.updater.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import junit.framework.TestCase;

/**
 * Tests for the FileUtil class.
 * 
 * @author cosmin_duna
 */
public class FileUtilTest extends TestCase{

  /**
   * <p><b>Description:</b> Test the 'isNewFile' method works properly.</p>
   * <p><b>Bug ID:</b> EXM-44798</p>
   *
   * @author cosmin_duna
   * @throws IOException 
   *
   * @throws Exception If it fails.
   */ 
  public void testIsNewFile() throws Exception {
    File testFile = new File("test/test.dita");
    testFile.getParentFile().mkdir();
    try {
      testFile.createNewFile();
      URL fileURL = testFile.toURI().toURL();
      assertTrue(FileUtil.isNewFile(fileURL));
      
      BasicFileAttributes attr = Files.readAttributes(testFile.toPath(), BasicFileAttributes.class);
      FileTime creationTime = attr.creationTime();
      // Add one second
      testFile.setLastModified(creationTime.toMillis() + 1000);
      Thread.sleep(500);
      assertTrue("We consider that file is new when "
          + "creation time and last modification time differ with one second.", FileUtil.isNewFile(fileURL));
      
      // Add 60 seconds
      testFile.setLastModified(creationTime.toMillis() + 60000);
      Thread.sleep(500);
      assertFalse("The file isn't new.", FileUtil.isNewFile(fileURL));
    } finally {
      testFile.delete();
      testFile.getParentFile().delete();
    }
  }
}
