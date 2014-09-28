package com.pixbits.tasks;

import java.util.*;

public class StitchJson
{
  public static class Element
  {
    String key;
    String hashCode;
    int x, y;
    int w, h;
  }
 
  public static class Category
  {
    AssetCategory category;
    ArrayList<Element> elements;
  }
}
