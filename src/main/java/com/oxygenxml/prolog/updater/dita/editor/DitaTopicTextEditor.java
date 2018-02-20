package com.oxygenxml.prolog.updater.dita.editor;

import org.apache.log4j.Logger;

import com.oxygenxml.prolog.updater.prolog.content.PrologContentCreator;
import com.oxygenxml.prolog.updater.utils.ElementXPathConstants;
import com.oxygenxml.prolog.updater.utils.ElementXPathUtils;
import com.oxygenxml.prolog.updater.utils.TextPageDocumentUtil;
import com.oxygenxml.prolog.updater.utils.XMLFragmentUtils;
import com.oxygenxml.prolog.updater.utils.XmlElementsConstants;
import com.oxygenxml.prolog.updater.utils.XmlElementsUtils;

import ro.sync.exml.editor.xmleditor.operations.context.RelativeInsertPosition;
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
public class DitaTopicTextEditor implements DitaEditor {

	/**
	 * Logger
	 */
	private static final Logger logger = Logger.getLogger(DitaTopicTextEditor.class);

	/**
	 * Contains all elements from prolog.
	 */
	private PrologContentCreator prologCreator;

	/**
	 * The page from Workspace text editor.
	 */
	private WSXMLTextEditorPage wsTextEditorPage;

	/**
	 * The type of the document( {@link DocumentType#TOPIC},
	 * {@link DocumentType#MAP} or {@link DocumentType#BOOKMAP} )..
	 */
	private DocumentType documentType = DocumentType.TOPIC;

	/**
	 * Constructor
	 * 
	 * @param wsEditorPage
	 *          workspace page editor.
	 * @param prologCreator
	 *          Content of prolog.
	 */
	public DitaTopicTextEditor(WSXMLTextEditorPage wsEditorPage, PrologContentCreator prologCreator) {
		this.wsTextEditorPage = wsEditorPage;

		documentType = getDocumentType();

		this.prologCreator = prologCreator;
	}

	/**
	 * Update the prolog element in text page.
	 * 
	 * @param isNewDocument
	 *          <code>true</code> if document is new
	 * 
	 * @return <code>true</code> if prolog was update, <code>false</code>
	 *         otherwise.
	 */
	public boolean updateProlog(boolean isNewDocument) {
		boolean toReturn = true;
		// get the prolog element
		WSXMLTextNodeRange[] prologs;
		try {
			prologs = wsTextEditorPage.findElementsByXPath(ElementXPathUtils.getPrologXpath(documentType));
			// The document doesn't have a prolog element
			if (prologs.length == 0) {
				// the prolog element doesn't exist
				addProlog(isNewDocument);
			} else {
				// the prolog element exists.
				// update the author element.
				updateAuthor(isNewDocument);

				// update the critdates element
				updateCritdates(isNewDocument);
			}
		} catch (XPathException e) {
			toReturn = false;
		} catch (TextOperationException e) {
			toReturn = false;
		}

		return toReturn;
	}

	/**
	 * Add the prolog element.
	 * 
	 * @param isNewDocument
	 *          <code>true</code> if document is new
	 * @throws TextOperationException
	 *           If the element could not be added.
	 *
	 */
	private void addProlog(boolean isNewDocument) throws TextOperationException {
		// Search for a possible prolog xpath.
		String xp = TextPageDocumentUtil.findElementXPath(
				wsTextEditorPage, 
				XmlElementsUtils.getPrologName(documentType),
				ElementXPathUtils.getRootXpath(documentType),
				documentType);
		
		if (xp != null) {
			TextPageDocumentUtil.insertXmlFragment(wsTextEditorPage,
					prologCreator.getPrologFragment(isNewDocument, documentType), xp,
					RelativeInsertPosition.INSERT_LOCATION_AFTER);
		} else {
			TextPageDocumentUtil.insertXmlFragment(wsTextEditorPage,
					prologCreator.getPrologFragment(isNewDocument, documentType), ElementXPathUtils.getRootXpath(documentType),
					RelativeInsertPosition.INSERT_LOCATION_AS_FIRST_CHILD);
		}
	}

