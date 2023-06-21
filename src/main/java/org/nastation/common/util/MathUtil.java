
package org.nastation.common.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class MathUtil {

	public static boolean isNumeric(String str) {
		int begin = 0;
		boolean once = true;
		if (str == null || str.trim().equals("")) {
			return false;
		}
		str = str.trim();
		if (str.startsWith("+") || str.startsWith("-")) {
			if (str.length() == 1) {
				// "+" "-"
				return false;
			}
			begin = 1;
		}
		for (int i = begin; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i))) {
				if (str.charAt(i) == '.' && once) {
					// '.' can only once
					once = false;
				} else {
					return false;
				}
			}
		}
		if (str.length() == (begin + 1) && !once) {
			// "." "+." "-."
			return false;
		}
		return true;
	}

	public static boolean isInteger(String str) {
		int begin = 0;
		if (str == null || str.trim().equals("")) {
			return false;
		}
		str = str.trim();
		if (str.startsWith("+") || str.startsWith("-")) {
			if (str.length() == 1) {
				// "+" "-"
				return false;
			}
			begin = 1;
		}
		for (int i = begin; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNumericEx(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	public static boolean isIntegerEx(String str) {
		str = str.trim();
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException ex) {
			if (str.startsWith("+")) {
				return isIntegerEx(str.substring(1));
			}
			return false;
		}
	}

	public static String formatByComma(double d) {
		String returnValue = "";

		DecimalFormat nf = new DecimalFormat("###,##0.##");
		returnValue = nf.format(d);

		return returnValue;
	}

	public static String clearNumberZero(String str) {
		String returnValue = str;

		if (returnValue.endsWith(".0")) {
			returnValue = returnValue.substring(0, returnValue.indexOf("."));
		}

		return returnValue;
	}

	public static String clearNumberZero(double num) {
		return clearNumberZero(String.valueOf(num));
	}

	public static String clearNumberDecimal(String str) {
		String returnValue = str;

		if (str.contains("."))
			returnValue = returnValue.substring(0, str.indexOf("."));

		return returnValue;
	}

	public static String clearNumberDecimal(double num) {
		return clearNumberDecimal(String.valueOf(num));
	}

	public static boolean isPrime(int x) {
		if (x <= 7) {
			if (x == 2 || x == 3 || x == 5 || x == 7)
				return true;
		}
		int c = 7;
		if (x % 2 == 0)
			return false;
		if (x % 3 == 0)
			return false;
		if (x % 5 == 0)
			return false;
		int end = (int) Math.sqrt(x);
		while (c <= end) {
			if (x % c == 0) {
				return false;
			}
			c += 4;
			if (x % c == 0) {
				return false;
			}
			c += 2;
			if (x % c == 0) {
				return false;
			}
			c += 4;
			if (x % c == 0) {
				return false;
			}
			c += 2;
			if (x % c == 0) {
				return false;
			}
			c += 4;
			if (x % c == 0) {
				return false;
			}
			c += 6;
			if (x % c == 0) {
				return false;
			}
			c += 2;
			if (x % c == 0) {
				return false;
			}
			c += 6;
		}
		return true;
	}

	public boolean isPrimes(int n) {
		for (int i = 2; i <= Math.sqrt(n); i++) {
			if (n % i == 0) {
				return false;
			}
		}
		return true;
	}

	private static final int DEF_DIV_SCALE = 10;

	public static double add(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.add(b2).doubleValue();
	}


	public static double subtract(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.subtract(b2).doubleValue();
	}


	public static double multiply(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.multiply(b2).doubleValue();
	}

	public static double divide(double v1, double v2) {
		return divide(v1, v2, DEF_DIV_SCALE);
	}

	public static double divide(double v1, double v2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("scale should be >=0");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}


	public static double round(double v, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("scale should be >=0");
		}
		BigDecimal b = new BigDecimal(Double.toString(v));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public static double floor(Double v, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("scale should be >=0");
		}

		if (v == 0) {
			return 0D;
		}

		BigDecimal b = new BigDecimal(v);
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_FLOOR).doubleValue();
	}

}