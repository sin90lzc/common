package com.dtc.common.zookeeper;

import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;

import com.dtc.common.util.StrUtils;

/**
 * 
 * @category 同步事件观察器
 * 
 *           用于zookeeper需要同步等待的一次性事件 当{@link WaitEventWatcher#eventType}
 *           发生时，并且事件对象是{@link WaitEventWatcher#nodePath}，唤醒等待。
 * @author tim
 *
 */
public class WaitEventWatcher extends CountDownLatch implements Watcher {

	private static final Logger logger = LogManager.getLogger();

	private String nodePath;

	private EventType eventType;

	public WaitEventWatcher(String nodePath, EventType eventType) {
		super(1);
		this.nodePath = nodePath;
		this.eventType = eventType;
		if (StrUtils.isEmpty(this.nodePath) || this.eventType == null || this.eventType == EventType.None) {
			logger.error("illegal argument exception:this.nodePath:{},this.eventType:{}", this.nodePath,
					this.eventType);
			throw new IllegalArgumentException();
		}
		logger.debug("watching for path:{} and event:{}", this.nodePath, this.eventType);
	}

	@Override
	public void process(WatchedEvent event) {
		logger.entry(event);
		EventType eventType = event.getType();
		if (this.eventType.equals(eventType)) {
			if (StrUtils.equals(event.getPath(), this.nodePath)) {
				logger.debug("event occour,wake watcher : {}",this);
				this.countDown();
			}
		}
	}

	@Override
	public String toString() {
		return "WaitEventWatcher [nodePath=" + nodePath + ", eventType=" + eventType + "]";
	}

}
