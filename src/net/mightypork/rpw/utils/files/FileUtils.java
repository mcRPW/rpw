package net.mightypork.rpw.utils.files;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
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
     * @param source      source file
     * @param target      target file
     * @param filter      filter accepting only files and dirs to be copied
     * @param filesCopied list into which all the target files will be added
     * @throws IOException on error
     */
    public static void copyDirectory(File source, File target, FileDirFilter filter, List<File> filesCopied) throws IOException {
        if (!source.exists()) return;

        if (source.isDirectory()) {
            if (filter != null && !filter.acceptDirectory(source)) {
                return;
            }

            if (!target.exists()) {
                target.mkdir();
            }

            final String[] children = source.list();
            for (final String element : children) {
                copyDirectory(new File(source, element), new File(target, element), filter, filesCopied);
            }

        } else {
            if (filter != null && !filter.acceptFile(source)) {
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
     * @param files  list of the found files
     * @throws IOException on error
     */
    public static void listDirectoryRecursive(File source, StringFilter filter, List<File> files) throws IOException {
        if (source.isDirectory()) {
            final String[] children = source.list();
            for (final String element : children) {
                listDirectoryRecursive(new File(source, element), filter, files);
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
        InputStream in = null;
        OutputStream out = null;

        try {
            in = new FileInputStream(source);
            out = new FileOutputStream(target);

            copyStream(in, out);
        } finally {
            Utils.close(in, out);
        }
    }


    /**
     * Copy bytes from input to output stream, leaving out stream open
     *
     * @param in  input stream
     * @param out output stream
     * @throws IOException on error
     */
    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        if (in == null) {
            throw new NullPointerException("Input stream is null");
        }

        if (out == null) {
            throw new NullPointerException("Output stream is null");
        }

        final byte[] buf = new byte[2048];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
    }


    public static boolean delete(File path, boolean recursive) {
        return delete(path, recursive, null, true);
    }


    public static boolean delete(File path, boolean recursive, boolean self) {
        return delete(path, recursive, null, self);
    }


    public static boolean delete(File path, boolean recursive, FileDirFilter filter) {
        return delete_do(path, recursive, filter, true, 0);
    }


    public static boolean delete(File path, boolean recursive, FileDirFilter filter, boolean self) {
        return delete_do(path, recursive, filter, self, 0);
    }


    private static boolean delete_do(File path, boolean recursive, FileDirFilter filter, boolean self, int depth) {
        if (filter != null) {
            if (path.isFile() && !filter.acceptFile(path)) return true;
            if (path.isDirectory() && !filter.acceptDirectory(path)) return true;
        }

        if (!path.exists()) {
            return true;
        }

        if (!recursive || !path.isDirectory()) {
            return path.delete();
        }

        final String[] list = path.list();

        // handle rare NPE crash (issue #62)
        if (list == null) return false;

        for (int i = 0; i < list.length; i++) {
            if (!delete_do(new File(path, list[i]), true, filter, self, depth + 1)) return false;
        }

        if (depth == 0) {
            if (self) return path.delete();
            else return true;
        } else {
            path.delete();
            return true;
        }
    }


    /**
     * Read entire file to a string.
     *
     * @param file file
     * @return file contents
     * @throws IOException
     */
    public static String fileToString(File file) throws IOException {
        final FileInputStream fin = new FileInputStream(file);
        return streamToString(fin);
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
     * @param dir    folder
     * @param filter file filter
     * @return list of files
     */
    public static List<File> listDirectory(File dir, FileFilter filter) {
        try {
            dir.mkdir();
        } catch (final RuntimeException e) {
            Log.e("Error creating folder " + dir, e);
        }

        final List<File> list = new ArrayList<File>();

        try {
            for (final File f : dir.listFiles(filter)) {
                list.add(f);
            }
        } catch (final Exception e) {
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
        } catch (final StringIndexOutOfBoundsException e) {
            ext = "";
        }

        try {
            name = Utils.toLastDot(filename);
        } catch (final StringIndexOutOfBoundsException e) {
            name = filename;
        }

        return new String[]{name, ext};
    }


    /**
     * Read entire input stream to a string, and close it.
     *
     * @param in input stream
     * @return file contents
     */
    public static String streamToString(InputStream in) {
        return streamToString(in, -1);
    }


    /**
     * Read input stream to a string, and close it.
     *
     * @param in    input stream
     * @param lines max number of lines (-1 to disable limit)
     * @return file contents
     */
    public static String streamToString(InputStream in, int lines) {
        if (in == null) {
            Log.e("Null stream to be converted to String, using empty instead.");
            return ""; // to avoid NPE's
        }

        BufferedReader br = null;
        final StringBuilder sb = new StringBuilder();

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

        } catch (final IOException e) {
            Log.e(e);
        } finally {
            Utils.close(br);
        }

        return sb.toString();
    }


    public static InputStream stringToStream(String text) {
        if (text == null) return null;

        try {
            return new ByteArrayInputStream(text.getBytes("UTF-8"));
        } catch (final UnsupportedEncodingException e) {
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
        for (final File f : listDirectory(base)) {
            if (!f.isDirectory()) continue;

            deleteEmptyDirs(f);

            final List<File> children = listDirectory(f);
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
        final StringBuilder sb = new StringBuilder();

        for (final char c : filestring.toCharArray()) {
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
        final String[] parts = getFilenameParts(filename);

        return escapeFileString(parts[0]) + "." + parts[1];
    }


    /**
     * Unescape filename, keeping the same extension
     *
     * @param filename escaped filename
     * @return unesaped
     */
    public static String unescapeFilename(String filename) {
        final String[] parts = getFilenameParts(filename);

        return unescapeFileString(parts[0]) + "." + parts[1];
    }


    /**
     * Load assets from a zip file to the output directory
     *
     * @param zipFile input zip (must contain an "assets" folder)
     * @param outDir  output dir for the files
     * @return map of entries found
     */
    public static Map<String, AssetEntry> loadAssetsFromZip(File zipFile, File outDir) {
        return loadAssetsFromZip(zipFile, outDir, null);
    }


    /**
     * Load assets from a zip file to the output directory
     *
     * @param file   input zip (must contain an "assets" folder)
     * @param outDir output dir for the files
     * @param assets the map which to use for string assets; NULL to make a new
     *               map.
     * @return map of entries found
     */
    public static Map<String, AssetEntry> loadAssetsFromZip(File file, File outDir, Map<String, AssetEntry> assets) {
        Log.f3("Extracting: " + file);
        if (assets == null) assets = new LinkedHashMap<String, AssetEntry>();

        try {
            final StringFilter filter = new StringFilter() {

                @Override
                public boolean accept(String path) {
                    boolean ok = false;

                    final String ext = FileUtils.getExtension(path);
                    final EAsset type = EAsset.forExtension(ext);

                    ok |= path.startsWith("assets");
                    ok &= type.isAssetOrMeta();
                    return ok;
                }
            };

            final List<String> list = ZipUtils.extractZip(file, outDir, filter);

            for (final String s : list) {
                if (s.startsWith("assets")) {
                    final String s2 = FileUtils.escapeFilename(s);
                    final String[] parts = FileUtils.getFilenameParts(s2);
                    final String key = parts[0].replace('\\', '.').replace('/', '.');
                    final String ext = parts[1];
                    final EAsset type = EAsset.forExtension(ext);

                    if (Config.LOG_EXTRACTED_ASSETS) Log.f3("+ " + s);

                    if (!type.isAsset()) {
                        continue;
                    }

                    final AssetEntry ae = new AssetEntry(key, type);

                    assets.put(key, ae);

                }
            }

            return assets;

        } catch (final Exception e) {
            Log.e("Error loading assets from zip: " + file.getName(), e);

            return null; // success = false
        }
    }


    /**
     * Copy object files to target paths / names from the Minecraft assets
     * directory
     *
     * @param indexFile   index file to be used
     * @param targetDir   output directory
     * @param filter      filename filter
     * @param filesCopied
     * @throws IOException
     */
    public static void extractObjectFiles(File indexFile, File targetDir, StringFilter filter, List<File> filesCopied) throws IOException {
        final File objectsDir = OsUtils.getMcDir("assets/objects");

        final String index_s = fileToString(indexFile);
        final FileObjectIndex index = FileObjectIndex.fromJson(index_s);

        // workaround for 1.6.4 weirdness
        boolean virtual = index.virtual;
        if (virtual) targetDir = new File(targetDir, "minecraft");

        for (final Entry<String, FileObject> entry : index.objects.entrySet()) {
            final String path = entry.getKey();
            final String hash = entry.getValue().hash;
            final int size = entry.getValue().size;

            if (!filter.accept(new File(path).getName())) {
                if (Config.LOG_EXTRACTED_ASSETS) Log.f3("Skipping file: " + path);
                continue;
            }

            final String hashPrefix = hash.substring(0, 2);

            final File source = new File(objectsDir + "/" + hashPrefix + "/" + hash);

            if (!source.exists()) {
                Log.w("Object '" + hash + "' does not exist, skipping.");
                continue;
            }

            if (source.length() != size) {
                Log.w("Object '" + hash + "' has wrong size.");
            }

            final File target = new File(targetDir, path);
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


    /**
     * Copy resource to file
     *
     * @param resname resource name
     * @param file    out file
     * @throws IOException
     */
    public static void resourceToFile(String resname, File file) throws IOException {
        InputStream in = null;
        OutputStream out = null;

        try {
            in = FileUtils.getResource(resname);
            out = new FileOutputStream(file);

            FileUtils.copyStream(in, out);
        } finally {
            Utils.close(in, out);
        }

    }


    /**
     * Get resource as string, safely closing streams.
     *
     * @param resname resource name
     * @return resource as string, empty string on failure
     */
    public static String resourceToString(String resname) {
        final InputStream in = FileUtils.getResource(resname);
        return streamToString(in);
    }

    /**
     * Ignore git directory
     */
    public static final FileDirFilter NoGitFilter = new FileDirFilter() {

        @Override
        public boolean acceptFile(File f) {
            return true;
        }


        @Override
        public boolean acceptDirectory(File f) {
            return !f.getName().equals(".git");
        }
    };
}
