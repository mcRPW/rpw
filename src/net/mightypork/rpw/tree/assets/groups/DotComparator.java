package net.mightypork.rpw.tree.assets.groups;


import java.util.Comparator;


public class DotComparator<T extends Comparable<T>> implements Comparator<T> {

	@Override
	public int compare(T a, T b) {

		Integer dotsA = a.toString().replaceAll("[^.]*", "").length();
		Integer dotsB = b.toString().replaceAll("[^.]*", "").length();

		if (dotsA != dotsB) {
			return dotsA.compareTo(dotsB);
		}

		return -a.compareTo(b); // backwards
	}

}
