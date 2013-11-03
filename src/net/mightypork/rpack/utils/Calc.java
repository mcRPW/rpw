package net.mightypork.rpack.utils;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Math helper
 * 
 * @author MightyPork
 */
public class Calc {

	/** Square root of two */
	public static final double SQ2 = 1.41421356237;


	/**
	 * Get longest side of a right-angled triangle
	 * 
	 * @param a side a (opposite)
	 * @param b side b (adjacent)
	 * @return longest side (hypotenuse)
	 */
	public static double pythC(double a, double b) {

		return Math.sqrt(square(a) + square(b));
	}


	/**
	 * Get adjacent side of a right-angled triangle
	 * 
	 * @param a side a (opposite)
	 * @param c side c (hypotenuse)
	 * @return side b (adjacent)
	 */
	public static double pythB(double a, double c) {

		return Math.sqrt(square(c) - square(a));
	}


	/**
	 * Get opposite side of a right-angled triangle
	 * 
	 * @param b side b (adjacent)
	 * @param c side c (hypotenuse)
	 * @return side a (opposite)
	 */
	public static double pythA(double b, double c) {

		return Math.sqrt(square(c) - square(b));
	}

	private static class Angles {

		public static double delta(double alpha, double beta, double a360) {

			while (Math.abs(alpha - beta) > a360 / 2D) {
				alpha = norm(alpha + a360 / 2D, a360);
				beta = norm(beta + a360 / 2D, a360);
			}

			return beta - alpha;
		}


		public static double norm(double angle, double a360) {

			while (angle < 0)
				angle += a360;
			while (angle > a360)
				angle -= a360;
			return angle;
		}
	}

	/**
	 * Angle calculations for degrees.
	 * 
	 * @author MightyPork
	 */
	public static class Deg {

		/** 180° in degrees */
		public static final double a180 = 180;
		/** 270° in degrees */
		public static final double a270 = 270;
		/** 360° in degrees */
		public static final double a360 = 360;
		/** 45° in degrees */
		public static final double a45 = 45;
		/** 90° in degrees */
		public static final double a90 = 90;


		/**
		 * Subtract two angles alpha - beta
		 * 
		 * @param alpha first angle
		 * @param beta second angle
		 * @return (alpha - beta) in degrees
		 */
		public static double delta(double alpha, double beta) {

			return Angles.delta(alpha, beta, a360);
		}


		/**
		 * Difference of two angles (absolute value of delta)
		 * 
		 * @param alpha first angle
		 * @param beta second angle
		 * @return difference in radians
		 */
		public static double diff(double alpha, double beta) {

			return Math.abs(Angles.delta(alpha, beta, a360));
		}


		/**
		 * Cosinus in degrees
		 * 
		 * @param deg angle in degrees
		 * @return cosinus
		 */
		public static double cos(double deg) {

			return Math.cos(toRad(deg));
		}


		/**
		 * Sinus in degrees
		 * 
		 * @param deg angle in degrees
		 * @return sinus
		 */
		public static double sin(double deg) {

			return Math.sin(toRad(deg));
		}


		/**
		 * Tangents in degrees
		 * 
		 * @param deg angle in degrees
		 * @return tangents
		 */
		public static double tan(double deg) {

			return Math.tan(toRad(deg));
		}


		/**
		 * Angle normalized to 0-360 range
		 * 
		 * @param angle angle to normalize
		 * @return normalized angle
		 */
		public static double norm(double angle) {

			return Angles.norm(angle, a360);
		}


		/**
		 * Convert to radians
		 * 
		 * @param deg degrees
		 * @return radians
		 */
		public static double toRad(double deg) {

			return Math.toRadians(deg);
		}


		/**
		 * Round angle to 0,45,90,135...
		 * 
		 * @param deg angle in deg. to round
		 * @param x rounding increment (45 - round to 0,45,90...)
		 * @return rounded
		 */
		public static int roundX(double deg, double x) {

			double half = x / 2d;
			deg += half;
			deg = norm(deg);
			int times = (int) Math.floor(deg / x);
			double a = times * x;
			if (a == 360) a = 0;
			return (int) Math.round(a);
		}


