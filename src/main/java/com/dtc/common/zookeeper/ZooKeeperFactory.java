package com.dtc.common.zookeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class ZooKeeperFactory { 

	private static final Logger logger=LogManager.getLogger();
	
	private static String connectString = null;

	private static Integer sessionTimeout = null;

	static {
		connectString = "172.16.31.1:2182,172.16.31.1:2181,172.16.31.1:2183";
		sessionTimeout = 100;
	}

	public static final ZooKeeper getZooKeeper(String chroot, Watcher watcher, boolean isReadOnly) {
		ZooKeeper zk = null;
		try {
			if (StringUtils.isNoneEmpty(chroot)) {
				connectString = connectString + chroot;
			}
			ConnectedWatcher connectedWatcher = new ConnectedWatcher(watcher);
			zk = new ZooKeeper(connectString, sessionTimeout, connectedWatcher, isReadOnly);
			connectedWatcher.waitConnected();
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return zk;
	}

	private static class ConnectedWatcher implements Watcher {
		private Watcher orignWatcher;

		private CountDownLatch countDownLatch = new CountDownLatch(1);

		private ConnectedWatcher(Watcher orignWatcher) {
			this.orignWatcher = orignWatcher;
		}

		@Override
		public void process(WatchedEvent event) {
			logger.entry(event);
			switch (event.getState()) {
			case SyncConnected:
				countDownLatch.countDown();
				break;
			default:
				break;
			}
			if(orignWatcher!=null)
				orignWatcher.process(event);
		}
		
		public void waitConnected(){
			try {
				countDownLatch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
