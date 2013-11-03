package net.mightypork.rpack.hierarchy.processors;


import java.util.HashSet;
import java.util.Set;

import net.mightypork.rpack.hierarchy.tree.AssetTreeNode;
import net.mightypork.rpack.hierarchy.tree.AssetTreeProcessor;


public class RenameSourceProcessor implements AssetTreeProcessor {

	private Set<AssetTreeNode> processed = new HashSet<AssetTreeNode>();

	private String oldSource, newSource;


	public RenameSourceProcessor(String oldSource, String newSource) {

		this.oldSource = oldSource;
		this.newSource = newSource;
	}


	@Override
	public void process(AssetTreeNode node) {

		if (processed.contains(node)) return; // no double-processing
		processed.add(node);

		if (node.getLibrarySource().equalsIgnoreCase(oldSource)) {
			node.setLibrarySource(newSource);
		}
	}

}
