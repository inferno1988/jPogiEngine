package net.ifno.com.ua;

import java.awt.Image;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class CachedLoop {
	/** TODO: Should be parameterized */
	private static int cacheSize = 1000; // size of image cache, should be
										// parameterized
	private static ConcurrentHashMap<String, Image> imgBuffer = new ConcurrentHashMap<String, Image>(
			cacheSize);
	private static ArrayBlockingQueue<String> fifoMap = new ArrayBlockingQueue<String>(
			cacheSize + 10);

	public static void put(String key, Image img) {
		if (fifoMap.size() < cacheSize) {
			imgBuffer.put(key, img);
			fifoMap.add(key);
		} else {
			if (fifoMap.size() > cacheSize)
				imgBuffer.remove(fifoMap.remove());
			imgBuffer.remove(fifoMap.remove());
			imgBuffer.put(key, img);
			fifoMap.add(key);
		}
	}

	public static boolean containsKey(String key) {
		return imgBuffer.containsKey(key);
	}

	public static Image get(String key) {
		return imgBuffer.get(key);
	}

	public static Integer size() {
		if (fifoMap.size() >= imgBuffer.size()) {
			return fifoMap.size();
		} else if (fifoMap.size() <= imgBuffer.size()) {
			return imgBuffer.size();
		}
		return 0;
	}

	public static int getCacheSize() {
		return cacheSize;
	}

	public static void setCacheSize(int cacheSize) {
		CachedLoop.cacheSize = cacheSize;
	}
}
