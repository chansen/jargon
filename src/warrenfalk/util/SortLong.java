package warrenfalk.util;

public class SortLong {
	public interface Comparator {
		public int compare(long x0, long x1);
	}
	
	private static int med3(long x[], int a, int b, int c, Comparator comparator) {
		return (comparator.compare(x[a], x[b]) == -1 ? (comparator.compare(x[b], x[c]) == -1 ? b : comparator.compare(x[a], x[c]) == -1 ? c : a)
				: (comparator.compare(x[b], x[c]) == 1 ? b : comparator.compare(x[a], x[c]) == 1 ? c : a));
	}

	private static void swap(long x[], int a, int b) {
		long t = x[a];
		x[a] = x[b];
		x[b] = t;
	}

	private static void vecswap(long x[], int a, int b, int n) {
		for (int i = 0; i < n; i++, a++, b++)
			swap(x, a, b);
	}

	public static void mergeSort(long x[], int off, int len, Comparator comparator) {
		// Insertion sort on smallest arrays
		if (len < 7) {
			for (int i = off; i < len + off; i++)
				for (int j = i; j > off && comparator.compare(x[j - 1], x[j]) == 1; j--)
					swap(x, j, j - 1);
			return;
		}

		// Choose a partition element, v
		int m = off + (len >> 1); // Small arrays, middle element
		if (len > 7) {
			int l = off;
			int n = off + len - 1;
			if (len > 40) { // Big arrays, pseudomedian of 9
				int s = len / 8;
				l = med3(x, l, l + s, l + 2 * s, comparator);
				m = med3(x, m - s, m, m + s, comparator);
				n = med3(x, n - 2 * s, n - s, n, comparator);
			}
			m = med3(x, l, m, n, comparator); // Mid-size, med of 3
		}
		long v = x[m];

		// Establish Invariant: v* (<v)* (>v)* v*
		int a = off, b = a, c = off + len - 1, d = c;
		while (true) {
			while (b <= c && comparator.compare(x[b], v) != 1) {
				if (x[b] == v)
					swap(x, a++, b);
				b++;
			}
			while (c >= b && comparator.compare(x[c], v) != -1) {
				if (x[c] == v)
					swap(x, c, d--);
				c--;
			}
			if (b > c)
				break;
			swap(x, b++, c--);
		}

		// Swap partition elements back to middle
		int s, n = off + len;
		s = Math.min(a - off, b - a);
		vecswap(x, off, b - s, s);
		s = Math.min(d - c, n - d - 1);
		vecswap(x, b, n - s, s);

		// Recursively sort non-partition-elements
		if ((s = b - a) > 1)
			mergeSort(x, off, s, comparator);
		if ((s = d - c) > 1)
			mergeSort(x, n - s, s, comparator);
	}

}
