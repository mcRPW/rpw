package net.mightypork.rpw.utils.files;


import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.mightypork.rpw.Config;
import net.mightypork.rpw.struct.FileObject;
import net.mightypork.rpw.struct.FileObjectIndex;
import net.mightypork.rpw.tree.assets.AssetEntry;
import net.mightypork.rpw.tree.assets.EAsset;
import net.mightypork.rpw.utils.Utils;
import net.mightypork.rpw.utils.logging.Log;
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

		copyDirectory(source, target, null, null);
	}


	/**
	 * Copy directory recursively - advanced variant.
	 * 
	 * @param source source file
	 * @param target target file
	 * @param filter filter accepting only files and dirs to be copied
	 * @param filesCopied list into which all the target files will be added
	 * @throws IOException on error
	 */
	public static void copyDirectory(File source, File target, FileFilter filter, List<File> filesCopied) throws IOException {

		if (!source.exists()) return;

		if (source.isDirectory()) {

			if (!target.exists()) {
				target.mkdir();
			}

			String[] children = source.list();
			for (int i = 0; i < children.length; i++) {
				copyDirectory(new File(source, children[i]), new File(target, children[i]), filter, filesCopied);
			}

		} else {
			if (filter != null && !filter.accept(source)) {
				return;
			}

			if (filesCopied != null) filesCopied.add(target);
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

		return streamToString(in, -1);
	}


	/**
	 * Read input stream to a string.
	 * 
	 * @param in input stream
	 * @param lines max number of lines (-1 to disable limit)
	 * @return file contents
	 */
	public static String streamToString(InputStream in, int lines) {

		if (in == null) {
			Log.e(new NullPointerException("Null stream to be converted to String."));
			return ""; // to avoid NPE's
		}

		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			int cnt = 0;
			br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			while ((line = br.readLine()) != null && (cnt < lines || lines <= 0)) {
				sb.append(line + "\n");
				cnt++;
			}

			if (cnt == lines && lines > 0) {
				sb.append("--- end of preview ---\n");
			}

		} catch (IOException e) {
			Log.e(e);
		} finally {
			try {
				if (br != null) br.close();
			} catch (IOException e) {
				// piss off
			}
		}

		return sb.toString();
	}


	public static InputStream stringToStream(String text) {

		if (text == null) return null;

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


	public static String getResourceAsString(String path) {

		return streamToString(FileUtils.class.getResourceAsStream(path));
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

			out = new PrintStream(new FileOutputStream(file), false, "UTF-8");

			out.print(text);

			out.flush();

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

		StringBuilder sb = new StringBuilder();

		for (char c : filestring.toCharArray()) {

			switch (c) {
				case '%':
					sb.append("%%");
					break;

				case '.':
					sb.append("%d");
					break;

				default:
					sb.append(c);
			}

		}

		return sb.toString();
	}


	/**
	 * Unescape filename string obtained by escapeFileString().
	 * 
	 * @param filestring escaped string
	 * @return clean string
	 */
	public static String unescapeFileString(String filestring) {

		filestring = filestring.replace("%d", ".");
		filestring = filestring.replace("%%", "%");

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
	 * @param file input zip (must contain an "assets" folder)
	 * @param outDir output dir for the files
	 * @param assets the map which to use for string assets; NULL to make a new
	 *            map.
	 * @return map of entries found
	 */
	public static Map<String, AssetEntry> loadAssetsFromZip(File file, File outDir, Map<String, AssetEntry> assets) {

		Log.f3("Extracting: " + file);
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

			List<String> list = ZipUtils.extractZip(file, outDir, filter);

			for (String s : list) {
				if (s.startsWith("assets")) {
					String s2 = FileUtils.escapeFilename(s);
					String[] parts = FileUtils.getFilenameParts(s2);
					String key = parts[0].replace('\\', '.').replace('/', '.');
					String ext = parts[1];
					EAsset type = EAsset.forExtension(ext);

					if (Config.LOG_EXTRACTED_ASSETS) Log.f3("+ " + s);

					if (!type.isAsset()) {
						continue;
					}

					AssetEntry ae = new AssetEntry(key, type);

					assets.put(key, ae);

				}
			}

			return assets;

		} catch (Exception e) {
			Log.e("Error loading assets from zip: "+file.getName(), e);

			return null; // success = false
		}
	}


	/**
	 * Copy object files to target paths / names from the Minecraft assets
	 * directory
	 * 
	 * @param indexFile index file to be used
	 * @param targetDir output directory
	 * @param filter filename filter
	 * @param filesCopied
	 * @throws IOException
	 */
	public static void extractObjectFiles(File indexFile, File targetDir, StringFilter filter, List<File> filesCopied) throws IOException {

		File objectsDir = OsUtils.getMcDir("assets/objects");

		String index_s = fileToString(indexFile);
		FileObjectIndex index = FileObjectIndex.fromJson(index_s);
		for (Entry<String, FileObject> entry : index.objects.entrySet()) {
			String path = entry.getKey();
			String hash = entry.getValue().hash;
			int size = entry.getValue().size;

			if (!filter.accept(new File(path).getName())) {
				if (Config.LOG_EXTRACTED_ASSETS) Log.f3("Skipping file: " + path);
				continue;
			}

			String hashPrefix = hash.substring(0, 2);

			File source = new File(objectsDir + "/" + hashPrefix + "/" + hash);

			if (!source.exists()) {
				Log.w("Object '" + hash + "' does not exist, skipping.");
				continue;
			}

			if (source.length() != size) {
				Log.w("Object '" + hash + "' has wrong size.");
			}

			File target = new File(targetDir, path);
			target.getParentFile().mkdirs();

			copyFile(source, target);

			filesCopied.add(target);
		}
	}


	public static String getBasename(String name) {

		return Utils.toLastChar(Utils.fromLastChar(name, '/'), '.');
	}


	public static String getFilename(String name) {

		return Utils.fromLastChar(name, '/');
	}
}
