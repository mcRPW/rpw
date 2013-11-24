package net.mightypork.rpw.utils;


import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.mightypork.rpw.Config;
import net.mightypork.rpw.tree.assets.AssetEntry;
import net.mightypork.rpw.tree.assets.EAsset;
import net.mightypork.rpw.utils.validation.StringFilter;


public class FileUtils {

	/**
	 * Copy directory recursively.
	 * 
	 * @param source source file
	 * @param target target file
	 * @throws IOException on error
	 */
	public static void copyDirectory(File source, File target) throws IOException {

		if (source.isDirectory()) {

			if (!target.exists()) {
				target.mkdir();
			}

			String[] children = source.list();
			for (int i = 0; i < children.length; i++) {
				copyDirectory(new File(source, children[i]), new File(target, children[i]));
			}

		} else {
			copyFile(source, target);
		}
	}


	/**
	 * Copy directory recursively - advanced variant.
	 * 
	 * @param filesCopied list to write down copied files
	 * @param source source file
	 * @param target target file
	 * @param filter filter accepting only files and dirs to be copied
	 * @throws IOException on error
	 */
	public static void copyDirectory(File source, File target, StringFilter filter, List<File> filesCopied) throws IOException {

		if (source.isDirectory()) {

			if (!target.exists()) {
				target.mkdir();
			}

			String[] children = source.list();
			for (int i = 0; i < children.length; i++) {
				copyDirectory(new File(source, children[i]), new File(target, children[i]), filter, filesCopied);
			}

		} else {
			if (filter != null && !filter.accept(source.getAbsolutePath())) {
				return;
			}

			filesCopied.add(source);
			copyFile(source, target);
		}
	}


	/**
	 * List directory recursively
	 * 
	 * @param source source file
	 * @param filter filter accepting only files and dirs to be copied (or null)
	 * @param files list of the found files
	 * @throws IOException on error
	 */
	public static void listDirectoryRecursive(File source, StringFilter filter, List<File> files) throws IOException {

		if (source.isDirectory()) {

			String[] children = source.list();
			for (int i = 0; i < children.length; i++) {
				listDirectoryRecursive(new File(source, children[i]), filter, files);
			}

		} else {
			if (filter != null && !filter.accept(source.getAbsolutePath())) {
				return;
			}

			files.add(source);
		}
	}


	/**
	 * Copy file using streams. Make sure target directory exists!
	 * 
	 * @param source source file
	 * @param target target file
	 * @throws IOException on error
	 */
	public static void copyFile(File source, File target) throws IOException {


		InputStream in = new FileInputStream(source);
		OutputStream out = new FileOutputStream(target);

		copyStream(in, out);
	}


	/**
	 * Copy bytes from input to output stream
	 * 
	 * @param in input stream
	 * @param out output stream
	 * @throws IOException on error
	 */
	public static void copyStream(InputStream in, OutputStream out) throws IOException {

		try {
			byte[] buf = new byte[2048];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
		} finally {
			in.close();
			out.close();
		}
	}


	/**
	 * Copy bytes from input to output stream, leaving out stream open
	 * 
	 * @param in input stream
	 * @param out output stream
	 * @throws IOException on error
	 */
	public static void copyStreamNoCloseOut(InputStream in, OutputStream out) throws IOException {

		try {
			byte[] buf = new byte[2048];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
		} finally {
			in.close();
		}
	}


	/**
	 * Improved delete
	 * 
	 * @param path deleted path
	 * @param recursive recursive delete
	 * @return success
	 */
	public static boolean delete(File path, boolean recursive) {

		if (!path.exists()) {
			return true;
		}

		if (!recursive || !path.isDirectory()) return path.delete();

		String[] list = path.list();
		for (int i = 0; i < list.length; i++) {
			if (!delete(new File(path, list[i]), true)) return false;
		}

		return path.delete();
	}


	/**
	 * Read entire file to a string.
	 * 
	 * @param file file
	 * @return file contents
	 * @throws IOException
	 */
	public static String fileToString(File file) throws IOException {

		return streamToString(new FileInputStream(file));
	}


	/**
	 * Get files in a folder (create folder if needed)
	 * 
	 * @param dir folder
	 * @return list of files
	 */
	public static List<File> listDirectory(File dir) {

		return FileUtils.listDirectory(dir, null);
	}


	/**
	 * Get files in a folder (create folder if needed)
	 * 
	 * @param dir folder
	 * @param filter file filter
	 * @return list of files
	 */
	public static List<File> listDirectory(File dir, FileFilter filter) {

		try {
			dir.mkdir();
		} catch (RuntimeException e) {
			Log.e("Error creating folder " + dir, e);
		}

		List<File> list = new ArrayList<File>();

		try {
			for (File f : dir.listFiles(filter)) {
				list.add(f);
			}
		} catch (Exception e) {
			Log.e("Error listing folder " + dir, e);
		}

		return list;
	}


	/**
	 * Remove extension.
	 * 
	 * @param file file
	 * @return filename without extension
	 */
	public static String[] getFilenameParts(File file) {

		return getFilenameParts(file.getName());
	}


	public static String getExtension(File file) {

		return getExtension(file.getName());
	}


	public static String getExtension(String file) {

		return Utils.fromLastChar(file, '.');
	}


