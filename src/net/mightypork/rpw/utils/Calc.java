package net.mightypork.rpw.utils;

/**
 * Math helper
 *
 * @author Ondřej Hruška (MightyPork)
 */
public class Calc {

    /**
     * Convert double to string, remove the mess at the end.
     *
     * @param d double
     * @return string
     */
    public static String doubleToString(double d) {
        String s = Double.toString(d);
        s = s.replaceAll("([0-9]+\\.[0-9]+)00+[0-9]+", "$1");
        s = s.replaceAll("0+$", "");
        s = s.replaceAll("\\.$", "");
        return s;
    }


    /**
     * Convert float to string, remove the mess at the end.
     *
     * @param f float
     * @return string
     */
    public static String floatToString(float f) {
        String s = Float.toString(f);
        s = s.replaceAll("([0-9]+\\.[0-9]+)00+[0-9]+", "$1");
        s = s.replaceAll("0+$", "");
        s = s.replaceAll("\\.$", "");
        return s;
    }

}
