package net.mightypork.rpack.utils.filters;


import java.io.File;
import java.io.FileFilter;


/**
 * File filter for certain suffixes
 * 
 * @author MightyPork
 */
public class FileSuffixFilter implements FileFilter {

	/** Array of allowed suffixes */
	private String[] suffixes = null;


	/**
	 * Suffix filter
	 * 
	 * @param suffixes var-args allowed suffixes, case insensitive
	 */
	public FileSuffixFilter(String... suffixes) {

		this.suffixes = suffixes;
	}


	@Override
	public boolean accept(File pathname) {

		for (String suffix : suffixes) {
			return pathname.isFile() && pathname.getName().toLowerCase().trim().endsWith(suffix.toLowerCase().trim());
		}
		return false;
	}

}