	/**
	 * Remove extension.
	 * 
	 * @param filename
	 * @return filename and extension
	 */
	public static String[] getFilenameParts(String filename) {

		String ext, name;

		try {
			ext = Utils.fromLastDot(filename);
		} catch (StringIndexOutOfBoundsException e) {
			ext = "";
		}

		try {
			name = Utils.toLastDot(filename);
		} catch (StringIndexOutOfBoundsException e) {
			name = "";
			Log.w("Error extracting extension from file " + filename);
			Utils.printStackTrace();
		}

		return new String[] { name, ext };
	}


	/**
	 * Read entire input stream to a string.
	 * 
	 * @param in input stream
	 * @return file contents
	 */
	public static String streamToString(InputStream in) {

		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(in));
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}

		} catch (IOException e) {
			Log.e(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					Log.e(e);
				}
			}
		}

		return sb.toString();
	}


	public static InputStream stringToStream(String text) {

		try {
			return new ByteArrayInputStream(text.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			Log.e(e);
			return null;
		}
	}


	public static InputStream getResource(String path) {

		return FileUtils.class.getResourceAsStream(path);
	}


	/**
	 * Save string to file
	 * 
	 * @param file file
	 * @param text string
	 * @throws IOException on error
	 */
	public static void stringToFile(File file, String text) throws IOException {

		PrintStream out = null;
		try {

			out = new PrintStream(new FileOutputStream(file));

			out.print(text);
		} finally {
			if (out != null) out.close();
		}
	}


	public static void deleteEmptyDirs(File base) {

		for (File f : listDirectory(base)) {
			if (!f.isDirectory()) continue;

			deleteEmptyDirs(f);

			List<File> children = listDirectory(f);
			if (children.size() == 0) {
				f.delete();
				continue;
			}
		}

	}


	/**
	 * Replace special characters with place holders in a filename.
	 * 
	 * @param filestring filename string
	 * @return escaped
	 */
	public static String escapeFileString(String filestring) {

		filestring = filestring.replace("{", "?");
		filestring = filestring.replace("}", "*");

		filestring = filestring.replace("?", "{LCB}");
		filestring = filestring.replace("*", "{RCB}");

		filestring = filestring.replace(".", "{DOT}");

		return filestring;
	}


	/**
	 * Unescape filename string obtained by escapeFileString().
	 * 
	 * @param filestring escaped string
	 * @return clean string
	 */
	public static String unescapeFileString(String filestring) {

		filestring = filestring.replace("{LCB}", "{");
		filestring = filestring.replace("{RCB}", "}");

		filestring = filestring.replace("{DOT}", ".");

		return filestring;
	}


	/**
	 * Escape filename, keeping the same extension
	 * 
	 * @param filename filename
	 * @return escaped
	 */
	public static String escapeFilename(String filename) {

		String[] parts = getFilenameParts(filename);

		return escapeFileString(parts[0]) + "." + parts[1];
	}


	/**
	 * Unescape filename, keeping the same extension
	 * 
	 * @param filename escaped filename
	 * @return unesaped
	 */
	public static String unescapeFilename(String filename) {

		String[] parts = getFilenameParts(filename);

		return unescapeFileString(parts[0]) + "." + parts[1];
	}


	/**
	 * Load assets from a zip file to the output directory
	 * 
	 * @param zipFile input zip (must contain an "assets" folder)
	 * @param outDir output dir for the files
	 * @return map of entries found
	 */
	public static Map<String, AssetEntry> loadAssetsFromZip(File zipFile, File outDir) {

		return loadAssetsFromZip(zipFile, outDir, null);
	}


	/**
	 * Load assets from a zip file to the output directory
	 * 
	 * @param zipFile input zip (must contain an "assets" folder)
	 * @param outDir output dir for the files
	 * @param assets the map which to use for string assets; NULL to make a new
	 *            map.
	 * @return map of entries found
	 */
	public static Map<String, AssetEntry> loadAssetsFromZip(File zipFile, File outDir, Map<String, AssetEntry> assets) {

		Log.f3("Extracting: " + zipFile);
		if (assets == null) assets = new LinkedHashMap<String, AssetEntry>();

		try {

			StringFilter filter = new StringFilter() {

				@Override
				public boolean accept(String path) {

					boolean ok = false;

					String ext = FileUtils.getExtension(path);
					EAsset type = EAsset.forExtension(ext);

					ok |= path.startsWith("assets");
					ok &= type.isAssetOrMeta();

					return ok;
				}
			};

			List<String> list = ZipUtils.extractZip(zipFile, outDir, filter);

			for (String s : list) {
				if (s.startsWith("assets")) {
					s = FileUtils.escapeFilename(s);
					String[] parts = FileUtils.getFilenameParts(s);
					String key = parts[0].replace('\\', '.');
					key = key.replace('/', '.');
					String ext = parts[1];
					EAsset type = EAsset.forExtension(ext);

					if (!type.isAsset()) {
						if (Config.LOG_ZIP_EXTRACTING) Log.f3("# excluding: " + s);
						continue;
					}

					assets.put(key, new AssetEntry(key, type));
				}
			}

			return assets;

		} catch (Exception e) {
			Log.e(e);

			return null; // success = false
		}
	}


	public static String getBasename(String name) {

		return Utils.toLastChar(Utils.fromLastChar(name, '/'), '.');
	}
}
