package com.pixbits.tasks;

public enum BlockSize
{
  NO_CHANGE("No Change",0),
  _8X8("8x8",8),
  _16X16("16x16",16),
  _32X32("32x32",32),
  _64X64("64x64",64),
  _128X128("128x128",128)
  ;
  
  public final int size;
  public final String name;
  
  BlockSize(String name, int size)
  {
    this.size = size;
    this.name = name;
  }
  
  public String toString() { return name; }
}
