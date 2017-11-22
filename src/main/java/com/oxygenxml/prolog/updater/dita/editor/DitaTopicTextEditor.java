package com.oxygenxml.prolog.updater.dita.editor;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import com.oxygenxml.prolog.updater.PrologContentCreator;

import ro.sync.exml.editor.xmleditor.operations.context.RelativeInsertPosition;
import ro.sync.exml.workspace.api.editor.page.text.xml.TextDocumentController;
import ro.sync.exml.workspace.api.editor.page.text.xml.TextOperationException;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextNodeRange;
import ro.sync.exml.workspace.api.editor.page.text.xml.XPathException;

/**
 * Edit DITA topic in text mode.
 * 
 * @author cosmin_duna
 *
 */
public class DitaTopicTextEditor implements DitaTopicEditor {

	/**
	 * XPath expression for Prolog element.
	 */
	private static final String PROLOG_XPATH = "//*[contains(@class,'topic/prolog')]";
	
	/**
	 * Xpath expression for critdates element.
	 */
	private static final String CRITDATES_XPATH = PROLOG_XPATH + "/critdates";
	
	
	/**
	 * Contains all elements from prolog.
	 */
	private PrologContentCreator prologContentCreater;
	/**
	 * Text document controller
	 */
	TextDocumentController documentController;
	
	/**
	 * WsTextEditorPage
	 */
	private WSXMLTextEditorPage wsTextEditorPage;

	/**
	 * Logger
	 */
	 private static final Logger logger = Logger.getLogger(DitaTopicTextEditor.class);
	

	/**
	 * Constructor
	 * @param wsEditorPage workspace page editor.
	 * @param prologContentCreater Content of prolog.
	 */
	public DitaTopicTextEditor(WSXMLTextEditorPage wsEditorPage, PrologContentCreator prologContentCreater) {
		this.wsTextEditorPage = wsEditorPage;
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
			WSXMLTextNodeRange[] prologElements = wsTextEditorPage.findElementsByXPath(PROLOG_XPATH);
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
			logger.debug(e.getMessage(), e);
		}

	}
 
	
	
	/**
	 * Update the critdates element of prolog.
	 * 
	 * @param isNewDocument
	 *          <code>true</code> if document is new, <code>false</code> otherwise
	 * @throws XPathException 
	 */
	private void updateCritdates(boolean isNewDocument) throws XPathException {

			// get the critdates element
			WSXMLTextNodeRange[] critdateElements = wsTextEditorPage
					.findElementsByXPath(CRITDATES_XPATH);

			// the critdates doesn't exist
			if (critdateElements.length == 0) {
				// add the cridates xml fragment
				String toAdd = "<critdates>\n" + prologContentCreater.getDateFragment(isNewDocument) + "\n</critdates>";
				addXmlFragment(toAdd, PROLOG_XPATH+"/author[last()]",
						RelativeInsertPosition.INSERT_LOCATION_AFTER);
			
			} else {
				// the critdates element exists
				if (isNewDocument) {
					// document is new
					// search for created element.
					Object[] createdElements = wsTextEditorPage
							.evaluateXPath(CRITDATES_XPATH + "/created");

					// created element doesn't exist
					if (createdElements.length == 0) {
						// add the created xml fragment
						addXmlFragment(prologContentCreater.getPrologAuthorElement(isNewDocument), CRITDATES_XPATH,
								RelativeInsertPosition.INSERT_LOCATION_AS_FIRST_CHILD);
					} 
				} else {
					//it's not a new document
					//search for revised elements that have local date as modified and have contributor as comment
					Object[] revisedElements = wsTextEditorPage
							.findElementsByXPath(CRITDATES_XPATH + "/revised[@modified = '"
									+ prologContentCreater.getLocalDate() + "']/"
											+ "preceding-sibling::node()[2][.='"+prologContentCreater.getAuthor()+"']"); 
					
					//if the element wasn't found
					if (revisedElements.length == 0) {
						//add revised xml fragament
						addXmlFragment(prologContentCreater.getResivedModifiedFragment(),
								CRITDATES_XPATH, RelativeInsertPosition.INSERT_LOCATION_AS_LAST_CHILD);
					}
				}
			}

		
	}

	
	/**
	 * Update the author elements of prolog.
	 * @param isNewDocument <code>true</code> if document is new, <code>false</code> otherwise
	 * @throws XPathException 
	 */
	private void updateAuthor( boolean isNewDocument) throws XPathException {
		// get the author elements
		Object[] authorElements = wsTextEditorPage.findElementsByXPath(PROLOG_XPATH + "/author");
		int authorElementSize = authorElements.length;

		if (authorElementSize == 0) {
			// if the author elements doesn't exist
			// add author xml fragment
			addXmlFragment(prologContentCreater.getPrologAuthorElement(isNewDocument), PROLOG_XPATH,
					RelativeInsertPosition.INSERT_LOCATION_AS_FIRST_CHILD);
		}else {
			// prolog contains author elements
			if (isNewDocument) {
				// the document is new
				// search for a author with value of attribute type equal with creator
				Object[] creatorAuthorElements = wsTextEditorPage
						.evaluateXPath(PROLOG_XPATH +"/author[@type='creator']");
				int creatorElementSize = creatorAuthorElements.length;

				// check if creator author was found
				if (creatorElementSize == 0) {
					// there aren't creator author elements in prolog
					// add the creator author xml fragment
					addXmlFragment(prologContentCreater.getCreatorFragment(), PROLOG_XPATH,
							RelativeInsertPosition.INSERT_LOCATION_AS_FIRST_CHILD);
				}

			} else {
				// the document isn't new
				// search for a contributor author that has local author name as text
				Object[] contributorAuthorElements = wsTextEditorPage.evaluateXPath(PROLOG_XPATH + 
						"/author[@type='contributor' and text()= '" + prologContentCreater.getAuthor() + "']");
				int contributorElementSize = contributorAuthorElements.length;

				if (contributorElementSize == 0) {
					// there aren't contributor author elements in prolog
					// add the contributor author xml content
					addXmlFragment(prologContentCreater.getContributorFragment(),
							PROLOG_XPATH + "/author[last()]", RelativeInsertPosition.INSERT_LOCATION_AFTER);
				}
			}
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
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						documentController.insertXMLFragment(xmlFragment, xPath, position);
					} catch (TextOperationException e) {
						logger.debug(e.getMessage(), e);
					}
				}
			});
	}
}
