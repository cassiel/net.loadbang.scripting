//	$Id: EngineImpl.java,v 4320f78ad535 2011/05/08 20:13:27 nick $
//	$Source: /Users/nick/workspace/MaxMSP/DEVELOPMENT_0/mxj-development/scripting/java/net/loadbang/scripting/EngineImpl.java,v $

package net.loadbang.scripting;

import java.io.IOException;

import net.loadbang.scripting.util.Converters;
import net.loadbang.scripting.util.exn.DataException;

import com.cycling74.max.Atom;

/**	A base implementation for a language-specific scripting engine.

	@author Nick Rothwell, nick@cassiel.com / nick@loadbang.net
 */

public abstract class EngineImpl implements Engine {
	/**	A proxy for the MaxObject housing this engine. */
	private MaxObjectProxy itsProxy;

	
	/**	Constructor: build an engine on a proxy.
	 	@param proxy the proxy
	 */

	protected EngineImpl(MaxObjectProxy proxy) {
		itsProxy = proxy;
	}
	
	protected MaxObjectProxy getProxy() {
		return itsProxy;
	}
	
	abstract protected Converters getConverters();
	
	/**	Set a value in the binding environment. */

	public void setVariable(Atom[] args) {
		if (args.length > 1 && args[0].isString()) {
			try {
				setVar(args[0].getString(), getConverters().atomsToObject(args, 1));
			} catch (DataException e) {
				getProxy().error(e.getMessage());
			}
		} else {
			getProxy().error("setvar: expecting <name> <args>");
		}
	}
	
	/**	Get and output a value from the binding environment. */

	public void getVariable(String id) {
		Object obj00 = getVar00(id);
		
		if (obj00 == null) {
			getProxy().error("no such variable: " + id);
		} else {
			getProxy().outlet(0, obj00);
		}
	}
	
	/**	Set up the engine to work on this place-holder directory
	 	(resetting the environment, and probably creating a new
	 	interpreter).
	 	
	 	@param directory the place-holder directory
	 	@see net.loadbang.scripting.Engine#setupEngineOnPlaceHolder(java.lang.String)
	 */

	abstract public void setupEngineOnPlaceHolder(String directory) throws IOException;

	/**	Set a value in the binding environment. Public because we have to
	 	expose it when testing Jython for reentrancy. */

	abstract public void setVar(String id, Object args);
	
	/**	Get and output a value from the binding environment. */

	abstract protected Object getVar00(String id);

	/** Execute a statement.

		@param statement the statement to execute
	 */

	abstract public void exec(String statement);
	
	/** Evaluate an expression, outputting the result to the leftmost outlet.

		@param statement the statement to execute
	 */

	abstract public void eval(String statement);
	
	/**	Invoke a named function with arguments. The first argument is the inlet number, or null
		for single-inlet wrappers. */

	abstract public void invoke(String fn, Integer inlet00, Atom[] args);

	/**	Unwind all callbacks: call the functions in the reverse order to
 		that in which they were declared.
	 */

	abstract public void unwindCallbacks();
	
	/**	Utility: prepend an Atom to a list of Atoms. */

	protected Atom[] prepend(Atom a, Atom[] args) {
		Atom[] result = new Atom[args.length + 1];
		result[0] = a;
		System.arraycopy(args, 0, result, 1, args.length);
		return result;
	}
}
