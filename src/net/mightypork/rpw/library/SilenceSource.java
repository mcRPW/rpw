package net.mightypork.rpw.library;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import net.mightypork.rpw.tree.assets.AssetEntry;
import net.mightypork.rpw.tree.assets.EAsset;
import net.mightypork.rpw.utils.files.FileUtils;


public class SilenceSource implements ISource {

    @Override
    public boolean doesProvideAsset(String key) {
        final AssetEntry ae = Sources.vanilla.getAssetForKey(key);
        return ae.getType() == EAsset.SOUND;
    }


    @Override
    public InputStream getAssetStream(String key) throws IOException {
        if (!doesProvideAsset(key)) return null;

        return FileUtils.getResource("/data/export/silence.ogg");
    }


    @Override
    public File getAssetFile(String key) {
        return null;
    }


    @Override
    public File getAssetMetaFile(String key) {
        return null;
    }


    @Override
    public File getAssetsDirectory() {
        return null;
    }


    @Override
    public InputStream getAssetMetaStream(String key) throws IOException {
        return null;
    }


    @Override
    public boolean doesProvideAssetMeta(String key) {
        return false;
    }

}
