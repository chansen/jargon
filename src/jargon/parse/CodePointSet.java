package jargon.parse;

/**
 * Represents a set of unicode code points
 * (optimized for dealing with "combed" ranges (every-other) which is common in upper unicode)
 * @author Warren Falk
 * 
 */
public final class CodePointSet {
	final int[] odd; // right shifted points
	final int[] even; // right shifted points
	
	public final static int UNICODE_END = 0x110000;
	static final int[] empty = new int[0];
	final static int MAXSHIFTED = UNICODE_END >> 1;
	
	public final static CodePointSet NONE = new CodePointSet(empty, empty);
	public final static CodePointSet ALL = all(0, 0x110000);
	/**
	 * The arrays, odd and even, should contain pairs of points where the first is the start (inclusive) and second is the end (exclusive) of the points in the set.
	 * The points are assumed to be in order (out of order is unpredictable)
	 * The points are assumed to be right-shifted
	 * @param odd
	 * @param even
	 */
	CodePointSet(int[] odd, int[] even) {
		this.odd = odd;
		this.even = even;
	}
	
	/**
	 * Returns true if this set contains the given code point
	 * @param codepoint the codepoint to test
	 * @return true if the code point is in the set
	 */
	public boolean contains(int codepoint) {
		int parity = codepoint & 1;
		codepoint >>= 1;
		int[] points = (parity == 0) ? even : odd;
		int i, len = points.length;
		for (i = 0; i < len; i++) {
			if (codepoint < points[i])
				return (i & 1) == 1;
		}
		return (i & 1) == 1;
	}
	
	/**
	 * Returns the union of this set and another
	 * @param other the other set
	 * @return a code point set containing every code point in either this set or the other
	 */
	public CodePointSet union(CodePointSet other) {
		int[] o = op(OP_UNION, odd, other.odd, null);
		int[] e = (even == odd && other.even == other.odd) ? o : op(OP_UNION, even, other.even, null);
		return new CodePointSet(o, e);
	}
	
	public static CodePointSet unionOf(CodePointSet cps0, CodePointSet cps1) {
		return cps0.union(cps1);
	}
	
	/**
	 * Returns all points in this set that are not in "other"
	 * @param other the other set
	 * @return a code point set containing all points in this set except those also in "other"
	 */
	public CodePointSet minus(CodePointSet other) {
		int[] o = op(OP_MINUS, odd, other.odd, null);
		int[] e = (even == odd && other.even == other.odd) ? o : op(OP_MINUS, even, other.even, null);
		return new CodePointSet(o, e);
	}
	
	/**
	 * Returns all points in this set that are also in "other"
	 * @param other the other set
	 * @return a code point set containing all points in this set which are also in "other"
	 */
	public CodePointSet intersect(CodePointSet other) {
		int[] o = op(OP_INTERSECT, odd, other.odd, null);
		int[] e = (even == odd && other.even == other.odd) ? o : op(OP_INTERSECT, even, other.even, null);
		return new CodePointSet(o, e);
	}
	
	public static CodePointSet intersectionOf(CodePointSet cps0, CodePointSet cps1) {
		return cps0.intersect(cps1);
	}
	
	public static CodePointSet not(CodePointSet cps) {
		return CodePointSet.ALL.minus(cps);
	}
	
	static final int OP_UNION = 0;
	static final int OP_MINUS = 1;
	static final int OP_INTERSECT = 2;
	
