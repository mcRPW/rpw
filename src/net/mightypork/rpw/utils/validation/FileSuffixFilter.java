package net.mightypork.rpw.utils.validation;

import java.io.File;
import java.io.FileFilter;


/**
 * File filter for certain suffixes
 *
 * @author Ondřej Hruška (MightyPork)
 */
public class FileSuffixFilter implements FileFilter {

    /**
     * Array of allowed suffixes
     */
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
        if (!pathname.isFile()) return false;

        final String fname = pathname.getName().toLowerCase().trim();

        for (final String suffix : suffixes) {
            if (fname.endsWith(suffix.toLowerCase().trim())) {
                return true;
            }
        }

        return false;
    }

}
