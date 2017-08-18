package net.mightypork.rpw.library;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.mightypork.rpw.Flags;
import net.mightypork.rpw.Paths;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.tasks.Tasks;
import net.mightypork.rpw.tree.assets.AssetEntry;
import net.mightypork.rpw.tree.assets.EAsset;
import net.mightypork.rpw.utils.Utils;
import net.mightypork.rpw.utils.files.FileUtils;
import net.mightypork.rpw.utils.files.OsUtils;
import net.mightypork.rpw.utils.logging.Log;


public class Sources {

    public static VanillaPack vanilla;
    public static SilenceSource silence;

    public static Map<String, ISource> sourceMap = new LinkedHashMap<String, ISource>();


    public static void init() {
        Log.f1("Initializing Sources");

        silence = new SilenceSource();

        initVanilla();

        initLibrary();

        Log.f1("Initializing Sources - done.");
    }


    /**
     * Init non-vanilla sources. Should be followed by taskRedrawTree.
     */
    public static void initLibrary() {
        Log.f2("Erasing library cache");

        sourceMap.clear();

        Log.f2("Loading resourcepacks...");
        final File library = OsUtils.getAppDir(Paths.DIR_RESOURCEPACKS);

        for (final File f : FileUtils.listDirectory(library)) {
            if (!f.isDirectory()) continue;
            final String dirName = f.getName();

            Log.f3("Adding Source: " + dirName + " -> " + f);
            sourceMap.put(dirName, new FolderSource(f));
        }

    }


    public static void initVanilla() {
        vanilla = new VanillaPack();

        final int i = Tasks.taskLoadVanillaStructure();
        while (Tasks.isRunning(i)) {
            Utils.sleep(100);
        }

        if (Flags.MUST_EXTRACT || !Flags.VANILLA_STRUCTURE_LOAD_OK) {
            Flags.MUST_EXTRACT = false;

            final int task = Tasks.taskExtractAssetsOrDie();
            while (Tasks.isRunning(task)) {
                Utils.sleep(10);
            }
        }
    }


    public static boolean doesSourceExist(String source) {
        if (MagicSources.isMagic(source)) return true;

        return sourceMap.containsKey(source);
    }


    public static boolean doesSourceProvideAsset(String source, AssetEntry asset) {
        if (asset == null || source == null) return false;

        if (MagicSources.isMagic(source)) {
            if (MagicSources.isVanilla(source)) {
                return Sources.vanilla.doesProvideAsset(asset.getKey());
            }

            if (MagicSources.isSilence(source)) {
                return asset.getType() == EAsset.SOUND;
            }

            if (MagicSources.isProject(source)) {
                return Projects.isOpen() && Projects.getActive().doesProvideAsset(asset.getKey());
            }
        }

        if (!sourceMap.containsKey(source)) return false;

        return sourceMap.get(source).doesProvideAsset(asset.getKey());
    }


    public static boolean doesSourceProvideAssetMeta(String source, AssetEntry asset) {
        if (asset == null || source == null) return false;

        if (MagicSources.isMagic(source)) {
            if (MagicSources.isVanilla(source)) {
                return Sources.vanilla.doesProvideAssetMeta(asset.getKey());
            }

            if (MagicSources.isSilence(source)) {
                return false;
            }

            if (MagicSources.isProject(source)) {
                return Projects.isOpen() && Projects.getActive().doesProvideAssetMeta(asset.getKey());
            }
        }

        if (!sourceMap.containsKey(source)) return false;

        return sourceMap.get(source).doesProvideAssetMeta(asset.getKey());
    }


    public static ISource getSource(String source) {
        if (MagicSources.isVanilla(source)) return Sources.vanilla;
        if (MagicSources.isProject(source)) return Projects.getActive();
        if (MagicSources.isSilence(source)) return Sources.silence;

        final ISource src = sourceMap.get(source);
        if (src == null) Log.w("No source named " + source);
        return src;
    }


    public static InputStream getAssetStream(String source, String assetKey) throws IOException {
        return getSource(source).getAssetStream(assetKey);
    }


    public static InputStream getAssetMetaStream(String source, String assetKey) throws IOException {
        return getSource(source).getAssetMetaStream(assetKey);
    }


    /**
     * Translate source name for display (handles MAGIC sources)
     *
     * @param source
     * @return displayed string
     */
    public static String processForDisplay(String source) {
        if (MagicSources.isMagic(source)) {
            if (MagicSources.isInherit(source)) return "(+)";
            if (MagicSources.isProject(source)) return "PROJECT";
            if (MagicSources.isSilence(source)) return "SILENCE";
            if (MagicSources.isVanilla(source)) return "Vanilla";
        }

        return source;
    }


    /**
     * Get all NON-MAGIC source names applicable for context menu
     *
     * @return source names
     */
    public static List<String> getSourceNames() {
        final List<String> list = new ArrayList<String>();

        for (final String s : sourceMap.keySet()) {
            list.add(s);
        }

        Collections.sort(list);

        return list;
    }


    public static List<String> getResourcepackNames() {
        final File library = OsUtils.getAppDir(Paths.DIR_RESOURCEPACKS);
        final List<String> names = new ArrayList<String>();

        for (final File f : FileUtils.listDirectory(library)) {
            if (f.isDirectory()) {
                names.add(f.getName());
            }
        }

        return names;
    }
}
