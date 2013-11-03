package net.mightypork.rpack.gui.helpers;


import javax.swing.Icon;

import net.mightypork.rpack.gui.Icons;
import net.mightypork.rpack.hierarchy.EAsset;
import net.mightypork.rpack.hierarchy.tree.AssetTreeGroup;
import net.mightypork.rpack.hierarchy.tree.AssetTreeLeaf;
import net.mightypork.rpack.hierarchy.tree.AssetTreeNode;

import org.jdesktop.swingx.renderer.IconValue;


public class TreeIconProvider implements IconValue {

	@Override
	public Icon getIcon(Object object) {

		if (!(object instanceof AssetTreeNode)) return null;

		if (object instanceof AssetTreeGroup) {
			return Icons.TREE_FOLDER;
		}

		if (object instanceof AssetTreeLeaf) {

			EAsset type = ((AssetTreeLeaf) object).getAssetType();

			if (type.isImage()) return Icons.TREE_FILE_IMAGE;

			if (type.isSound()) return Icons.TREE_FILE_AUDIO;

			if (type.isText()) return Icons.TREE_FILE_TEXT;
		}

		return null;
	}

}
