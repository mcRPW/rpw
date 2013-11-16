# Sources

Source is an object that provides asset files.


## Context menu in tree

When you right-click a node (group or file) in the tree view, a context menu will appear.
Among other things, you can select source which you want to assign to the selected nodes.


## Types of sources

An obvious example of sources are all **imported packs in Library**.

Another simple source is **VANILLA**, which means the files will be taken from the vanilla pack.

Other basic source is **SILENCE**, which provides short silent sound as a replacement for
audio assets, typically to disable annoying sounds of villagers and slimes, but some people will surely
find other uses for it.

Technically, also **PROJECT** is a source, as it provides the files you copied there for editing.

A little mysterious source is **INHERIT**. It alone does not provide anything, but it looks
higher in the tree until it finds some suitable source for the asset. With this, you can set source
only to the topmost group, and all files will use it - if the source provides them, of course.


## Resolving sources</h2>

When you assign a source to a file, and the source does not provide anything suitable, INHERIT will
be used instead. As such, it will bubble up through the tree until it finds some valid source.

If it happens that no good source is found, the asset is provided by vanilla. That, in fact, means that when
you export the pack, this asset will be missing and Minecraft will replace it with a default one.
