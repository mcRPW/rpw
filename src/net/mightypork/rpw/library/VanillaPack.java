package net.mightypork.rpw.library;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import net.mightypork.rpw.Paths;
import net.mightypork.rpw.tasks.TaskLoadVanillaStructure;
import net.mightypork.rpw.tree.assets.AssetEntry;
import net.mightypork.rpw.utils.files.OsUtils;


/**
 * Storage for information about vanilla pack
 *
 * @author Ondřej Hruška (MightyPork)
 */
public class VanillaPack extends Source {

    private Map<String, AssetEntry> keyToAssetDict = new LinkedHashMap<String, AssetEntry>();


    /**
     * Load entries from data file
     */
    public void loadFromFile() {
        TaskLoadVanillaStructure.run();
    }


    @Override
    public boolean doesProvideAsset(String key) {
        return keyToAssetDict.containsKey(key);
    }


    /**
     * Get asset for assetKey
     *
     * @param key assetKey
     * @return asset entry
     */
    public AssetEntry getAssetForKey(String key) {
        return keyToAssetDict.get(key);
    }


    /**
     * Store asset entries loaded in a task
     *
     * @param assets entries
     */
    public void setAssets(Map<String, AssetEntry> assets) {
        keyToAssetDict = assets;
    }


    /**
     * Get all asset keys
     *
     * @return collection of all provided asset keys
     */
    public Collection<String> getAssetKeys() {
        return keyToAssetDict.keySet();
    }


    /**
     * Get all asset entries
     *
     * @return collection of all provided asset entries
     */
    public Collection<AssetEntry> getAssetEntries() {
        return keyToAssetDict.values();
    }


    @Override
    public File getAssetsDirectory() {
        return OsUtils.getAppDir(Paths.DIR_VANILLA);
    }
}
