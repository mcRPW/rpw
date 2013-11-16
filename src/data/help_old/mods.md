# Support for mods

If you want to re-texture some mod, it's not really hard to do. Simply install the mod assets into
minecraft.jar (actually, it's now the jar in the versions folder), and let RPW **re-extract vanilla 
pack**.

Since RPW can't know what assets are vanilla and what are from mods, it will take all that is in the
**assets** folder - which is what you want.


## Setting up "Fancy Tree" for mods

This is a rather advanced thing, so if you are lazy, just switch *"fancy tree"* off and you're done.
The mod files will be then shown in the real folders and you can modify them easilly.

However, if you insist on fancy tree, look into **Library** menu. There are two text files which you
can edit in order to add groups to the tree. They have comments inside, which explain how to add groups
and how to put assets into them using filters.

It's recommended to add your groups first, and after that add the filters.

To see how your config files work, just use **Tree→Refresh tree display** and it will immediately
use your config files to build the groups and put files to them. If you do something very wrong, it will
be shown in the runtime log.
