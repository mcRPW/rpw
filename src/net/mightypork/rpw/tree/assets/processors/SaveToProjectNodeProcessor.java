package net.mightypork.rpw.tree.assets.processors;

import java.util.HashSet;
import java.util.Set;

import net.mightypork.rpw.project.Project;
import net.mightypork.rpw.tree.assets.tree.AssetTreeGroup;
import net.mightypork.rpw.tree.assets.tree.AssetTreeLeaf;
import net.mightypork.rpw.tree.assets.tree.AssetTreeNode;
import net.mightypork.rpw.tree.assets.tree.AssetTreeProcessor;


public class SaveToProjectNodeProcessor implements AssetTreeProcessor {

    private final Project project;
    private final Set<AssetTreeNode> processed = new HashSet<AssetTreeNode>();


    public SaveToProjectNodeProcessor(Project target) {
        this.project = target;
    }


    @Override
    public void process(AssetTreeNode node) {
        if (processed.contains(node)) return;
        processed.add(node);

        if (node instanceof AssetTreeGroup) {
            final AssetTreeGroup group = (AssetTreeGroup) node;
            if (group.getGroupKey() == null) return;

            project.setSourceForGroup(group.getGroupKey(), group.getLibrarySource());

        } else if (node instanceof AssetTreeLeaf) {
            final AssetTreeLeaf leaf = (AssetTreeLeaf) node;
            if (leaf.getAssetKey() == null) return;

            project.setSourceForFile(leaf.getAssetKey(), leaf.getLibrarySource());
        }
    }

}
