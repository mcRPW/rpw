package net.mightypork.rpw.utils.files;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.mightypork.rpw.utils.Utils;
import net.mightypork.rpw.utils.validation.StringFilter;


/**
 * Utilities for manipulating zip files
 *
 * @author Ondřej Hruška (MightyPork)
 */
public class ZipUtils {

    private static final int BUFFER_SIZE = 2048;


    /**
     * Extract zip file to target directory
     *
     * @param file      zip file
     * @param outputDir target directory
     * @param filter    string filter (will be used to test entry names (paths))
     * @return list of entries extracted (paths)
     * @throws IOException
     */
    public static List<String> extractZip(File file, File outputDir, StringFilter filter) throws IOException {
        ZipFile zip = null;
        try {
            zip = new ZipFile(file);

            return extractZip(zip, outputDir, filter);

        } finally {
            Utils.close(zip);
        }
    }


    /**
     * Extract zip file to target directory
     *
     * @param zip       open zip file
     * @param outputDir target directory
     * @param filter    string filter (will be used to test entry names (paths))
     * @return list of entries extracted (paths)
     * @throws IOException
     */
    public static List<String> extractZip(ZipFile zip, File outputDir, StringFilter filter) throws IOException {
        final ArrayList<String> files = new ArrayList<String>();

        outputDir.mkdirs();

        final Enumeration<? extends ZipEntry> zipFileEntries = zip.entries();

        // process each entry
        while (zipFileEntries.hasMoreElements()) {
            final ZipEntry entry = zipFileEntries.nextElement();

            // parse filename and path
            final String entryPath = entry.getName();
            final File destFile = new File(outputDir, entryPath);
            final File destinationParent = destFile.getParentFile();

            if (entry.isDirectory() || (filter != null && !filter.accept(entryPath))) continue;

            // make sure directories exist
            destinationParent.mkdirs();

            if (!entry.isDirectory()) {
                extractZipEntry(zip, entry, destFile);
                files.add(entryPath);
            }
        }

        return files;
    }


    /**
     * Read zip entries and add their paths to a list
     *
     * @param zipFile open zip file
     * @return list of entry names
     * @throws IOException on error
     */
    public static List<String> listZip(File zipFile) throws IOException {
        ZipFile zip = null;
        try {
            zip = new ZipFile(zipFile);
            return listZip(zip);
        } finally {
            Utils.close(zip);
        }
    }


    /**
     * Read zip entries and add their paths to a list
     *
     * @param zip open zip file
     * @return list of entry names
     * @throws IOException on error
     */
    public static List<String> listZip(ZipFile zip) throws IOException {
        final ArrayList<String> files = new ArrayList<String>();

        final Enumeration<? extends ZipEntry> zipFileEntries = zip.entries();

        // process each entry
        while (zipFileEntries.hasMoreElements()) {
            final ZipEntry entry = zipFileEntries.nextElement();

            if (!entry.isDirectory()) {
                files.add(entry.getName());
            }
        }

        return files;
    }


    /**
     * Extract one zip entry to target file
     *
     * @param zip      open zip file
     * @param entry    entry from the zip file
     * @param destFile destination file ((NOT directory!)
     * @throws IOException on error
     */
    public static void extractZipEntry(ZipFile zip, ZipEntry entry, File destFile) throws IOException {
        destFile.getParentFile().mkdirs();

        BufferedInputStream is = null;
        FileOutputStream fos = null;
        BufferedOutputStream dest = null;

        try {
            is = new BufferedInputStream(zip.getInputStream(entry));
            fos = new FileOutputStream(destFile);
            dest = new BufferedOutputStream(fos, BUFFER_SIZE);

            FileUtils.copyStream(is, dest);
        } finally {
            Utils.close(is, dest); // closes also fos
        }
    }


    /**
     * Load zip entry to String
     *
     * @param zip   open zip file
     * @param entry entry from the zip file
     * @return loaded string
     * @throws IOException on error
     */
    public static String zipEntryToString(ZipFile zip, ZipEntry entry) throws IOException {
        BufferedInputStream is = null;
        try {
            is = new BufferedInputStream(zip.getInputStream(entry));
            final String s = FileUtils.streamToString(is);
            return s;
        } finally {
            Utils.close(is);
        }
    }


    public static boolean entryExists(File selectedFile, String string) {
        ZipFile zf = null;

        try {
            zf = new ZipFile(selectedFile);
            return zf.getEntry(string) != null;
        } catch (final Exception e) {
            return false;
        } finally {
            Utils.close(zf);
        }

    }
}
