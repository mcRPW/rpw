package net.mightypork.rpw.tree.filesystem;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;

import net.mightypork.rpw.utils.files.FileUtils;


/**
 * Directory filesystem tree node
 *
 * @author Ondřej Hruška (MightyPork)
 */
public class DirectoryFsTreeNode extends AbstractFsTreeNode {

    private String name = null;
    private final ArrayList<AbstractFsTreeNode> children = new ArrayList<AbstractFsTreeNode>();
    private boolean pathRoot = false;

    private FileFilter filter = null;


    /**
     * @param path represented folder
     */
    public DirectoryFsTreeNode(File path) {
        this(path.getName(), path, null);

    }


    /**
     * @param path   represented folder
     * @param filter file filter
     */
    public DirectoryFsTreeNode(File path, FileFilter filter) {
        this(path.getName(), path, filter);

    }


    /**
     * Create directory node without adding children.
     *
     * @param name the name
     */
    public DirectoryFsTreeNode(String name) {
        this.name = name;
    }


    /**
     * @param name display name
     * @param path paths to this directory
     */
    public DirectoryFsTreeNode(String name, File path) {
        this(name, path, null);
    }


    /**
     * @param name   display name
     * @param path   paths to this directory
     * @param filter file filter
     */
    public DirectoryFsTreeNode(String name, File path, FileFilter filter) {
        this(name, FileUtils.listDirectory(path), filter);

        this.path = path;
    }


    /**
     * @param name       display name
     * @param childPaths paths to children
     */
    public DirectoryFsTreeNode(String name, List<File> childPaths) {
        this(name, childPaths, null);
    }


    /**
     * @param name       display name
     * @param childPaths paths to children
     * @param filter     file filter
     */
    public DirectoryFsTreeNode(String name, List<File> childPaths, FileFilter filter) {
        this.name = name;

        this.filter = filter;

        for (final File f : childPaths) {
            final AbstractFsTreeNode node = makeChildForFile(f);

            addChild(node);
        }
    }


    private AbstractFsTreeNode makeChildForFile(File f) {
        if (!f.exists()) return null;
        if (filter != null && !filter.accept(f)) return null; // filter can remove directories
        if (f.isDirectory()) return new DirectoryFsTreeNode(f, filter);
        if (f.isFile()) return new FileFsTreeNode(f);
        return null;
    }


    public void addChild(AbstractFsTreeNode node) {
        if (node != null) {
            node.parent = this;
            children.add(node);
        }
    }


    @Override
    public Enumeration children() {
        return Collections.enumeration(children);
    }


    @Override
    public AbstractFsTreeNode getChildAt(int childIndex) {
        return children.get(childIndex);
    }


    @Override
    public int getChildCount() {
        return children.size();
    }


    @Override
    public int getIndex(TreeNode node) {
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i) == node) return i;
        }

        return -1;
    }


    @Override
    public File getPath() {
        return path;
    }


    @Override
    public void sort() {
        Collections.sort(children);
        for (final AbstractFsTreeNode n : children) {
            n.sort();
        }
    }


    @Override
    public String getName() {
        return name;
    }


    @Override
    public boolean isDirectory() {
        return true;
    }


    @Override
    public boolean isFile() {
        return false;
    }


    @Override
    public boolean isSound() {
        return false;
    }


    @Override
    public boolean isImage() {
        return false;
    }


    @Override
    public boolean isText() {
        return false;
    }


    @Override
    public boolean isJson() {
        return false;
    }


    /**
     * Get if this node is a path root
     *
     * @return is path root
     */
    @Override
    public boolean isRoot() {
        return pathRoot;
    }


    /**
     * Set if this is the path root
     *
     * @param pathRoot is root
     */
    public void setPathRoot(boolean pathRoot) {
        this.pathRoot = pathRoot;
    }


    /**
     * Get root path
     *
     * @return root path
     */
    public File getRoot() {
        if (this.isRoot() || getParent() == null) return path;

        return getParent().getRoot();
    }


    /**
     * Reload this directory node, if it was initialized using File
     */
    public void reload() {
        if (path == null) return; // can't do this, path wasn't used to init this dir.

        children.clear();

        for (final File f : FileUtils.listDirectory(path)) {
            final AbstractFsTreeNode node = makeChildForFile(f);

            addChild(node);
        }

        setMark(mark);

        sort();
    }
}
