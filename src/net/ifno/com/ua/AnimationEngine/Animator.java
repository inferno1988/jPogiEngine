package net.ifno.com.ua.AnimationEngine;

import java.util.concurrent.CopyOnWriteArrayList;

public class Animator {
	private CopyOnWriteArrayList<Animation> animations;
	
	public Animator() {
		animations = new CopyOnWriteArrayList<Animation>();
	}
	
	public boolean hasAnimations() {
		if (animations.size() > 0)
			return true;
		return false;
	}

	public CopyOnWriteArrayList<Animation> getAnimations() {
		return animations;
	}
	
	public void addAnimation(Animation animation) {
		if (animation != null)
			animations.add(animation);
	}
	
	public void setPlayed(Animation animation) {
		if (animation != null)
			animations.remove(animation);
	}
	
	public boolean contains(Animation animation) {
		if (animation != null)
			return animations.contains(animation);
		return false;
	}
}
