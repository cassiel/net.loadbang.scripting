//	$Id: ScriptEngineBase.java,v 67fbb962f6b8 2011/10/10 21:38:19 nick $
//	$Source: /Users/nick/workspace/MaxMSP/DEVELOPMENT_0/mxj-development/scripting/java/net/loadbang/scripting/ScriptEngineBase.java,v $

package net.loadbang.scripting;

import java.io.File;

import net.loadbang.scripting.util.Manifest;
import net.loadbang.scripting.util.exn.DataException;
import net.loadbang.util.EnrichedMaxObject;
import net.loadbang.util.FileUtils;

import com.cycling74.max.Atom;

/**	A base class for MXJ instance classes which support scripting languages.

	@author Nick Rothwell, nick@cassiel.com / nick@loadbang.net
 */

abstract public class ScriptEngineBase extends EnrichedMaxObject {
	/**	A proxy for this MaxObject. */
	private MaxObjectProxy itsProxy;
	
	/**	The script engine. */
	private Engine itsEngine;
	
	private String itsPlaceHolderStem00 = null;
	private String itsScript00 = null;
	
	/**	Pass the inlet number to each invocation? */
	private boolean itsPassingInletNumber;
	
	/**	Arguments passed as an attribute. */
	private Atom[] itsArgs = new Atom[] { };
	
	/**	The extension used for script files (.groovy, .py etc.) */
	private String itsScriptFileExtension;
	
	/**	Constructor.

		@param id an RCS tag
	 	@param propertyPackagePrefix a package prefix (used for property files etc.)
	 	@param loggerOwnerClass the owning class for log4j
	 	@param args the arguments to the MXJ (number of inlets and outlets)
	 	@param scriptFileExtension the extension used for script files
	 */

	protected ScriptEngineBase(String id,
							   String propertyPackagePrefix,
							   Class<?> loggerOwnerClass,
							   Atom[] args,
							   String scriptFileExtension,
							   String... otherAttributes
							  ) {
		super(id, propertyPackagePrefix, loggerOwnerClass);
		itsScriptFileExtension = scriptFileExtension;

		int numIns = 1, numOuts = 1;
		
		itsProxy = new MaxObjectProxyImpl(this);
		itsEngine = buildEngine(itsProxy);
		
		//	No args means 1-in, 1-out; otherwise, we expect two integer args
		//	for number of ins and outs.

		if (args.length == 0) {
			numIns = 1;
			numOuts = 1;
		} else if (args.length == 2 && args[0].isInt() && args[1].isInt()) {
			numIns = args[0].getInt();
			numOuts = args[1].getInt();
		} else {
			bail("bad arguments to " + loggerOwnerClass.getName());
		}
		
		declareIO(numIns, numOuts);
		itsPassingInletNumber = (numIns > 1);
		
		//	Argument attribute: the file stem to use as place-holder.
		attribute("PlaceHolder");
		attribute("Script");
		attribute("Args");
		
		for (String attr: otherAttributes) {
			attribute(attr);
		}
	}
	
	protected void attribute(String mixedCaseName) {
		declareAttribute(mixedCaseName.toLowerCase(), "get" + mixedCaseName, "set" + mixedCaseName);
	}
	
	
	/**	Build an engine on this proxy.

	 	@param proxy the proxy
	 	@return an engine for the appropriate scripting language
	 */

	protected abstract Engine buildEngine(MaxObjectProxy proxy);
	
	/**	Generic method for running a script (given the file stem as attribute).
	 	We pass the filename, and the enclosing directory, into the abstract
	 	method for running the script, once we've located the file in
	 	Max's search path. The engine will root an interpreter
	 	on the directory for this file. */
	
	private void establishScript(String stem) {
		String filename = stem + itsScriptFileExtension;
		File f00 = FileUtils.locateFile00(filename);
		
		if (f00 == null) {
			error("cannot find script " + filename);
		} else {
			String directory00 = f00.getParent();
	
			if (directory00 == null) {
				error("cannot find directory for " + f00);
			} else {
				itsEngine.runScript(directory00, filename);
			}
		}
	}

	/**	Set up the link to the place-holder file (so that we know where to find scripts);
		we (might) also replace the actual scripting engine.
		
	 	@param stem the stem of the place-holder file name (which must be in Max's
	 	search path)
	 */
	
