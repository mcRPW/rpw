package net.mightypork.rpw.tasks;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Config;
import net.mightypork.rpw.gui.windows.Alerts;
import net.mightypork.rpw.library.MagicSources;
import net.mightypork.rpw.library.Sources;
import net.mightypork.rpw.project.Project;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.struct.PackInfo;
import net.mightypork.rpw.struct.PackInfoMap;
import net.mightypork.rpw.tree.assets.tree.AssetTreeLeaf;
import net.mightypork.rpw.tree.assets.tree.AssetTreeNode;
import net.mightypork.rpw.tree.assets.tree.AssetTreeProcessor;
import net.mightypork.rpw.utils.FileUtils;
import net.mightypork.rpw.utils.Log;
import net.mightypork.rpw.utils.ZipBuilder;
import net.mightypork.rpw.utils.validation.FileSuffixFilter;


public class TaskExportProject {

	public static void showDialog() {

		if (Projects.getActive() == null) return;

		initFileChooser();

		int opt = fc.showDialog(App.getFrame(), "Export");
		if (opt != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File f = fc.getSelectedFile();

		if (f.exists()) {
			//@formatter:off
			int overwrite = Alerts.askYesNoCancel(
					App.getFrame(),
					"File Exists",
					"File \"" + f.getName() + "\" already exists.\n" +
					"Do you want to overwrite it?"
			);
			//@formatter:on

			if (overwrite != JOptionPane.YES_OPTION) return;
		}

		Tasks.taskExportProject(f, new Runnable() {

			@Override
			public void run() {

				Alerts.info(App.getFrame(), "Export successful.");
			}
		});
	}


	public static void doExport(File target) throws Exception {

		Project project = Projects.getActive();

		final ZipBuilder zb = new ZipBuilder(target);

		InputStream in = null;

		File f;

		// Add includes

		if (Config.LOG_EXPORT) {
			Log.f2("Adding included extra files");
		}

		try {
			File extrasDir = project.getExtrasDirectory();

			List<File> extras = new ArrayList<File>();

			FileUtils.listDirectoryRecursive(extrasDir, null, extras);

			for (File file : extras) {

				if (!file.isFile()) return;

				String path = file.getAbsolutePath();
				path = path.replace(extrasDir.getAbsolutePath(), "");

				in = new FileInputStream(file);

				zb.addStream(path, in);

				if (Config.LOG_EXPORT) {
					Log.f3(path);
				}
			}
		} catch (Throwable t) {
			Log.e("Error when including extras.", t);
		}


		if (Config.LOG_EXPORT) {
			Log.f2("Adding project files");
		}

		// pack.png
		try {

			f = new File(project.getProjectDirectory(), "pack.png");
			if (f.exists()) {
				in = new FileInputStream(f);
			} else {
				in = FileUtils.getResource("/data/export/pack.png");
			}
			zb.addStream("pack.png", in);

		} finally {
			if (in != null) in.close();
		}


		// readme.txt
		zb.addResource("readme.txt", "/data/export/pack-readme.txt");


		// json mcmeta
		String desc = project.getProjectName();

		PackInfoMap pim = new PackInfoMap();
		pim.setPackInfo(new PackInfo(1, desc));

		zb.addString("pack.mcmeta", pim.toJson());


		// assets
		AssetTreeProcessor processor = new AssetTreeProcessor() {

			@Override
			public void process(AssetTreeNode node) {

				if (node instanceof AssetTreeLeaf) {

					AssetTreeLeaf leaf = (AssetTreeLeaf) node;

					String logSrcAsset = null;
					String logSrcMeta = null;

					// file
					do {
						String srcName = leaf.resolveAssetSource();
						if (srcName == null) break;
						if (MagicSources.isVanilla(srcName)) break;
						if (MagicSources.isInherit(srcName)) break;

						InputStream data = null;

						try {

							try {
								data = Sources.getAssetStream(srcName, leaf.getAssetKey());
								if (data == null) break;

								String path = leaf.getAssetEntry().getPath();

								zb.addStream(path, data);
								logSrcAsset = srcName;
							} finally {
								if (data != null) {
									data.close();
								}
							}

						} catch (IOException e) {
							Log.e("Error getting asset stream.", e);
						}

					} while (false);

					// meta
					do {
						String srcName = node.resolveAssetMetaSource();
						if (srcName == null) break;
						if (MagicSources.isVanilla(srcName)) break;
						if (MagicSources.isInherit(srcName)) break;

						InputStream data = null;

						try {

							try {
								data = Sources.getAssetMetaStream(srcName, leaf.getAssetKey());
								if (data == null) {
									Log.w("null meta stream");
									break;
								}

								String path = leaf.getAssetEntry().getPath() + ".mcmeta";

								zb.addStream(path, data);
								logSrcMeta = srcName;
							} finally {
								if (data != null) {
									data.close();
								}
							}

						} catch (IOException e) {
							Log.e("Error getting asset meta stream.", e);
						}

					} while (false);


					if (Config.LOG_EXPORT) {
						if (logSrcAsset != null) {
							String s = leaf.getAssetKey();
							s += " <- \"" + logSrcAsset + "\"";

							if (logSrcMeta != null) s += ", m: \"" + logSrcMeta + "\"";

							Log.f3(s);
						}
					}

				}
			}
		};


		AssetTreeNode root = App.getTreeDisplay().treeModel.getRoot();
		root.processThisAndChildren(processor);

		zb.close();
	}

	private static JFileChooser fc = null;


	private static void initFileChooser() {

		Project project = Projects.getActive();

		if (fc == null) fc = new JFileChooser();

		fc.setAcceptAllFileFilterUsed(false);
		fc.setDialogTitle("Export project");
		fc.setFileFilter(new FileFilter() {

			FileSuffixFilter fsf = new FileSuffixFilter("zip");


			@Override
			public String getDescription() {

				return "ZIP archives";
			}


			@Override
			public boolean accept(File f) {

				if (f.isDirectory()) return true;
				return fsf.accept(f);
			}
		});

		fc.setSelectedFile(null);
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(false);
		fc.setFileHidingEnabled(!Config.SHOW_HIDDEN_FILES);

		File dir = fc.getCurrentDirectory();
		File file = new File(dir, project.getDirName() + ".zip");
		fc.setSelectedFile(file);
	}
}
