//$Id: RSSItem.java,v 1.6 2004/03/25 10:09:10 taganaka Exp $
package org.gnu.stealthp.rsslib;

/**
 * RSSItems's definitions class.
 * 
 * <blockquote> <em>This module, both source code and documentation, is in the
 * Public Domain, and comes with <strong>NO WARRANTY</strong>.</em>
 * </blockquote>
 * 
 * @since RSSLIB4J 0.1
 * @author Francesco aka 'Stealthp' stealthp[@]stealthp.org
 * @version 0.2
 */

public class RSSItem extends RSSObject {

	private String date;
	private String auth;
	private String comm;
	private Object tag;
	private boolean isReaded = false;
	private boolean isVisible = true;

	private String id;

	private String thumbailUrl;
	private String imageUrl;
	private String favLink;

	/**
	 * Get the date
	 * 
	 * @return the date as string
	 */
	public String getDate() {
		if (super.getDoublinCoreElements() == null) {
			if (super.getPubDate() == null) {
				date = null;
				return null;
			} else {
				date = super.getPubDate();
				return date;
			}
		} else {
			date = (String) super.getDoublinCoreElements().get(
					RSSHandler.DC_DATE_TAG);
			return date;
		}
	}

	public boolean isReaded() {
		return isReaded;
	}

	public void setReaded(boolean isReaded) {
		this.isReaded = isReaded;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Set the date of the item
	 * 
	 * @param d
	 *            the date
	 */
	public void setDate(String d) {
		date = d;
		if (super.getDoublinCoreElements() != null) {
			if (super.getDoublinCoreElements().containsKey(
					RSSHandler.DC_DATE_TAG)) {
				super.addDoublinCoreElement(RSSHandler.DC_DATE_TAG, d);
			} else {
				if (super.getPubDate() != null)
					super.setPubDate(d);
				date = d;
			}
		}
		date = d;
	}

	/**
	 * Set the item's author
	 * 
	 * @param author
	 *            Email address of the author of the item.
	 */
	public void setAuthor(String author) {
		auth = author;
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
	 * Get the item's author
	 * 
	 * @return author (optional)
	 */
	public String getAuthor() {
		return auth;
	}

	/**
	 * Useful for debug
	 * 
	 * @return the info string
	 */
	public String toString() {
		String info = "ABOUT ATTRIBUTE: " + about + "\n" + "TITLE: " + title
				+ "\n" + "LINK: " + link + "\n" + "DESCRIPTION: " + description
				+ "\n" + "DATE: " + getDate();
		return info;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setThumbailUrl(String thumbailUrl) {
		this.thumbailUrl = thumbailUrl;
	}

	public String getThumbailUrl() {
		return thumbailUrl;
	}

	public void setTag(Object tag) {
		this.tag = tag;
	}

	public Object getTag() {
		return tag;
	}

	public String getFavLink() {
		return favLink;
	}

	public void setFavLink(String favLink) {
		this.favLink = favLink;
	}
}