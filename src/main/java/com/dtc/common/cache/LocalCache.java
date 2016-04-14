/**
 * 
 */
package com.dtc.common.cache;

/**
 * 本地缓存接口
 * @category @author tim
 *
 */
public interface LocalCache<K, V> {

	/**
	 * 
	 * @category 获取本地缓存方法
	 * @param key 缓存的键值
	 * @return
	 */
	public V get(K key);

}
