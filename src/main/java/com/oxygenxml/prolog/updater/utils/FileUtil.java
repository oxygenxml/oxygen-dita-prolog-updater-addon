package com.oxygenxml.prolog.updater.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.TimeUnit;

/**
 * Utility methods for working with Files.
 * 
 * @author cosmin_duna
 *
 */
public class FileUtil {

  /**
   * Private constructor.
   */
  private FileUtil() {
    // Avoid instantiation.
  }
  

  /**
   * Check if the given file is a new one,
   * verifying the creation time and the last modification time.
   * ONLY for WINDOWS OS!
   * 
   * @param fileLocation The file location.
   * 
   * @return <code>true</code> if the file is new, <code>false</code> if it's not new and this doesn't exist.
   */
  public static boolean isNewFile(URL fileLocation) {
    boolean isNew = false;
    File file = new File(fileLocation.getFile());
    try {
      BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
      FileTime creationTime = attr.creationTime();
      FileTime lastModifiedTime = attr.lastModifiedTime();
      if (creationTime != null && lastModifiedTime != null) {
        long creationTimeToSeconds = creationTime.to(TimeUnit.SECONDS);
        long lastModTimeToSeconds = lastModifiedTime.to(TimeUnit.SECONDS);
        if (creationTimeToSeconds == lastModTimeToSeconds 
            || creationTimeToSeconds == lastModTimeToSeconds - 1) {
          isNew = true;
        }
      }
    } catch (IOException e) {
      // Ignore this
    }

    return isNew;
  }
}
