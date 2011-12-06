//	$Id: MaxObjectProxy.java,v 67fbb962f6b8 2011/10/10 21:38:19 nick $
//	$Source: /Users/nick/workspace/MaxMSP/DEVELOPMENT_0/mxj-development/scripting/java/net/loadbang/scripting/MaxObjectProxy.java,v $

package net.loadbang.scripting;

import java.util.List;

import net.loadbang.scripting.util.exn.DataException;

import com.cycling74.max.Atom;

/**	An interface representing a proxy for the enclosing MaxObject, so that we can unit test
	the Groovy object against it, and so that it fits into the Groovy world more neatly.
 	
 	@author Nick Rothwell, nick@cassiel.com / nick@loadbang.net
 */

public interface MaxObjectProxy {
	/**	A new outlet() crafted for Groovy, Python etc. We convert to Atom[]. */
	boolean outlet(int i, Object arg);
	
	/**	A new high-priority outputter. */
	boolean outletHigh(int i, Object arg);
	
	/**	Output console string. */
	void post(String message);

	/**	Output console serror tring. */
	void error(String message);

	/**	Set argument vector for Groovy world. (Best not called from the Groovy world: hence
	 	the unconventional name.) 
	 
	 	@throws DataException
	 */

	void setupArgs(Atom[] args) throws DataException;
	
	/**	Get arguments in the Groovy world. */
	List<Object> getArgs();
}
