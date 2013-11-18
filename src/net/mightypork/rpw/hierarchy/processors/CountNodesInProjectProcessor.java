package net.mightypork.rpw.hierarchy.processors;


import java.util.HashSet;
import java.util.Set;

import net.mightypork.rpw.hierarchy.tree.AssetTreeGroup;
import net.mightypork.rpw.hierarchy.tree.AssetTreeLeaf;
import net.mightypork.rpw.hierarchy.tree.AssetTreeNode;
import net.mightypork.rpw.hierarchy.tree.AssetTreeProcessor;
import net.mightypork.rpw.library.MagicSources;
import net.mightypork.rpw.project.Projects;


public class CountNodesInProjectProcessor implements AssetTreeProcessor {


	private int groups = 0;
	private int count = 0;
	private int countMeta = 0;
	private int leaves = 0;
	private int vanillaLeaves = 0;

	private Set<AssetTreeNode> processed = new HashSet<AssetTreeNode>();


	public CountNodesInProjectProcessor() {

	}


	@Override
	public void process(AssetTreeNode node) {

		if (processed.contains(node)) return; // no double-processing
		processed.add(node);

		if (node instanceof AssetTreeGroup) {

			groups++;
			return; // we want leafs

		} else if (node instanceof AssetTreeLeaf) {

			AssetTreeLeaf leaf = (AssetTreeLeaf) node;

			leaves++;

			if (Projects.getActive().doesProvideAsset(leaf.getAssetKey())) {
				count++;
			}


			if (Projects.getActive().doesProvideAssetMeta(leaf.getAssetKey())) {
				countMeta++;
			}

			if (MagicSources.isVanilla(leaf.resolveAssetSource())) {
				vanillaLeaves++;
			}
		}
	}


	public int getInProject() {

		return count;
	}


	public int getInProjectMeta() {

		return countMeta;
	}


	public int getGroups() {

		return groups;
	}


	public int getLeaves() {

		return leaves;
	}


	public int getVanillaLeaves() {

		return vanillaLeaves;
	}

}
