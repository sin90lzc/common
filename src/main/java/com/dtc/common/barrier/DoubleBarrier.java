package com.dtc.common.barrier;

import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import com.dtc.common.exception.BarrierRuntimeException;
import com.dtc.common.zookeeper.ZooKeeperFactory;

public class DoubleBarrier extends AbstractBarrier {

	private static final Logger logger=LogManager.getLogger();
	
	private static final String BARRIER_ROOT_PATH = "/double_barrier";
	private static final String READY_NODE_NAME = "ready";

	private static final String BARRIER_ELEMENT_NAME = "b_";

	private String BARRIER_PATH = null;

	private static Integer mutex = new Integer(-1);

	private static Integer leaveLock = new Integer(-1);

	private static Integer largerLock = new Integer(-1);

	private ZooKeeper zk;

	private ThreadLocal<String> barrierElementPath = new ThreadLocal<>();

	public DoubleBarrier(int threshold, String barrierName) {
		super(threshold);
		if (StringUtils.isEmpty(barrierName)) {
			throw new RuntimeException("barrierName should not be empty!");
		}
		BARRIER_PATH = "/" + barrierName;

		zk = ZooKeeperFactory.getZooKeeper(BARRIER_ROOT_PATH,new Watcher() {

			@Override
			public void process(WatchedEvent event) {
				logger.entry(event);;
				if (event != null) {
					switch (event.getType()) {
					case NodeCreated:
						if (StringUtils.equals(event.getPath(), BARRIER_PATH + "/" + READY_NODE_NAME)) {
							synchronized (mutex) {
								mutex.notifyAll();
							}
						}
						break;
					case NodeDeleted:
						if (StringUtils.equals(event.getPath(), BARRIER_PATH + "/" + READY_NODE_NAME)) {
							synchronized (leaveLock) {
								leaveLock.notifyAll();
							}
						}
						break;
					default:
						break;
					}
				}
			}

		}, false);

		init(BARRIER_PATH);

	}

