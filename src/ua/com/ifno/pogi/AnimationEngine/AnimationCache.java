package ua.com.ifno.pogi.AnimationEngine;

import java.util.concurrent.ConcurrentHashMap;

public class AnimationCache {
	
	private final ConcurrentHashMap<String, Animation> cache;

	public AnimationCache() {
		cache = new ConcurrentHashMap<String, Animation>();
	}
	
	public void addAnimation(String name, Animation animation) {
		if ((name != null) && (!name.equals("")) && (animation != null))
			cache.put(name, animation);
	}
	
	public Animation getAnimation(String name) {
		if (cache.containsKey(name))
			return cache.get(name);
		return null;
	}
	
	public int getSize() {
		if (cache != null)
			return cache.size();
		return 0;
	}
}