	/**
	 * Update the critdates element of prolog.
	 * 
	 * @param isNewDocument
	 *          <code>true</code> if document is new, <code>false</code> otherwise
	 * @throws XPathException
	 *           If the element could not be update
	 * @throws TextOperationException
	 *           If the element could not be update.
	 */
	private void updateCritdates(boolean isNewDocument) throws XPathException, TextOperationException {

		// get the critdates element
		WSXMLTextNodeRange[] critdateElements = wsTextEditorPage
				.findElementsByXPath(ElementXPathUtils.getCritdatesXpath(documentType));

		if (critdateElements.length == 0) {
			//The critdates element doesn't exit.
			// Add the element.
			addCritdates(isNewDocument);

		} else {
			// The critdates element exists.
			// Edit the element.
			editCritdates(isNewDocument);
		}
	}

	/**
	 * Add the critdates element.
	 * 
	 * @param isNewDocument
	 *          <code>true</code> if document is new
	 * @throws TextOperationException
	 *           If the element could not be added.
	 *
	 */
	private void addCritdates(boolean isNewDocument) throws TextOperationException {
		// Search for a Xpath where to insert the critdates element.
		String xpath = TextPageDocumentUtil.findElementXPath(wsTextEditorPage,
				XmlElementsConstants.CRITDATES_NAME, 
				ElementXPathUtils.getPrologXpath(documentType),
				documentType);
		
		if(xpath == null) {
			xpath = ElementXPathUtils.getLastAuthorXpath(documentType);
		}
		
		String dateFragment = prologCreator.getDateFragment(isNewDocument, documentType);
		String toAdd = XMLFragmentUtils.createCritdateTag(dateFragment);
		
		// Add the cridates xml fragment
		TextPageDocumentUtil.insertXmlFragment(wsTextEditorPage, toAdd,
				xpath, RelativeInsertPosition.INSERT_LOCATION_AFTER);
	}

	/**
	 * Edit the existing critdates element.
	 * 
	 * @param isNewDocument
	 *          <code>true</code> if document is new, <code>false</code> otherwise
	 * @throws XPathException
	 *           If the element could not be update
	 * @throws TextOperationException
	 *           If the element could not be edited.
	 */
	private void editCritdates(boolean isNewDocument) throws XPathException, TextOperationException {
		// the critdates element exists
		if (isNewDocument) {
			// document is new
			// search for created element.
			Object[] createdElements = wsTextEditorPage.evaluateXPath(ElementXPathUtils.getCreatedXpath(documentType));

			// created element doesn't exist
			if (createdElements.length == 0) {
				// add the created xml fragment
				TextPageDocumentUtil.insertXmlFragment(wsTextEditorPage,
						prologCreator.getPrologAuthorElement(isNewDocument, documentType),
						ElementXPathUtils.getCritdatesXpath(documentType), RelativeInsertPosition.INSERT_LOCATION_AS_FIRST_CHILD);
			}
		} else {
			// it's not a new document
			// search for revised elements that have local date as modified and have
			// contributor as comment
			Object[] revisedElements = wsTextEditorPage.findElementsByXPath(
					ElementXPathUtils.getCritdatesXpath(documentType) + "/revised[@modified = '" + prologCreator.getLocalDate()
							+ "']/" + "preceding-sibling::node()[2][.='" + prologCreator.getAuthor() + "']");

			// if the element wasn't found
			if (revisedElements.length == 0) {
				// add revised xml fragament
				TextPageDocumentUtil.insertXmlFragment(wsTextEditorPage, prologCreator.getRevisedDateFragment(documentType),
						ElementXPathUtils.getCritdatesXpath(documentType), RelativeInsertPosition.INSERT_LOCATION_AS_LAST_CHILD);
			}
		}
	}

	/**
	 * Update the author elements of prolog.
	 * 
	 * @param isNewDocument
	 *          <code>true</code> if document is new, <code>false</code> otherwise
	 * @throws XPathException
	 *           If the element could not be update
	 * @throws TextOperationException
	 *           If the element could not be update
	 */
	private void updateAuthor(boolean isNewDocument) throws XPathException, TextOperationException {
		// get the author elements
		Object[] authorElements = wsTextEditorPage.findElementsByXPath(ElementXPathUtils.getAuthorXpath(documentType));
		int authorElementSize = authorElements.length;

		// if the author elements doesn't exist
		if (authorElementSize == 0) {
			// The author element doesn't exist.
			// Add this element.
			addAuthor(isNewDocument);
		} else {
			// The author element exists.
			// Edit the author element.
			editAuthor(isNewDocument);
		}
	}