		/**
		 * Round angle to 0,45,90,135...
		 * 
		 * @param deg angle in deg. to round
		 * @return rounded
		 */
		public static int round45(double deg) {

			return roundX(deg, 45);
		}


		/**
		 * Round angle to 0,90,180,270
		 * 
		 * @param deg angle in deg. to round
		 * @return rounded
		 */
		public static int round90(double deg) {

			return roundX(deg, 90);
		}


		/**
		 * Round angle to 0,15,30,45,60,75,90...
		 * 
		 * @param deg angle in deg to round
		 * @return rounded
		 */
		public static int round15(double deg) {

			return roundX(deg, 15);
		}
	}

	/**
	 * Angle calculations for radians.
	 * 
	 * @author MightyPork
	 */
	public static class Rad {

		/** 180° in radians */
		public static final double a180 = Math.PI;
		/** 270° in radians */
		public static final double a270 = Math.PI * 1.5D;
		/** 360° in radians */
		public static final double a360 = Math.PI * 2D;
		/** 45° in radians */
		public static final double a45 = Math.PI / 4D;
		/** 90° in radians */
		public static final double a90 = Math.PI / 2D;


		/**
		 * Subtract two angles alpha - beta
		 * 
		 * @param alpha first angle
		 * @param beta second angle
		 * @return (alpha - beta) in radians
		 */
		public static double delta(double alpha, double beta) {

			return Angles.delta(alpha, beta, a360);
		}


		/**
		 * Difference of two angles (absolute value of delta)
		 * 
		 * @param alpha first angle
		 * @param beta second angle
		 * @return difference in radians
		 */
		public static double diff(double alpha, double beta) {

			return Math.abs(Angles.delta(alpha, beta, a360));
		}


		/**
		 * Cos
		 * 
		 * @param rad angle in rads
		 * @return cos
		 */
		public static double cos(double rad) {

			return Math.cos(rad);
		}


		/**
		 * Sin
		 * 
		 * @param rad angle in rads
		 * @return sin
		 */
		public static double sin(double rad) {

			return Math.sin(rad);
		}


		/**
		 * Tan
		 * 
		 * @param rad angle in rads
		 * @return tan
		 */
		public static double tan(double rad) {

			return Math.tan(rad);
		}


		/**
		 * Angle normalized to 0-2*PI range
		 * 
		 * @param angle angle to normalize
		 * @return normalized angle
		 */
		public static double norm(double angle) {

			return Angles.norm(angle, a360);
		}


		/**
		 * Convert to degrees
		 * 
		 * @param rad radians
		 * @return degrees
		 */
		public static double toDeg(double rad) {

			return Math.toDegrees(rad);
		}
	}

	private static Random rand = new Random();


	/**
	 * Get volume of a sphere
	 * 
	 * @param radius sphere radius
	 * @return volume in cubic units
	 */
	public static double sphereGetVolume(double radius) {

		return (4D / 3D) * Math.PI * cube(radius);
	}


	/**
	 * Get radius of a sphere
	 * 
	 * @param volume sphere volume
	 * @return radius in units
	 */
	public static double sphereGetRadius(double volume) {

		return Math.cbrt((3D * volume) / (4 * Math.PI));
	}


	/**
	 * Get surface of a circle
	 * 
	 * @param radius circle radius
	 * @return volume in square units
	 */
	public static double circleGetSurface(double radius) {

		return Math.PI * square(radius);
	}


	/**
	 * Get radius of a circle
	 * 
	 * @param surface circle volume
	 * @return radius in units
	 */
	public static double circleGetRadius(double surface) {

		return Math.sqrt(surface / Math.PI);
	}


