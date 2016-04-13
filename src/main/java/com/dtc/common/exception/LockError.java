package com.dtc.common.exception;

/**
 * 
 * @category 加锁异常
 * @author tim
 *
 */
public class LockError extends Exception{

	/**
	 * @category
	 */
	private static final long serialVersionUID = -4682302056453132053L;
	

	public LockError(){
		super();
	}
	
	public LockError(String errMsg){
		super(errMsg);
	}
	
	public LockError(Throwable e){
		super(e);
	}
}
