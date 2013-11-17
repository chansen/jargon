package jargon.parse.reflect;

import jargon.parse.CodePointSet;

public class StaticCodePoint extends Pattern {
	final CodePointSet cps;

	public StaticCodePoint(String name, CodePointSet cps) {
		super(name);
		this.cps = cps;
	}

}