	/**
	 * Check if objects are equal (for equals function)
	 * 
	 * @param a
	 * @param b
	 * @return are equal
	 */
	public static boolean areObjectsEqual(Object a, Object b) {

		return a == null ? b == null : a.equals(b);
	}


	/**
	 * Private clamping helper.
	 * 
	 * @param number number to be clamped
	 * @param min min value
	 * @param max max value
	 * @return clamped double
	 */
	private static double clamp_double(Number number, Number min, Number max) {

		double n = number.doubleValue();
		double mind = min.doubleValue();
		double maxd = max.doubleValue();
		if (n > maxd) n = maxd;
		if (n < mind) n = mind;
		if (Double.isNaN(n)) return mind;
		return n;
	}


	/**
	 * Private clamping helper.
	 * 
	 * @param number number to be clamped
	 * @param min min value
	 * @return clamped double
	 */
	private static double clamp_double(Number number, Number min) {

		double n = number.doubleValue();
		double mind = min.doubleValue();
		if (n < mind) n = mind;
		return n;
	}


	/**
	 * Clamp number to min and max bounds, inclusive.<br>
	 * DOUBLE version
	 * 
	 * @param number clamped number
	 * @param min minimal allowed value
	 * @param max maximal allowed value
	 * @return result
	 */
	public static double clampd(Number number, Number min, Number max) {

		return clamp_double(number, min, max);
	}


	/**
	 * Clamp number to min and max bounds, inclusive.<br>
	 * FLOAT version
	 * 
	 * @param number clamped number
	 * @param min minimal allowed value
	 * @param max maximal allowed value
	 * @return result
	 */
	public static float clampf(Number number, Number min, Number max) {

		return (float) clamp_double(number, min, max);
	}


	/**
	 * Clamp number to min and max bounds, inclusive.<br>
	 * INTEGER version
	 * 
	 * @param number clamped number
	 * @param min minimal allowed value
	 * @param max maximal allowed value
	 * @return result
	 */
	public static int clampi(Number number, Number min, Number max) {

		return (int) Math.round(clamp_double(number, min, max));
	}


	/**
	 * Clamp number to min and infinite bounds, inclusive.<br>
	 * DOUBLE version
	 * 
	 * @param number clamped number
	 * @param min minimal allowed value
	 * @return result
	 */
	public static double clampd(Number number, Number min) {

		return clamp_double(number, min);
	}


	/**
	 * Clamp number to min and infinite bounds, inclusive.<br>
	 * FLOAT version
	 * 
	 * @param number clamped number
	 * @param min minimal allowed value
	 * @return result
	 */
	public static float clampf(Number number, Number min) {

		return (float) clamp_double(number, min);
	}


	/**
	 * Clamp number to min and infinite bounds, inclusive.<br>
	 * INTEGER version
	 * 
	 * @param number clamped number
	 * @param min minimal allowed value
	 * @return result
	 */
	public static int clampi(Number number, Number min) {

		return (int) Math.round(clamp_double(number, min));
	}


	/**
	 * Get class simple name
	 * 
	 * @param obj object
	 * @return simple name
	 */
	public static String cname(Object obj) {

		if (obj == null) return "NULL";
		return obj.getClass().getSimpleName();
	}


	/**
	 * Cube a double
	 * 
	 * @param a squared double
	 * @return square
	 */
	public static double cube(double a) {

		return a * a * a;
	}


	/**
	 * Convert double to string, remove the mess at the end.
	 * 
	 * @param d double
	 * @return string
	 */
	public static String doubleToString(double d) {

		String s = Double.toString(d);
		s = s.replaceAll("([0-9]+\\.[0-9]+)00+[0-9]+", "$1");
		s = s.replaceAll("0+$", "");
		s = s.replaceAll("\\.$", "");
		return s;
	}


	/**
	 * Convert float to string, remove the mess at the end.
	 * 
	 * @param f float
	 * @return string
	 */
	public static String floatToString(float f) {

		String s = Float.toString(f);
		s = s.replaceAll("([0-9]+\\.[0-9]+)00+[0-9]+", "$1");
		s = s.replaceAll("0+$", "");
		s = s.replaceAll("\\.$", "");
		return s;
	}


