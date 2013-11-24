# Texture metadata

Animated textures use `*.mcmeta` files to keep information about frame order, 
speed and special effects. You can edit them using context menu in the main 
tree area.


## Image size

To make a texture animated, you will have to put all the animation frames in 
the image file. If your file is *16px* wide, and you want 4 frames, then it 
will be *4*16px* high and the frames will be placed in a column.

When making a water or lava animation, different rules apply, so it's best to 
see the vanilla textures for reference.


## Metadata syntax

All "McMeta" files use `JSON` format. Any syntax errors in the format will 
most likely produce the "failed texture" (purple-black checkered) in-game. 

When editing such a file in the built-in editor, you can use the `Check JSON` 
button to validate your code.

You can also click the `Templates` button to use one of the template 
files.


### Animation

The `animation` section can be used only for blocks and items.

    {
      "animation": {}
    }

If left empty (`{}`), the default timing and frame order will be used. However, 
only some blocks, such as clock or compass, can use this.

The `animation` section can contain two parts:


#### "frametime"

* number of ticks per frame (20 ticks = 1s)
* when omitted, default timing is used.

Example:

    {
      "animation": {
        "frametime": 2
      }
    }


#### "frames"

* list of frame numbers to loop
* when omitted, default frame-set is used (if applicable)

Example:

    {
      "animation": {
        "frames": [
          0, 1, 2, 3, 4, 5
        ]
      }
    }

To define duration of individual frames, you'd use this:

    { "index" : 2, "time" : 40 }

Example:

    {
      "animation": {
        "frames": [
          0,
          1,
          { "index" : 2, "time" : 40 },
          3,
          4,
          5
        ]
      }
    }


### Special Effects

The section `texture` works only for some textures, such as pumpkin and 
under-water screen overlays. You can't use this and `animation` together.

    {
      "texture": {}
    }

There are two possible `boolean` options for `texture`:

    {
      "texture": {
        "blur": true,
        "clamp": false
      }
    }

`Blur` makes the texture blurred, and `clamp` is used to prevent texture 
tiling (used in the Shadow texture). 