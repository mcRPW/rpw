package net.mightypork.rpw.gui.helpers;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import net.mightypork.rpw.Config;
import net.mightypork.rpw.Config.FilePath;
import net.mightypork.rpw.utils.validation.FileSuffixFilter;


/**
 * RPW wrapper for JFileChooser
 *
 * @author Ondřej Hruška (MightyPork)
 */
public class FileChooser {

    private final JFileChooser fc;
    private final Component parent;
    private int state;
    private final FilePath pathEnum;

    private static final int CANCEL = JFileChooser.CANCEL_OPTION;
    private static final int APPROVE = JFileChooser.APPROVE_OPTION;
    private static final int ERROR = JFileChooser.ERROR_OPTION;

    public static final FileChooserFilter ZIP = new FileChooserFilter("ZIP archives", "zip");
    public static final FileChooserFilter ZIP_JAR = new FileChooserFilter("ZIP, JAR archives", "zip,jar");
    public static final FileChooserFilter PNG = new FileChooserFilter("PNG images", "png");
    public static final FileChooserFilter JPG = new FileChooserFilter("JPEG images", "jpg,jpeg");
    public static final FileChooserFilter OGG = new FileChooserFilter("OGG sounds", "ogg");
    public static final FileChooserFilter TXT = new FileChooserFilter("Text files", "txt,text,lang,json,properties,cfg,ini,conf");
    public static final FileChooserFilter VSH = new FileChooserFilter("Vertex shaders", "vsh");
    public static final FileChooserFilter FSH = new FileChooserFilter("Fragment shaders", "fsh");
    public static final FileChooserFilter FOLDERS = new FolderChooserFilter();
    public static final FileChooserFilter FOLDERS_ZIP = new FolderZipChooserFilter();


    /**
     * A filechooser
     *
     * @param parent   parent component (frame, dialog...)
     * @param pathEnum file path (used for remembering last path. Use DEFAULT for
     *                 User's home)
     * @param title    dialog window title
     * @param filter   file filter
     * @param files    allow choosing files
     * @param dirs     allow choosing directories
     * @param multi    allow multiple selection
     */
    public FileChooser(Component parent, FilePath pathEnum, String title, FileChooserFilter filter, boolean files, boolean dirs, boolean multi) {
        this.parent = parent;
        this.pathEnum = pathEnum;

        fc = new JFileChooser();
        fc.setCurrentDirectory(new File(pathEnum.getPath()));
        fc.setDialogTitle(title);

        if (filter != null) {
            fc.setAcceptAllFileFilterUsed(false);
            fc.setFileFilter(filter);
        }

        fc.setFileSelectionMode((files && dirs) ? JFileChooser.FILES_AND_DIRECTORIES : (files ? JFileChooser.FILES_ONLY : JFileChooser.DIRECTORIES_ONLY));
        fc.setMultiSelectionEnabled(multi);
        fc.setFileHidingEnabled(!Config.SHOW_HIDDEN_FILES);
    }


    /**
     * Change dialog title
     *
     * @param title new title
     */
    public void setTitle(String title) {
        fc.setDialogTitle(title);
    }


    /**
     * Show dialog with "Open" button
     */
    public void showOpenDialog() {
        this.state = fc.showOpenDialog(getParent());
        rememberPath();
    }


    /**
     * Show dialog woth "Save" button
     */
    public void showSaveDialog() {
        this.state = fc.showSaveDialog(getParent());
        rememberPath();
    }


    /**
     * Show dialog with custom OK button text
     *
     * @param approveButtonText OK button text
     */
    public void showDialog(String approveButtonText) {
        this.state = fc.showDialog(getParent(), approveButtonText);
        rememberPath();
    }


    /**
     * Store path to the config file
     */
    private void rememberPath() {
        this.pathEnum.savePath(fc.getCurrentDirectory().getPath());
    }


    /**
     * @return file selected in filechooser
     */
    public File getSelectedFile() {
        return fc.getSelectedFile();
    }


    /**
     * @return files selected in filechooser (may be null or empty)
     */
    public File[] getSelectedFiles() {
        return fc.getSelectedFiles();
    }


    /**
     * Select file
     *
     * @param file file to set
     */
    public void setSelectedFile(File file) {
        fc.setSelectedFile(file);
    }


    /**
     * @return true if the dialog was closed with "OK"
     */
    public boolean approved() {
        return state == APPROVE;
    }


    /**
     * @return true if the dialog window was closed or "Cancel" was pressed, or
     * an error occurred.
     */
    public boolean canceled() {
        return state == CANCEL || state == ERROR;
    }


    /**
     * @return filechooser current directory
     */
    public File getCurrentDirectory() {
        return fc.getCurrentDirectory();
    }


    protected Component getParent() {
        return parent;
    }

    public static class FolderChooserFilter extends FileChooserFilter {
        public FolderChooserFilter() {
            super("Folders", "");
        }


        @Override
        public String getDescription() {
            return "Folders";
        }
    }

    public static class FolderZipChooserFilter extends FileChooserFilter {
        public FolderZipChooserFilter() {
            super("Folders & Zip files", "");
        }


        @Override
        public String getDescription() {
            return "Folders & Zip files";
        }
    }

    public static class FileChooserFilter extends FileFilter {

        private final FileSuffixFilter fsf;
        private final String name;

        public FileChooserFilter(String name, String suffixes) {
            this.name = name;
            this.fsf = new FileSuffixFilter(suffixes.split(","));
        }


        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) return true;
            return fsf.accept(f);
        }


        @Override
        public String getDescription() {
            return name;
        }

    }
}
