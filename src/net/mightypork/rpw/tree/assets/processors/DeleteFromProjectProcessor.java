package net.mightypork.rpw.tree.assets.processors;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import net.mightypork.rpw.library.MagicSources;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.tree.assets.tree.AssetTreeGroup;
import net.mightypork.rpw.tree.assets.tree.AssetTreeLeaf;
import net.mightypork.rpw.tree.assets.tree.AssetTreeNode;
import net.mightypork.rpw.tree.assets.tree.AssetTreeProcessor;


public class DeleteFromProjectProcessor implements AssetTreeProcessor {

    private final Set<AssetTreeNode> processed = new HashSet<AssetTreeNode>();

    private boolean assets = true, meta = true;


    public DeleteFromProjectProcessor() {
    }


    public DeleteFromProjectProcessor(boolean assets, boolean meta) {
        this.assets = assets;
        this.meta = meta;
    }


    @Override
    public void process(AssetTreeNode node) {
        if (processed.contains(node)) return;
        processed.add(node);

        if (assets && MagicSources.isProject(node.getLibrarySource())) {
            node.setLibrarySource(MagicSources.INHERIT);
        }

        if (node instanceof AssetTreeGroup) {
            return;

        } else {
            final AssetTreeLeaf leaf = (AssetTreeLeaf) node;

            if (!Projects.getActive().doesProvideAsset(leaf.getAssetKey())) {
                return; // not in project
            }

            final String path = leaf.getAssetEntry().getPath();

            final File base = Projects.getActive().getAssetsDirectory();
            final File target = new File(base, path);
            final File targetMeta = new File(base, path + ".mcmeta");

            if (assets) target.delete();
            if (meta) targetMeta.delete();
        }
    }

}
