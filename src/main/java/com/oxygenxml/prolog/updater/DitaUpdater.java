package com.oxygenxml.prolog.updater;

import com.oxygenxml.prolog.updater.dita.editor.DitaTopicAuthorEditor;
import com.oxygenxml.prolog.updater.dita.editor.DitaTopicEditor;
import com.oxygenxml.prolog.updater.dita.editor.DitaTopicTextEditor;

import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextEditorPage;

/**
 * Update the prolog in DITA topics.
 * 
 * @author cosmin_duna
 */
public class DitaUpdater {
  
  /**
   * Unknown user name value.
   */
  private static final String UNKNOWN = "unknown";
  /**
   * System property of user name.
   */
  private static final String USER_NAME_PROPERTY = "user.name";
  
  /**
   * Update the prolog of the current page from given wsEditor.
   * 
   * @param wsEditor Workspace editor.
   */
  public void updateProlog(WSEditor wsEditor) {

    //get the currentPage
    WSEditorPage currentPage = wsEditor.getCurrentPage();

    //create a PrologContentCreator
    PrologContentCreator prologContentCreater = new PrologContentCreator(getAuthorName());

    DitaTopicEditor ditaEditor = null;

    if (currentPage instanceof WSAuthorEditorPage) {
      ditaEditor = new DitaTopicAuthorEditor((WSAuthorEditorPage)currentPage, prologContentCreater);

    } else if (currentPage instanceof WSXMLTextEditorPage) {
      ditaEditor = new DitaTopicTextEditor((WSXMLTextEditorPage)currentPage, prologContentCreater);
    }

    if(ditaEditor != null){
      ditaEditor.updateProlog(wsEditor.isNewDocument());
    }
  }


  /**
   * @return The author's name. Never <code>null</code>.
   */
  protected String getAuthorName(){
    String name = System.getProperty(USER_NAME_PROPERTY);
    return name != null ? name : UNKNOWN;
  }
}