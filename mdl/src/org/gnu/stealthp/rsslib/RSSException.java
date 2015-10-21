//$Id: RSSException.java,v 1.5 2004/03/25 10:09:10 taganaka Exp $
package org.gnu.stealthp.rsslib;

/**
 * RSSlib exception handler.
 *
 * <blockquote>
 * <em>This module, both source code and documentation, is in the
 * Public Domain, and comes with <strong>NO WARRANTY</strong>.</em>
 * </blockquote>
 *
 * @since RSSLIB4J 0.1
 * @author Francesco aka 'Stealthp' stealthp[@]stealthp.org
 * @version 0.2
 */

public class RSSException extends Exception{

  public RSSException(String err){
    super(err);
  }

}