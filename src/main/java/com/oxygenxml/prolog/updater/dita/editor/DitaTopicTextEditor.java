package com.oxygenxml.prolog.updater.dita.editor;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import org.apache.xerces.dom.ElementNSImpl;

import com.oxygenxml.prolog.updater.PrologContentCreater;

import ro.sync.exml.editor.xmleditor.operations.context.RelativeInsertPosition;
import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.TextDocumentController;
import ro.sync.exml.workspace.api.editor.page.text.xml.TextOperationException;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextNodeRange;
import ro.sync.exml.workspace.api.editor.page.text.xml.XPathException;

/**
 * Edit DITA topic in text mode.
 * 
 * @author intern4
 *
 */
public class DitaTopicTextEditor implements DitaTopicEditor {

	/**
	 * Contains all elements from prolog.
	 */
	private PrologContentCreater prologContentCreater;
	/**
	 * Text document controller
	 */
	TextDocumentController documentController;
	
	/**
	 * WsTextEditorPage
	 */
	private WSXMLTextEditorPage wsTextEditorPage;


	/**
	 * Constructor
	 * @param wsEditorPage workspace page editor.
	 * @param prologContentCreater Content of prolog.
	 */
	public DitaTopicTextEditor(WSEditorPage wsEditorPage, PrologContentCreater prologContentCreater) {
		this.wsTextEditorPage = (WSXMLTextEditorPage) wsEditorPage;
		this.documentController = wsTextEditorPage.getDocumentController();
		this.prologContentCreater = prologContentCreater;
	}

	
	
	
	/**
	 * Update the prolog in DITA topic document(text mode) according to given
	 * flag(isNewDocument)
	 * 
	 * @param isNewDocument
	 *          <code>true</code> if document is new, <code>false</code> otherwise
	 * 
	 */
	public void updateProlog(boolean isNewDocument) {

		try {
			// get the prolog element
			WSXMLTextNodeRange[] prologElements = wsTextEditorPage
					.findElementsByXPath("//*[contains(@class,'topic/prolog')]");
			int prologElementsSize = prologElements.length;

			// The document doesn't has a prolog element
			if (prologElementsSize == 0) {
				// add the prolog xml fragment
				addXmlFragment(prologContentCreater.getPrologXMLFragment(isNewDocument), "//*[contains(@class,'topic/body')]",
						RelativeInsertPosition.INSERT_LOCATION_BEFORE);
			} else {
				// the prolog element exists
				//update the author element
				updateAuthor(isNewDocument);
				
				//update the critdates element
				updateCritdates(isNewDocument);

			}

		} catch (XPathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
 
	
	
	/**
	 * Update the critdates element of prolog.
	 * 
	 * @param isNewDocument
	 *          <code>true</code> if document is new, <code>false</code> otherwise
	 */
	private void updateCritdates(boolean isNewDocument) {

		try {
			// get the critdates element
			WSXMLTextNodeRange[] critdateElements = wsTextEditorPage
					.findElementsByXPath("//*[contains(@class,'topic/prolog')]/critdates");

			// the critdates doesn't exist
			if (critdateElements.length == 0) {
				// add the cridates xml fragment
				String toAdd = "<critdates>\n" + prologContentCreater.getDateXmlFragment(isNewDocument) + "\n</critdates>";
				addXmlFragment(toAdd, "//*[contains(@class,'topic/prolog')]/author[last()]",
						RelativeInsertPosition.INSERT_LOCATION_AFTER);
			} else {
				// the critdates element exists
				if (isNewDocument) {
					// document is new
					// search for created element.
					Object[] createdElements = wsTextEditorPage
							.evaluateXPath("//*[contains(@class,'topic/prolog')]/critdates/created");

					// created element doesn't exist
					if (createdElements.length == 0) {
						// add the created xml fragment
						addXmlFragment(prologContentCreater.getAuthorXmlFragment(isNewDocument),
								"//*[contains(@class,'topic/prolog')]/critdates",
								RelativeInsertPosition.INSERT_LOCATION_AS_FIRST_CHILD);
					} else {
						// created element exists
						if (createdElements[0] instanceof ElementNSImpl) {
							ElementNSImpl createdElement = (ElementNSImpl) createdElements[0];
							// check if value of attribute date is empty
							if (createdElement.getAttribute("date").isEmpty()) {
								System.out.println("data empty");
								// TODO problema aici
								// add the local date as value
								createdElement.setAttribute("date", prologContentCreater.getLocalDate());
							}
						}
					}
				} else {
					//it's not a new document
					//search for revised elements that have local date as modified value.
					WSXMLTextNodeRange[] revisedElements = wsTextEditorPage
							.findElementsByXPath("//*[contains(@class,'topic/prolog')]/critdates/revised[@modified = '"
									+ prologContentCreater.getLocalDate() + "']");
					
					//if the element wasn't found
					if (revisedElements.length == 0) {
						//add revised xml fragament
						addXmlFragment(prologContentCreater.getResivedModifiedXmlFragment(),
								"//*[contains(@class,'topic/prolog')]/critdates", RelativeInsertPosition.INSERT_LOCATION_AS_LAST_CHILD);
					}
				}
			}

		} catch (XPathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/**
	 * Update the author elements of prolog.
	 * @param isNewDocument <code>true</code> if document is new, <code>false</code> otherwise
	 */
	private void updateAuthor( boolean isNewDocument) {
		try {
			//get the author elements
			Object[] authorElements = wsTextEditorPage.findElementsByXPath("//*[contains(@class,'topic/prolog')]/author");
			int authorElementSize = authorElements.length;

			//if the author elements doesn't exist;
			if (authorElementSize == 0) {
				//add author xml fragment
				addXmlFragment(prologContentCreater.getAuthorXmlFragment(isNewDocument), "//*[contains(@class,'topic/prolog')]",
						RelativeInsertPosition.INSERT_LOCATION_AS_FIRST_CHILD);
			}

			else {
				// prolog contains author elements
				if (isNewDocument) {
					//the document is new
					// search for a author with value of attribute type equal with creator
					Object[] creatorAuthorElements = wsTextEditorPage
							.evaluateXPath("//*[contains(@class,'topic/prolog')]/author[@type='creator']");
					int creatorElementSize = creatorAuthorElements.length;

					//check if creator author was found 
					if (creatorElementSize == 0) {
						// there aren't creator author elements in prolog
						// add the creator author xml fragment
						addXmlFragment(prologContentCreater.getAuthorCreatorXmlFragment(), "//*[contains(@class,'topic/prolog')]",
								RelativeInsertPosition.INSERT_LOCATION_AS_FIRST_CHILD);
					} else {
						// was found a creator
						if (creatorAuthorElements[0] instanceof ElementNSImpl) {
							ElementNSImpl currentCreatorAuthor = (ElementNSImpl) creatorAuthorElements[0];
							// check if the text content is empty
							System.out.println("textConte:"+ currentCreatorAuthor.getTextContent());
							if (currentCreatorAuthor.getTextContent().isEmpty()) {
								System.out.println("isEmpty");
								//TODO Problema aici(setTextContent)
								//set the author text content
								try {
									currentCreatorAuthor.setTextContent(prologContentCreater.getAuthor());
								} catch (Throwable e) {
									e.printStackTrace();
								}
							}
						}
					}
				} else {
					// the document isn't new
					// search for a contributor author that has local author name as text.
					Object[] contributorAuthorElements = wsTextEditorPage.evaluateXPath("//*[contains(@class,'topic/prolog')]/"
							+ "author[@type='contributor' and text()= '" + prologContentCreater.getAuthor() + "']");
					int contributorElementSize = contributorAuthorElements.length;

					if (contributorElementSize == 0) {
						// there aren't contributor author elements in prolog
						//add the contributor author xml content
						addXmlFragment(prologContentCreater.getAuthorContributorXmlFragment(),
								"//*[contains(@class,'topic/prolog')]/author[last()]", RelativeInsertPosition.INSERT_LOCATION_AFTER);
					}
				}
			}

		} catch (XPathException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Create a AWT thread and insert the given fragment,
	 * 
	 * @param xmlFragment
	 *          The XML fragment.
	 * @param xPath
	 *          The xPath to insert fragment relative to.
	 * @param position
	 *          The position relative to the node. Can be one of the constants:
	 *          {@link RelativeInsertPosition#INSERT_LOCATION_AFTER}, {@link RelativeInsertPosition#INSERT_LOCATION_AS_FIRST_CHILD},
	 *          {@link RelativeInsertPosition#INSERT_LOCATION_AS_LAST_CHILD} or
	 *          {@link RelativeInsertPosition#INSERT_LOCATION_BEFORE}.
	 */
	public void addXmlFragment(final String xmlFragment, final String xPath, final RelativeInsertPosition position) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				public void run() {
					try {
						documentController.insertXMLFragment(xmlFragment, xPath, position);
					} catch (TextOperationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
