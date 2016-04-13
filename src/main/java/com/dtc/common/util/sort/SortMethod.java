package com.dtc.common.util.sort;

import java.util.List;
/**
 * @category 排序算法枚举
 * @author tim
 *
 */
public enum SortMethod {
	//堆排序升序
	HEAP_ASC() {
		@Override
		public <T extends Comparable<T>> List<T> sort(List<T> arr) {
			return new HeapSort(arr, SortOrder.ASC).sort();
		}
	},
	//堆排序降序
	HEAP_DESC() {
		@Override
		public <T extends Comparable<T>> List<T> sort(List<T> arr) {
			return new HeapSort(arr, SortOrder.DESC).sort();
		}
	};

	public abstract <T extends Comparable<T>> List<T> sort(List<T> arr);

}
