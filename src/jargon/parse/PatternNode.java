package jargon.parse;


public abstract class PatternNode {
	public static final class BranchNode extends PatternNode {
		PatternNode nextInSeries;
		PatternNode nextAlternative;
	}
	
	public static final class LeafNode extends PatternNode {
		final int codePoint;
		
		public LeafNode(int codePoint) {
			this.codePoint = codePoint;
		}
	}
	
	public static void main(String[] args) {
	}
	
	public static PatternNode stringLiteral(String string) {
		LeafNode[] leaves = new int[string.length()];
		for (int i = 0; i < string.length(); i++) {
			leaves[i] = new LeafNode(string.codePointAt(i));
		}
		return series(0, leaves);
	}
	
	public static LeafNode codePoint(int codePoint) {
		return new LeafNode(codePoint);
	}
	
	final static PatternNode series(int offset, PatternNode[] nodes) {
		
	}
}
