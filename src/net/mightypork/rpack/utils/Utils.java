package net.mightypork.rpack.utils;


import java.util.*;
import java.util.Map.Entry;


/**
 * Sector's utils class
 * 
 * @author MightyPork
 */
public class Utils {

	public static Object fallback(Object... options) {

		for (Object o : options) {
			if (o != null) return o;
		}
		return null; // error
	}


	public static String fromLastDot(String s) {

		return fromLastChar(s, '.');
	}


	public static String toLastDot(String s) {

		return toLastChar(s, '.');
	}


	public static String fromLastChar(String s, char c) {

		if (s == null) return null;
		return s.substring(s.lastIndexOf(c) + 1, s.length());
	}


	public static String toLastChar(String s, char c) {

		if (s == null) return null;
		return s.substring(0, s.lastIndexOf(c));
	}


	/**
	 * Sort a map by keys, maintaining key-value pairs.
	 * 
	 * @param map map to be sorted
	 * @return linked hash map with sorted entries
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <K extends Comparable, V extends Comparable> Map<K, V> sortByKeys(Map<K, V> map) {

		List<K> keys = new LinkedList<K>(map.keySet());
		Collections.sort(keys);

		//LinkedHashMap will keep the keys in the order they are inserted
		//which is currently sorted on natural ordering
		Map<K, V> sortedMap = new LinkedHashMap<K, V>();
		for (K key : keys) {
			sortedMap.put(key, map.get(key));
		}

		return sortedMap;
	}


	/**
	 * Sort a map by values, maintaining key-value pairs.
	 * 
	 * @param map map to be sorted
	 * @return linked hash map with sorted entries
	 */
	@SuppressWarnings("rawtypes")
	public static <K extends Comparable, V extends Comparable> Map<K, V> sortByValues(Map<K, V> map) {

		List<Map.Entry<K, V>> entries = new LinkedList<Map.Entry<K, V>>(map.entrySet());

		Collections.sort(entries, new Comparator<Map.Entry<K, V>>() {

			@Override
			public int compare(Entry<K, V> o1, Entry<K, V> o2) {

				return o1.getValue().compareTo(o2.getValue());
			}
		});

		//LinkedHashMap will keep the keys in the order they are inserted
		//which is currently sorted on natural ordering
		Map<K, V> sortedMap = new LinkedHashMap<K, V>();

		for (Map.Entry<K, V> entry : entries) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}


	public static void printStackTrace() {

		new Throwable().printStackTrace();
	}


	public static void sleep(int milis) {

		try {
			Thread.sleep(milis);
		} catch (InterruptedException e) {}
	}


	public static boolean isValidFilenameChar(char ch) {

		String s = ch + "";

		return isValidFilenameString(s);
	}


	public static boolean isValidFilenameString(String filename) {

		return filename.matches("[a-zA-Z0-9 +\\-.,_%@#$!'\"]+");
	}


	public static String cropStringAtStart(String orig, int length) {

		if (orig.length() > length) {
			orig = "\u2026" + orig.substring(orig.length() - length, orig.length());
		}
		return orig;
	}
	
	public static String cropStringAtEnd(String orig, int length) {

		if (orig.length() > length) {
			orig = orig.substring(0, Math.min(orig.length(), length) -1) + "\u2026";
		}
		return orig;
	}
}
