package net.mightypork.rpw.library;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;


public interface ISource {

    /**
     * Get if this source can provide given asset
     *
     * @param key asset key to check
     * @return if key is provided
     */
    public boolean doesProvideAsset(String key);


    /**
     * Get if this source can provide given asset meta
     *
     * @param key asset key to check
     * @return if key is provided
     */
    public boolean doesProvideAssetMeta(String key);


    /**
     * Get provided asset as stream
     *
     * @param key assetKey
     * @return the stream
     * @throws IOException on error
     */
    public InputStream getAssetStream(String key) throws IOException;


    /**
     * Get metadata file of an asset as stream = if exists
     *
     * @param key assetKey
     * @return the stream of it's meta file
     * @throws IOException on error
     */
    public InputStream getAssetMetaStream(String key) throws IOException;


    /**
     * Get provided asset as file
     *
     * @param key assetKey
     * @return the file of the asset, or null if (not provided or not exists)
     */
    public File getAssetFile(String key);


    /**
     * Get provided asset meta as file
     *
     * @param key assetKey
     * @return the file of the asset meta, or null if (not provided or not
     * exists)
     */
    public File getAssetMetaFile(String key);


    /**
     * Get source's base directory
     *
     * @return base directory file
     */
    public File getAssetsDirectory();

}