	/**
	 * Check if number is in range
	 * 
	 * @param number checked
	 * @param left lower end
	 * @param right upper end
	 * @return is in range
	 */
	public static boolean inRange(double number, double left, double right) {

		return number >= left && number <= right;
	}


	/**
	 * Get number from A to B at delta time (tween A to B)
	 * 
	 * @param last last number
	 * @param now new number
	 * @param dtime delta time
	 * @return current number to render
	 */
	public static double interpolate(double last, double now, double dtime) {

		return last + (now - last) * dtime;
	}


	/**
	 * Get angle [degrees] from A to B at delta time (tween A to B)
	 * 
	 * @param last last angle
	 * @param now new angle
	 * @param delta delta time
	 * @return current angle to render
	 */
	public static double interpolateDeg(double last, double now, double delta) {

		return Deg.norm(last + Deg.delta(now, last) * delta);
	}


	/**
	 * Get highest number of a list
	 * 
	 * @param numbers numbers
	 * @return lowest
	 */
	public static double max(double... numbers) {

		double highest = numbers[0];
		for (double num : numbers) {
			if (num > highest) highest = num;
		}
		return highest;
	}


	/**
	 * Get highest number of a list
	 * 
	 * @param numbers numbers
	 * @return lowest
	 */
	public static float max(float... numbers) {

		float highest = numbers[0];
		for (float num : numbers) {
			if (num > highest) highest = num;
		}
		return highest;
	}


	/**
	 * Get highest number of a list
	 * 
	 * @param numbers numbers
	 * @return lowest
	 */
	public static int max(int... numbers) {

		int highest = numbers[0];
		for (int num : numbers) {
			if (num > highest) highest = num;
		}
		return highest;
	}


	/**
	 * Get lowest number of a list
	 * 
	 * @param numbers numbers
	 * @return lowest
	 */
	public static double min(double... numbers) {

		double lowest = numbers[0];
		for (double num : numbers) {
			if (num < lowest) lowest = num;
		}
		return lowest;
	}


	/**
	 * Get lowest number of a list
	 * 
	 * @param numbers numbers
	 * @return lowest
	 */
	public static float min(float... numbers) {

		float lowest = numbers[0];
		for (float num : numbers) {
			if (num < lowest) lowest = num;
		}
		return lowest;
	}


	/**
	 * Get lowest number of a list
	 * 
	 * @param numbers numbers
	 * @return lowest
	 */
	public static int min(int... numbers) {

		int lowest = numbers[0];
		for (int num : numbers) {
			if (num < lowest) lowest = num;
		}
		return lowest;
	}


	/**
	 * Split comma separated list of integers.
	 * 
	 * @param list String containing the list.
	 * @return array of integers or null.
	 */
	public static List<Integer> parseIntList(String list) {

		if (list == null) {
			return null;
		}
		String[] parts = list.split(",");

		ArrayList<Integer> intList = new ArrayList<Integer>();

		for (String part : parts) {
			try {
				intList.add(Integer.parseInt(part));
			} catch (NumberFormatException e) {}
		}

		return intList;

	}


	/**
	 * Pick random element from a given list.
	 * 
	 * @param list list of choices
	 * @return picked element
	 */
	public static Object pick(List<?> list) {

		if (list.size() == 0) return null;
		return list.get(rand.nextInt(list.size()));
	}


	/**
	 * Square a double
	 * 
	 * @param a squared double
	 * @return square
	 */
	public static double square(double a) {

		return a * a;
	}


	/**
	 * Signum.
	 * 
	 * @param number
	 * @return sign, -1,0,1
	 */
	public static int sgn(double number) {

		return number > 0 ? 1 : number < 0 ? -1 : 0;
	}


	public static double frag(double d) {

		return d - Math.floor(d);
	}

}
