package net.mightypork.rpw.tree.assets;

import net.mightypork.rpw.utils.Utils;
import net.mightypork.rpw.utils.files.FileUtils;


/**
 * Represents a node in the assets folder, based in pack root
 *
 * @author Ondřej Hruška (MightyPork)
 */
public class AssetEntry implements Comparable<AssetEntry> {

    private final String key;
    private final EAsset type;


    /**
     * Create a node (meta files for animation will be considered when
     * exporting)
     *
     * @param key  node key, eg. assets.minecraft.textures.blocks.bedrock
     * @param type a type of file, eg. EAsset.IMAGE
     */
    public AssetEntry(String key, EAsset type) {
        this.key = key;
        this.type = type;
    }


    public String getKey() {
        return key;
    }


    public EAsset getType() {
        return type;
    }


    /**
     * Get path relative to pack root
     *
     * @return path based in package root, eg.
     * assets/minecraft/textures/blocks/bedrock.png
     */
    public String getPath() {
        final String path = key.replace('.', '/') + "." + type.getExtension();

        return FileUtils.unescapeFilename(path);
    }


    @Override
    public String toString() {
        return key + " (" + type + ")";
    }


    public String getLabel() {
        return FileUtils.unescapeFileString(Utils.fromLastDot(key));
    }


    @Override
    public int compareTo(AssetEntry o) {
        int c = o.key.compareToIgnoreCase(key);
        if (c == 0) c = o.type.compareTo(type);
        return -c;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof AssetEntry)) return false;

        final AssetEntry other = (AssetEntry) obj;

        return other.key.equals(key) && other.type == type;
    }


    @Override
    public int hashCode() {
        return key.hashCode() ^ type.hashCode();
    }

}