	void setPlaceHolder(String stem) {
		String filename = stem + ".PLACE_HOLDER";
		File f00 = FileUtils.locateFile00(filename);
		
		if (f00 == null) {
			error("cannot find place-holder " + filename);
		} else {
			itsPlaceHolderStem00 = stem;
			//	Replace engine with a new root.
			String directory00 = f00.getParent();
			
			if (directory00 == null) {
				error("cannot find directory for " + f00);
			} else {
				post("establishing script engine on " + directory00);
				
				try {
					itsEngine.setupEngineOnPlaceHolder(directory00);
					clear();
				} catch (java.io.IOException exn) {
					error("I/O error: " + exn);
				}
			}
		}
	}

	/**	Return the place-holder filename stem, or "none" if none has been set. */
	
	String getPlaceHolder() {
		if (itsPlaceHolderStem00 == null) {
			error("no place-holder declared");
			return Manifest.Strings.NONE;
		} else {
			return itsPlaceHolderStem00;
		}
	}
	
	
	/**	Establish a script

	 	@param stem the script file name (sans extension)
	 */

	void setScript(String stem) {
		itsScript00 = stem;
		establishScript(stem);
	}
	
	/**	Get the script name. */
	
	String getScript() {
		if (itsScript00 == null) {
			error("no script declared");
			return Manifest.Strings.NONE;
		} else {
			return itsScript00;
		}
	}

	/**	Set the arguments for the script (via an attribute).

		@param args the arguments
	 */

	void setArgs(Atom[] args) {
		try {
			itsProxy.setupArgs(args);
			itsArgs = args;
		} catch (DataException e) {
			e.printStackTrace();
		}
	}
	
	/**	Return the arguments attribute. */

	Atom[] getArgs() {
		return itsArgs;
	}
	
	/**	Clear the binding environment (and unwind callbacks). */

	public void clear() {
		itsEngine.clear();
	}
	
	/**	Set a value in the binding environment. */

	public void setvar(Atom[] args) {
		itsEngine.setVariable(args);
	}
	
	/**	Get and output a value from the binding environment.
	
		@param id the name of the variable
	 */

	public void getvar(String id) {
		itsEngine.getVariable(id);
	}
	
	/**	Execute a line of script in the current engine's environment. */
	
	public void exec(Atom[] expression) {
		itsEngine.exec(Atom.toOneString(expression));
	}
	
	/**	Execute a line of script in the current engine's environment, sending result to outlet 0. */
	
	public void eval(Atom[] statement) {
		itsEngine.eval(Atom.toOneString(statement));
	}
	
	/**	Run a script; the engine will reload it as required.

	 	@param name the body (without path or extension) of the script file
	 	to run; a name "foo" will run "foo.groovy" or "foo.py" or whatever
	 */
	
	public void run(String name) {
		itsEngine.runUsingPlaceHolder(name + itsScriptFileExtension);
	}
	
	/**	Attempt to invoke a function in the current binding. If we have more than one
	 	inlet, we also pass in the inlet number. */
	
	@Override
	protected void anything(String tok, Atom[] args) {
		itsEngine.invoke(sanitise(tok), (itsPassingInletNumber ? getInlet() : null), args);
	}
	
	/**	We have to sanitise some of Max's automatic tags (like "int" and "float") since they
		clash with Java tokens or Python built-ins.
		
	 	@param tok the Max token
	 	@return the sanitised token
	 */
	
	private String sanitise(String tok) {
		if (tok.equals("int") || tok.equals("float") || tok.equals("list")) {
			return "_" + tok;
		} else{
			return tok;
		}
	}
	
	/**	Deletion: we must tell the engine to unwind all callbacks.
	 	Once done (I'm assuming we can outlet on notifyDeleted())
	 	we plug the proxy so that no background Java code can do
	 	output. */
	
	@Override
	public void notifyDeleted() {
		itsEngine.unwindCallbacks();
		itsProxy = new BogusMaxObjectProxy();
	}

	/**	Attempt to invoke a function called "bang".

		@see com.cycling74.max.MaxObject#bang()
	 */

	@Override
	protected void bang() {
		anything(Manifest.Strings.BANG, new Atom[] { });
	}
}
