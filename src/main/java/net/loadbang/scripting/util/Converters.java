//	$Id: Converters.java,v 4320f78ad535 2011/05/08 20:13:27 nick $
//	$Source: /Users/nick/workspace/MaxMSP/DEVELOPMENT_0/mxj-development/scripting/java/net/loadbang/scripting/util/Converters.java,v $

package net.loadbang.scripting.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.loadbang.scripting.util.exn.DataException;

import com.cycling74.max.Atom;

public class Converters {
	/**	Turn an Atom into an Object for Groovy to use. */

	private Object objectify(Atom a) throws DataException {
		if (a.isInt()) {
			return a.getInt();
		} else if (a.isFloat()) {
			return a.getFloat();
		} else if (a.isString()) {
			return a.getString();
		} else {
			throw new DataException("objectify: " + a);
		}
	}
	
	/**	Turn an Object (assumed scalar) into an Atom: we can handle ints and floats properly
		(and lets do longs and doubles as well - caveat emptor). 
	 */
	
	private Atom atomify(Object obj) {
		if (obj instanceof Integer) {
			return Atom.newAtom((Integer) obj);
		} else if (obj instanceof Long) {
			return Atom.newAtom((Long) obj);
		} else if (obj instanceof Float) {
			return Atom.newAtom((Float) obj);
		} else if (obj instanceof Double) {
			return Atom.newAtom((Double) obj);
		} else if (obj instanceof BigInteger) {
			return Atom.newAtom(((BigInteger) obj).intValue());
		} else if (obj instanceof BigDecimal) {
			return Atom.newAtom(((BigDecimal) obj).floatValue());
		} else {
			return Atom.newAtom(obj.toString());
		}
	}
	
	/**	Create an object which encodes an array of Atoms into a form which
	 	Groovy can use. If there's a single entry then we just convert that,
	 	otherwise we'll need to create a Collection (or something).

		@throws DataException 
	 */
	
	public Object atomsToObject(Atom[] args, int start) throws DataException {
		if (start == args.length - 1) {		//	Single argument
			return objectify(args[start]);
		} else {
			return atomsToObjects(args, start);
		}
	}
	
	public List<Object> atomsToObjects(Atom[] args, int start) throws DataException {
		List<Object> result = new ArrayList<Object>(args.length - start);
		
		for (int i = start; i < args.length; i++) {
			result.add(objectify(args[i]));
		}
		
		return result;
	}
	
	private Atom[] fromArray(Object[] objs) {
		Atom[] a = new Atom[objs.length];
		int p = 0;
		
		for (Object x: objs) {
			a[p++] = atomify(x);
		}
		
		return a;
	}
	
	private Atom[] fromCollection(Collection<?> c) {
		Atom[] a = new Atom[c.size()];
		int p = 0;
		
		for (Object x: c) {
			a[p++] = atomify(x);
		}
		
		return a;
	}
	
	/**	Attempt to convert a Groovy value to an atom list, reverting to toString()
	 	if necessary: we always generate a list
		since MaxObject's outputter handles singletons correctly. */
	
	public Atom[] objectToAtoms(Object value00) {
		if (value00 == null) {
			return new Atom[] { Atom.newAtom(Manifest.Strings.NULL) };
		} else if (value00.getClass().isArray()) {
			return fromArray((Object[]) value00);
		} else if (value00 instanceof Collection) {
			return fromCollection((Collection<?>) value00);
		} else {
			return new Atom[] { atomify(value00) };
		}
	}
}
