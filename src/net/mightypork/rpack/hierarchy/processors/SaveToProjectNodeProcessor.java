package net.mightypork.rpack.hierarchy.processors;


import java.util.HashSet;
import java.util.Set;

import net.mightypork.rpack.hierarchy.tree.AssetTreeGroup;
import net.mightypork.rpack.hierarchy.tree.AssetTreeLeaf;
import net.mightypork.rpack.hierarchy.tree.AssetTreeNode;
import net.mightypork.rpack.hierarchy.tree.AssetTreeProcessor;
import net.mightypork.rpack.project.Project;


public class SaveToProjectNodeProcessor implements AssetTreeProcessor {

	private Project project;
	private Set<AssetTreeNode> processed = new HashSet<AssetTreeNode>();


	public SaveToProjectNodeProcessor(Project target) {

		this.project = target;
	}


	@Override
	public void process(AssetTreeNode node) {

		if (processed.contains(node)) return;
		processed.add(node);

		if (node instanceof AssetTreeGroup) {

			AssetTreeGroup group = (AssetTreeGroup) node;
			if (group.getGroupKey() == null) return;

			project.setSourceForGroup(group.getGroupKey(), group.getLibrarySource());

		} else if (node instanceof AssetTreeLeaf) {

			AssetTreeLeaf leaf = (AssetTreeLeaf) node;
			if (leaf.getAssetKey() == null) return;

			project.setSourceForFile(leaf.getAssetKey(), leaf.getLibrarySource());
		}
	}

}
