package ua.com.ifno.pogi.AnimationEngine;

import java.util.concurrent.CopyOnWriteArrayList;

public class Animator {
	private final CopyOnWriteArrayList<Animation> animations;
	
	public Animator() {
		animations = new CopyOnWriteArrayList<Animation>();
	}
	
	public boolean hasAnimations() {
        return animations.size() > 0;
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
	
	public boolean contains_invert(Animation animation) {
        return animation == null || !animations.contains(animation);
    }
}
