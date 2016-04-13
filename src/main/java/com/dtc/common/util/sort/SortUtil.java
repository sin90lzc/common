package com.dtc.common.util.sort;

import java.util.List;

public class SortUtil {

	private static final SortMethod DEFAULT_SORT_METHOD = null;

	public static final <T extends Comparable<T>> List<T> sort(List<T> arr) {
		return sort(arr, DEFAULT_SORT_METHOD);
	}

	public static final <T extends Comparable<T>> List<T> sort(List<T> arr, SortMethod sortMethod) {
		return sortMethod.sort(arr);
	}

	public static final <T extends Comparable<T>> List<T> topList(List<T> arr, SortOrder order, int topN) {
		TopScreen topScreen = new HeapSort(arr, order);
		return topScreen.top(topN);
	}

}
