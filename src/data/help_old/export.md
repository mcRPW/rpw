# Exporting your project

When your project is finished or you just want to try it in-game, use the **Export** menu option.
You will be asked to select the destination file, and the rest of the task will be done for you.

In the export, all non-vanilla assets will be included, together with `pack.png` and a metadata
file `pack.mcmeta`. If you later decide to change the icon, nothing is easier than just replace
it in the ZIP, or change it in project and export it again.


## Included Extra Files

RPW (since v.3.5) lets you add extra files to the include path. In the project directory, a folder
`included_files` is created, containing (by default) the folders `assets/minecraft` for convenience.

**Everything** in this folder will be packed into the output ZIP, which means you can add new sound files,
textures for "CTM" in OptiFine, perhaps some mod files and a whole lot of other stuff.


### Overwriting project files

If both project configuration and `included_files` provide the same file, it is taken from the
`included_files` folder. This way, you can easily overwrite individual files.

If the `included_files` folder contains the same file that is already taken from the project configuration, 
it's possible that you will get a corrupt zip, or that the export will fail. It's easy to avoid this: **Don't do it!**


## Types of export

You have two options for export: (1) Export to a folder of your choice, or (2) Export directly to
Minecraft's `resourcepacks` folder. The later option will also modify Minecraft settings to use
this pack next time you start it.


## Exporting a complete pack

In case you want to export a **complete pack** (usually not needed), that is, include ALL the files,
even those you didn't change at all, then the best option is to click at project's root node and select
**Copy to project**. All the vanilla files will be copied to your project, resulting in a complete pack.
