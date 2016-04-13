package com.dtc.common.util.sort;

import java.util.Collections;
import java.util.List;

import javax.sound.midi.ControllerEventListener;

abstract class AbstractSortMethod implements Sortable {

	protected List<? extends Comparable<?>> arr;
	
	/**
	 * @category 排列顺序
	 */
	protected SortOrder order;
	
	public <T extends Comparable<T>> AbstractSortMethod(List<T> arr) {
		this(arr,SortOrder.ASC);
	}
	
	public <T extends Comparable<T>> AbstractSortMethod(List<T> arr,SortOrder order){
		this.order=order;
		this.arr=arr;
	}
	
	@Override
	public <T extends Comparable<T>> List<T> sort() {
		
		List<T> positiveSort = positiveSort();
		
		if(this.order!=SortOrder.ASC){
			return reverseSort(positiveSort);
		}
		
		return positiveSort;
	}
	
	/**
	 * 
	 * @category 正向排序（由小至大） 
	 * @param arr
	 * @return
	 */
	public abstract <T extends Comparable<T>> List<T> positiveSort();
	
	/**
	 * 
	 * @category 反向排序（由大至小）
	 * @param arr
	 * @return
	 */
	protected <T extends Comparable<T>> List<T> reverseSort(List<T> arr){
		Collections.reverse(arr);
		return arr;
	}

}
