package com.pixbits.rpw.stitcher;

import java.awt.Rectangle;
import java.awt.Point;
import java.util.*;


public class AssetLayout implements Iterable<AssetImage>
{
	private final SortedSet<AssetImage> entries;
	private Node tree;


	AssetLayout() {
		entries = new TreeSet<AssetImage>();
	}


	@Override
	public Iterator<AssetImage> iterator()
	{
		return entries.iterator();
	}


	void add(AssetImage asset)
	{
		entries.add(asset);
	}


	private void reset()
	{
		for (AssetImage asset : entries)
			asset.reset();
	}


	Point computeLayout()
	{
		int gw = 16, gh = 16;

		while (!computeLayout(gw, gh)) {
			if (gw > gh) gh *= 2;
			else gw *= 2;
		}

		return new Point(gw, gh);
	}


	public AssetImage first()
	{
		return entries.first();
	}


	static String rectToString(Rectangle rc)
	{
		return "{" + rc.x + ", " + rc.y + ", " + rc.width + "x" + rc.height + "}";
	}


	private boolean computeLayout(int gw, int gh)
	{
		reset();
		tree = new Node(0, 0, gw, gh);

		for (AssetImage asset : entries) {
			Node n = tree.insert(asset);

			if (n == null) return false;

			// System.out.println("Inserted at "+rectToString(n.rc));

			n.element = asset;
			asset.place(n.rc.x, n.rc.y);
		}

		return true;
	}

	private static class Node
	{
		final Node[] child = new Node[2];
		final Rectangle rc;
		AssetImage element;


		Node(int x, int y, int w, int h) {
			rc = new Rectangle(x, y, w, h);
		}


		Node insert(AssetImage asset)
		{
			if (element == null && child[0] != null && child[1] != null) {
				Node newNode = child[0].insert(asset);

				if (newNode != null) return newNode;
				else {
					// System.out.println("Can't fit in "+rectToString(child[0].rc)+", testing other child");
					return child[1].insert(asset);
				}
			} else {
				if (element != null) return null;

				if (asset.width() > rc.width || asset.height() > rc.height) return null;
				else if (asset.width() == rc.width && asset.height() == rc.height) return this;

				int dw = rc.width - asset.width();
				int dh = rc.height - asset.height();

				if (dw > dh) {
					child[0] = new Node(rc.x, rc.y, asset.width(), rc.height);
					child[1] = new Node(rc.x + asset.width(), rc.y, rc.width - asset.width(), rc.height);
				} else {
					child[0] = new Node(rc.x, rc.y, rc.width, asset.height());
					child[1] = new Node(rc.x, rc.y + asset.height(), rc.width, rc.height - asset.height());
				}

				// System.out.println("Splitting "+rectToString(rc)+" into "+rectToString(child[0].rc)+" and "+rectToString(child[1].rc));

				return child[0].insert(asset);
			}
		}
	}

	public Set<AssetImage> getEntries() {
		return entries;
	}
}