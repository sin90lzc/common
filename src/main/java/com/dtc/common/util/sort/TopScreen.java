package com.dtc.common.util.sort;

import java.util.List;

/**
 * 
 * @category 最大或最小视图，目标是筛选出数组的N个最大值或N个最小值
 * @author tim
 *
 */
public interface TopScreen {

	public <T extends Comparable<T>> List<T> top(int topN);
	
}
