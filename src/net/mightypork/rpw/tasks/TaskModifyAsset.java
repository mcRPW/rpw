package net.mightypork.rpw.tasks;

import java.awt.Dialog;
import java.io.File;
import java.io.IOException;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Config;
import net.mightypork.rpw.gui.windows.dialogs.DialogEditMeta;
import net.mightypork.rpw.gui.windows.dialogs.DialogEditText;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.library.MagicSources;
import net.mightypork.rpw.library.Sources;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.tree.assets.EAsset;
import net.mightypork.rpw.tree.assets.processors.CopyToProjectProcessor;
import net.mightypork.rpw.tree.assets.tree.AssetTreeLeaf;
import net.mightypork.rpw.tree.assets.tree.AssetTreeProcessor;
import net.mightypork.rpw.utils.files.DesktopApi;
import net.mightypork.rpw.utils.files.FileUtils;
import net.mightypork.rpw.utils.logging.Log;


public class TaskModifyAsset {

    public static void edit(AssetTreeLeaf node, boolean editMeta) {
        if (!assertIsInProject(node, editMeta)) return;

        if (editMeta && Config.USE_INTERNAL_META_EDITOR) {
            Alerts.loading(true);
            final Dialog d = new DialogEditMeta(node);
            Alerts.loading(false);
            d.setVisible(true);

            return;
        }

        if (!editMeta && Config.USE_INTERNAL_TEXT_EDITOR && node.getAssetType().isText()) {
            try {
                Alerts.loading(true);
                final Dialog d = new DialogEditText(node);
                Alerts.loading(false);
                d.setVisible(true);
            } catch (final IOException e) {
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

        final EAsset type = node.getAssetType();

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


    public static void editModel(AssetTreeLeaf node) {
        File file = Projects.getActive().getAssetFile(node.getAssetKey());
        if (Config.def_USE_MODEL_EDITOR && node.getAssetKey().contains("models") && file.getPath().endsWith(".json")) {
            if (!DesktopApi.editModel(file)) {
                alertCouldNotEdit();
            }
        }
    }

    /**
     * Check if open project contains this asset or meta<br>
     * If not, ask user to copy it, and do so if agreed.
     *
     * @param node     asset tree node - leaf
     * @param wantMeta true = meta, false = asset
     * @return is in project
     */
    private static boolean assertIsInProject(AssetTreeLeaf node, boolean wantMeta) {
        final boolean hasMeta = node.isMetaProvidedByProject();

        final boolean doesMetaExist = Sources.doesSourceProvideAssetMeta(node.resolveAssetMetaSource(), node.getAssetEntry());

        final boolean hasAsset = node.isAssetProvidedByProject();

        if (!(wantMeta ? hasMeta : hasAsset)) {
            if (wantMeta && !doesMetaExist) {
                //@formatter:off
                final boolean choice = Alerts.askOkCancel(
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
                        final AssetTreeProcessor proc = new CopyToProjectProcessor();
                        proc.process(node);
                    }

                    final File projbase = Projects.getActive().getAssetsDirectory();

                    final File metafile = new File(projbase, node.getAssetEntry().getPath() + ".mcmeta");

                    final String def = "{\n}";

                    try {
                        FileUtils.stringToFile(metafile, def);
                    } catch (final IOException e) {
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
            final boolean choice = Alerts.askOkCancel(
                    App.getFrame(),
                    "Copy To Project",
                    "To edit a file, it must first be\n" +
                            "copied to your project.\n" +
                            "\n" +
                            "Shall it be copied?"
            );
            //@formatter:on

            if (choice == true) {
                final AssetTreeProcessor proc = new CopyToProjectProcessor();
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
