/**
 * 
 */
package com.dtc.common.exception;

/**
 * @category @author tim 2016年4月20日
 */
public class TimeoutException extends RuntimeException {

	/**
	 * @category
	 */
	private static final long serialVersionUID = 7178612242410520552L;

	/**
	 * @category
	 */
	public TimeoutException() {
	}

	public TimeoutException(Throwable e) {
		super(e);
	}

	public TimeoutException(String errMsg) {
		super(errMsg);
	}
}
