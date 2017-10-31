package com.oxygenxml.prolog.updater;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Creates and contains all content from prolog. 
 * @author intern4
 *
 */
public class PrologContentCreater {

	
	/**
	 * XML fragment for author that has type creator.
	 */
	private  String authorCreatorXmlFragment;
	/**
	 * XML fragment for author that has type contributor.
	 */
	private  String authorContributorXmlFragment;
	
	/**
	 * XML fragment for create tag
	 */
	private  String createdDateXmlFragment;
	/**
	 * XML fragment for resived tag
	 */
	private  String resivedModifiedXmlFragment;
	
	/**
	 * The local date in format "yyyy/MM/dd".
	 */
	private  String localDate;
	/**
	 * The author
	 */
	private String author;
	
	
	
	/**
	 * Constructor
	 * @param author The name of author
	 */
	public PrologContentCreater (String author) {
		this.author = author;
		authorCreatorXmlFragment = "<author type=\"creator\">" + author + "</author>";
		authorContributorXmlFragment = "<author type=\"contributor\">" + author + "</author>";
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();
		localDate = dateFormat.format(date);
		
		createdDateXmlFragment  =  "  <created date=\"" + localDate + "\" />";
		resivedModifiedXmlFragment = "<!--"+author+"-->\n<revised modified=\"" + localDate + "\" />";
	}

	/**
	 * Get all XML fragment for prolog tag, according to given state of document.
	 * @param isNewDocument <code>true</code> if document is new, <code>false</code> otherwise.
	 * @return the XML fragment in String format
	 */
	public String getPrologXMLFragment( boolean isNewDocument) {
		String toReturn = "<prolog>";

		if (isNewDocument) {
			toReturn += "\n "+authorCreatorXmlFragment+" \n<critdates>\r\n"
					+ "          "+createdDateXmlFragment+"  \n</critdates>";
		} else {
			toReturn += "\n "+authorContributorXmlFragment +"\n <critdates>\r\n"
					+ "            "+resivedModifiedXmlFragment+"  \n</critdates> ";
		}
		return toReturn + "\n</prolog>";
	}

	
	//Gettters
	public String getAuthorCreatorXmlFragment() {
		return authorCreatorXmlFragment;
	}

	public String getAuthorContributorXmlFragment() {
		return authorContributorXmlFragment;
	}

	public String getCreatedDateXmlFragment() {
		return createdDateXmlFragment;
	}

	public String getResivedModifiedXmlFragment() {
		return resivedModifiedXmlFragment;
	}

	public String getAuthor() {
		return author;
	}

	public String getLocalDate() {
		return localDate;
	}
	
	/**
	 * Get the XML fragment of author tag, according to given state of document.
	 * @param isNewDocument <code>true</code> if document is new, <code>false</code> otherwise.
	 * @return the XML fragment in String format
	 */
	public String getAuthorXmlFragment(boolean isNewDocument){
		if(isNewDocument){
			return authorCreatorXmlFragment;
		}
		else{
			return authorContributorXmlFragment;
		}
	}
	
	/**
	 * Get the XML fragment of date element, according to given state of document.
	 * @param isNewDocument <code>true</code> if document is new, <code>false</code> otherwise.
	 * @return the XML fragment in String format
	 */
	public String getDateXmlFragment(boolean isNewDocument){
		if(isNewDocument){
			return createdDateXmlFragment;
		}
		else{
			return resivedModifiedXmlFragment;
		}
	}
	
	

	
	
}
