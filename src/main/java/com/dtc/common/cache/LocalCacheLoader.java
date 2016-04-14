/**
 * 
 */
package com.dtc.common.cache;

/**
 * @category 缓存加载器
 * @author tim
 *
 */
public interface LocalCacheLoader<K,V> {

	public V load(K key);
	
}
