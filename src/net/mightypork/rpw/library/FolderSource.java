package net.mightypork.rpw.library;

import java.io.File;

import net.mightypork.rpw.tree.assets.AssetEntry;


public class FolderSource extends Source {

    private final File folder;


    public FolderSource(File base) {
        folder = base;
    }


    @Override
    public File getAssetFile(String key) {
        final AssetEntry ae = Sources.vanilla.getAssetForKey(key);
        final File file = new File(folder, ae.getPath());

        if (!file.exists()) return null;
        return file;
    }


    @Override
    public File getAssetsDirectory() {
        return folder;
    }

}
