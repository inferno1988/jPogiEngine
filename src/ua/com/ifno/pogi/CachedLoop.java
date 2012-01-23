package ua.com.ifno.pogi;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class CachedLoop<K, V> implements Map<K, V> {
	
	private int cacheSize = 1000;
	private ConcurrentHashMap<K, V> cache = new ConcurrentHashMap<K, V>(
			cacheSize);
	private ArrayBlockingQueue<K> fifoMap = new ArrayBlockingQueue<K>(
			cacheSize + 10);

	@Override
	public int size() {
		if (fifoMap.size() >= cache.size()) {
			return fifoMap.size();
		} else if (fifoMap.size() <= cache.size()) {
			return cache.size();
		} else {
		return 0;
		}
	}

	@Override
	public boolean isEmpty() {
		return cache.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		if (key == null)
			throw new NullPointerException();
		return cache.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		if (value == null)
			throw new NullPointerException();
		return cache.containsValue(value);
	}

	@Override
	public V get(Object key) {
		if (key == null)
			throw new NullPointerException();
		return cache.get(key);
	}

	@Override
	public V put(K key, V value) {
		if (value == null)
			throw new NullPointerException();
		if (fifoMap.size() < cacheSize) {
			cache.put(key, value);
			fifoMap.add(key);
			if (cache.containsKey(key))
				return cache.get(key);
			else 
				return null;
		} else {
			if (fifoMap.size() > cacheSize)
				cache.remove(fifoMap.remove());
			cache.remove(fifoMap.remove());
			cache.put(key, value);
			fifoMap.add(key);
			if (cache.containsKey(key))
				return cache.get(key);
			else 
				return null;
		}
	}

	@Override
	public V remove(Object key) {
		if (key == null)
			throw new NullPointerException();
		return cache.remove(key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		if (m == null)
			throw new NullPointerException();
		cache.putAll(m);
	}

	@Override
	public void clear() {
		cache.clear();
	}

	@Override
	public Set<K> keySet() {
		return cache.keySet();
	}

	@Override
	public Collection<V> values() {
		return cache.values();
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return cache.entrySet();
	}

}
