package net.mightypork.rpack.hierarchy.processors;


import java.util.HashSet;
import java.util.Set;

import net.mightypork.rpack.hierarchy.tree.AssetTreeGroup;
import net.mightypork.rpack.hierarchy.tree.AssetTreeLeaf;
import net.mightypork.rpack.hierarchy.tree.AssetTreeNode;
import net.mightypork.rpack.hierarchy.tree.AssetTreeProcessor;


public class CountNodesWithSourceProcessor implements AssetTreeProcessor {

	private int count = 0;

	private Set<AssetTreeNode> processed = new HashSet<AssetTreeNode>();

	private String source;


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

			AssetTreeLeaf leaf = (AssetTreeLeaf) node;

			if (leaf.getLibrarySource().equals(source)) count++;
		}
	}


	public int getCount() {

		return count;
	}

}
