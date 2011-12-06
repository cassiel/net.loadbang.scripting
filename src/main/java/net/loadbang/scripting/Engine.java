//	$Id: Engine.java,v 4320f78ad535 2011/05/08 20:13:27 nick $
//	$Source: /Users/nick/workspace/MaxMSP/DEVELOPMENT_0/mxj-development/scripting/java/net/loadbang/scripting/Engine.java,v $

package net.loadbang.scripting;

import java.io.IOException;

import com.cycling74.max.Atom;

/**	The interface for a scripting engine which is attached to a proxy object which
	can do output and report errors.

	@author Nick Rothwell, nick@cassiel.com / nick@loadbang.net
 */

public interface Engine {
	/**	Clear the environment, unwind callbacks, set up initial bindings. */
	public void clear();

	/**	Evaluate an expression: output the result to the leftmost outlet.
		@param expression the expression to evaluate
	 */

	public void eval(String expression);
	
	/**	Execute a statement.
	 	@param statement the statement to execute.
	 */

	public void exec(String statement);

	/**	Get and output a value from the binding environment, or
		report an error if not bound.

		@param id the variable name
	 */

	public void getVariable(String id);
	
	/**	Invoke a named function with arguments. 

		@param fn the function to invoke
		@param inlet00 the inlet number, or null for single-inlet wrappers
	 	@param args the arguments to the function
	 */

	public void invoke(String fn, Integer inlet00, Atom[] args);

	/**	Set a variable in the binding environment.
		@param args the variable name and list of values
	 */

	public void setVariable(Atom[] args);

	/**	Run a script, establishing an interpreter rooted at the script file's
	 	location.

		@param directory the directory containing the script
		@param filename the name of the script file
	 */

	abstract public void runScript(String directory, String filename);

	/**	Establish an interpreter rooted on a directory (identified by a
	 	place-holder file), so that it can load "run" scripts in that directory.
	 	
	 	@param directory the root directory for the interpreter
		@throws IOException
	 */

	public void setupEngineOnPlaceHolder(String directory) throws IOException;
	
	/**	Run a script using an interpreter rooted by a place-holder file
		in a target directory
		
	 	@param name the name of the script file to run (the full name,
	 	including the extension)
	 */

	public void runUsingPlaceHolder(String name);

	/**	Add a clean-up function to call when the environment is cleared, or
		the enclosing MXJ instance is deleted

	 	@param obj the function to call (in a language-specific form)
	 */

	public void addCleanup(Object obj);

	/**	Unwind all callbacks: call the functions in the reverse order to
	 	that in which they were declared.
	 */

	public void unwindCallbacks();
}
