/**
 * Copyright 2010 Mentor Graphics Corporation. All Rights Reserved.
 * <p>
 * Recipients who obtain this code directly from Mentor Graphics use it solely
 * for internal purposes to serve as example Java or Java Script plugins.
 * This code may not be used in a commercial distribution. Recipients may
 * duplicate the code provided that all notices are fully reproduced with
 * and remain in the code. No part of this code may be modified, reproduced,
 * translated, used, distributed, disclosed or provided to third parties
 * without the prior written consent of Mentor Graphics, except as expressly
 * authorized above.
 * <p>
 * THE CODE IS MADE AVAILABLE "AS IS" WITHOUT WARRANTY OR SUPPORT OF ANY KIND.
 * MENTOR GRAPHICS OFFERS NO EXPRESS OR IMPLIED WARRANTIES AND SPECIFICALLY
 * DISCLAIMS ANY WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * OR WARRANTY OF NON-INFRINGEMENT. IN NO EVENT SHALL MENTOR GRAPHICS OR ITS
 * LICENSORS BE LIABLE FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING LOST PROFITS OR SAVINGS) WHETHER BASED ON CONTRACT, TORT
 * OR ANY OTHER LEGAL THEORY, EVEN IF MENTOR GRAPHICS OR ITS LICENSORS HAVE BEEN
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * <p>
 */

package table;

import com.mentor.chs.api.IXObject;
import java.util.Comparator;

public class AlphaNumComparator<T> implements Comparator<T>
{

	private boolean caseInsensitive;
	private boolean makeUniqueObjectsUnique;
        private String attribute = null;

	public static <K> AlphaNumComparator<K> getAlphaNumComparatorForTreeUsage()
	{
		return new AlphaNumComparator<K>(true, true);
	}

	public AlphaNumComparator(boolean caseInsensitive,
			boolean makeUniqueObjectsUnique)
	{
		this.caseInsensitive = caseInsensitive;
		this.makeUniqueObjectsUnique = makeUniqueObjectsUnique;
	}

	public AlphaNumComparator(boolean caseInsensitive)
	{
		//
		// Note, Changing the 'uniqueness' flag to true seems like the right thing
		// but it will appear to break some usages - esp. with TreeMaps - be careful.
		//
		// Also new String("a1") != new String("a1") - this could hose the
		// if (makeUniqueObjectsUnique && retVal == 0 && a != b) check...
		//
		this(caseInsensitive, false);
	}

	public AlphaNumComparator()
	{
		this(true);
	}
	public AlphaNumComparator(String attribute)
	{
		this(true);
                this.attribute = attribute;
	}

	private int compare(char[] a, int ai, char[] b, int bi)
	{
		while (true) {
			// Handle the case where we run of the end of one or both strings.
			if (ai >= a.length && bi >= b.length) {
				return 0;
			}
			if (ai >= a.length) {
				return -1;
			}
			if (bi >= b.length) {
				return +1;
			}

			char ca = a[ai];
			char cb = b[bi];

			if (ca >= '0' && cb >= '0' && ca <= '9' && cb <= '9') {
				// Current character in each string is a digit, compare the contiguous sequence of digits as integers.

				// First sort out how long the digit sequences are.
				int la = 0;
				int lb = 0;
				while (ai < a.length && ca >= '0' && ca <= '9') {
					la += 1;
					if (++ai < a.length) {
						ca = a[ai];
					}
				}

				while (bi < b.length && cb >= '0' && cb <= '9') {
					lb += 1;
					if (++bi < b.length) {
						cb = b[bi];
					}
				}

				int maxlen = Math.max(la, lb);
				int ina = la - maxlen;
				int inb = lb - maxlen;
				int rina = ai - maxlen;
				int rinb = bi - maxlen;

				// Process each digit in turn, starting with the most significant.
				for (int i = 0; i < maxlen; i++) {
					// If one digit sequence is shorter, we pad it with leading zeroes.
					char cha = (ina++ < 0) ? '0' : a[rina];
					char chb = (inb++ < 0) ? '0' : b[rinb];
					rina++;
					rinb++;

					// The most significant digit with a difference defines the result of the comparison.
					if (cha > chb) {
						return +1;
					}
					if (cha < chb) {
						return -1;
					}
				}
				// Indexes already point to the characters following the numbers.
			}
			else {
				// Character from one or both strings is non-digit, compare the characters themselves.
				if (ca > cb) {
					return +1;
				}
				if (ca < cb) {
					return -1;
				}

				ai++;
				bi++;
			}
		}
	}

	public int compare(T a, T b)
	{
		int retVal = 0;
		boolean aNull = (a == null || a.toString() == null);
		boolean bNull = (b == null || b.toString() == null);
		if (aNull && bNull) {
			return 0;
		}
		if (aNull) {
			return -1;
		}
		if (bNull) {
			return 1;
		}
                if (a instanceof IXObject) {
                    if (attribute == null) {
                        a = (T)((IXObject)a).getAttribute("Name");
                        if (a == null)
                        {
                            a = (T)"";
                        }
                    } else {
                        String value = ((IXObject)a).getAttribute(attribute);
                        if (value == null) {
                            value = ((IXObject)a).getProperty(attribute);
                        }
                        if (value == null)
                        {
                            value = "";
                        }
                        a = (T)value;
                    }
                }
                if (b instanceof IXObject) {
                    if (attribute == null) {
                        b = (T)((IXObject)b).getAttribute("Name");
                        if (b == null)
                        {
                            b = (T)"";
                        }
                    } else {
                        String value = ((IXObject)b).getAttribute(attribute);
                        if (value == null) {
                            value = ((IXObject)b).getProperty(attribute);
                        }
                        if (value == null)
                        {
                            value = "";
                        }
                        b = (T)value;
                    }
                }
		if (caseInsensitive) {
			retVal = compare((a.toString()).toUpperCase().toCharArray(), 0,
					(b.toString()).toUpperCase().toCharArray(), 0);
		}
		else {
			retVal = compare((a.toString()).toCharArray(), 0,
					(b.toString()).toCharArray(), 0);
		}

		if (makeUniqueObjectsUnique && retVal == 0 && a != b) {
			if (caseInsensitive) {
				//we have a case insensitive match so try case sensitive
				// this will put two case insensitive matches next to each other
				retVal = compare((a.toString()).toCharArray(), 0,
						(b.toString()).toCharArray(), 0);
			}
			// this check on length is for where
			// there have been some leading 0's we cannot sort comparing
			// X001X1 with X1X001 we would need to fix that at the number
			// level but then we would get X001X1A and X1X1B wrong
			if (a.toString().length() > b.toString().length()) {
				return +1;
			}
			if (a.toString().length() < b.toString().length()) {
				return -1;
			}
			if (retVal == 0) {
				int aHash = System.identityHashCode(a);
				int bHash = System.identityHashCode(b);
				if (aHash > bHash) {
					return +1;
				}
				if (bHash <= aHash) {
					return retVal;
				}
				return -1;
			}
			// I give up just pretend they are the same.
			// perhaps this will never happen Ha.
		}
		return retVal;
	}

	public static <K> int compare(K a, K b, boolean caseInsensitive)
	{
		return new AlphaNumComparator<K>(caseInsensitive).compare(a, b);
	}

	public static <K> int compare(K a, K b, boolean caseInsensitive,
			boolean makeUniqueObjectUnique)
	{
		return new AlphaNumComparator<K>(caseInsensitive, makeUniqueObjectUnique).compare(a, b);
	}
}
