import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


public class CachedLoop {
	public static HashMap<String, BufferedImage> imgBuffer = new HashMap<String, BufferedImage>(500);
	private static int cacheSize = 100; //size of image cache, should be parameterized
	private static ConcurrentLinkedQueue<String> fifoMap = new ConcurrentLinkedQueue<String>();
	
	public static void put(String key, BufferedImage img) {
		if (fifoMap.size() < cacheSize) {
			imgBuffer.put(key, img);
			fifoMap.add(key);
		} else {
			imgBuffer.remove(fifoMap.remove());
			imgBuffer.put(key, img);
			fifoMap.add(key);
		}
	}
	
	public static boolean containsKey(String key) {
		return imgBuffer.containsKey(key);
	}
	
	public static BufferedImage get(String key) {
		return imgBuffer.get(key);
	}
	
	public static Integer size() {
		if (imgBuffer.size() == fifoMap.size())
			return imgBuffer.size();
		return 0;
	}
	
	public static int getCacheSize() {
		return cacheSize;
	}
	public static void setCacheSize(int cacheSize) {
		CachedLoop.cacheSize = cacheSize;
	}

	
}
