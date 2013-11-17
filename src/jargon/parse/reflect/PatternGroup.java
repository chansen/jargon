package jargon.parse.reflect;


public abstract class PatternGroup extends Pattern {
	final PatternRef[] children;

	public PatternGroup(String name, PatternRef[] children) {
		super(name);
		this.children = children;
	}
	
}
