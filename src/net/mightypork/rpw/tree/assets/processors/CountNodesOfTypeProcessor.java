package net.mightypork.rpw.tree.assets.processors;

import java.util.HashSet;
import java.util.Set;

import net.mightypork.rpw.tree.assets.EAsset;
import net.mightypork.rpw.tree.assets.tree.AssetTreeGroup;
import net.mightypork.rpw.tree.assets.tree.AssetTreeLeaf;
import net.mightypork.rpw.tree.assets.tree.AssetTreeNode;
import net.mightypork.rpw.tree.assets.tree.AssetTreeProcessor;


public class CountNodesOfTypeProcessor implements AssetTreeProcessor {

    private final EAsset type;

    private int count = 0;

    private final Set<AssetTreeNode> processed = new HashSet<AssetTreeNode>();


    public CountNodesOfTypeProcessor(EAsset type) {
        this.type = type;
    }


    @Override
    public void process(AssetTreeNode node) {
        if (processed.contains(node)) return; // no double-processing
        processed.add(node);

        if (node instanceof AssetTreeGroup) {
            return; // we want leafs

        } else if (node instanceof AssetTreeLeaf) {
            final AssetTreeLeaf leaf = (AssetTreeLeaf) node;

            if (leaf.getAssetType() == type) count++;
        }
    }


    public int getCount() {
        return count;
    }

}
