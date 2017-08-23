package net.mightypork.rpw.tree;

import javax.swing.Icon;

import net.mightypork.rpw.gui.Icons;

import org.jdesktop.swingx.renderer.IconValue;


public class TreeIconProvider implements IconValue {

    @Override
    public Icon getIcon(Object object) {
        if (!(object instanceof IFileTreeNode)) return null;

        final IFileTreeNode node = (IFileTreeNode) object;

        if (node.isDirectory()) {
            return Icons.TREE_FOLDER;
        }

        if (node.isFile()) {
            // from most specific to general
            if (node.isImage()) return Icons.TREE_FILE_IMAGE;
            if (node.isSound()) return Icons.TREE_FILE_AUDIO;
            if (node.isJson()) return Icons.TREE_FILE_JSON;
            if (node.isText()) return Icons.TREE_FILE_TEXT;
            return Icons.TREE_FILE_GENERIC;
        }

        return null;
    }

}
