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
	private String ver;

	private Object tag;
	private boolean isReaded = false;
	private boolean isSelected = false;
	private boolean isVisible = true;

	private String thumbailUrl;
	private String imageUrl;

	/**
	 * Get the date
	 * 
	 * @return the date as string
	 */
	public String getDate() {
		if (super.getPubDate() == null) {
			date = null;
			return null;
		} else {
			date = super.getPubDate();
			return date;
		}
	}

	public boolean isReaded() {
		return isReaded;
	}

	public void setReaded(boolean isReaded) {
		this.isReaded = isReaded;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	/**
	 * Set the date of the item
	 * 
	 * @param d
	 *            the date
	 */
	public void setDate(String d) {
		date = d;
		if (super.getPubDate() != null)
			super.setPubDate(d);
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

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}
}