package com.yzy.spelling;

public class NGramDistance implements StringDistance {

	
	private int n;

	/**
	 * Creates an N-Gram distance measure using n-grams of the specified size.
	 * 
	 * @param size
	 *            The size of the n-gram to be used to compute the string
	 *            distance.
	 */
	public NGramDistance(int size) {
		this.n = size;
	}

	/**
	 * Creates an N-Gram distance measure using n-grams of size 2.
	 */
	public NGramDistance() {
		this(2);
	}

	public float getDistance(String source, String target) {
		final int sl = source.length();
		final int tl = target.length();

		if (sl == 0 || tl == 0) {
			if (sl == tl) {
				return 1;
			} else {
				return 0;
			}
		}

		int cost = 0;
		if (sl < n || tl < n) {
			for (int i = 0, ni = Math.min(sl, tl); i < ni; i++) {
				if (source.charAt(i) == target.charAt(i)) {
					cost++;
				}
			}
			return (float) cost / Math.max(sl, tl);
		}

		char[] sa = new char[sl + n - 1];
		float p[]; // 'previous' cost array, horizontally
		float d[]; // cost array, horizontally
		float _d[]; // placeholder to assist in swapping p and d

		// construct sa with prefix
		for (int i = 0; i < sa.length; i++) {
			if (i < n - 1) {
				sa[i] = 0; // add prefix
			} else {
				sa[i] = source.charAt(i - n + 1);
			}
		}
		p = new float[sl + 1];
		d = new float[sl + 1];

		// indexes into strings s and t
		int i; // iterates through source
		int j; // iterates through target

		char[] t_j = new char[n]; // jth n-gram of t

		for (i = 0; i <= sl; i++) {
			p[i] = i;
		}

		for (j = 1; j <= tl; j++) {
			// construct t_j n-gram
			if (j < n) {
				for (int ti = 0; ti < n - j; ti++) {
					t_j[ti] = 0; // add prefix
				}
				for (int ti = n - j; ti < n; ti++) {
					t_j[ti] = target.charAt(ti - (n - j));
				}
			} else {
				t_j = target.substring(j - n, j).toCharArray();
			}
			d[0] = j;
			for (i = 1; i <= sl; i++) {
				cost = 0;
				int tn = n;
				// compare sa to t_j
				for (int ni = 0; ni < n; ni++) {
					if (sa[i - 1 + ni] != t_j[ni]) {
						cost++;
					} else if (sa[i - 1 + ni] == 0) { // discount matches on
														// prefix
						tn--;
					}
				}
				float ec = (float) cost / tn;
				// minimum of cell to the left+1, to the top+1, diagonally left
				// and up +cost
				d[i] = Math
						.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + ec);
			}
			// copy current distance counts to 'previous row' distance counts
			_d = p;
			p = d;
			d = _d;
		}

		// our last action in the above loop was to switch d and p, so p now
		// actually has the most recent cost counts
		return 1.0f - ((float) p[sl] / Math.max(tl, sl));
	}

}
