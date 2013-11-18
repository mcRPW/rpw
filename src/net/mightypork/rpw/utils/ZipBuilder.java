package net.mightypork.rpw.utils;


import java.io.*;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class ZipBuilder {

	private ZipOutputStream out;
	private HashSet<String> included = new HashSet<String>();


	public ZipBuilder(File target) throws FileNotFoundException {

		target.getParentFile().mkdirs();

		FileOutputStream dest = new FileOutputStream(target);
		out = new ZipOutputStream(new BufferedOutputStream(dest));
	}


	public void addStream(String path, InputStream in) throws IOException {

		path = preparePath(path);
		if (included.contains(path)) return; // ignore
		included.add(path);

		//System.out.println("Adding path: " + path);

		out.putNextEntry(new ZipEntry(path));

		FileUtils.copyStreamNoCloseOut(in, out);
	}


	public void addString(String path, String text) throws IOException {

		path = preparePath(path);
		if (included.contains(path)) return; // ignore
		included.add(path);

		//System.out.println("Adding path: " + path);

		out.putNextEntry(new ZipEntry(path));

		InputStream in = FileUtils.stringToStream(text);
		FileUtils.copyStreamNoCloseOut(in, out);
	}


	public void addResource(String path, String resPath) throws IOException {

		path = preparePath(path);
		if (included.contains(path)) return; // ignore
		included.add(path);

		//System.out.println("Adding path: " + path);

		out.putNextEntry(new ZipEntry(path));

		InputStream in = FileUtils.getResource(resPath);
		FileUtils.copyStreamNoCloseOut(in, out);
	}


	private String preparePath(String path) {

		path = path.replace("\\", "/");

		if (path.charAt(0) == '/') path = path.substring(1);

		return path;
	}


	public void close() throws IOException {

		out.close();
	}
}
