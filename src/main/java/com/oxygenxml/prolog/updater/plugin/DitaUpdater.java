package com.oxygenxml.prolog.updater.plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingUtilities;

import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.TextDocumentController;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextNodeRange;
import ro.sync.exml.workspace.api.editor.page.text.xml.XPathException;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * Update the prolog in DITA topics.
 * 
 * @author intern4
 *
 */
public class DitaUpdater {

	/**
	 * StandalonePluginWorkspace
	 */
	private StandalonePluginWorkspace workspace;

	/**
	 * Class name value of prolog anterior note.
	 */
	private static final String ANTERIOR_NODE_CLASS_NAME_VALUE = "topic/body";

	/**
	 * Constructor
	 * 
	 * @param workspace
	 *          standalonePluginWorkspace
	 */
	public DitaUpdater(StandalonePluginWorkspace workspace) {
		this.workspace = workspace;
	}

	/**
	 * Update the prolog of the current page from given wsEditor.
	 * 
	 * @param wsEditor
	 *          Workspace editor.
	 * @return <code>true</code> if document was update, <code>false</code>
	 *         otherwise;
	 */
	public boolean updateProlog(WSEditor wsEditor) {
		boolean toReturn = true;

		WSEditorPage currentPage = wsEditor.getCurrentPage();

		if (currentPage instanceof WSAuthorEditorPage) {

			updateInAuthor((WSAuthorEditorPage) currentPage, wsEditor.isNewDocument());
			System.out.println("save from autor");

		} else if (currentPage instanceof WSXMLTextEditorPage) {
			System.out.println(" save from text");
			updateInTextEditor((WSXMLTextEditorPage) currentPage, wsEditor.isNewDocument());
		}

		return toReturn;
	}

	/**
	 * 
	 * @param currentPage
	 * @param isNewDocument
	 */
	private void updateInAuthor(WSAuthorEditorPage wsAuthorEditorPage, boolean isNewDocument) {

		// get the reviewerAuthorName
		String reviewerAuthorName = wsAuthorEditorPage.getAuthorAccess().getReviewController().getReviewerAuthorName();
		System.out.println("author name: " + reviewerAuthorName);

		final AuthorDocumentController documentController = wsAuthorEditorPage.getDocumentController();

		PrologContentCreater prologContentCreater = new PrologContentCreater(documentController, reviewerAuthorName);

		AuthorElement rootElement = documentController.getAuthorDocumentNode().getRootElement();

		AuthorElement[] prologElement = rootElement.getElementsByLocalName("prolog");
		int prologElementSize = prologElement.length;

		if (prologElementSize != 0) {
			// prolog node exists;
			prologContentCreater.updatePrologElement(prologElement[0], isNewDocument);

		} else {
			// prolog node doesn't exist
			final AuthorElement bodyAuthorElement = getPrologAnteriorAuthorElement(rootElement);

			if (bodyAuthorElement != null) {
				final String prologXMLFragment = prologContentCreater.createPrologXMLFragment(isNewDocument);

				try {
					SwingUtilities.invokeAndWait(new Runnable() {

						public void run() {
							try {
								documentController.insertXMLFragment(prologXMLFragment, bodyAuthorElement.getStartOffset());
							} catch (AuthorOperationException e) {
								e.printStackTrace();
							}
						}
					});
				} catch (InvocationTargetException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}

	}

	/**
	 * 
	 * @param currentPage
	 * @param isNewDocument
	 */
	private void updateInTextEditor(WSXMLTextEditorPage currentPage, boolean isNewDocument) {

		try {
			WSXMLTextNodeRange[] findElementsByXPath = currentPage.findElementsByXPath("//node()");
			System.out.println("size "+ findElementsByXPath.length);
			for (int i = 0; i < findElementsByXPath.length; i++) {
				System.out.println(findElementsByXPath[i].toString());
			} 
			
		} catch (XPathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
	/**
	 * Get the anterior author element of prolog node.
	 * 
	 * @param rootElement
	 * @return
	 */
	private AuthorElement getPrologAnteriorAuthorElement(AuthorElement rootElement) {
		List<AuthorNode> contentNodes = rootElement.getContentNodes();

		for (Iterator<AuthorNode> iterator = contentNodes.iterator(); iterator.hasNext();) {
			AuthorNode authorNode = (AuthorNode) iterator.next();
			if (authorNode.getType() == AuthorNode.NODE_TYPE_ELEMENT) {
				AuthorElement authorElement = (AuthorElement) authorNode;
				AttrValue attribute = authorElement.getAttribute("class");

				if (attribute.toString().contains(ANTERIOR_NODE_CLASS_NAME_VALUE)) {
					return authorElement;
				}
			}

		}
		return null;
	}
}
