//	$Id: BogusMaxObjectProxy.java,v 67fbb962f6b8 2011/10/10 21:38:19 nick $
//	$Source: /Users/nick/workspace/MaxMSP/DEVELOPMENT_0/mxj-development/scripting/java/net/loadbang/scripting/BogusMaxObjectProxy.java,v $

package net.loadbang.scripting;

import java.util.List;

import net.loadbang.scripting.util.exn.DataException;

import com.cycling74.max.Atom;
import com.cycling74.max.MaxObject;

/**	An empty implementation of {@link MaxObjectProxy} which we can plug to make sure
 	bits of our embedded languages don't try to do things like call out into MaxObjects
 	which have been deleted.
 
	@author Nick Rothwell, nick@cassiel.com / nick@loadbang.net

 */
public class BogusMaxObjectProxy implements MaxObjectProxy {

	public boolean outlet(int i, Object arg) {
		error("cannot outlet: MaxObject not available");
		return false;
	}

	public boolean outletHigh(int i, Object arg) {
		error("cannot outletHigh: MaxObject not available");
		return false;
	}

	public void post(String message) {
		MaxObject.post(message);
	}

	public void error(String message) {
		MaxObject.error(message);
	}

	public void setupArgs(Atom[] args) throws DataException {
	}

	public List<Object> getArgs() {
		return null;
	}
}
