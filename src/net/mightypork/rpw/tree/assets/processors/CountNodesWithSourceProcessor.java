package net.mightypork.rpw.tree.assets.processors;

import java.util.HashSet;
import java.util.Set;

import net.mightypork.rpw.tree.assets.tree.AssetTreeGroup;
import net.mightypork.rpw.tree.assets.tree.AssetTreeLeaf;
import net.mightypork.rpw.tree.assets.tree.AssetTreeNode;
import net.mightypork.rpw.tree.assets.tree.AssetTreeProcessor;


public class CountNodesWithSourceProcessor implements AssetTreeProcessor {

    private int count = 0;

    private final Set<AssetTreeNode> processed = new HashSet<AssetTreeNode>();

    private final String source;


    public CountNodesWithSourceProcessor(String source) {
        this.source = source;
    }


    @Override
    public void process(AssetTreeNode node) {
        if (processed.contains(node)) return; // no double-processing
        processed.add(node);

        if (node instanceof AssetTreeGroup) {
            return; // we want leafs

        } else if (node instanceof AssetTreeLeaf) {
            final AssetTreeLeaf leaf = (AssetTreeLeaf) node;

            if (leaf.getLibrarySource().equals(source)) count++;
        }
    }


    public int getCount() {
        return count;
    }

}
