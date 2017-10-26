package com.oxygenxml.prolog.updater.plugin;

import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

import ro.sync.ecss.extensions.api.AuthorConstants;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;

public class PrologContentCreater {

	private final AuthorDocumentController documentController;
	private final String authorCreatorXmlFragment;
	private final String authorContributorXmlFragment;
	private final String createdDateXmlFragment;
	private final String resivedModifiedXmlFragment;
	private final String localDate;
	private String author;
	
	
	
	/**
	 * 
	 * @param documentController
	 * @param author
	 */
	public PrologContentCreater(AuthorDocumentController documentController, String author) {
		this.documentController = documentController;
		this.author = author;
		authorCreatorXmlFragment = "<author type=\"creator\">" + author + "</author>";
		authorContributorXmlFragment = "<author type=\"contributor\">" + author + "</author>";
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();
		localDate = dateFormat.format(date);
		
		createdDateXmlFragment  =  "<created date=\"" + localDate + "\" />";
		resivedModifiedXmlFragment = "<revised modified=\"" + localDate + "\" />";
	}

	/**
	 * 
	 * @param author
	 * @param isNewDocument
	 * @return
	 */
	public String createPrologXMLFragment( boolean isNewDocument) {
		String toReturn = "<prolog>";

		if (isNewDocument) {
			toReturn += "\n "+authorCreatorXmlFragment+" <critdates>\r\n"
					+ "          "+createdDateXmlFragment+"  \n</critdates>";
		} else {
			toReturn += "\n "+authorContributorXmlFragment +"<critdates>\r\n"
					+ "            "+resivedModifiedXmlFragment+"  \n</critdates> ";
		}

		return toReturn + "\n</prolog>";
	}

	/**
	 * 
	 * @param documentController
	 * @param authorPrologElement
	 * @param author
	 * @param isNewDocument
	 */
	public void updatePrologElement(AuthorElement authorPrologElement, boolean isNewDocument) {

		updateAuthorElements(authorPrologElement, isNewDocument);
		updateCritdatesElements(authorPrologElement, isNewDocument);

	}

	/**
	 * 
	 * @param authorPrologElement
	 * @param isNewDocument
	 */
	private void updateAuthorElements(final AuthorElement authorPrologElement, final boolean isNewDocument) {
		boolean foundCreator = false;
		boolean foundContributor = false;

		//get the author elements
		final AuthorElement[] authorElements = authorPrologElement.getElementsByLocalName("author");
		final int length = authorElements.length;
		
		//it's found author elements
		if (length != 0) {
			
			if (isNewDocument) {
				//it's a new document

				//check it's already set a creator
				for (int i = 0; i < length; i++) {
					AuthorElement authorElement = authorElements[i];
					if (authorElement.getAttribute("type").getRawValue().equals("creator")) {
						foundCreator = true;
						break;
					}
				}
				//if wasn't found a creator
				if (!foundCreator) {
					addXmlFragment(authorCreatorXmlFragment, authorElements[0],
							AuthorConstants.POSITION_BEFORE);
				}
				
				
			} else {
				//it's not a new document
				//check it's already set this contributor
				for (int i = 0; i < length; i++) {
					AuthorElement authorElement = authorElements[i];
					String textContent = "";

					//get the text content of node
					try {
						textContent = authorElement.getTextContent();
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
					if (authorElement.getAttribute("type").getRawValue().equals("contributor") && author.equals(textContent)) {
						foundContributor = true;
						break;
					}
				}
				
				//if wasn't found this contributor
				if (!foundContributor) {
					addXmlFragment(authorContributorXmlFragment, authorElements[length-1],
							AuthorConstants.POSITION_AFTER);
				}
			}

		}
		else{
			//It's not found author elements
			if(isNewDocument){
				addXmlFragment(authorCreatorXmlFragment, authorPrologElement.getStartOffset()+1);
			}
			else{
				addXmlFragment(authorContributorXmlFragment, authorPrologElement.getStartOffset()+1);
			}
		}
	}

	
	
	/**
	 * 
	 * @param authorPrologElement
	 * @param isNewDocument
	 */
	private void updateCritdatesElements(AuthorElement authorPrologElement, boolean isNewDocument) {
		//get the critdates author elements
		final AuthorElement[] critdatesElements = authorPrologElement.getElementsByLocalName("critdates");
		int length = critdatesElements.length;
		
		if(length != 0){
			//was found critdates element
			if(isNewDocument){
				//is a new document
				//get the created elements
				 AuthorElement[] creatDateElements = critdatesElements[0].getElementsByLocalName("created");
		
				 //if wasn't found a created element.
				 if(creatDateElements.length == 0){
					 addXmlFragment(createdDateXmlFragment, critdatesElements[0].getStartOffset() + 1);
					 
				 }else{
					 //if was found a created element
					 AttrValue date = creatDateElements[0].getAttribute("date");
					 //check if value of atribute date is empty
					 if(date.getRawValue().isEmpty()){
						 //add the localDate as value
						 creatDateElements[0].setAttribute("date", new AttrValue(localDate));
					 }
				 }
			}
			else{
				//it's not a new document
				 AuthorElement[] reviDateElements = critdatesElements[0].getElementsByLocalName("revised");
				 int reviDateElementsLength = reviDateElements.length;
				 
				 if(reviDateElementsLength == 0){
					 addXmlFragment(resivedModifiedXmlFragment, critdatesElements[0].getEndOffset());
				 }else{
					 boolean localDateExist = false;
					 for (int i = 0; i < reviDateElementsLength; i++) {
						 AuthorElement curentRevisedElement = reviDateElements[i];
						 
						 String currentModifiedDate = curentRevisedElement.getAttribute("modified").getRawValue();
						 if(localDate.equals(currentModifiedDate) ){
							 localDateExist = true;
							 break;
						 }
					}
					 if(!localDateExist){
						 addXmlFragment(resivedModifiedXmlFragment, reviDateElements[reviDateElementsLength-1], AuthorConstants.POSITION_AFTER);
					 }
					 
				 }
			}
			
		}else{
			//wasn't found critdates element
			final String toAdd;
			if(isNewDocument){
				toAdd = "<critdates>\n" + createdDateXmlFragment + "\n</critdates>";
			}
			else{
				toAdd = "<critdates>\n" + resivedModifiedXmlFragment + "\n</critdates>";
			}
			final AuthorElement[] authorElements = authorPrologElement.getElementsByLocalName("author");
			
			addXmlFragment(toAdd, authorElements[authorElements.length-1], AuthorConstants.POSITION_AFTER);
		}
	}
	
	/**
	 * 
	 * @param xmlFragment
	 * @param offset
	 */
	private void addXmlFragment(final String xmlFragment, final int offset){
		 try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						try {
							//insert create date .
							documentController.insertXMLFragment(xmlFragment, offset );
						} catch (AuthorOperationException e) {
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
	
	/**
	 * 
	 * @param xmlFragment
	 * @param relativeTo
	 * @param relativPosition
	 */
	private void addXmlFragment(final String xmlFragment, final AuthorNode relativeTo, final String relativPosition){
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					try {
						//insert create date .
						documentController.insertXMLFragment(xmlFragment, relativeTo, relativPosition);
					} catch (AuthorOperationException e) {
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
