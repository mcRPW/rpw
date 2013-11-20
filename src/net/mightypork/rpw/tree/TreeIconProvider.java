package net.mightypork.rpw.tree;


import javax.swing.Icon;

import net.mightypork.rpw.gui.Icons;

import org.jdesktop.swingx.renderer.IconValue;


public class TreeIconProvider implements IconValue {

	@Override
	public Icon getIcon(Object object) {

		if (!(object instanceof IFileTreeNode)) return null;

		IFileTreeNode node = (IFileTreeNode) object;

		if (node.isDirectory()) {
			return Icons.TREE_FOLDER;
		}

		if (node.isFile()) {
			if (node.isImage()) return Icons.TREE_FILE_IMAGE;
			if (node.isSound()) return Icons.TREE_FILE_AUDIO;
			if (node.isText()) return Icons.TREE_FILE_TEXT;
		}

		return null;
	}

}
