package com.dtc.common.lock;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import com.dtc.common.exception.LockError;
import com.dtc.common.exception.UnLockError;
import com.dtc.common.util.StrUtils;
import com.dtc.common.util.sort.SortOrder;
import com.dtc.common.util.sort.SortUtil;
import com.dtc.common.zookeeper.ProtocolSupport;
import com.dtc.common.zookeeper.WaitEventWatcher;
import com.dtc.common.zookeeper.ZKSeqNode;
import com.dtc.common.zookeeper.ZKSeqNodeDefine;
import com.dtc.common.zookeeper.ZooKeeperFactory;
import com.dtc.common.zookeeper.ZooKeeperOperation;

/**
 * 
 * @category 全局同步流程锁
 * @author tim
 *
 */
public class ZKFlowLock extends ProtocolSupport implements Lock {

	private static final Logger logger = LogManager.getLogger();

	/**
	 * @category 要加锁的资源名称
	 */
	private String resource;

	/**
	 * @category 要加锁的资源在zookeeper中对应的路径
	 */
	private String lockPath;

	/**
	 * @category 锁文件的UUID值，用于在发生ConnectionLoss异常时，重检创建的文件是否已经生成。
	 */
	private String lockUUID;

	/**
	 * @category 锁文件的名称
	 */
	private String lockID;

	private static ZooKeeper zk = ZooKeeperFactory.getZooKeeper(Lock.FLOW_LOCK_ROOT_PATH, null, false);

	public ZKFlowLock(String resource) {
		super(zk);
		this.resource = resource;
		this.lockUUID = StrUtils.remove(UUID.randomUUID().toString(), "-");
		logger.debug("lock UUID:{}", lockUUID);
	}

	@Override
	public void lock() throws LockError {
		if (isClosed()) {
			logger.error("lock has close!");
			throw new LockError("lock has close!");
		}
		// 确保要加锁的资源目录已经存在
		ensurePathExists(getLockPath());

		try {
			retryOperation(new LockOperation());
		} catch (KeeperException | InterruptedException e) {
			logger.catching(e);
			throw new LockError(e);
		}

	}

	private final class LockOperation implements ZooKeeperOperation {
		private void reCheckMyLockID() throws KeeperException, InterruptedException {
			List<String> children = zookeeper.getChildren(getLockPath(), false);
			for (String c : children) {
				if (StrUtils.startsWith(c, getLockUUID())) {
					lockID = c;
					return;
				}
			}

			// 如果之前没有生成加锁节点，创建该加锁节点
			String lockPath = zookeeper.create(getLockPath() +"/"+ getLockUUID() + "-", new byte[0], getAcl(),
					CreateMode.EPHEMERAL_SEQUENTIAL);
			lockID = StrUtils.substringAfterLast(lockPath, "/");
			logger.debug("created lockID：{}", lockID);
		}

		private ZKSeqNodeDefine getForestNode(List<ZKSeqNode> l, ZKSeqNode myNode) {
			Long preSeq = null;
			do {
				preSeq = myNode.getSeq() - 1;
				if (preSeq >= 0) {
					for (ZKSeqNode i : l) {
						if (preSeq.equals(i.getSeq())) {
							logger.debug("the forest node of myNode:{} is {}", myNode, i);
							return i;
						}
					}
				}
			} while (preSeq >= 0);
			return null;
		}

		@Override
		public boolean execute() throws KeeperException, InterruptedException {
			do {
				reCheckMyLockID();
				List<String> children = zookeeper.getChildren(getLockPath(), false);
				if (children == null || children.size() == 0) {
					logger.debug("no lock file in lock resource, recheck...");
				} else {
					List<ZKSeqNode> l = new ArrayList<>();
					for (String c : children) {
						l.add(new ZKSeqNode(getLockPath(), c));
					}
					ZKSeqNode myNode = new ZKSeqNode(getLockPath(), getLockID());
					List<ZKSeqNode> topList = SortUtil.topList(l, SortOrder.ASC, 1);// 查出最小节点
					logger.debug("current lock node is {}", topList.get(0));
					if (!topList.get(0).equals(myNode)) {
						ZKSeqNodeDefine forestNode = getForestNode(l, myNode);
						if (forestNode != null) {
							WaitEventWatcher watcher = new WaitEventWatcher(forestNode.getWholePath(),
									EventType.NodeDeleted);
							Stat exists = zookeeper.exists(forestNode.getWholePath(), watcher);
							if(exists!=null){
								logger.debug("{} waiting for lock release:{}",ZKFlowLock.this, forestNode);
								watcher.await();
							}else{
								continue;
							}
						} else {
							continue;
						}
					}
					logger.debug("{} acquire lock!", ZKFlowLock.this);
					break;

				}
			} while (true);
			return true;
		}

	}

	private String getLockUUID() {
		return this.lockUUID;
	}

	private String getLockNodePath() {
		return getLockPath() + "/" + this.lockID;
	}

	public String getLockID() {
		return this.lockID;
	}

	private String getLockPath() {
		return this.lockPath != null ? this.lockPath : "/" + getResource();
	}

	public String getResource() {
		return resource;
	}

	@Override
	public void unLock() throws UnLockError {

		try {
			zookeeper.delete(getLockNodePath(), -1);

		} catch (InterruptedException e) {
			logger.warn("unLock is interrupted", e);
		} catch (KeeperException.NoNodeException | KeeperException.ConnectionLossException
				| KeeperException.SessionExpiredException e) {
			// 由于是EPHEMERAL类型的节点，如果发生这些异常，文件会自动删除，所以不需要处理。
			logger.warn("unexpected exception occour at unLock！", e);
		} catch (KeeperException e) {
			throw new UnLockError(e);
		}
		logger.debug("release lock:{}", this);
	}

	@Override
	public String toString() {
		return "ZKFlowLock [resource=" + resource + ", lockPath=" + lockPath + ", lockUUID=" + lockUUID + ", lockID="
				+ lockID + "]";
	}

	
	
}
