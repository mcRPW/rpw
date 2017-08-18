package net.mightypork.rpw.tree.assets.tree;

import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;

import net.mightypork.rpw.library.MagicSources;
import net.mightypork.rpw.library.Sources;
import net.mightypork.rpw.project.Project;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.tree.assets.AssetEntry;
import net.mightypork.rpw.tree.assets.EAsset;


public class AssetTreeLeaf extends AssetTreeNode {

    /**
     * Create leaf node
     *
     * @param asset         asset entry for this leaf
     * @param librarySource asset source
     */
    public AssetTreeLeaf(AssetEntry asset, String librarySource) {
        super(asset.getLabel(), librarySource);
        this.asset = asset;
    }

    private AssetEntry asset = null;


    @Override
    public boolean isLeaf() {
        return true;
    }


    @Override
    public int getChildCount() {
        return 0;
    }


    @Override
    public int getIndex(TreeNode child) {
        return -1;
    }


    @Override
    public AssetTreeNode getChildAt(int index) {
        return null;
    }


    /**
     * Get asset key
     *
     * @return asset key
     */
    public String getAssetKey() {
        return asset.getKey();
    }


    /**
     * Get asset key
     *
     * @return asset key
     */
    public EAsset getAssetType() {
        return asset.getType();
    }


    @Override
    public String resolveAssetSource() {
        String source = librarySource;

        if (!Sources.doesSourceExist(source) || !Sources.doesSourceProvideAsset(source, asset)) {
            source = MagicSources.INHERIT;
        }

        if (MagicSources.isInherit(source)) {
            if (parent != null) {
                source = parent.resolveAssetSource();

                if (!Sources.doesSourceExist(source) || !Sources.doesSourceProvideAsset(source, asset)) {
                    source = MagicSources.VANILLA;
                }

            } else {
                return MagicSources.VANILLA;
            }
        }

        return source;

    }


    @Override
    public String resolveAssetMetaSource() {
        String source = librarySource;

        if (!Sources.doesSourceExist(source) || !Sources.doesSourceProvideAssetMeta(source, asset)) {
            source = MagicSources.INHERIT;
        }

        if (MagicSources.isInherit(source)) {
            if (parent != null) {
                source = parent.resolveAssetMetaSource();

                if (!Sources.doesSourceExist(source) || !Sources.doesSourceProvideAssetMeta(source, asset)) {
                    source = MagicSources.VANILLA;
                }

            } else {
                return MagicSources.VANILLA;
            }
        }

        return source;
    }


    @Override
    public void prepareForDisplay() {
    }


    @Override
    public void processThisAndChildren(AssetTreeProcessor processor) {
        processor.process(this);
    }


    @Override
    public Enumeration children() {
        return null;
    }


    public AssetEntry getAssetEntry() {
        return asset;
    }


    @Override
    public List<AssetTreeNode> getChildrenList() {
        return null;
    }


    public boolean isAssetProvidedByProject() {
        final Project p = Projects.getActive();
        if (p == null) return false;

        return p.doesProvideAsset(getAssetKey());
    }


    public boolean isMetaProvidedByProject() {
        final Project p = Projects.getActive();
        if (p == null) return false;

        return p.doesProvideAssetMeta(getAssetKey());
    }


    @Override
    public boolean isDirectory() {
        return false;
    }


    @Override
    public boolean isFile() {
        return true;
    }


    @Override
    public boolean isSound() {
        return getAssetType().isSound();
    }


    @Override
    public boolean isImage() {
        return getAssetType().isImage();
    }


    @Override
    public boolean isText() {
        return getAssetType().isText();
    }


    @Override
    public boolean isJson() {
        return getAssetType().isJson();
    }


    @Override
    public boolean canHaveMeta() {
        return isImage();
    }
}
