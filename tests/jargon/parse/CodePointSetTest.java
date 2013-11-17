package jargon.parse;

import static jargon.parse.CodePointSet.all;
import static jargon.parse.CodePointSet.even;
import static jargon.parse.CodePointSet.odd;
import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class CodePointSetTest {
	@Test
	public void testEven() {
		CodePointSet cps;
		cps = even(6, 10);
		assertEquals(false, cps.contains(4));
		assertEquals(false, cps.contains(5));
		assertEquals(true, cps.contains(6));
		assertEquals(false, cps.contains(7));
		assertEquals(true, cps.contains(8));
		assertEquals(false, cps.contains(9));
		assertEquals(false, cps.contains(10));
		assertEquals(false, cps.contains(11));
		assertEquals(false, cps.contains(12));
		cps = even(5, 9);
		assertEquals(false, cps.contains(4));
		assertEquals(false, cps.contains(5));
		assertEquals(true, cps.contains(6));
		assertEquals(false, cps.contains(7));
		assertEquals(true, cps.contains(8));
		assertEquals(false, cps.contains(9));
		assertEquals(false, cps.contains(10));
		assertEquals(false, cps.contains(11));
		assertEquals(false, cps.contains(12));
	}

	@Test
	public void testOdd() {
		CodePointSet cps;
		cps = odd(7, 11);
		assertEquals(false, cps.contains(5));
		assertEquals(false, cps.contains(6));
		assertEquals(true, cps.contains(7));
		assertEquals(false, cps.contains(8));
		assertEquals(true, cps.contains(9));
		assertEquals(false, cps.contains(10));
		assertEquals(false, cps.contains(11));
		assertEquals(false, cps.contains(12));
		assertEquals(false, cps.contains(13));
		cps = odd(6, 10);
		assertEquals(false, cps.contains(5));
		assertEquals(false, cps.contains(6));
		assertEquals(true, cps.contains(7));
		assertEquals(false, cps.contains(8));
		assertEquals(true, cps.contains(9));
		assertEquals(false, cps.contains(10));
		assertEquals(false, cps.contains(11));
		assertEquals(false, cps.contains(12));
		assertEquals(false, cps.contains(13));
	}
	
	@Test
	public void testAll() {
		CodePointSet cps;
		cps = all(7, 11);
		assertEquals(false, cps.contains(5));
		assertEquals(false, cps.contains(6));
		assertEquals(true, cps.contains(7));
		assertEquals(true, cps.contains(8));
		assertEquals(true, cps.contains(9));
		assertEquals(true, cps.contains(10));
		assertEquals(false, cps.contains(11));
		assertEquals(false, cps.contains(12));
		assertEquals(false, cps.contains(13));
		cps = all(6, 10);
		assertEquals(false, cps.contains(4));
		assertEquals(false, cps.contains(5));
		assertEquals(true, cps.contains(6));
		assertEquals(true, cps.contains(7));
		assertEquals(true, cps.contains(8));
		assertEquals(true, cps.contains(9));
		assertEquals(false, cps.contains(10));
		assertEquals(false, cps.contains(11));
		assertEquals(false, cps.contains(12));
		cps = all(7, 12);
		assertEquals(false, cps.contains(5));
		assertEquals(false, cps.contains(6));
		assertEquals(true, cps.contains(7));
		assertEquals(true, cps.contains(8));
		assertEquals(true, cps.contains(9));
		assertEquals(true, cps.contains(10));
		assertEquals(true, cps.contains(11));
		assertEquals(false, cps.contains(12));
		assertEquals(false, cps.contains(13));
		cps = all(6, 11);
		assertEquals(false, cps.contains(4));
		assertEquals(false, cps.contains(5));
		assertEquals(true, cps.contains(6));
		assertEquals(true, cps.contains(7));
		assertEquals(true, cps.contains(8));
		assertEquals(true, cps.contains(9));
		assertEquals(true, cps.contains(10));
		assertEquals(false, cps.contains(11));
		assertEquals(false, cps.contains(12));
	}
	
	@Test
	public void testUnion() {
		CodePointSet cps;
		cps = odd(5, 15).union(all(15, 20));
		assertEquals(false, cps.contains(4));
		assertEquals(true, cps.contains(5));
		assertEquals(false, cps.contains(6));
		assertEquals(true, cps.contains(7));
		assertEquals(false, cps.contains(12));
		assertEquals(true, cps.contains(13));
		assertEquals(false, cps.contains(14));
		assertEquals(true, cps.contains(15));
		assertEquals(true, cps.contains(16));
		assertEquals(true, cps.contains(18));
		assertEquals(true, cps.contains(19));
		assertEquals(false, cps.contains(20));
		assertEquals(
				all(5, 10), 
				all(7, 10).union(all(5, 8))
				);
	}
	
	@Test
	public void testMinus() {
		CodePointSet cps;
		cps = all(5, 15).minus(odd(9, 13));
		assertEquals(false, cps.contains(4));
		assertEquals(true, cps.contains(5));
		assertEquals(true, cps.contains(6));
		assertEquals(false, cps.contains(9));
		assertEquals(true, cps.contains(10));
		assertEquals(false, cps.contains(11));
		assertEquals(true, cps.contains(12));
		assertEquals(true, cps.contains(13));
		assertEquals(true, cps.contains(14));
		assertEquals(false, cps.contains(15));
		assertEquals(false, cps.contains(16));
		assertEquals(
				all(7, 9).union(all(12, 17)), 
				all(7, 17).minus(all(9, 12))
				);
	}
	
	@Test
	public void testToString() {
		assertEquals("{00-09}", all(0x0, 0xA).toString());
		assertEquals("{05-}", all(0x5, CodePointSet.UNICODE_END).toString());
		assertEquals("{05-09}", all(0x5, 0xA).toString());
		assertEquals("{0106-0109}", all(0x106, 0x10A).toString());
		assertEquals("{101106-10F00A}", all(0x101106, 0x10F00B).toString());
		assertEquals("{06~0E}", even(0x6, 0x10).toString());
		assertEquals("{05~0D}", odd(0x5, 0xF).toString());
		assertEquals("{05~0D,19~21}", odd(0x5, 0xF).union(odd(0x19, 0x23)).toString());
		assertEquals("{05~0F-13}", odd(0x5, 0xF).union(all(0xF, 0x14)).toString());
		assertEquals("{05~0D,10-13}", odd(0x5, 0xF).union(all(0x10, 0x14)).toString());
		assertEquals("{05-09~11}", all(0x5, 0xA).union(odd(0x5, 0x13)).toString());
		assertEquals("{05~09-11~17}", odd(0x5, 0x19).union(even(0xA, 0x12)).toString());
		assertEquals("{05~09-0A~10-11~17}", odd(0x5, 0xB).union(even(0xA, 0x12)).union(odd(0x11,0x19)).toString());
	}
	
}