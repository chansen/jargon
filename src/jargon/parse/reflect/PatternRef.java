package jargon.parse.reflect;

public class PatternRef extends Pattern {
	final Pattern pattern;
	final int min;
	final int max;

	public PatternRef(final Pattern pattern, final int min, final int max) {
		super(null);
		this.pattern = pattern;
		this.min = min;
		this.max = max;
	}

}
