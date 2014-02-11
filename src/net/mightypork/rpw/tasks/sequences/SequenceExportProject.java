package net.mightypork.rpw.tasks.sequences;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Config;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.library.MagicSources;
import net.mightypork.rpw.library.Sources;
import net.mightypork.rpw.project.Project;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.struct.PackInfo;
import net.mightypork.rpw.struct.PackMcmeta;
import net.mightypork.rpw.tree.assets.TreeBuilder;
import net.mightypork.rpw.tree.assets.tree.AssetTreeLeaf;
import net.mightypork.rpw.tree.assets.tree.AssetTreeNode;
import net.mightypork.rpw.tree.assets.tree.AssetTreeProcessor;
import net.mightypork.rpw.utils.files.FileUtils;
import net.mightypork.rpw.utils.files.ZipBuilder;
import net.mightypork.rpw.utils.logging.Log;


public class SequenceExportProject extends AbstractMonitoredSequence {

	private File target;
	private ZipBuilder zb;
	private Project project;
	private Runnable successRunnable;


	public SequenceExportProject(File target, Runnable onSuccess) {

		this.target = target;
		this.successRunnable = onSuccess;

		this.project = Projects.getActive();

	}


	@Override
	protected String getMonitorHeading() {

		return "Exporting project";
	}


	@Override
	public int getStepCount() {

		return 6;
	}


	@Override
	public String getStepName(int step) {

		//@formatter:off
		switch (step) {
			case 0: return "Preparing zip builder.";
			case 1: return "Exporting included extra files.";
			case 2: return "Exporting custom sounds.";
			case 3: return "Exporting custom languages.";
			case 4: return "Exporting configuration files.";
			case 5: return "Exporting project assets.";
		}
		//@formatter:on

		return null;
	}


	@Override
	protected boolean step(int step) {

		try {

			//@formatter:off
			switch (step) {
				case 0: return stepPrepareOutput();
				case 1: return stepAddIncludedExtras();
				case 2: return stepAddCustomSounds();
				case 3: return stepAddCustomLanguages();
				case 4: return stepAddConfigFiles();
				case 5: return stepExportProjectAssets();
			}
			//@formatter:on

		} catch (IOException e) {
			Log.e(e);
			return false;
		}

		return false;
	}


	private boolean stepPrepareOutput() throws FileNotFoundException {

		zb = new ZipBuilder(target);

		return true;
	}


	private boolean stepAddIncludedExtras() {

		try {
			File dir = project.getExtrasDirectory();
			addDirectoryToZip(dir, "");
		} catch (Throwable t) {
			Log.e("Error when including extras.", t);
			return false;
		}

		return true;
	}


	private boolean stepAddCustomSounds() throws IOException {

		File dir = project.getCustomSoundsDirectory();
		addDirectoryToZip(dir, "assets/minecraft/sounds");

		return true;
	}


	private boolean stepAddCustomLanguages() throws IOException {

		File dir = project.getCustomLangDirectory();
		addDirectoryToZip(dir, "assets/minecraft/lang");

		return true;
	}


	private boolean stepAddConfigFiles() throws IOException {

		// pack.png
		if (Config.LOG_EXPORT_FILES) Log.f3("+ pack.png");
		InputStream in = null;
		try {
			File f = new File(project.getProjectDirectory(), "pack.png");
			if (f.exists()) {
				in = new FileInputStream(f);
			} else {
				in = FileUtils.getResource("/data/export/pack.png");
			}
			zb.addStream("pack.png", in);

		} finally {
			if (in != null) in.close();
		}


		if (Config.LOG_EXPORT_FILES) Log.f3("+ readme.txt");
		zb.addResource("readme.txt", "/data/export/pack-readme.txt");


		if (Config.LOG_EXPORT_FILES) Log.f3("+ assets/minecraft/sounds.json");
		zb.addString("assets/minecraft/sounds.json", project.getSoundsMap().toJson());


		// json mcmeta
		if (Config.LOG_EXPORT_FILES) Log.f3("+ pack.mcmeta");
		String desc = project.getTitle();

		PackMcmeta pim = new PackMcmeta();
		pim.setPackInfo(new PackInfo(1, desc));

		zb.addString("pack.mcmeta", pim.toJson());

		return true;
	}


	private boolean stepExportProjectAssets() {

		Log.f2("Adding project asset files.");

		AssetTreeProcessor processor = new ExportProcessor();

		AssetTreeNode root = new TreeBuilder().buildTreeForExport(Projects.getActive());

		root.processThisAndChildren(processor);

		return true;
	}


	@Override
	protected void doBefore() {

		Alerts.loading(true);

		Log.f1("Exporting project \"" + project.getTitle() + "\" to " + target);

	}


	@Override
	protected void doAfter(boolean success) {

		Alerts.loading(false);

		try {
			zb.close();
		} catch (IOException e) {
			Log.e(e);
		}

		if (!success) {
			Log.w("Exporting project \"" + project.getTitle() + "\" - FAILED.");
			// cleanup
			target.delete();

			//@formatter:off
			Alerts.error(
					App.getFrame(),
					"An error occured while exporting.\n" +
					"Check log file for details."
			);
			//@formatter:on	

			return;
		}

		if (successRunnable != null) successRunnable.run();

		Log.f1("Exporting project \"" + project.getTitle() + "\" to " + target + " - done.");

		Alerts.info(App.getFrame(), "Export successful.");
	}


	private void addDirectoryToZip(File dir, String pathPrefix) throws IOException {

		List<File> filesToAdd = new ArrayList<File>();

		FileUtils.listDirectoryRecursive(dir, null, filesToAdd);

		for (File file : filesToAdd) {

			if (!file.isFile()) return;

			String path = file.getAbsolutePath();
			path = pathPrefix + path.replace(dir.getAbsolutePath(), "");

			FileInputStream in = new FileInputStream(file);

			zb.addStream(path, in);

			in.close();

			if (Config.LOG_EXPORT_FILES) Log.f3("+ " + path);
		}

	}


	private class ExportProcessor implements AssetTreeProcessor {

		@Override
		public void process(AssetTreeNode node) {

			if (node instanceof AssetTreeLeaf) {

				AssetTreeLeaf leaf = (AssetTreeLeaf) node;

				String logEntry = null;

				// file
				boolean fileSaved = false;
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

							logEntry = "+ " + path;

							zb.addStream(path, data);

							logEntry += " <- \"" + srcName + "\"";

						} finally {
							if (data != null) {
								data.close();
							}
						}

						fileSaved = true;

					} catch (IOException e) {
						Log.e("Error getting asset stream.", e);
					}

				} while (false);

				if (!fileSaved) return;

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
								Log.e("null meta stream");
								break;
							}

							String path = leaf.getAssetEntry().getPath() + ".mcmeta";

							zb.addStream(path, data);

							logEntry += ", m: \"" + srcName + "\"";

						} finally {
							if (data != null) {
								data.close();
							}
						}

					} catch (IOException e) {
						Log.e("Error getting asset meta stream.", e);
					}

				} while (false);


				if (Config.LOG_EXPORT_FILES) {
					if (logEntry != null) {
						Log.f3(logEntry);
					}
				}

			}
		}
	};


}
