//$Id: RSSChannel.java,v 1.7 2004/09/04 12:09:24 taganaka Exp $
package org.gnu.stealthp.rsslib;

import java.util.ArrayList;

/**
 * RSSChannel's definitions class.
 * 
 * <blockquote> <em>This module, both source code and documentation, is in the
 * Public Domain, and comes with <strong>NO WARRANTY</strong>.</em>
 * </blockquote>
 * 
 * @since RSSLIB4J 0.1
 * @author Francesco aka 'Stealthp' stealthp[@]stealthp.org
 * @version 0.2
 */

public class RSSChannel extends RSSObject {

	private ArrayList<RSSItem> rss_items;
	private RSSImage img;
	private RSSTextInput input;
	private RSSSyndicationModule sy;


	private String nextLink;
	private String prevLink;

	public String getPrevLink() {
		return prevLink;
	}

	public void setPrevLink(String prevLink) {
		this.prevLink = prevLink;
	}

	private String lang;
	private String copy;
	private String managing;
	private String master;
	private String bdate;
	private String udate;
	private String gen;
	private String t;
	private String docs;

	public RSSChannel() {
		rss_items = new ArrayList<RSSItem>();
	}

	/**
	 * Set the language of channel
	 * 
	 * @param language
	 *            The language the channel is written in
	 */
	public void setLanguage(String language) {
		lang = language;
	}

	/**
	 * Set channel's copyright
	 * 
	 * @param copyright
	 *            Copyright notice for content in the channel
	 */
	public void setCopyright(String copyright) {
		copy = copyright;
	}

	/**
	 * Set the lastBuildDate
	 * 
	 * @param lastBuildDate
	 *            The last time the content of the channel changed
	 */
	public void setLastBuildDate(String lastBuildDate) {
		bdate = lastBuildDate;
	}

	/**
	 * Set the managingEditor
	 * 
	 * @param managingEditor
	 *            Email address for person responsible for editorial content
	 */
	public void setManagingEditor(String managingEditor) {
		managing = managingEditor;
	}

	/**
	 * Set the webMaster
	 * 
	 * @param webMaster
	 *            Email address for person responsible for technical issues
	 *            relating to channel.
	 */
	public void setWebMaster(String webMaster) {
		master = webMaster;
	}

	/**
	 * Set the gerator
	 * 
	 * @param generator
	 *            A string indicating the program used to generate the channel
	 */
	public void setGenerator(String generator) {
		gen = generator;
	}

	/**
	 * Set the TTL time
	 * 
	 * @param ttl
	 *            the time to live
	 */
	public void setTTL(String ttl) {
		t = ttl;
	}

	/**
	 * Set the documentator
	 * 
	 * @param docs
	 *            thw documentator
	 */
	public void setDocs(String docs) {
		this.docs = docs;
	}

	/**
	 * Set a RSSImage object associated to the channel
	 * 
	 * @param im
	 *            Specifies a GIF, JPEG or PNG image that can be displayed with
	 *            the channel.
	 */
	public void setRSSImage(RSSImage im) {
		this.img = im;
	}

	/**
	 * Set a RSSTextInput object to a channel
	 * 
	 * @param in
	 *            Specifies a text input box that can be displayed with the
	 *            channel
	 */
	public void setRSSTextInput(RSSTextInput in) {
		this.input = in;
	}

	/**
	 * Get channel's lastBuildDate
	 * 
	 * @return lastBuildDate
	 */
	public String getLastBuildDate() {
		return bdate;
	}

	/**
	 * Get the chyannel's copyright
	 * 
	 * @return copyright (optional)
	 */
	public String getCopyright() {
		return copy;
	}

	/**
	 * Get the generator program's channel
	 * 
	 * @return generator (optional)
	 */
	public String getGenerator() {
		return gen;
	}

	/**
	 * Return the TTL's channel
	 * 
	 * @return TTL (optional)
	 */
	public String getTTL() {
		return t;
	}

	/**
	 * Get the docs url about Rss specifications
	 * 
	 * @return the url (optional)
	 */
	public String getDocs() {
		return docs;
	}

	/**
	 * Get the language of channell
	 * 
	 * @return language (optional)
	 */
	public String getLanguage() {
		return lang;
	}

	/**
	 * Get the webmaster email
	 * 
	 * @return email of webmaster (optional)
	 */
	public String getWebMaster() {
		return master;
	}

	/**
	 * Get a RSSTextInput object from the channel
	 * 
	 * @return the RSSTextInput or null
	 */
	public RSSTextInput getRSSTextInput() {
		return input;
	}

	/**
	 * Add an RSSItem to a channel object
	 * 
	 * @param itm
	 *            the RSSItem item
	 */
	public void addItem(RSSItem itm) {
		rss_items.add(itm);
	}

	/**
	 * Get a RSSImage from the channel
	 * 
	 * @return RSSImage if exists (optional)
	 */
	public RSSImage getRSSImage() {
		return this.img;
	}

	/**
	 * Get a linkedList wich contains the Channel's RSSItem
	 * 
	 * @return the RSSItems's list
	 */
	public ArrayList<RSSItem> getItems() {
		return this.rss_items;
	}

	/**
	 * 
	 * @param index
	 * @return
	 */
	public RSSItem getItem(int index) {
		if (index >= this.rss_items.size())
			return null;
		return this.rss_items.get(index);
	}

	/**
	 * Get the syndication module object from the RSS object
	 * 
	 * @return The object or null
	 */
	public RSSSyndicationModule getRSSSyndicationModule() {
		return sy;
	}

	/**
	 * Useful for debug
	 * 
	 * @return An info string about channel
	 */
	public String toString() {
		String info = "ABOUT ATTRIBUTE: " + about + "\n" + "TITLE: " + title
				+ "\n" + "LINK: " + link + "\n" + "DESCRIPTION: " + description
				+ "\nLANGUAGE: " + lang;
		return info;
	}

	public void setNextLink(String nextLink) {
		this.nextLink = nextLink;
	}

	public String getNextLink() {
		return nextLink;
	}
}