package net.mightypork.rpw.utils;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * Utility for parsing simple config files<br>
 * # and // mark a comment<br>
 * empty lines and lines without "=" are ignored<br>
 * lines with "=" must have "key = value" format, or a warning is logged.<br>
 * use "NULL" to create empty value.
 * 
 * @author MightyPork
 */
public class SimpleConfig {

	/**
	 * Load list from file
	 * 
	 * @param file file
	 * @return map of keys and values
	 * @throws IOException
	 */
	public static List<String> listFromFile(File file) throws IOException {

		String fileText = FileUtils.fileToString(file);

		return listFromString(fileText);
	}


	/**
	 * Load map from file
	 * 
	 * @param file file
	 * @return map of keys and values
	 * @throws IOException
	 */
	public static Map<String, String> mapFromFile(File file) throws IOException {

		String fileText = FileUtils.fileToString(file);

		return mapFromString(fileText);
	}


	/**
	 * Load list from string
	 * 
	 * @param text text of the file
	 * @return map of keys and values
	 */
	public static List<String> listFromString(String text) {

		List<String> list = new ArrayList<String>();

		String[] groupsLines = text.split("\n");

		for (String s : groupsLines) {
			// ignore invalid lines
			if (s.length() == 0) continue;
			if (s.startsWith("#") || s.startsWith("//")) continue;

			// NULL value
			if (s.equalsIgnoreCase("NULL")) s = null;

			if (s != null) s = s.replace("\\n", "\n");

			// save extracted key-value pair
			list.add(s);
		}

		return list;
	}


	/**
	 * Load map from string
	 * 
	 * @param text text of the file
	 * @return map of keys and values
	 */
	public static Map<String, String> mapFromString(String text) {

		LinkedHashMap<String, String> pairs = new LinkedHashMap<String, String>();

		String[] groupsLines = text.split("\n");

		for (String s : groupsLines) {
			// ignore invalid lines
			if (s.length() == 0) continue;
			if (s.startsWith("#") || s.startsWith("//")) continue;
			if (!s.contains("=")) continue;

			// split and trim
			String[] parts = s.split("=");
			for (int i = 0; i < parts.length; i++) {
				parts[i] = parts[i].trim();
			}

			// check if both parts are valid
			if (parts.length == 0) {
				Log.w("Bad line in config file: " + s);
				continue;
			}

			if (parts.length == 1) {
				parts = new String[] { parts[0], "" };
			}

			if (parts.length != 2) {
				Log.w("Bad line in config file: " + s);
				continue;
			}


			// NULL value
			if (parts[0].equalsIgnoreCase("NULL")) parts[0] = null;
			if (parts[1].equalsIgnoreCase("NULL")) parts[1] = null;

			if (parts[0] != null) parts[0] = parts[0].replace("\\n", "\n");
			if (parts[1] != null) parts[1] = parts[1].replace("\\n", "\n");

			// save extracted key-value pair
			pairs.put(parts[0], parts[1]);
		}

		return pairs;
	}


	/**
	 * Save map to file
	 * 
	 * @param target
	 * @param data
	 * @throws IOException
	 */
	public static void mapToFile(File target, Map<String, String> data) throws IOException {

		String text = ""; //# File written by SimpleConfig

		for (Entry<String, String> e : data.entrySet()) {
			if (text.length() > 0) text += "\n";

			String key = e.getKey();
			String value = e.getValue();

			if (key == null) key = "NULL";
			if (value == null) value = "NULL";

			key = key.replace("\n", "\\n");
			value = value.replace("\n", "\\n");

			text += key + " = " + value;
		}

		FileUtils.stringToFile(target, text);

	}


	/**
	 * Save list to file
	 * 
	 * @param target
	 * @param data
	 * @throws IOException
	 */
	public static void listToFile(File target, List<String> data) throws IOException {

		String text = ""; //# File written by SimpleConfig

		for (String s : data) {
			if (text.length() > 0) text += "\n";

			if (s == null) s = "NULL";

			s = s.replace("\n", "\\n");

			text += s;
		}

		FileUtils.stringToFile(target, text);

	}
}
