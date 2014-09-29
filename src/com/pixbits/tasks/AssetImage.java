package com.pixbits.tasks;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import net.mightypork.rpw.tree.assets.AssetEntry;

public class AssetImage implements Comparable<AssetImage>
{
  final AssetEntry entry;
  final File file;
  BufferedImage image;
  StitchJson.Element element;
  
  AssetImage(File file, AssetEntry entry)
  {
    this.file = file;
    this.entry = entry;
    
    element = new StitchJson.Element();
  }
  
  public int compareTo(AssetImage o)
  {
    int area1 = element.w*element.h;
    int area2 = o.element.w*o.element.h;
    
    if (area1 == area2)
      return entry.getKey().compareTo(o.entry.getKey());
    else
      return area2 - area1;
  }
  
  public void cacheImage() throws IOException
  {
    image = ImageIO.read(file);
    
    element.w = image.getWidth();
    element.h = image.getHeight();
  }
}
