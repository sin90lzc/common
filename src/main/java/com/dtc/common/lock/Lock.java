package com.dtc.common.lock;

import com.dtc.common.exception.LockError;
import com.dtc.common.exception.UnLockError;
/**
 * 锁接口
 * @author tim
 *
 */
public interface Lock {

	public static final String FLOW_LOCK_ROOT_PATH="/flowlock";
	
	/**
	 * @category 对资源加锁
	 * @param resource 要锁定的资源唯一标识
	 */
	public void lock() throws LockError;

	/**
	 * @category 对资源解锁
	 * @param resource
	 */ 
	public void unLock() throws UnLockError;
	
}
