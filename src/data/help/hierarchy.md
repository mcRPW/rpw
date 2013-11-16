# Hierarchy

Information about inheritance and related stuff can be found on page "Sources".


## Tree display

The assets in a resource pack are organized in folders, but the original structure is a bit messy.
That is why this tool lets you use an alternate representation called **fancy tree**.

While you will see the assets organized in neat groups, the tree nodes keep information about
the real file locations in an pack, so it's no problem to export a valid resource pack even
if you use the "fancy" style.


## Toggling fancy tree

To toggle the "fancy" style, un-tick the corresponding option in the Options menu.

Please note that each tree style has different groups, so after switching the style,
the groups change, and obviously if you depend on INHERIT and groups change, it can
cause problems. Thus it's not recommended to mess with the Fancy Tree option once
you start a project which you mean seriously.


## Expand / Collapse

The basic controls are obvious, just click the **"+"** and **"-"** signs to expand or
collapse the nodes.

However, there are some other tricks that can be good to know. Firstly, there are a few
in the "Tree" menu: **Expand all**, **Collapse all** and **Rerfesh tree**. 
But that's not all: in the context menu - if you select a group - you will get options to
expand/collapse all children recursively, all direct children, and all siblings. These options are very
useful if you work in some deep place like a group with mob textures and you start getting
lost in the tree.
