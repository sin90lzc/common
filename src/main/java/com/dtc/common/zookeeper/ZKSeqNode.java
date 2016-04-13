package com.dtc.common.zookeeper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dtc.common.util.StrUtils;

public class ZKSeqNode extends ZKNode implements ZKSeqNodeDefine, Comparable<ZKSeqNode> {

	private static final Logger logger = LogManager.getLogger();

	private static final String DEFAULT_SPLITER = "-";

	private Long seq;

	private String spliter;

	public ZKSeqNode(String upperPath, String name) {
		this(upperPath, name, DEFAULT_SPLITER);
	}

	public ZKSeqNode(String upperPath, String name, String spliter) {
		super(upperPath, name);
		this.spliter = spliter;
		String seqStr = StrUtils.substringAfterLast(name, spliter);
		try {
			this.seq = Long.parseLong(seqStr);
		} catch (NumberFormatException e) {
			logger.error("The node is not a seq node!");
			throw new RuntimeException("The node is not a seq node!", e);
		}
	}

	@Override
	public int compareTo(ZKSeqNode o) {
		return this.seq.compareTo(o.getSeq());
	}

	@Override
	public Long getSeq() {
		return this.seq;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ZKSeqNode other = (ZKSeqNode) obj;
		if (seq == null) {
			if (other.seq != null)
				return false;
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		if (!super.equals(obj)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ZKSeqNode [seq=" + seq + ", spliter=" + spliter + ", getWholePath()=" + getWholePath() + "]";
	}

}
