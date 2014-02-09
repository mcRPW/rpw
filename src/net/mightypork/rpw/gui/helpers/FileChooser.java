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
 * @author MightyPork
 */
public class FileChooser {

	private JFileChooser fc;
	private Component parent;
	private int state;
	private FilePath pathEnum;

	public static final int CANCEL = JFileChooser.CANCEL_OPTION;
	public static final int APPROVE = JFileChooser.APPROVE_OPTION;
	public static final int ERROR = JFileChooser.ERROR_OPTION;


	/**
	 * A filechooser
	 * 
	 * @param parent parent component (frame, dialog...)
	 * @param pathEnum file path (used for remembering last path. Use DEFAULT
	 *            for User's home)
	 * @param title dialog window title
	 * @param filterSuffixes file suffixes allowed (comma separated)
	 * @param filterName name of the filter (ie. "Zip files")
	 * @param files allow choosing files
	 * @param dirs allow choosing directories
	 * @param multi allow multiple selection
	 */
	public FileChooser(Component parent, FilePath pathEnum, String title, String filterSuffixes, final String filterName, boolean files, boolean dirs, boolean multi) {

		this.parent = parent;
		this.pathEnum = pathEnum;

		fc = new JFileChooser();
		fc.setCurrentDirectory(new File(pathEnum.getPath()));
		fc.setDialogTitle(title);

		final String[] suffixes = filterSuffixes.split(",");

		fc.setAcceptAllFileFilterUsed(false);
		fc.setFileFilter(new FileFilter() {

			FileSuffixFilter fsf = new FileSuffixFilter(suffixes);


			@Override
			public String getDescription() {

				return filterName;
			}


			@Override
			public boolean accept(File f) {

				if (f.isDirectory()) return true;
				return fsf.accept(f);
			}
		});

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

		this.state = fc.showOpenDialog(parent);
		rememberPath();
	}


	/**
	 * Show dialog woth "Save" button
	 */
	public void showSaveDialog() {

		this.state = fc.showSaveDialog(parent);
		rememberPath();
	}


	/**
	 * Show dialog with custom OK button text
	 * 
	 * @param approveButtonText OK button text
	 */
	public void showDialog(String approveButtonText) {

		this.state = fc.showDialog(parent, approveButtonText);
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
	 *         an error occurred.
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
}
