//$Id: RSSObject.java,v 1.6 2004/03/25 10:09:10 taganaka Exp $
package org.gnu.stealthp.rsslib;


/**
 * Handler for all common informations about rss elements.
 * 
 * <blockquote> <em>This module, both source code and documentation, is in the
 * Public Domain, and comes with <strong>NO WARRANTY</strong>.</em>
 * </blockquote>
 * 
 * @since RSSLIB4J 0.1
 * @author Francesco aka 'Stealthp' stealthp[@]stealthp.org
 * @version 0.2
 */

public abstract class RSSObject {
	private long id;
	private String category;
	protected String about;
	protected String title;
	protected String link;
	protected String description;
	protected String pdate;
	private String feedback = "";
	private String favLink;
	private String comm;

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public RSSObject() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Set the element title
	 * 
	 * @param t
	 *            The title
	 */
	public void setTitle(String t) {
		this.title = t;
	}

	/**
	 * Set about attribute of the element (if have)
	 * 
	 * @param ab
	 *            The about content
	 */
	public void setAboutAttribute(String ab) {
		this.about = ab;
	}

	/**
	 * Set the link of the resource
	 * 
	 * @param l
	 *            The link
	 */
	public void setLink(String l) {
		this.link = l;
	}

	/**
	 * Set the descriprion of the element
	 * 
	 * @param des
	 *            The description
	 */
	public void setDescription(String des) {
		this.description = des;
	}

	/**
	 * The publication date for the content in the channel or in the items
	 * 
	 * @param pubDate
	 *            The date
	 */
	public void setPubDate(String pubDate) {
		pdate = pubDate;
	}

	/**
	 * Get about attribute of element
	 * 
	 * @return The attribute value
	 */
	public String getAboutAttribute() {
		return this.about;
	}

	/**
	 * Get the element's title
	 * 
	 * @return the title
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * Get the publication date of the channel or of an item
	 * 
	 * @return The publication date for the content in the channel
	 */
	public String getPubDate() {
		String pubdate = pdate;
		return pubdate;
	}

	/**
	 * Get the element's link
	 * 
	 * @return the link
	 */
	public String getLink() {
		return this.link;
	}

	/**
	 * Get the element's description
	 * 
	 * @return the descricption
	 */
	public String getDescription() {
		return this.description;
	}

	public String getFavLink() {
		return favLink;
	}

	public void setFavLink(String favLink) {
		this.favLink = favLink;
	}

	/**
	 * Set the item's comment
	 * 
	 * @param comment
	 *            URL of a page for comments relating to the item
	 */
	public void setComments(String comment) {
		comm = comment;
	}

	/**
	 * Get the comments url
	 * 
	 * @return comments url (optional)
	 */
	public String getComments() {
		return comm;
	}

	/**
	 * Each class have to implement this information method
	 * 
	 * @return An information about element
	 */
	public abstract String toString();
}