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
					try {
						SwingUtilities.invokeAndWait(new Runnable() {

							public void run() {
								try {
									//insert a author creator before first author.
									documentController.insertXMLFragment(authorCreatorXmlFragment, authorElements[0],
											AuthorConstants.POSITION_BEFORE);
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
					// TODO add contributor
					try {
						SwingUtilities.invokeAndWait(new Runnable() {
							public void run() {
								try {
									//insert this contributer after last one.
									documentController.insertXMLFragment(authorContributorXmlFragment, authorElements[length-1],
											AuthorConstants.POSITION_AFTER);
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

		}
		else{
			//It's not found author elements
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						try {
							if(isNewDocument){
								documentController.insertXMLFragment(authorCreatorXmlFragment, authorPrologElement.getStartOffset()+1);
							}
							else {
								documentController.insertXMLFragment(authorContributorXmlFragment, authorPrologElement.getStartOffset()+1);
							}
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

	
	
	/**
	 * 
	 * @param authorPrologElement
	 * @param isNewDocument
	 */
	private void updateCritdatesElements(AuthorElement authorPrologElement, boolean isNewDocument) {
	
		final AuthorElement[] critdatesElements = authorPrologElement.getElementsByLocalName("critdates");
	
		int length = critdatesElements.length;
		
		if(length != 0){
			if(isNewDocument){
				 AuthorElement[] creatDateElements = critdatesElements[0].getElementsByLocalName("created");
				 if(creatDateElements.length == 0){
					 try {
							SwingUtilities.invokeAndWait(new Runnable() {
								public void run() {
									try {
										//insert create date .
										documentController.insertXMLFragment(createdDateXmlFragment, critdatesElements[0].getStartOffset() + 1);
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
					 
				 }else{
					 AttrValue date = creatDateElements[0].getAttribute("date");
					 System.out.println("get date: " + date.getRawValue());
					 if(date.getRawValue().isEmpty()){
						 creatDateElements[0].setAttribute("date", new AttrValue(localDate));
					 }
				 }
			}
			else{
				 AuthorElement[] reviDateElements = critdatesElements[0].getElementsByLocalName("revised");
				 if(reviDateElements.length == 0){
					 try {
							SwingUtilities.invokeAndWait(new Runnable() {
								public void run() {
									try {
										//insert create date .
										documentController.insertXMLFragment(resivedModifiedXmlFragment, critdatesElements[0].getEndOffset() );
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
				 }else{
						 reviDateElements[0].setAttribute("modified", new AttrValue(localDate));
				 }
			}
		}else{
			final String toAdd;
			if(isNewDocument){
				toAdd = "<critdates>\n" + createdDateXmlFragment + "\n</critdates>";
			}
			else{
				toAdd = "<critdates>\n" + resivedModifiedXmlFragment + "\n</critdates>";
			}
			final AuthorElement[] authorElements = authorPrologElement.getElementsByLocalName("author");
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						try {
							//insert this contributer after last one.
							documentController.insertXMLFragment(toAdd, authorElements[authorElements.length-1],
									AuthorConstants.POSITION_AFTER);
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
}
