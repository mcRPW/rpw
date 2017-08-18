package net.mightypork.rpw.tree.assets.processors;

import java.util.HashSet;
import java.util.Set;

import net.mightypork.rpw.library.MagicSources;
import net.mightypork.rpw.library.Sources;
import net.mightypork.rpw.tree.assets.tree.AssetTreeLeaf;
import net.mightypork.rpw.tree.assets.tree.AssetTreeNode;
import net.mightypork.rpw.tree.assets.tree.AssetTreeProcessor;


public class ApplyInheritProcessor implements AssetTreeProcessor {

    private final Set<AssetTreeNode> processed = new HashSet<AssetTreeNode>();

    private final String defaultSource;


    public ApplyInheritProcessor() {
        this.defaultSource = MagicSources.VANILLA;
    }


    public ApplyInheritProcessor(String defaultSource) {
        this.defaultSource = defaultSource;
    }


    @Override
    public void process(AssetTreeNode node) {
        if (processed.contains(node)) return;
        processed.add(node);

        if (!node.isLeaf()) return; // leave groups alone

        final AssetTreeLeaf leaf = (AssetTreeLeaf) node;

        final String assigned = leaf.getLibrarySource();
        String resolved = leaf.resolveAssetSource();

        if (!assigned.equals(resolved)) {
            if (MagicSources.isInherit(resolved) || MagicSources.isVanilla(resolved)) {
                if (MagicSources.isInherit(defaultSource) || Sources.doesSourceProvideAsset(defaultSource, leaf.getAssetEntry())) {
                    resolved = defaultSource;
                }
            }

            node.setLibrarySource(resolved);
        }
    }

}
