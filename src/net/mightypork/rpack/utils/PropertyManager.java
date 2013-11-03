package net.mightypork.rpack.utils;


import java.io.*;
import java.util.*;
import java.util.Map.Entry;


/**
 * Property manager with advanced formatting and value checking.<br>
 * Methods starting with put are for filling. Most of the others are shortcuts
 * to getters.
 * 
 * @author MightyPork
 */
public class PropertyManager {

	/**
	 * Properties stored in file, alphabetically sorted.<br>
	 * Property file is much cleaner than the normal java.util.Properties,
	 * newlines can be inserted to separate categories, and individual keys can
	 * have their own inline comments.
	 * 
	 * @author MightyPork
	 * @copy (c) 2012
	 */
	private static class SortedProperties extends Properties {

		/** A table of hex digits */
		private static final char[] hexDigit_custom = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };


		/**
		 * this is here because the original method is private.
		 * 
		 * @param nibble
		 * @return hex char.
		 */
		private static char toHex_custom(int nibble) {

			return hexDigit_custom[(nibble & 0xF)];
		}


		private static void writeComments_custom(BufferedWriter bw, String comm) throws IOException {

			String comments = comm.replace("\n\n", "\n \n");

			int len = comments.length();
			int current = 0;
			int last = 0;
			char[] uu = new char[6];
			uu[0] = '\\';
			uu[1] = 'u';
			while (current < len) {
				char c = comments.charAt(current);
				if (c > '\u00ff' || c == '\n' || c == '\r') {
					if (last != current) {
						bw.write("# " + comments.substring(last, current));
					}


					if (c > '\u00ff') {
						uu[2] = toHex_custom((c >> 12) & 0xf);
						uu[3] = toHex_custom((c >> 8) & 0xf);
						uu[4] = toHex_custom((c >> 4) & 0xf);
						uu[5] = toHex_custom(c & 0xf);
						bw.write(new String(uu));
					} else {
						bw.newLine();
						if (c == '\r' && current != len - 1 && comments.charAt(current + 1) == '\n') {
							current++;
						}
						// if (current == len - 1 || (comments.charAt(current + 1) != '#' && comments.charAt(current + 1) != '!'))
						// bw.write("#");
					}
					last = current + 1;
				}
				current++;
			}
			if (last != current) {
				bw.write("# " + comments.substring(last, current));
			}
			bw.newLine();
			bw.newLine();
			bw.newLine();
		}

		/** Option: put empty line before each comment. */
		public boolean cfgEmptyLineBeforeComment = true;
		/**
		 * Option: Separate sections by newline<br>
		 * Section = string before first dot in key.
		 */
		public boolean cfgSeparateSectionsByEmptyLine = true;

		private boolean firstEntry = true;

		private Hashtable<String, String> keyComments = new Hashtable<String, String>();

		private String lastSectionBeginning = "";


		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public synchronized Enumeration keys() {

			Enumeration keysEnum = super.keys();
			Vector keyList = new Vector();
			while (keysEnum.hasMoreElements()) {
				keyList.add(keysEnum.nextElement());
			}
			Collections.sort(keyList);
			return keyList.elements();
		}


		private String saveConvert_custom(String theString, boolean escapeSpace, boolean escapeUnicode) {

			int len = theString.length();
			int bufLen = len * 2;
			if (bufLen < 0) {
				bufLen = Integer.MAX_VALUE;
			}
			StringBuffer outBuffer = new StringBuffer(bufLen);

			for (int x = 0; x < len; x++) {
				char aChar = theString.charAt(x);
				// Handle common case first, selecting largest block that
				// avoids the specials below
				if ((aChar > 61) && (aChar < 127)) {
					if (aChar == '\\') {
						outBuffer.append('\\');
						outBuffer.append('\\');
						continue;
					}
					outBuffer.append(aChar);
					continue;
				}
				switch (aChar) {
					case ' ':
						if (x == 0 || escapeSpace) {
							outBuffer.append('\\');
						}
						outBuffer.append(' ');
						break;
					case '\t':
						outBuffer.append('\\');
						outBuffer.append('t');
						break;
					case '\n':
						outBuffer.append('\\');
						outBuffer.append('n');
						break;
					case '\r':
						outBuffer.append('\\');
						outBuffer.append('r');
						break;
					case '\f':
						outBuffer.append('\\');
						outBuffer.append('f');
						break;
					case '=': // Fall through
					case ':': // Fall through
					case '#': // Fall through
					case '!':
						outBuffer.append('\\');
						outBuffer.append(aChar);
						break;
					default:
						if (((aChar < 0x0020) || (aChar > 0x007e)) & escapeUnicode) {
							outBuffer.append('\\');
							outBuffer.append('u');
							outBuffer.append(toHex_custom((aChar >> 12) & 0xF));
							outBuffer.append(toHex_custom((aChar >> 8) & 0xF));
							outBuffer.append(toHex_custom((aChar >> 4) & 0xF));
							outBuffer.append(toHex_custom(aChar & 0xF));
						} else {
							outBuffer.append(aChar);
						}
				}
			}
			return outBuffer.toString();
		}


