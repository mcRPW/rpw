package com.pixbits.rpw.stitcher;

import java.util.*;


public class StitchJson
{
	public static class Element
	{
		String key;
		String hashCode;
		int x, y;
		int w, h;


		Element() {
			x = -1;
			y = -1;
		}
	}

	public static class Category
	{
		ArrayList<Element> elements;
	}
}
