/**
 * 
 */
package com.dtc.common.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @category @author tim
 *
 */
class CommonLocalCache<K, V> implements LocalCache<K, V> {

	private Map<K, V> cache = new ConcurrentHashMap<>();
	private LocalCacheLoader<K, V> loader;

	public CommonLocalCache(LocalCacheLoader<K, V> loader) {
		this.loader = loader;
	}

	/**
	 * @see com.dtc.common.cache.LocalCache#get(java.lang.Object,
	 *      com.dtc.common.cache.LocalCacheLoader)
	 */
	@Override
	public V get(K key) {
		return cache.computeIfAbsent(key, k -> {
			return this.loader.load(key);
		});
	}

}
