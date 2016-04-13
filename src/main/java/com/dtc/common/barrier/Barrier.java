package com.dtc.common.barrier;

/**
 * 
 * @category Barrier接口
 * @author tim
 *
 */
public interface Barrier {

	/**
	 * 
	 * @category 进入栏栅
	 * @return
	 */
	public void enter();
	
	/**
	 * 
	 * @category 离开栏栅
	 * @return
	 */
	public void leave();
	
}
