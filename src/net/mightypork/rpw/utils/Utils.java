package net.mightypork.rpw.utils;

import java.io.Closeable;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipFile;

import net.mightypork.rpw.utils.logging.Log;


/**
 * Sector's utils class
 *
 * @author Ondřej Hruška (MightyPork)
 */
public class Utils {
    public static String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        return dateFormat.format(date);
    }


    public static Object fallback(Object... options) {
        for (final Object o : options) {
            if (o != null) return o;
        }
        return null; // error
    }


    public static String fromLastDot(String s) {
        return fromLastChar(s, '.');
    }


    public static String toLastDot(String s) {
        return toLastChar(s, '.');
    }


    public static String fromLastChar(String s, char c) {
        if (s == null) return null;
        return s.substring(s.lastIndexOf(c) + 1, s.length());
    }


    public static String toLastChar(String s, char c) {
        if (s == null) return null;
        return s.substring(0, s.lastIndexOf(c));
    }


    /**
     * Sort a map by keys, maintaining key-value pairs.
     *
     * @param map map to be sorted
     * @return linked hash map with sorted entries
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <K extends Comparable, V extends Comparable> Map<K, V> sortByKeys(Map<K, V> map) {
        final List<K> keys = new LinkedList<K>(map.keySet());
        Collections.sort(keys);

        // LinkedHashMap will keep the keys in the order they are inserted
        // which is currently sorted on natural ordering
        final Map<K, V> sortedMap = new LinkedHashMap<K, V>();
        for (final K key : keys) {
            sortedMap.put(key, map.get(key));
        }

        return sortedMap;
    }


    /**
     * Sort a map by values, maintaining key-value pairs.
     *
     * @param map map to be sorted
     * @return linked hash map with sorted entries
     */
    @SuppressWarnings("rawtypes")
    public static <K extends Comparable, V extends Comparable> Map<K, V> sortByValues(Map<K, V> map) {
        final List<Map.Entry<K, V>> entries = new LinkedList<Map.Entry<K, V>>(map.entrySet());

        Collections.sort(entries, new Comparator<Map.Entry<K, V>>() {

            @Override
            public int compare(Entry<K, V> o1, Entry<K, V> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        // LinkedHashMap will keep the keys in the order they are inserted
        // which is currently sorted on natural ordering
        final Map<K, V> sortedMap = new LinkedHashMap<K, V>();

        for (final Map.Entry<K, V> entry : entries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }


    public static void printStackTrace() {
        (new Throwable()).printStackTrace();
    }


    /**
     * Sleep without throwing anything
     */
    public static void sleep(int milis) {
        try {
            Thread.sleep(milis);
        } catch (final InterruptedException e) {
        }
    }


    public static boolean isValidFilenameChar(char ch) {
        return isValidFilenameString(Character.toString(ch));
    }


    public static boolean isValidFilenameString(String filename) {
        return filename.matches("[a-zA-Z0-9 +\\-.,_%@#!]+");
    }


    public static boolean isValidIdentifierChar(char ch) {
        return isValidIdentifierString(Character.toString(ch));
    }


    public static boolean isValidIdentifierString(String filename) {
        return filename.matches("[a-zA-Z0-9._]+");
    }


    public static String cropStringAtStart(String orig, int length) {
        if (orig.length() > length) {
            orig = "\u2026" + orig.substring(strSizeWeighted(orig) - length, orig.length());
        }
        return orig;
    }


    public static String cropStringAtEnd(String orig, int length) {
        if (orig.length() > length) {
            orig = orig.substring(0, Math.min(strSizeWeighted(orig), length) - 1) + "\u2026";
        }
        return orig;
    }


    private static int strSizeWeighted(String str) {
        double size = 0;
        for (final char c : str.toCharArray()) {
            final String s = String.valueOf(c);
            if ("1li,.'I;".contains(s)) {
                size += 0.4;
            } else if ("WwmM".contains(s)) {
                size += 1.5;
            } else if ("tf".contains(s)) {
                size += 0.8;
            } else {
                size += 1;
            }
        }
        return (int) Math.round(size);
    }


    public static String arrayToString(Object[] sounds) {
        final StringBuilder sb = new StringBuilder();

        sb.append('[');
        final boolean first = true;
        for (final Object o : sounds) {
            if (!first) sb.append(',');
            sb.append(o.toString());
        }
        sb.append(']');

        return sb.toString();
    }


    public static <T extends Object> List<T> arrayToList(T[] objs) {
        final ArrayList<T> list = new ArrayList<T>();
        for (final T o : objs) {
            list.add(o);
        }
        return list;
    }


    public static void close(Object... something) {
        if (something == null || something.length == 0) return;
        for (final Object o : something) {
            if (o == null) continue;
            try {
                if (o instanceof Closeable) {
                    ((Closeable) o).close();
                    continue;
                }

                if (o instanceof ZipFile) {
                    ((ZipFile) o).close();
                    continue;
                }

                final Method m = o.getClass().getMethod("close");
                m.setAccessible(true);
                m.invoke(o);

            } catch (final Exception e) {
                Log.e("Could not close " + o.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    }
}
