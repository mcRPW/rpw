package net.mightypork.rpack.hierarchy.processors;


import java.util.HashSet;
import java.util.Set;

import net.mightypork.rpack.hierarchy.tree.AssetTreeGroup;
import net.mightypork.rpack.hierarchy.tree.AssetTreeLeaf;
import net.mightypork.rpack.hierarchy.tree.AssetTreeNode;
import net.mightypork.rpack.hierarchy.tree.AssetTreeProcessor;
import net.mightypork.rpack.library.MagicSources;


public class SetToSourceProcessor implements AssetTreeProcessor {

	private String source;

	private Set<AssetTreeNode> processed = new HashSet<AssetTreeNode>();

	private boolean modifyLeaves = true;


	public void setModifyLeaves(boolean modifyLeaves) {

		this.modifyLeaves = modifyLeaves;
	}


	public void setSource(String source) {

		this.source = source;
	}


	public SetToSourceProcessor(String source) {

		this.source = source;
	}


	@Override
	public void process(AssetTreeNode node) {

		if (processed.contains(node)) return; // no double-processing
		processed.add(node);

		if (node instanceof AssetTreeGroup) {

			AssetTreeGroup group = (AssetTreeGroup) node;
			group.setLibrarySource(source);

		} else if (node instanceof AssetTreeLeaf) {
			if (!modifyLeaves) return;

			AssetTreeLeaf leaf = (AssetTreeLeaf) node;

			if (MagicSources.isSilence(source)) {
				if (!leaf.getAssetType().isSound()) return;
			}

			leaf.setLibrarySource(source);

		}
	}

}
