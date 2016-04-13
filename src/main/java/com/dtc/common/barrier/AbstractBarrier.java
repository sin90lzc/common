package com.dtc.common.barrier;

public abstract class AbstractBarrier implements Barrier{

	protected int threshold;
	
	public AbstractBarrier(int threshold){
		if(threshold<1){
			throw new RuntimeException("threshold should bigger than zero!");
		}
		this.threshold=threshold;
	}
	
}
