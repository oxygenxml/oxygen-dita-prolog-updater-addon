package com.oxygenxml.prolog.updater.prolog.content;

public class ElementGetter {

  public static String getPrologStartElement(boolean isTopic) {
    if(isTopic) {
      return "<prolog>";
    }else {
      return "<topicmeta>";
    }
  }
  
  
  public static String getPrologEndElement(boolean isTopic) {
    if(isTopic) {
      return "</prolog>";
    }else {
      return "</topicmeta>";
    }
  }
}
