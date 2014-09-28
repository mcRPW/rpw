package com.pixbits.tasks;

import java.io.InputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.awt.Dimension;
import java.awt.RenderingHints;
import java.awt.image.*;
import java.awt.Graphics2D;
import java.awt.geom.*;

import javax.imageio.ImageIO;

import net.mightypork.rpw.library.Sources;
import net.mightypork.rpw.project.Project;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.library.*;
import net.mightypork.rpw.tree.assets.*;
import net.mightypork.rpw.tree.assets.groups.*;
import net.mightypork.rpw.utils.logging.Log;



public class Tasks
{ 
  public static void exportPackToStitchedPng(File outputFolder, Project project, Set<AssetCategory> categories, boolean exportMissing, boolean exportExisting)
  {
    for (AssetCategory c : categories)
      exportPackToStitchedPng(outputFolder, project, c, exportMissing, exportExisting);
  }
  
  private static void exportPackToStitchedPng(File outputFolder, Project project, AssetCategory category, boolean exportMissing, boolean exportExisting)
  {
    VanillaPack vanilla = Sources.vanilla;
       
    File output = new File(outputFolder.getAbsolutePath() + File.separator + category.name.toLowerCase() + ".png");
    
    Collection<AssetEntry> totalEntries = vanilla.getAssetEntries();
    List<AssetEntry> entries = new ArrayList<AssetEntry>();
    
    GroupFilter filter = new GroupFilter(null, category.prefix);

    for (AssetEntry e : totalEntries)
    {
      if (filter.matches(e))
        entries.add(e);
    }
 
    try {
      Dimension dimension = null;
      int imgType = BufferedImage.TYPE_INT_ARGB;
      
      for (AssetEntry e : entries)
      {      
        File file = project.getAssetFile(e.getKey());
        BufferedImage img = null;
        
        if (file != null)
        {
            img = ImageIO.read(file);
            imgType = img.getType();
            
            if (dimension == null) 
            {
              Log.i("Stitcher recognized image size: "+img.getWidth()+", "+img.getHeight());
              dimension = new Dimension(img.getWidth(), img.getHeight());
            }
            else if (img.getWidth() != dimension.width || img.getHeight() != dimension.height)
            {
              Log.e("Can't stitch a resource pack with different sizes per asset category");
              return;
            }
  
        }
      }
      
      int count = entries.size();
      final int rows = (int)Math.ceil(Math.sqrt(count));
      Dimension imageSize = new Dimension(rows*dimension.width, rows*dimension.height);
      Log.i("Stitcher export image size: "+imageSize.width+", "+imageSize.height);
  
      BufferedImage image = new BufferedImage(imageSize.width, imageSize.height, imgType);
      Graphics2D g2d = (Graphics2D)image.getGraphics();
      
      g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            
      final int w = dimension.width, h = dimension.height;
      //final int tw = imageSize.width, th = imageSize.height;
      int x = 0, y = 0;

      for (AssetEntry e : entries)
      {
        File file = project.getAssetFile(e.getKey());
        BufferedImage img = null;
        
        int dx = x*w;
        int dy = y*h;
        
        if (file != null && exportExisting)
          img = ImageIO.read(file);
        else if (file == null && exportMissing)
          img = ImageIO.read(vanilla.getAssetFile(e.getKey()));
        
        if (img != null)
          g2d.drawImage(img, dx, dy, dimension.width, dimension.height, null);
        
        ++x;
        if (x >= rows)
        {
          x %= rows;
          ++y;
        }
      }
      
      ImageIO.write(image, "png", output);
    }
    catch (IOException exception)
    {
      Log.e(exception);
    }
  }
}
