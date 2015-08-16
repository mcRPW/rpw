# Working with asset tree

RPW shows the resource files (assets) using a TreeTable component. The assets 
are shown in hierarchical structure, and this hierarchy is very important.


## Fancy Tree

The files are shown in a custom structure called *Fancy Tree*, which is easier 
to read and work with. However, Fancy Tree can't properly show mod files, so 
you can disable it in the `View` menu. 

When Fancy Tree is disabled, the files are shown exactly in the way they 
are stored in a resource pack.

Actually, you *can* use Fancy Tree for mod files, but you must create groups 
and filters for them. See the `Library > Fancy Tree mod support` menu.


## Using the tree

If you right click a group or a node, you will be shown with a big context 
menu. In the top part there are commands for manipulating the files, and in the 
bottom, cou can **assign a source** to the node.

Your assigned source will be shown in the `Assigned` column of the main table.


### Assigning sources

Source can be one of the following:

- `inherit` - takes source from the parent group
- `vanilla` - take the file from vanilla
- `silence` - use a silent audio file
- `project` - file that is located in your project
- `library pack` - a name of the pack used to provide the source

The `inherit` source is initially assigned to all the assets, so when you 
change the source of your root node, this change will affect the entire pack.


### How sources resolve

If you assign a source, it's not a rule, but more a hint for RPW. If the source 
does not provide this file, `vanilla` will be used instead.

You can see this "resolved source" in the `Resolved` column.
