package com.dtc.common.zookeeper;

public class ZKNode implements ZKNodeDefine {
	
	private String upperPath;
	private String name;
	public ZKNode(String upperPath,String name){
		this.upperPath=upperPath;
		this.name=name;
	}
	

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getUpperPath() {
		return this.upperPath;
	}

	@Override
	public String getWholePath() {
		return this.upperPath+"/"+this.name;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((upperPath == null) ? 0 : upperPath.hashCode());
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
		ZKNode other = (ZKNode) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (upperPath == null) {
			if (other.upperPath != null)
				return false;
		} else if (!upperPath.equals(other.upperPath))
			return false;
		return true;
	}

	
	
}
