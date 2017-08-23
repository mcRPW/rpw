package net.mightypork.rpw.utils.files;

import java.io.File;


public interface FileDirFilter {
    boolean acceptFile(File f);


    boolean acceptDirectory(File f);
}
