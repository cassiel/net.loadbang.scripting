package net.loadbang.scripting.util;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.cycling74.max.Atom;

public class ConvertersTest {
	@Test
	public void canConvertListToAtoms() {
		Converters converters = new Converters();
		
		List<Object> list = new ArrayList<Object>();
		
		list.add(1);
		list.add(0.0);

		assertEquals(new Atom[] { Atom.newAtom(1), Atom.newAtom(0.0) },
					 converters.objectToAtoms(list)
					);
	}

	@Test
	public void canConvertBigDecimalToAtom() {
		Converters converters = new Converters();
		
		assertEquals(new Atom[] { Atom.newAtom(0.0) },
					 converters.objectToAtoms(new BigDecimal(0.0))
					);
	}
}
