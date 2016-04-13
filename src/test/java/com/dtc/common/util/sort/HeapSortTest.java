/**
 * 
 */
package com.dtc.common.util.sort;

import java.util.Arrays;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @category 堆排序测试
 * @author tim
 *
 */
@Test(groups = { "common", "util", "sort" })
public class HeapSortTest {

	@BeforeClass
	private void beforeClass() {

	}

	/**
	 * 
	 * @category 测试正向排序
	 * @param l
	 */
	@Test(dataProvider = "list_provider")
	public void testAscSort(List<Integer> l) {
		HeapSort heapSort = new HeapSort(l);
		List<Integer> sortList = heapSort.sort();

		for (int i = 1; i < sortList.size(); i++) {
			Integer left = sortList.get(i - 1);
			Integer right = sortList.get(i);
			Assert.assertTrue(left <= right);
		}

	}

	/**
	 * 
	 * @category 测试反向排序
	 * @param l
	 */
	@Test(dataProvider = "list_provider")
	public void testDescSort(List<Integer> l) {
		HeapSort heapSort = new HeapSort(l, SortOrder.DESC);
		List<Integer> sortList = heapSort.sort();

		for (int i = 1; i < sortList.size(); i++) {
			Integer left = sortList.get(i - 1);
			Integer right = sortList.get(i);
			Assert.assertTrue(left >= right);
		}
	}

	/**
	 * 
	 * @category 测试最小N个值
	 * @param l
	 */
	@Test(dataProvider = "list_provider")
	public void testAscTop(List<Integer> l) {
		TopScreen topScreen = new HeapSort(l);
		List<Integer> topList = topScreen.top(2);

		for (int i = 1; i < topList.size(); i++) {
			Integer left = topList.get(i - 1);
			Integer right = topList.get(i);
			Assert.assertTrue(left <= right);
		}

		for (int i = 0; i < topList.size(); i++) {
			lessThanAssert(l, topList, topList.get(i));
		}
	}

	/**
	 * 
	 * @category 测试最大N个值
	 * @param l
	 */
	@Test(dataProvider = "list_provider")
	public void testDescTop(List<Integer> l) {
		TopScreen topScreen = new HeapSort(l, SortOrder.DESC);
		List<Integer> topList = topScreen.top(2);

		for (int i = 1; i < topList.size(); i++) {
			Integer left = topList.get(i - 1);
			Integer right = topList.get(i);
			Assert.assertTrue(left >= right);
		}

		for (int i = 0; i < topList.size(); i++) {
			moreThanAssert(l, topList, topList.get(i));
		}
	}

	private void lessThanAssert(List<Integer> l, List<Integer> topList, Integer v) {
		for (Integer i : l) {
			if (!topList.contains(i)) {
				Assert.assertTrue(v <= i);
			}
		}
	}

	private void moreThanAssert(List<Integer> l, List<Integer> topList, Integer v) {
		for (Integer i : l) {
			if (!topList.contains(i)) {
				Assert.assertTrue(v >= i);
			}
		}
	}

	@DataProvider(name = "list_provider")
	private Object[][] dataProvider() {
		return new Object[][] { { Arrays.asList(1, 2, 0, 3, 9, 11, 5, 67, 89, 100, -21) },
				{ Arrays.asList(3, 2, 1, 4, 7, 99, 44, 12, 67, 0, -1) },
				{ Arrays.asList(101, 100, 989, 768, -100, 0, -44, 100) } };
	}

}
