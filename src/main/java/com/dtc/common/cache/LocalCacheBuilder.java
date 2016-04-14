/**
 * 
 */
package com.dtc.common.cache;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * @category 本地缓存生成器
 * @author tim 2016年4月14日
 */
public class LocalCacheBuilder<K, V> {

	private boolean isFlexible = Boolean.FALSE;

	private CacheBuilder builder = CacheBuilder.newBuilder();

	@SuppressWarnings("unchecked")
	public LocalCache<K, V> build(LocalCacheLoader<K, V> loader) {
		if (!isFlexible) {
			return new CommonLocalCache<>(loader);
		} else {
			LoadingCache<K, V> loadingCache = builder.build(new CacheLoader<K, V>() {
				@Override
				public V load(K key) throws Exception {
					return loader.load(key);
				}
			});
			return new LocalCache<K, V>() {
				@Override
				public V get(K key) {
					return loadingCache.getUnchecked(key);
				}
			};
		}
	}

	public LocalCacheBuilder expireAfterAccess(long duration, TimeUnit unit) {
		setFlexible();
		builder.expireAfterAccess(duration, unit);
		return this;
	}
	public LocalCacheBuilder expireAfterWrite(long duration, TimeUnit unit) {
		setFlexible();
		builder.expireAfterWrite(duration, unit);
		return this;
	}

	public LocalCacheBuilder maximumSize(long size){
		setFlexible();
		builder.maximumSize(size);
		return this;
	}
	private void setFlexible() {
		this.isFlexible = true;
	}

}
