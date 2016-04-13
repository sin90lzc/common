package com.dtc.common.exception;

/**
 * @category 解锁异常
 * @author tim
 *
 */
public class UnLockError extends Exception {

	/**
	 * @category
	 */
	private static final long serialVersionUID = -3460509754814772910L;

	public UnLockError() {
		super();
	}

	public UnLockError(Throwable e) {
		super(e);
	}

	public UnLockError(String errMsg) {
		super(errMsg);
	}

}
