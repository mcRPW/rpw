package net.mightypork.rpw.library;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import net.mightypork.rpw.Config;
import net.mightypork.rpw.tree.assets.AssetEntry;


/**
 * Asset provider - base
 *
 * @author Ondřej Hruška (MightyPork)
 */
public abstract class Source implements ISource {

    @Override
    public boolean doesProvideAsset(String key) {
        return getAssetFile(key) != null;
    }


    @Override
    public boolean doesProvideAssetMeta(String key) {
        return getAssetMetaFile(key) != null;
    }


    /**
     * Get provided asset as stream
     *
     * @param key assetKey
     * @return the stream
     * @throws IOException on error
     */
    @Override
    public InputStream getAssetStream(String key) throws IOException {
        final File f = getAssetFile(key);
        if (f == null) return null;

        return new FileInputStream(f);
    }


    /**
     * Get provided asset as file
     *
     * @param key assetKey
     * @return the file of the asset, or null if (not provided or not exists)
     */
    @Override
    public File getAssetFile(String key) {
        if (!doesProvideAsset(key)) return null;

        final AssetEntry asset = Sources.vanilla.getAssetForKey(key);

        final String path = asset.getPath();

        final File file = new File(getAssetsDirectory(), Config.LIBRARY_VERSION + "/" + path);

        if (!file.exists()) return null;

        return file;
    }


    @Override
    public File getAssetMetaFile(String key) {
        final File f = getAssetFile(key);
        if (f == null) return null;

        String path = f.getPath();
        path += ".mcmeta";

        final File metafile = new File(path);
        if (!metafile.exists()) return null;

        return metafile;
    }


    /**
     * Get source's base directory
     *
     * @return base directory file
     */
    @Override
    public abstract File getAssetsDirectory();


    @Override
    public InputStream getAssetMetaStream(String key) throws IOException {
        File f = getAssetFile(key);
        if (f == null) return null;

        String p = f.getPath();
        p += ".mcmeta";
        f = new File(p);

        if (!f.exists()) return null;

        return new FileInputStream(f);
    }
}
