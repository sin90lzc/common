/**
 * 
 */
package com.dtc.common.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.dtc.common.exception.LockError;
import com.dtc.common.exception.UnLockError;

/**
 * @category @author tim
 *
 */
@Test(groups = { "common", "lock" })
public class ZKFlowLockTest {

	private static final Logger logger = LogManager.getLogger();
	
	private ThreadLocal<Lock> lock=new ThreadLocal<>();

	private static AtomicInteger releaseCounter = new AtomicInteger(1);

	private static AtomicInteger realCounter = new AtomicInteger(0);

	@BeforeMethod()
	private void getLock() {
		logger.debug("getLock current Thread name:{}",Thread.currentThread().getName());
		lock.set(new ZKFlowLock("lockTest"));
	}
	@Test(threadPoolSize = 1000,invocationCount=1000)
	public void lockTest() throws LockError, UnLockError, InterruptedException {
		logger.debug("lockTest current Thread name:{},Thread ID:{}",Thread.currentThread().getName(),Thread.currentThread().getId());
		
		lock.get().lock();
		TimeUnit.SECONDS.sleep(1L);
		logger.debug("aquire lock {}",lock.get());
		Assert.assertEquals(realCounter.incrementAndGet(), releaseCounter.get());
		logger.debug("unlock {}",lock.get());
		releaseCounter.incrementAndGet();
		
		lock.get().unLock();
	}

//	@Test(dependsOnMethods={"lockTest"})
//	public void unlockTest() throws UnLockError {
//		logger.debug("unlockTest current Thread name:{},Thread ID:{}",Thread.currentThread().getName(),Thread.currentThread().getId());
//		lock.set(new ZKFlowLock("lockTest"));
//		releaseCounter.incrementAndGet();
//		lock.get().unLock();
//	}

}