		/**
		 * Set additional comment to a key
		 * 
		 * @param key key for comment
		 * @param comment the comment
		 */
		public void setKeyComment(String key, String comment) {

			keyComments.put(key, comment);
		}


		@Override
		public void store(OutputStream out, String comments) throws IOException {

			store_custom(new BufferedWriter(new OutputStreamWriter(out, "UTF-8")), comments, false);
		}


		@SuppressWarnings("rawtypes")
		private void store_custom(BufferedWriter bw, String comments, boolean escUnicode) throws IOException {

			if (comments != null) {
				writeComments_custom(bw, comments);
			}
			synchronized (this) {
				for (Enumeration e = keys(); e.hasMoreElements();) {

					boolean wasNewLine = false;

					String key = (String) e.nextElement();
					String val = (String) get(key);
					key = saveConvert_custom(key, true, escUnicode);
					val = saveConvert_custom(val, false, escUnicode);

					if (cfgSeparateSectionsByEmptyLine && !lastSectionBeginning.equals(key.split("[.]")[0])) {

						if (!firstEntry) {
							bw.newLine();
							bw.newLine();
						}

						wasNewLine = true;
						lastSectionBeginning = key.split("[.]")[0];
					}

					if (keyComments.containsKey(key)) {
						String cm = keyComments.get(key);
						cm = cm.replace("\r", "\n");
						cm = cm.replace("\r\n", "\n");
						cm = cm.replace("\n\n", "\n \n");

						String[] cmlines = cm.split("\n");

						if (!wasNewLine && !firstEntry && cfgEmptyLineBeforeComment) {
							bw.newLine();
						}
						for (String cmline : cmlines) {
							bw.write("# " + cmline);
							bw.newLine();
						}
					}

					bw.write(key + " = " + val);
					bw.newLine();

					firstEntry = false;
				}
			}
			bw.flush();
		}
	}

	/**
	 * Helper class which loads Properties from UTF-8 file (Properties use
	 * "ISO-8859-1" by default)
	 * 
	 * @author Itay Maman
	 */
	private static class PropertiesLoader {

		private static String escapifyStr(String str) {

			StringBuilder result = new StringBuilder();

			int len = str.length();
			for (int x = 0; x < len; x++) {
				char ch = str.charAt(x);
				if (ch <= 0x007e) {
					result.append(ch);
					continue;
				}

				result.append('\\');
				result.append('u');
				result.append(hexDigit(ch, 12));
				result.append(hexDigit(ch, 8));
				result.append(hexDigit(ch, 4));
				result.append(hexDigit(ch, 0));
			}
			return result.toString();
		}


		private static char hexDigit(char ch, int offset) {

			int val = (ch >> offset) & 0xF;
			if (val <= 9) {
				return (char) ('0' + val);
			}

			return (char) ('A' + val - 10);
		}


		public static SortedProperties loadProperties(SortedProperties props, InputStream is) throws IOException {

			return loadProperties(props, is, "utf-8");
		}


		public static SortedProperties loadProperties(SortedProperties props, InputStream is, String encoding) throws IOException {

			StringBuilder sb = new StringBuilder();
			InputStreamReader isr = new InputStreamReader(is, encoding);
			while (true) {
				int temp = isr.read();
				if (temp < 0) {
					break;
				}

				char c = (char) temp;
				sb.append(c);
			}

			String read = sb.toString();

			String inputString = escapifyStr(read);
			byte[] bs = inputString.getBytes("ISO-8859-1");
			ByteArrayInputStream bais = new ByteArrayInputStream(bs);

			SortedProperties ps = props;
			ps.load(bais);
			return ps;
		}
	}

	/**
	 * Property entry in Property manager.
	 * 
	 * @author MightyPork
	 * @copy (c) 2012
	 */
	private class Property {

		public boolean bool = false;
		public String entryComment;
		public boolean defbool = false;
		public double defnum = -1;

		public String defstr = "";
		public String name;
		public double num = -1;
		public String str = "";
		public PropertyType type;


		/**
		 * Property
		 * 
		 * @param key key
		 * @param default_value default value
		 * @param entry_type type
		 * @param entry_comment entry comment
		 */
		public Property(String key, boolean default_value, PropertyType entry_type, String entry_comment) {

			name = key;
			defbool = default_value;
			type = entry_type;
			entryComment = entry_comment;
		}


		/**
		 * Property entry
		 * 
		 * @param key property key
		 * @param default_value default value
		 * @param entry_type property type from enum
		 * @param entry_comment property comment or null
		 */
		public Property(String key, double default_value, PropertyType entry_type, String entry_comment) {

			name = key;
			defnum = default_value;
			type = entry_type;
			entryComment = entry_comment;
		}


		/**
		 * Property
		 * 
		 * @param key key
		 * @param default_value default value
		 * @param entry_type type
		 * @param entry_comment entry comment
		 */
		public Property(String key, String default_value, PropertyType entry_type, String entry_comment) {

			name = key;
			defstr = default_value;
			type = entry_type;
			entryComment = entry_comment;
		}


		/**
		 * Get boolean
		 * 
		 * @return the boolean
		 */
		public boolean getBoolean() {

			return bool;
		}


		/**
		 * Get number
		 * 
		 * @return the number
		 */
		public int getInteger() {

			return (int) Math.round(num);
		}


		/**
		 * Get number as double
		 * 
		 * @return the number
		 */
		public double getDouble() {

			return num;
		}


		/**
		 * Get string
		 * 
		 * @return the string
		 */
		public String getString() {

			return str;
		}


		/**
		 * Is this entry valid?
		 * 
		 * @return is valid
		 */
		public boolean isValid() {

			if (type == PropertyType.STRING) {
				return str != null;
			}
			if (type == PropertyType.BOOLEAN || type == PropertyType.INT || type == PropertyType.DOUBLE) {
				return true;
			}
			return false;
		}


		/**
		 * Load property value from a file
		 * 
		 * @param string the string loaded
		 * @return this entry
		 */
		public boolean parse(String string) {

			if (type == PropertyType.INT) {
				if (string == null) {
					num = defnum;
					return false;
				}
				try {
					num = Integer.parseInt(string.trim());
				} catch (NumberFormatException e) {
					num = defnum;
				}
			}

			if (type == PropertyType.DOUBLE) {
				if (string == null) {
					num = defnum;
					return false;
				}
				try {
					num = Double.parseDouble(string.trim());
				} catch (NumberFormatException e) {
					num = defnum;
				}
			}

			if (type == PropertyType.STRING) {
				if (string == null) {
					str = defstr;
					return false;
				}
				this.str = string;
			}

			if (type == PropertyType.BOOLEAN) {
				if (string == null) {
					bool = defbool;
					return false;
				}
				String string2 = string.toLowerCase();
				bool = string2.equals("yes") || string2.equals("true") || string2.equals("on") || string2.equals("enabled")
						|| string2.equals("enable");
			}

			return true;
		}


		/**
		 * prepare the contents for insertion into Properties
		 * 
		 * @return the string prepared, or null if type is invalid
		 */
		@Override
		public String toString() {

			if (!isValid()) {
				if (type == PropertyType.INT || type == PropertyType.DOUBLE) {
					num = defnum;
				}
			}
			if (type == PropertyType.INT) {
				return Integer.toString((int) num);
			}
			if (type == PropertyType.DOUBLE) {
				return Calc.floatToString((float) num);
			}
			if (type == PropertyType.STRING) {
				return str;
			}
			if (type == PropertyType.BOOLEAN) {
				return bool ? "True" : "False";
			}
			return null;
		}


		/**
		 * If this entry is not valid, change it to the dafault value.
		 */
		public void validate() {

			if (!isValid()) {
				if (type == PropertyType.STRING) {
					str = defstr;
				}
			}
		}
	}

	/**
	 * Property type enum.
	 * 
	 * @author MightyPork
	 * @copy (c) 2012
	 */
	private enum PropertyType {
		BOOLEAN, INT, STRING, DOUBLE;
	}

	/**
	 * Option to put newline before inline comments
	 */
	private boolean cfgNewlineBeforeComments = true;
	/** Disable entry validation */
	private boolean cfgNoValidate = true;

	/**
	 * Option to put newline between sections.<br>
	 * Sections are detected by text before first dot in identifier.
	 */
	private boolean cfgSeparateSections = true;

	private String comment = "";

	private TreeMap<String, Property> entries;


	private File file;

	private TreeMap<String, String> keyRename;

	private SortedProperties pr = new SortedProperties();

	private TreeMap<String, String> setValues;


	/**
	 * Create property manager from file path and an initial comment.
	 * 
	 * @param file file with the props
	 * @param comment the initial comment. Use \n in it if you want.
	 */
	public PropertyManager(File file, String comment) {

		this.file = file;
		this.entries = new TreeMap<String, Property>();
		this.setValues = new TreeMap<String, String>();
		this.keyRename = new TreeMap<String, String>();
		this.comment = comment;
	}


	/**
	 * Load, fix and write to file.
	 */
	public void apply() {

		boolean needsSave = false;
		try {

			new File(file.getParent()).mkdirs();

			pr = PropertiesLoader.loadProperties(pr, new FileInputStream(file));

		} catch (IOException e) {
			needsSave = true;
			pr = new SortedProperties();
		}

		pr.cfgSeparateSectionsByEmptyLine = cfgSeparateSections;
		pr.cfgEmptyLineBeforeComment = cfgNewlineBeforeComments;

		ArrayList<String> keyList = new ArrayList<String>();

		// rename keys
		for (Entry<String, String> entry : keyRename.entrySet()) {
			if (pr.getProperty(entry.getKey()) == null) {
				continue;
			}
			pr.setProperty(entry.getValue(), pr.getProperty(entry.getKey()));
			pr.remove(entry.getKey());
			needsSave = true;
		}

		// set the override values into the freshly loaded properties file
		for (Entry<String, String> entry : setValues.entrySet()) {
			pr.setProperty(entry.getKey(), entry.getValue());
			needsSave = true;
		}


		// validate entries one by one, replace with default when needed
		for (Property entry : entries.values()) {

			keyList.add(entry.name);

			String propOrig = pr.getProperty(entry.name);
			if (!entry.parse(propOrig)) needsSave = true;
			if (!cfgNoValidate) {
				entry.validate();
			}

			if (entry.entryComment != null) {
				pr.setKeyComment(entry.name, entry.entryComment);
			}

			if (propOrig == null || !entry.toString().equals(propOrig)) {

				pr.setProperty(entry.name, entry.toString());

				needsSave = true;
			}
		}

		// removed unused props
		for (String propname : pr.keySet().toArray(new String[pr.size()])) {

			if (!keyList.contains(propname)) {
				pr.remove(propname);
				needsSave = true;
			}

		}

		// save if needed
		if (needsSave) {
			try {
				pr.store(new FileOutputStream(file), comment);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}

		setValues.clear();
		keyRename.clear();
	}


	/**
	 * Get boolean property
	 * 
	 * @param n key
	 * @return the boolean found, or false
	 */
	public Boolean bool(String n) {

		return getBoolean(n);
	}


	/**
	 * @param newlineBeforeComments put newline before comments
	 */
	public void cfgNewlineBeforeComments(boolean newlineBeforeComments) {

		this.cfgNewlineBeforeComments = newlineBeforeComments;
	}


	/**
	 * @param separateSections do separate sections by newline
	 */
	public void cfgSeparateSections(boolean separateSections) {

		this.cfgSeparateSections = separateSections;
	}


	/**
	 * @param validate enable validation
	 */
	public void enableValidation(boolean validate) {

		this.cfgNoValidate = !validate;
	}


	/**
	 * Get boolean property
	 * 
	 * @param n key
	 * @return the boolean found, or false
	 */
	public Boolean flag(String n) {

		return getBoolean(n);
	}


	/**
	 * Get a property entry (rarely used)
	 * 
	 * @param n key
	 * @return the entry
	 */
	private Property get(String n) {

		try {
			return entries.get(n);
		} catch (Throwable t) {
			return null;
		}
	}


	/**
	 * Get boolean property
	 * 
	 * @param n key
	 * @return the boolean found, or false
	 */
	public Boolean getBoolean(String n) {

		try {
			return entries.get(n).getBoolean();
		} catch (Throwable t) {
			return false;
		}
	}


	/**
	 * Get numeric property
	 * 
	 * @param n key
	 * @return the int found, or null
	 */
	public Integer getInt(String n) {

		return getInteger(n);
	}


	/**
	 * Get numeric property
	 * 
	 * @param n key
	 * @return the int found, or null
	 */
	public Integer getInteger(String n) {

		try {
			return get(n).getInteger();
		} catch (Throwable t) {
			return -1;
		}
	}


	/**
	 * Get numeric property as double
	 * 
	 * @param n key
	 * @return the double found, or null
	 */
	public Double getDouble(String n) {

		try {
			return get(n).getDouble();
		} catch (Throwable t) {
			return -1D;
		}
	}


	/**
	 * Get numeric property
	 * 
	 * @param n key
	 * @return the int found, or null
	 */
	public Integer getNum(String n) {

		return getInteger(n);
	}


	/**
	 * Get string property
	 * 
	 * @param n key
	 * @return the string found, or null
	 */
	public String getString(String n) {

		try {
			return get(n).getString();
		} catch (Throwable t) {
			return null;
		}
	}


	/**
	 * Get numeric property
	 * 
	 * @param n key
	 * @return the int found, or null
	 */
	public Integer integer(String n) {

		try {
			return get(n).getInteger();
		} catch (Throwable t) {
			return -1;
		}
	}


	/**
	 * Get numeric property
	 * 
	 * @param n key
	 * @return the int found, or null
	 */
	public Integer num(String n) {

		return getInteger(n);
	}


	/**
	 * Add a boolean property
	 * 
	 * @param n key
	 * @param d default value
	 */
	public void putBoolean(String n, boolean d) {

		entries.put(n, new Property(n, d, PropertyType.BOOLEAN, null));
		return;
	}


	/**
	 * Add a boolean property
	 * 
	 * @param n key
	 * @param d default value
	 * @param comment the in-file comment
	 */
	public void putBoolean(String n, boolean d, String comment) {

		entries.put(n, new Property(n, d, PropertyType.BOOLEAN, comment));
		return;
	}


	/**
	 * Add a numeric property (double)
	 * 
	 * @param n key
	 * @param d default value
	 */
	public void putDouble(String n, int d) {

		entries.put(n, new Property(n, d, PropertyType.DOUBLE, null));
		return;
	}


	/**
	 * Add a numeric property (double)
	 * 
	 * @param n key
	 * @param d default value
	 * @param comment the in-file comment
	 */
	public void putDouble(String n, int d, String comment) {

		entries.put(n, new Property(n, d, PropertyType.DOUBLE, comment));
		return;
	}


	/**
	 * Add a numeric property
	 * 
	 * @param n key
	 * @param d default value
	 */
	public void putInteger(String n, int d) {

		entries.put(n, new Property(n, d, PropertyType.INT, null));
		return;
	}


	/**
	 * Add a numeric property
	 * 
	 * @param n key
	 * @param d default value
	 * @param comment the in-file comment
	 */
	public void putInteger(String n, int d, String comment) {

		entries.put(n, new Property(n, d, PropertyType.INT, comment));
		return;
	}


	/**
	 * Add a string property
	 * 
	 * @param n key
	 * @param d default value
	 */
	public void putString(String n, String d) {

		entries.put(n, new Property(n, d, PropertyType.STRING, null));
		return;
	}


	/**
	 * Add a string property
	 * 
	 * @param n key
	 * @param d default value
	 * @param comment the in-file comment
	 */
	public void putString(String n, String d, String comment) {

		entries.put(n, new Property(n, d, PropertyType.STRING, comment));
		return;
	}


	/**
	 * Rename key before doing "apply"; value is preserved
	 * 
	 * @param oldKey old key
	 * @param newKey new key
	 */
	public void renameKey(String oldKey, String newKey) {

		keyRename.put(oldKey, newKey);
		return;
	}


	/**
	 * Set value saved to certain key; use to save runtime-changed configuration
	 * values.
	 * 
	 * @param key key
	 * @param value the saved value
	 */
	public void setValue(String key, Object value) {

		setValues.put(key, value.toString());
		return;
	}


	/**
	 * Get string property
	 * 
	 * @param n key
	 * @return the string found, or null
	 */
	public String str(String n) {

		try {
			return get(n).getString();
		} catch (Throwable t) {
			return null;
		}
	}


	/**
	 * Get string property
	 * 
	 * @param n key
	 * @return the string found, or null
	 */
	public String string(String n) {

		try {
			return get(n).getString();
		} catch (Throwable t) {
			return null;
		}
	}
}