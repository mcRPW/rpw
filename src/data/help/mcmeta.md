# "McMeta" files

"McMeta" files are metadata files that accompany some texture files in a resource pack.
Those files can define animation speed, frame order, add blur etc.

All "McMeta" files use **JSON** format. Any syntax errors in the format will most likely
cause the "failed texture" (purple-black checkered) to be used instead. Since it's JSON, 
all whitespace (spaces, tabs, line-breaks) outside strings ("string") will be ignored,
so you can format the files as you want.


## McMeta sections

The JSON object consists of several sections, keys. Since you will be using it to alter
textures, we'll focus on those.


### Animation

The section "animation" can be used only for blocks and items.

    {
      "animation": {}
    }

If left empty (`{}`), the default timing and frame order will be used, but there's no way
to tell what exactly that will be.

The "animation" section can contain two tags:


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
* when omitted, default frameset is used

Example:

    {
      "animation": {
        "frames": [
          0, 1, 2, 3, 4, 5
        ]
      }
    }

To define duration of individual frames, you'd use this syntax instead of frame number:

    { "index" : 2, "time" : 40 }

...like this:

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


### Texture

The section "texture" works only for some textures, such as pumpkin and under-water screen overlays.

    {
      "texture": {}
    }

There are two possible **boolean** options for "texture":

    {
      "texture": {
        "blur": true,
        "clamp": false
      }
    }

Blur makes the texture blurred, and clamp is used to prevent texture tiling. There isn't much you can do
with "texture", as you can see.
