//	$Id: MaxObjectProxyImpl.java,v 4320f78ad535 2011/05/08 20:13:27 nick $
//	$Source: /Users/nick/workspace/MaxMSP/DEVELOPMENT_0/mxj-development/scripting/java/net/loadbang/scripting/MaxObjectProxyImpl.java,v $

package net.loadbang.scripting;

import java.util.ArrayList;
import java.util.List;

import net.loadbang.scripting.util.Converters;
import net.loadbang.scripting.util.exn.DataException;

import com.cycling74.max.Atom;
import com.cycling74.max.MaxObject;

/**	A subclass of EnrichedMaxObject mainly to match a proxy interface, so that we
	can also mock against it.
 	
 	@author Nick Rothwell, nick@cassiel.com / nick@loadbang.net
 */

public class MaxObjectProxyImpl implements MaxObjectProxy {
	private Converters itsConverters;
	private MaxObject itsMaxObject;
	private List<Object> itsArgs = new ArrayList<Object>();

	public MaxObjectProxyImpl(MaxObject maxObject) {
		itsMaxObject = maxObject;
		itsConverters = new Converters();
	}
	
	/**	Proxy for post(), so that we can mock it. */
	
	public void post(String message) {
		MaxObject.post(message);
	}

	/**	Proxy for error(), so that we can mock it. */
	
	public void error(String message) {
		MaxObject.error(message);
	}
	
	/**	A generalised outlet() function which attempts to unpack anything which
		Groovy creates.
	 */

	public boolean outlet(int i, Object arg) {
		Atom[] a = itsConverters.objectToAtoms(arg);
		
		return itsMaxObject.outlet(i, a);
	}
	
	/**	A high-priority outputter. */

	public boolean outletHigh(int i, Object arg) {
		Atom[] a = itsConverters.objectToAtoms(arg);
		
		return itsMaxObject.outletHigh(i, a);
	}

	/**	The arguments attribute, made accessible to the scripting language
	 	as a list of Objects. */

	public List<Object> getArgs() {
		return itsArgs;
	}

	public void setupArgs(Atom[] args) throws DataException {
		itsArgs = itsConverters.atomsToObjects(args, 0);
	}
}