	// call this with the out parameter allocated to just the right number of elements
	// if you don't know, then make out null and the function will run twice, once to count+allocate, next to fill
	static int[] op(int operation, int[] left, int[] right, int[] out) {
		if (left.length == 0)
			return right;
		if (right.length == 0)
			return left;
		int leftCursor = 1, rightCursor = 1;
		int leftValue = left[0], rightValue = right[0];
		// first, count
		int outValue = 0;
		boolean useRight, useLeftl;
		boolean leftOn = false, rightOn = false;
		boolean outOn = false;
		boolean leftContinue = true, rightContinue = true;
		int outCursor = 0;
		boolean isCalcSizePhase = out == null;
		while (leftContinue || rightContinue) {
			useLeftl = leftValue <= rightValue;
			useRight = rightValue <= leftValue;
			if (useLeftl) {
				leftOn = 1 == (leftCursor & 1);
				outValue = leftValue;
				if (leftCursor == left.length){
					leftValue = MAXSHIFTED;
					leftContinue = false;
				}
				else {
					leftValue = left[leftCursor++];
				}
			}
			if (useRight) {
				rightOn = 1 == (rightCursor & 1);
				outValue = rightValue;
				if (rightCursor == right.length) {
					rightValue = MAXSHIFTED;
					rightContinue = false;
				}
				else {
					rightValue = right[rightCursor++];
				}
			}
			boolean newb;
			switch (operation) {
			case OP_UNION:
				newb = leftOn || rightOn;
				break;
			case OP_MINUS:
				newb = leftOn && !rightOn;
				break;
			case OP_INTERSECT:
				newb = leftOn && rightOn;
				break;
			default:
				newb = leftOn;
				break;
			}
			if (newb != outOn) {
				outOn = newb;
				if (isCalcSizePhase)
					outCursor++;
				else
					out[outCursor++] = outValue;
			}
		}
		if (isCalcSizePhase)
			return op(operation, left, right, new int[outCursor]);
		else
			return out;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CodePointSet)
			return equals((CodePointSet)obj);
		return false;
	}
	
	/**
	 * Compare this CodePointSet to another for equality.
	 * This should return true if both CodePointSets contain the same code points and neither contains any the other does not.
	 * @param other The other code point set
	 * @return true if the two sets are equivalent.
	 */
	public boolean equals(CodePointSet other) {
		return other.odd.length == odd.length
			&& other.even.length == even.length
			&& _eqcontents(odd, other.odd)
			&& _eqcontents(even, other.even);
	}
	
	private static boolean _eqcontents(int[] left, int[] right) {
		if (left == right)
			return true;
		for (int i = 0; i < left.length; i++) {
			if (left[i] != right[i])
				return false;
		}
		return true;
	}
	
	
	/* This could probably be optimized, but I coded toString() for simplicity (believe it or not).
	 * Turns out a simple implementation of toString() is a challenge with this implementation of CodePointSet.
	 * toString() in general, is probably better off trading performance in for simplicity.
	 */
	@Override
	public String toString() {
		int length = even.length + odd.length;
		if (length == 0)
			return "{}";
		int evenCursor = 0;
		int oddCursor = 0;
		int value = 0;
		int evenStatus = 0, oddStatus = 0; // statuses of the two sides (i.e. on or off)
		int phase = 0; // (i.e. even/odd/all)
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		char dash = ' ';
		for (int cursor = 0; cursor < length;) {
			int evenValue = ((evenCursor < even.length) ? even[evenCursor] : MAXSHIFTED ) << 1;
			int oddValue = (((oddCursor < odd.length) ? odd[oddCursor] : MAXSHIFTED) << 1) | 1;
			if (evenValue < oddValue) {
				value = evenValue;
				evenCursor++;
				evenStatus = (evenCursor & 1) << 1; // left shift even status for later OR-ing
				cursor++;
				// when an odd range limit is adjacent to an even one, they should be merged
				if (oddValue == evenValue + 1 && (oddCursor & 1) != (evenCursor & 1)) {
					oddCursor++;
					oddStatus = (oddCursor & 1);
					cursor++;
				}
			}
			else {
				assert(oddValue < evenValue); // note this should be true - they certainly should never be equal
				value = oddValue;
				oddCursor++;
				oddStatus = (oddCursor & 1);
				cursor++;
				// when an even range limit is adjacent to an odd one, they should be merged
				if (evenValue == oddValue + 1 && (evenCursor & 1) != (oddCursor & 1)) {
					evenCursor++;
					evenStatus = (evenCursor & 1) << 1; // left shift even status for later OR-ing
					cursor++;
				}
			}
			// calculate the output phase which is the OR-ing of even and odd phases
			int oldphase = phase;
			phase = evenStatus | oddStatus;
			// append the dash (or comma) from the previous round if any
			if (dash != ' ')
				sb.append(dash);
			// range closing values should be in the phase of the range being closed
			int closevalue = value;
			if (oldphase != 0) {
				while (!_inPhase(oldphase, closevalue)) {
					closevalue--;
				}
			}
			// if you output a number in the toString result, it signifies that the number is actually in the set,
			// therefore we ensure here that the value we're outputting is in the current phase
			while (!_inPhase(phase, value)) {
				value--;
				if (_inPhase(oldphase, value))
					break;
			}
			if (closevalue < value)
				value = closevalue;
			if (value < (UNICODE_END - 1))
				appendcode(sb, value);
			// the phase governs the dash
			dash = phase == 0 ? ',' : phase == 3 ? '-' : '~';
		}
		sb.append('}');
		return sb.toString();
	}
	
	private static void appendcode(StringBuilder sb, int value) {
		int b;
		b = value & 0xFF0000;
		if (b != 0) { 
			appendcode(sb, b >> 16);
		}
		b = value & 0xFF00;
		if (b != 0) {
			appendcode(sb, b >> 8);
		}
		b = (value & 0xF0) >> 4;
		sb.append((char)((b < 0xA) ? '0' + b : 'A' + (b - 0xA)));
		b = value & 0xF;
		sb.append((char)((b < 0xA) ? '0' + b : 'A' + (b - 0xA)));
	}

	private static boolean _inPhase(int phase, int value) {
		if (phase == 0)
			return false;
		if (phase == 3)
			return true;
		return (phase & 1) == (value & 1);
	}
	
	/**
	 * Create a CodePointSet with one range including every point between start (inclusive) and end (exclusive)
	 * @param start beginning of range (inclusive)
	 * @param end end of range (exclusive)
	 * @return the new CodePointSet containing the single range
	 */
	public static CodePointSet all(int start, int end) {
		int[] odd, even;
		odd = new int[] {start >> 1, end >> 1};
		// the below code allows us to use the same array for even and odd if there would be no difference
		if (0 == (start & 1) && 0 == (end & 1))
			even = odd;
		else
			even = new int[] {(start + 1) >> 1, (end + 1) >> 1};
		return new CodePointSet(odd, even);
	}
	
	/**
	 * Create a CodePointSet with one range including odd points between start (inclusive) and end (exclusive)
	 * @param start beginning of range (inclusive)
	 * @param end end of range (exclusive)
	 * @return the new CodePointSet containing the single range
	 */
	public static CodePointSet odd(int start, int end) {
		return new CodePointSet(range(start >> 1, end >> 1), empty);
	}
	
	/**
	 * Create a CodePointSet with one range including even points between start (inclusive) and end (exclusive)
	 * @param start beginning of range (inclusive)
	 * @param end end of range (exclusive)
	 * @return the new CodePointSet containing the single range
	 */
	public static CodePointSet even(int start, int end) {
		return new CodePointSet(empty, range((start + 1) >> 1, (end + 1) >> 1));
	}
	
	// Note: start and end points are assumed to already be right-shifted
	// if they are equal, an empty array is returned
	static int[] range(int start, int end) {
		if (end > start)
			return new int[] {start, end};
		if (start == end)
			return empty;
		else
			throw new IllegalArgumentException("effective end (" + end + ") must be greater than or equal to effective start (" + start + ")");
	}

	/**
	 * Shortcut function to return a code point set from a list of character ranges
	 * @param endpoint each odd char represents a start char (inclusive) and each even char represents an even char (inclusive)
	 * @return
	 */
	public static CodePointSet charRanges(char... endpoint) {
		CodePointSet cps = CodePointSet.NONE;
		for (int i = 0; i < endpoint.length;) {
			int s = endpoint[i++];
			int e;
			if (i < endpoint.length)
				e = endpoint[i++] + 1;
			else
				e = CodePointSet.UNICODE_END;
			CodePointSet seg = all(s, e);
			cps = cps.union(seg);
		}
		return cps;
	}
	
	public static CodePointSet chars(char... chars) {
		CodePointSet cps = CodePointSet.NONE;
		for (int i = 0; i < chars.length; i++) {
			cps = cps.union(all(chars[i], chars[i] + 1));
		}
		return cps;
	}
}
