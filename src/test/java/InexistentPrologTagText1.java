import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

import com.oxygenxml.prolog.updater.DitaUpdater;
import com.oxygenxml.prolog.updater.dita.editor.DitaTopicEditor;

import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;

public class InexistentPrologTagText1 {

	
	@Test
	public void test() {
		WSEditor wsEditor = mock(WSEditor.class);
		WSAuthorEditorPage wsAuthorEditorPage = mock(WSAuthorEditorPage.class); 
		AuthorDocumentController authorDocumentController = mock(AuthorDocumentController.class);
		AuthorElement rootElement  = mock(AuthorElement.class);
		AuthorElement bodyElement = mock(AuthorElement.class);
		
		when(wsEditor.getCurrentPage()).thenReturn(wsAuthorEditorPage);
		when(wsEditor.isNewDocument()).thenReturn(true);
		when(wsAuthorEditorPage.getDocumentController()).thenReturn(authorDocumentController);
		when(authorDocumentController.getAuthorDocumentNode().getRootElement()).thenReturn(rootElement);	
		
		
		when(bodyElement.getAttribute("class")).thenReturn(new AttrValue(DitaTopicEditor.ANTERIOR_NODE_CLASS_VALUE));
		when(bodyElement.getType()).thenReturn(AuthorNode.NODE_TYPE_ELEMENT);
		when(bodyElement.getStartOffset()).thenReturn(2);
		
		DitaUpdater ditaUpdater = new DitaUpdater();
		ditaUpdater.updateProlog(wsEditor);
		
		try {
			verify(authorDocumentController, times(1)).insertXMLFragment(anyString(), 1);
		} catch (AuthorOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		fail("Not yet implemented");
	}

}
