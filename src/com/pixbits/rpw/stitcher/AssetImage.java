package com.pixbits.rpw.stitcher;

import java.io.File;
import java.io.IOException;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

import javax.imageio.ImageIO;

import net.mightypork.rpw.tree.assets.AssetEntry;


public class AssetImage implements Comparable<AssetImage>
{
	final AssetEntry entry;
	final File file;
	BufferedImage image;
	StitchJson.Element element;
	boolean placed;


	AssetImage(File file, AssetEntry entry) {
		this.file = file;
		this.entry = entry;

		placed = true;

		element = new StitchJson.Element();
		element.key = entry.getKey();
	}


	@Override
	public boolean equals(Object o)
	{
		return o != null & o instanceof AssetImage && ((AssetImage) o).entry.equals(entry);
	}


	@Override
	public int compareTo(AssetImage o)
	{
		int area1 = element.w * element.h;
		int area2 = o.element.w * o.element.h;

		if (area1 == area2) return entry.getKey().compareTo(o.entry.getKey());
		else return area2 - area1;
	}


	public int width()
	{
		return element.w;
	}


	public int height()
	{
		return element.h;
	}


	public int x()
	{
		return element.x;
	}


	public int y()
	{
		return element.y;
	}


	public void place(int x, int y)
	{
		element.x = x;
		element.y = y;
	}


	public void reset()
	{
		place(-1, -1);
	}


	public void cacheImage(float w, float h) throws IOException
	{
		BufferedImage tmpImage = ImageIO.read(file);

		if (tmpImage.getWidth() == w && tmpImage.getHeight() == h) image = tmpImage;
		else {
			image = new BufferedImage((int)(tmpImage.getWidth() * w), (int)(tmpImage.getHeight() * h), tmpImage.getType());
			((Graphics2D) image.getGraphics()).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			((Graphics2D) image.getGraphics()).drawImage(tmpImage, 0, 0, (int)(tmpImage.getWidth() * w), (int)(tmpImage.getHeight() * h), null);
		}

		element.w = image.getWidth();
		element.h = image.getHeight();
	}

}