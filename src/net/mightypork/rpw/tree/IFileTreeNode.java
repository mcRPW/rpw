package net.mightypork.rpw.tree;

public interface IFileTreeNode {

    /**
     * Get if this node is a directory node.
     *
     * @return is directory
     */
    public boolean isDirectory();


    /**
     * Get if this node is a file node.
     *
     * @return is file
     */
    public boolean isFile();


    public boolean isSound();


    public boolean isImage();


    public boolean isText();


    public boolean isJson();
}
