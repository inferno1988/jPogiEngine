package iipimage.jiipimage;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * A class to cache <code>Tile</code> objects.
 * 
 * @see Tile
 * @author Denis Pitzalis
 */

public class TileCache {
	private final float hashTableLoadFactor = 0.75f;

	private LinkedHashMap map;

	private int cacheSize;

	public TileCache(int cacheSize) {
		this.cacheSize = cacheSize;
		int hashTableCapacity = (int) Math.ceil(getSize() / hashTableLoadFactor) + 1;
		map = new LinkedHashMap(hashTableCapacity, hashTableLoadFactor, true) {
			private static final long serialVerisionUID = 1;

			protected boolean removeEldestEntry(java.util.Map.Entry entry) {
				return size() > getSize();
			}
		};
	}

	/**
	 * Retrieves an entry from the cache. <br>
	 * The retrieved entry becomes the MRU (most recently used) entry.
	 * 
	 * @param key
	 *            the key whose associated value is to be returned.
	 * @return the value associated to this key, or null if no value with this
	 *         key exists in the cache.
	 */
	public synchronized Tile get(String key) {
		return (Tile) map.get(key);
	}

	/**
	 * Adds an entry to this cache. If the cache is full, the LRU (least
	 * recently used) entry is dropped.
	 * 
	 * @param key
	 *            the key with which the specified value is to be associated.
	 * @param value
	 *            a value to be associated with the specified key.
	 */
	public synchronized void put(String key, Tile value) {
		map.put(key, value);
	}

	/**
	 * Clears the cache.
	 */
	public synchronized void clear() {
		map.clear();
	}

	/**
	 * Returns the number of used entries in the cache.
	 * 
	 * @return the number of entries currently in the cache.
	 */
	public synchronized int usedEntries() {
		return map.size();
	}

	/**
	 * Returns a <code>Collection</code> that contains a copy of all cache
	 * entries.
	 * 
	 * @return a <code>Collection</code> with a copy of the cache content.
	 */
	public synchronized Collection getAll() {
		return new ArrayList(map.entrySet());
	}

	public synchronized boolean IsPresent(String key) {
		return map.containsKey(key);
	}

	/**
	 * @param text
	 */
	public void setSize(int tmp) {
		cacheSize= tmp;
	}
	/**
	 * @return
	 */
	protected int getSize() {
		return cacheSize;
	}
}