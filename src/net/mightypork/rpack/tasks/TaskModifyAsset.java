package net.mightypork.rpack.tasks;


import java.awt.Dialog;
import java.io.File;
import java.io.IOException;

import net.mightypork.rpack.App;
import net.mightypork.rpack.Config;
import net.mightypork.rpack.gui.windows.Alerts;
import net.mightypork.rpack.gui.windows.DialogEditMeta;
import net.mightypork.rpack.gui.windows.DialogEditText;
import net.mightypork.rpack.hierarchy.EAsset;
import net.mightypork.rpack.hierarchy.processors.CopyToProjectProcessor;
import net.mightypork.rpack.hierarchy.tree.AssetTreeLeaf;
import net.mightypork.rpack.hierarchy.tree.AssetTreeProcessor;
import net.mightypork.rpack.library.MagicSources;
import net.mightypork.rpack.library.Sources;
import net.mightypork.rpack.project.Projects;
import net.mightypork.rpack.utils.DesktopApi;
import net.mightypork.rpack.utils.FileUtils;
import net.mightypork.rpack.utils.Log;


public class TaskModifyAsset {

	public static void edit(AssetTreeLeaf node, boolean editMeta) {

		if (!assertIsInProject(node, editMeta)) return;

		if (editMeta && Config.USE_INTERNAL_META_EDITOR) {

			try {
				Alerts.loading(true);
				Dialog d = new DialogEditMeta(node);
				Alerts.loading(false);
				d.setVisible(true);
			} catch (IOException e) {
				Log.e("Error openning mcmeta file for edit.", e);
				Alerts.error(App.getFrame(), "Error openning mcmeta file for edit.");
			}

			return;
		}

		if (!editMeta && Config.USE_INTERNAL_TEXT_EDITOR && node.getAssetType().isText()) {

			try {
				Alerts.loading(true);
				Dialog d = new DialogEditText(node);
				Alerts.loading(false);
				d.setVisible(true);
			} catch (IOException e) {
				Log.e("Error openning text file for edit.", e);
				Alerts.error(App.getFrame(), "Error openning text file for edit.");
			}

			return;
		}

		File f;

		// get file
		if (editMeta) {
			f = Projects.getActive().getAssetMetaFile(node.getAssetKey());
		} else {
			f = Projects.getActive().getAssetFile(node.getAssetKey());
		}

		// if null, isn't available
		if (f == null) {
			Alerts.error(App.getFrame(), "File not accessible.");
			return;
		}

		// if meta, always text
		if (editMeta) {
			if (!DesktopApi.editText(f)) {
				alertCouldNotEdit();
			}
			return;
		}

		// not meta - select by type

		EAsset type = node.getAssetType();

		if (type.isImage()) {
			if (!DesktopApi.editImage(f)) {
				alertCouldNotEdit();
			}
		}

		if (type.isText()) {
			if (!DesktopApi.editText(f)) {
				alertCouldNotEdit();
			}
		}

		if (type.isSound()) {
			if (!DesktopApi.editAudio(f)) {
				alertCouldNotEdit();
			}
		}

		return;
	}


	/**
	 * Check if open project contains this asset or meta<br>
	 * If not, ask user to copy it, and do so if agreed.
	 * 
	 * @param node asset tree node - leaf
	 * @param wantMeta true = meta, false = asset
	 * @return is in project
	 */
	private static boolean assertIsInProject(AssetTreeLeaf node, boolean wantMeta) {

		boolean hasMeta = node.isMetaProvidedByProject();

		boolean doesMetaExist = Sources.doesSourceProvideAssetMeta(node.resolveAssetMetaSource(), node.getAssetEntry());

		boolean hasAsset = node.isAssetProvidedByProject();

		if (!(wantMeta ? hasMeta : hasAsset)) {

			if (wantMeta && !doesMetaExist) {
				//@formatter:off
				boolean choice = Alerts.askOkCancel(
						App.getFrame(),
						"Create new McMeta",
						"The selected asset doesn't have\n" +
						"a \"mcmeta\" file.\n" +
						"\n" +
						"Shall a new \"mcmeta\" file be created?"
				);
				//@formatter:on

				if (choice == true) {

					if (!hasAsset) {
						AssetTreeProcessor proc = new CopyToProjectProcessor();
						proc.process(node);
					}

					File projbase = Projects.getActive().getAssetsBaseDirectory();

					File metafile = new File(projbase, node.getAssetEntry().getPath() + ".mcmeta");

					String def = "{\n}";

					try {
						FileUtils.stringToFile(metafile, def);
					} catch (IOException e) {
						Log.e(e);
						return false;
					}

					node.setLibrarySourceIfNeeded(MagicSources.PROJECT);

					Tasks.taskTreeRedraw();
					App.getSidePanel().redrawPreview();
					return true;
				} else {
					return false;
				}

			}


			//@formatter:off
			boolean choice = Alerts.askOkCancel(
					App.getFrame(),
					"Copy To Project",
					"To edit a file, it must first be\n" +
					"copied to your project.\n" +
					"\n" +
					"Shall it be copied?"
			);
			//@formatter:on

			if (choice == true) {
				AssetTreeProcessor proc = new CopyToProjectProcessor();
				proc.process(node);
				node.setLibrarySourceIfNeeded(MagicSources.PROJECT);

				Tasks.taskTreeRedraw();
				App.getSidePanel().redrawPreview();
				return true;
			} else {
				return false;
			}
		}

		return true;
	}


	private static void alertCouldNotEdit() {

		//@formatter:off
		Alerts.error(
				App.getFrame(),
				"Could not edit file, your platform is\n" +
				"probably not supported.\n" +
				"\n" +
				"Please, check the log file for details."
		);
		//@formatter:on
	}

}
