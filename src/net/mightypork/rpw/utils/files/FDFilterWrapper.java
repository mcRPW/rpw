package net.mightypork.rpw.utils.files;

import java.io.File;
import java.io.FileFilter;


public class FDFilterWrapper implements FileFilter {
    private final FileDirFilter fdf;


    public FDFilterWrapper(FileDirFilter fdf) {
        this.fdf = fdf;
    }


    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) return fdf.acceptDirectory(f);
        return fdf.acceptFile(f);
    }

    public static final FileFilter ACCEPT_ALL = new FileFilter() {

        @Override
        public boolean accept(File pathname) {
            return true;
        }
    };
}
