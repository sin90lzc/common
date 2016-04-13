package com.dtc.common.exception;

public class BarrierRuntimeException extends RuntimeException{

	/**
	 * @category
	 */
	private static final long serialVersionUID = 535554179008775752L;
	public BarrierRuntimeException(){
		super();
	}
	
	public BarrierRuntimeException(String e){
		super(e);
	}
	
	public BarrierRuntimeException(Throwable e){
		super(e);
	}
}
