package jargon.parse;

import jargon.parse.reflect.Pattern;
import jargon.parse.reflect.Pattern.Builder;


public class PatternTest {
	public static void main(String[] args) {
		Builder pb = new Builder();
		pb.choice("UnicodeInputCharacter");
		pb.sequence("UnicodeEscape");
		pb.choice("UnicodeMarker");
		pb.codepoints("RawInputCharacter", CodePointSet.ALL);
		pb.codepoints("HexDigit", CodePointSet.charRanges('0', '9', 'a', 'f', 'A', 'F'));
		pb.choice("LineTerminator");
		pb.codepoints("InputCharacter", CodePointSet.not(CodePointSet.chars('\r', '\n')));
	}
}
