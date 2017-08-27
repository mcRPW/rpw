package com.pixbits.rpw.stitcher;

public enum Scale {
    ZEROTWOFIVE("x0.25", 0.25f), ZEROFIVE("x0.5", 0.5f), ONE("x1", 1), TWO("x2", 2), FOUR("x4", 4), EIGHT("x8", 8), SIXTEEN("x16", 16), THIRTYTWO("x32", 32);

    public final float scale;
    public final String name;


    Scale(String name, float scale) {
        this.scale = scale;
        this.name = name;
    }


    @Override
    public String toString()
    {
        return name;
    }
}