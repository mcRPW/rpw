package net.mightypork.rpack.utils;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import net.mightypork.rpack.utils.filters.StringFilter;


public class ZipUtils {

	private static final int BUFFER_SIZE = 2048;


	public static List<String> extractZip(File zipFile, File outputDir, StringFilter filter) throws ZipException, IOException {

		outputDir.mkdirs();

		ArrayList<String> files = new ArrayList<String>();

		ZipFile zip = new ZipFile(zipFile);

		Enumeration<? extends ZipEntry> zipFileEntries = zip.entries();

		// process each entry
		while (zipFileEntries.hasMoreElements()) {

			ZipEntry entry = zipFileEntries.nextElement();


			// parse filename and path
			String entryPath = entry.getName();
			File destFile = new File(outputDir, entryPath);
			File destinationParent = destFile.getParentFile();

			if (entry.isDirectory() || (filter != null && !filter.accept(entryPath))) continue;


			// make sure directories exist
			destinationParent.mkdirs();

			if (!entry.isDirectory()) {
				BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry));

				byte data[] = new byte[BUFFER_SIZE];

				FileOutputStream fos = new FileOutputStream(destFile);
				BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER_SIZE);

				int b;
				while ((b = is.read(data, 0, BUFFER_SIZE)) != -1) {
					dest.write(data, 0, b);
				}
				dest.flush();
				dest.close();
				is.close();

				files.add(entryPath);
			}
		}

		return files;
	}


	public static List<String> listZip(File zipFile) throws ZipException, IOException {

		ArrayList<String> files = new ArrayList<String>();

		ZipFile zip = new ZipFile(zipFile);

		Enumeration<? extends ZipEntry> zipFileEntries = zip.entries();

		// process each entry
		while (zipFileEntries.hasMoreElements()) {
			ZipEntry entry = zipFileEntries.nextElement();

			if (!entry.isDirectory()) {
				files.add(entry.getName());
			}
		}

		return files;
	}
}