	private void init(String path) {
		for (;;) {
			try {
				zk.create(path, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				return;
			} catch (KeeperException.NoNodeException e) {
				try {
					zk.create(BARRIER_ROOT_PATH, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
					continue;
				} catch (KeeperException.NodeExistsException e1) {
					logger.catching(Level.WARN, e1);
					// Nothing to do
				} catch (KeeperException | InterruptedException e1) {
					throw new BarrierRuntimeException();
				}
			} catch (KeeperException.NodeExistsException e) {
				return;
			} catch (KeeperException e) {
				throw new BarrierRuntimeException(e);
			} catch (InterruptedException e) {
				throw new BarrierRuntimeException(e);
			}
		}
	}

	@Override
	public void enter() {
		logger.entry();
		try {
			String createPath = zk.create(BARRIER_PATH + "/" + BARRIER_ELEMENT_NAME, new byte[0], Ids.OPEN_ACL_UNSAFE,
					CreateMode.EPHEMERAL_SEQUENTIAL);
			barrierElementPath.set(createPath);
			Stat exists = zk.exists(BARRIER_PATH + "/" + READY_NODE_NAME, true);
			if (exists != null) {
				return;
			}

			List<String> children = zk.getChildren(BARRIER_PATH, false);
			if (children.size() < this.threshold) {
				synchronized (mutex) {
					do {
						logger.debug("{} is waiting！",createPath);
						mutex.wait();
					} while (zk.exists(BARRIER_PATH + "/" + READY_NODE_NAME, true) == null);
					logger.debug("{} is ready!",createPath);
					return;
				}
			} else {
				try {
					zk.create(BARRIER_PATH + "/" + READY_NODE_NAME, new byte[0], Ids.OPEN_ACL_UNSAFE,
							CreateMode.PERSISTENT);
					logger.debug("{} is ready!",createPath);
					logger.debug("all thread is ready!");
				} catch (KeeperException.NodeExistsException e1) {
					logger.catching(Level.WARN, e1);
				}
			}

		} catch (KeeperException | InterruptedException e) {
			throw new BarrierRuntimeException(e);
		}
	}

	private TreeMap<Long, String> orderedChildren(List<String> children) {
		TreeMap<Long, String> ret = new TreeMap<>();
		for (String c : children) {
			if (c.regionMatches(0, BARRIER_ELEMENT_NAME, 0, BARRIER_ELEMENT_NAME.length())) {
				ret.put(Long.valueOf(c.substring(BARRIER_ELEMENT_NAME.length())), BARRIER_PATH + "/" + c);
			}
		}
		return ret;
	}

	@Override
	public void leave() {
		try {
			while (true) {
				List<String> children = zk.getChildren(BARRIER_PATH, false);
				if (children.size() < 1) {
					logger.debug( "{} has leave!",barrierElementPath.get());
					return;
				} else {
					TreeMap<Long, String> orderedChildren = orderedChildren(children);
					String curPath = barrierElementPath.get();
					String lowestPath = orderedChildren.firstEntry().getValue();
					String largestPath = orderedChildren.lastEntry().getValue();
					logger.debug("最小路径：{},最大路径：{},当前路径：{},children size：",lowestPath,largestPath,curPath,orderedChildren.size());
					Watcher w = new LargerWatcher(largestPath);

					if (StringUtils.equals(lowestPath, curPath)) {
						if (orderedChildren.size() == 1) {
							// 删除节点并离开
							zk.delete(curPath, -1);
							zk.delete(BARRIER_PATH + "/" + READY_NODE_NAME, -1);
							continue;
						} else {
							// 监听更大号的znode的删除事件
							Stat exists = zk.exists(largestPath, w);
							logger.debug("监听大路径{}",largestPath);
							if (exists == null) {
								continue;
							} else {
								synchronized (largerLock) {
									largerLock.wait();
									logger.debug("唤醒{}", largestPath);
								}
							}
						}
					} else {
						// 删除节点并监听ready的删除事件
						zk.exists(BARRIER_PATH + "/" + READY_NODE_NAME, w);
						try {
							if (curPath != null) {
								logger.debug( "{} is waiting to leave!",curPath);
								zk.delete(curPath, -1);
								logger.debug( "删除路径：{}", curPath);
								barrierElementPath.remove();
							}
						} catch (KeeperException.NodeExistsException ex) {

						}
						synchronized (leaveLock) {
							leaveLock.wait();
						}
					}
				}
			}

		} catch (KeeperException | InterruptedException e) {
			throw new BarrierRuntimeException(e);
		}
	}

	private static class LargerWatcher implements Watcher {
		private String largerPath;

		private LargerWatcher(String largerPath) {
			this.largerPath = largerPath;
		}

		@Override
		public void process(WatchedEvent event) {
			logger.entry(event);
			if (event != null) {
				switch (event.getType()) {
				case NodeDeleted:
					if (event.getPath().equals(largerPath)) {
						synchronized (largerLock) {
							largerLock.notifyAll();
						}
					} else {
						synchronized (leaveLock) {
							leaveLock.notifyAll();
						}
					}
					break;
				default:
					break;

				}
			}

		}
	}

	public static void main(String[] args) throws KeeperException, InterruptedException {
		Barrier b = new DoubleBarrier(5, "test");

		for (int i = 0; i < 1; i++) {
			new Thread(new BarrierRunner(b)).start();
		}

		// ZooKeeper zk = ZooKeeperFactory.getZooKeeper(null, false);
		// Stat exists = zk.exists("/double_barrier", false);
		// System.out.println(exists);

	}

	private static class BarrierRunner implements Runnable {

		private Barrier barrier;

		private Random r = new Random(37);

		BarrierRunner(Barrier b) {
			this.barrier = b;
		}

		@Override
		public void run() {
			try {
//				TimeUnit.SECONDS.sleep(r.nextInt(5));
				barrier.enter();
				TimeUnit.SECONDS.sleep(r.nextInt(1));
				barrier.leave();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}

}
