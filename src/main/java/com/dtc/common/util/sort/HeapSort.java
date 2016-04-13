package com.dtc.common.util.sort;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @category 堆排序算法实现
 * 
 *           堆排序
 *           原理：把数组看成是二叉树，并把二叉树排列成大根堆二叉树或小根堆二叉树（算法的核心思想），大根堆二叉树是指所有父节点的值都比子节点大，
 *           小根堆二叉树则指所有父节点的值都比子节点小。
 *           因此位于最顶端的父节点一定是最大（最小）值，取出最大（最小）值，把除最大（最小）值之外的数再次排列成大根（小根）堆二叉数，再取出次大值
 *           ，如此循环...
 *
 *           核心计算公式： 最后一个二叉树的父节点序号：arr.length/2-1 父节点左边子节点序号：parent*2+1
 *           父节点右边子节点序号：parent*2+2
 * 
 *           堆排序适用于查找最大N个，或最小N个值的场景
 * 
 * @author tim
 *
 */
class HeapSort extends AbstractSortMethod implements Sortable, TopScreen {

	public <T extends Comparable<T>> HeapSort(List<T> arr) {
		this(arr, SortOrder.ASC);
	}

	public <T extends Comparable<T>> HeapSort(List<T> arr, SortOrder order) {
		super(arr, order);
	}

	/**
	 * 
	 * @category 从_parent节点至length之间的一次大根堆排序逻辑，一共要排_parent+1次才能找到一个最大值
	 * @param arr
	 *            要排列的数组
	 * @param _parent
	 *            父节点索引
	 * @param length
	 *            在数组的length长度范围内进行大根堆排序
	 */
	private <T extends Comparable<T>> void flowUp(List<T> arr, int _parent, int length) {
		T p = arr.get(_parent);
		int child = 2 * _parent + 1;// 父节点的左侧子节点
		while (child < length) {
			if (child + 1 < length && arr.get(child + 1).compareTo(arr.get(child)) > 0) {
				child++;
			}
			if (arr.get(child).compareTo(p) <= 0) {
				break;
			}
			arr.set(_parent, arr.get(child));
			_parent = child;
			child = 2 * _parent + 1;
		}
		arr.set(_parent, p);
	}

	/**
	 * 
	 * @category 从_parent节点至length之间的一次小根堆排序逻辑，一共要排_parent+1次才能找到一个最小值
	 * @param arr
	 *            要排列的数组
	 * @param _parent
	 *            父节点索引
	 * @param length
	 *            在数组的length长度范围内进行小根堆排序
	 */
	private <T extends Comparable<T>> void flowDown(List<T> arr, int _parent, int length) {
		T p = arr.get(_parent);
		int child = 2 * _parent + 1;// 父节点的左侧子节点
		while (child < length) {
			if (child + 1 < length && arr.get(child + 1).compareTo(arr.get(child)) < 0) {
				child++;
			}
			if (arr.get(child).compareTo(p) >= 0) {
				break;
			}
			arr.set(_parent, arr.get(child));
			_parent = child;
			child = 2 * _parent + 1;
		}
		arr.set(_parent, p);
	}

	@Override
	public <T extends Comparable<T>> List<T> positiveSort() {
		return positiveSort((List<T>) this.arr, -1);
	}

	private <T extends Comparable<T>> List<T> positiveSort(List<T> arr, int highestTopN) {
		// 使整个二叉树按大根堆排列
		for (int i = arr.size() / 2 - 1; i >= 0; i--) {
			flowUp(arr, i, arr.size());
		}
		int limit = highestTopN >= 0 ? arr.size() - highestTopN - 1 : 0;

		List<T> topList = null;
		if (highestTopN >= 0) {
			topList = new ArrayList<>(highestTopN);
		}
		for (int i = arr.size() - 1; i > limit; i--) {
			// 由于现在数组的二叉树按大根堆排列，交换首尾元素意味着最大值的元素会被置于数组末尾
			T temp = arr.get(0);
			arr.set(0, arr.get(i));
			arr.set(i, temp);
			if (topList != null)
				topList.add(temp);
			// 由于按大根堆排列的二叉树，最大值必然会在二叉树顶层，而次大值必然会在第二层，而这里i值控制了数组的范围，使得已排序的最大值不受flowUp影响，因此再次执行一次flowUp,次大值将上浮至二叉树顶层
			flowUp(arr, 0, i);
		}
		return topList != null ? topList : arr;
	}

	private <T extends Comparable<T>> List<T> reverseSort(List<T> arr, int highestTopN) {
		// 使整个二叉树按小根堆排列
		for (int i = arr.size() / 2 - 1; i >= 0; i--) {
			flowDown(arr, i, arr.size());
		}
		int limit = highestTopN >= 0 ? arr.size() - highestTopN - 1 : 0;

		List<T> topList = null;
		if (highestTopN >= 0) {
			topList = new ArrayList<>(highestTopN);
		}
		for (int i = arr.size() - 1; i > limit; i--) {
			// 由于现在数组的二叉树按小根堆排列，交换首尾元素意味着最小值的元素会被置于数组末尾
			T temp = arr.get(0);
			arr.set(0, arr.get(i));
			arr.set(i, temp);
			if (topList != null)
				topList.add(temp);
			// 由于按小根堆排列的二叉树，最小值必然会在二叉树顶层，而次小值必然会在第二层，而这里i值控制了数组的范围，使得已排序的最大值不受flowDown影响，因此再次执行一次flowDown,次小值将上浮至二叉树顶层
			flowDown(arr, 0, i);
		}
		return topList != null ? topList : arr;
	}

	@Override
	public <T extends Comparable<T>> List<T> top(int topN) {
		switch (this.order) {
		case ASC:
			return reverseSort((List<T>) this.arr, topN);
		case DESC:
			return positiveSort((List<T>) this.arr, topN);
		default:
			break;
		}
		return null;
	}

	public static void main(String[] args) {
		List<Integer> l = new ArrayList<Integer>();
		l.add(1);
		l.add(9);
		l.add(8);
		l.add(6);
		l.add(7);
		l.add(4);
		l.add(3);
		l.add(9);

		l = new HeapSort(l, SortOrder.ASC).top(1);
		System.out.println("result:");
		for (Integer i : l) {
			System.out.print(i);
			System.out.print(",");
		}
	}
}
