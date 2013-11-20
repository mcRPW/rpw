package net.mightypork.rpw.tree.assets.processors;


import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import net.mightypork.rpw.library.MagicSources;
import net.mightypork.rpw.tree.assets.tree.AssetTreeGroup;
import net.mightypork.rpw.tree.assets.tree.AssetTreeLeaf;
import net.mightypork.rpw.tree.assets.tree.AssetTreeNode;
import net.mightypork.rpw.tree.assets.tree.AssetTreeProcessor;
import net.mightypork.rpw.utils.Utils;


public class GetProjectSummaryProcessor implements AssetTreeProcessor {

	private Set<AssetTreeNode> processed = new HashSet<AssetTreeNode>();

	private Map<String, String> summary = new LinkedHashMap<String, String>();


	public GetProjectSummaryProcessor() {

	}


	@Override
	public void process(AssetTreeNode node) {

		if (processed.contains(node)) return; // no double-processing
		processed.add(node);

		if (node instanceof AssetTreeGroup) {

			return; // we want leafs

		} else if (node instanceof AssetTreeLeaf) {

			AssetTreeLeaf leaf = (AssetTreeLeaf) node;

			String src = leaf.resolveAssetSource();

			if (!MagicSources.isVanilla(src) && !MagicSources.isInherit(src)) {
				summary.put(leaf.getAssetKey(), src);
			}
		}
	}


	public Map<String, String> getSummary() {

		return Utils.sortByKeys(summary);
	}
}