	/**
	 * Add the author element.
	 * 
	 * @param isNewDocument
	 *          <code>true</code> if document is new
	 * @throws TextOperationException
	 *           If the element could not be added.
	 *
	 */
	private void addAuthor(boolean isNewDocument) throws TextOperationException {
		// Search for a xPath where to insert the author element.
		String xpath = TextPageDocumentUtil.findElementXPath(wsTextEditorPage,
				XmlElementsConstants.AUTHOR_NAME, 
				ElementXPathUtils.getPrologXpath(documentType),
				documentType);
		
		// add author xml fragment
		if(xpath != null) {
			TextPageDocumentUtil.insertXmlFragment(wsTextEditorPage,
					prologCreator.getPrologAuthorElement(isNewDocument, documentType),
					xpath,
					RelativeInsertPosition.INSERT_LOCATION_AFTER);
		} else {
			TextPageDocumentUtil.insertXmlFragment(
					wsTextEditorPage,
					prologCreator.getPrologAuthorElement(isNewDocument, documentType),
					ElementXPathUtils.getPrologXpath(documentType), 
					RelativeInsertPosition.INSERT_LOCATION_AS_FIRST_CHILD);
		}
	}

	/**
	 * Edit the author elements of prolog element.
	 * 
	 * @param isNewDocument
	 *          <code>true</code> if document is new, <code>false</code> otherwise
	 * @throws XPathException
	 *           If the element could not be edited.
	 * @throws TextOperationException
	 *           If the element could not be edited.
	 */
	private void editAuthor(boolean isNewDocument) throws XPathException, TextOperationException {
		// prolog contains author elements
		if (isNewDocument) {
			// the document is new
			// search for a author with value of attribute type equal with creator
			Object[] creatorAuthorElements = wsTextEditorPage
					.evaluateXPath(ElementXPathUtils.getAuthorCreatorXpath(documentType));
			int creatorElementSize = creatorAuthorElements.length;

			// check if creator author was found
			if (creatorElementSize == 0) {
				// there aren't creator author elements in prolog
				// add the creator author xml fragment
				TextPageDocumentUtil.insertXmlFragment(wsTextEditorPage, prologCreator.getCreatorFragment(documentType),
						ElementXPathUtils.getLastAuthorXpath(documentType), RelativeInsertPosition.INSERT_LOCATION_AFTER);
			}

		} else {
			// the document isn't new
			// search for a contributor author that has local author name as text
			Object[] contributorAuthorElements = wsTextEditorPage.evaluateXPath(ElementXPathUtils.getPrologXpath(documentType)
					+ "/author[@type='contributor' and text()= '" + prologCreator.getAuthor() + "']");
			int contributorElementSize = contributorAuthorElements.length;

			if (contributorElementSize == 0) {
				// there aren't contributor author elements in prolog
				// add the contributor author xml content
				TextPageDocumentUtil.insertXmlFragment(wsTextEditorPage, prologCreator.getContributorFragment(documentType),
						ElementXPathUtils.getLastAuthorXpath(documentType), RelativeInsertPosition.INSERT_LOCATION_AFTER);
			}
		}
	}

	/**
	 * Get the document type of the current document.
	 * @return The document type of the current document 
	 * ( {@link DocumentType#TOPIC}, {@link DocumentType#MAP}, 
	 * {@link DocumentType#BOOKMAP} or {@link DocumentType#SUBJECT_SCHEME} ).
	 */
	private DocumentType getDocumentType() {
		DocumentType docType = DocumentType.TOPIC;
		
		try {
			WSXMLTextNodeRange[] mapRoot = wsTextEditorPage.findElementsByXPath(ElementXPathConstants.ROOT_MAP_XPATH);
			if (mapRoot.length != 0) {
				docType = DocumentType.MAP;
			}
			WSXMLTextNodeRange[] bookmapRoot = wsTextEditorPage.findElementsByXPath(ElementXPathConstants.ROOT_BOOKMAP_XPATH);
			if (bookmapRoot.length != 0) {
				docType = DocumentType.BOOKMAP;
			} else {
				WSXMLTextNodeRange[] subjectSchemaRoot = wsTextEditorPage.findElementsByXPath(
						ElementXPathConstants.ROOT_SUBJECT_SCHEMA_XPATH);
				if(subjectSchemaRoot.length != 0) {
					docType = DocumentType.SUBJECT_SCHEME;
				}
			}
		} catch (XPathException e) {
			logger.debug(e, e.getCause());
		}
		
		return docType;
	}

}
