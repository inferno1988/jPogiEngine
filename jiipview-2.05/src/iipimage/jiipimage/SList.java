package iipimage.jiipimage;
import java.util.Enumeration;
/*
 * Used for the main test engine.
 *
 * import java.io.*;
 * import java.util.*;
 */
/**
 * A singularly linked list class.
 */
public final class SList {
	class Enumerator implements Enumeration {
		private SListNode mNode;
		public Enumerator(SListNode node) {
			mNode = node;
		}
		public boolean hasMoreElements() {
			return (mNode != null);
		}
		public Object nextElement() {
			Object object = mNode.mObject;
			mNode = mNode.mNext;
			return object;
		}
	}
	final static class SListNode {
		/**
		 * The next node in the list.
		 */
		public SListNode mNext;
		/**
		 * The object stored in this node.
		 */
		public Object mObject;
	}
	/**
	 * The node at the back of the list.
	 */
	private SListNode mBack;
	/**
	 * The node at the front of the list.
	 */
	private SListNode mFront;
	/**
	 * The number of elements in the list.
	 */
	private int mSize;
	/**
	 * Create a new empty singularly linked list.
	 */
	public SList() {
		/*
		 * mSize will be initialised to 0, mFront and mBack will be
		 * initialised to null.
		 */
	}
	/**
	 * Push an object onto the back of the list.
	 */
	public synchronized void pushBack(Object object) {
		SListNode node = new SListNode();
		node.mObject = object;
		if (++mSize == 1) {
			// DebugWin.printc((mFront == null) + "list empty but front not null");
			//DebugWin.printc((mBack == null) + "list empty but front not null");
			mFront = node; // First element - add to front of list.
		} else {
			mBack.mNext = node; // New element - add to back of list.
		}
		mBack = node;
	}
	/**
	 * Pop (return and remove) the object from the front of the list.
	 */
	public synchronized Object popFront() {
		Object object = mFront.mObject;
		mFront = mFront.mNext;
		if (--mSize == 0) {
			//DebugWin.printc((mFront == null) + "list inconsistent");
			mBack = null;
		}
		return object;
	}
	/**
	 * Return <code>true</code> if the given object is contained within the
	 * list, <code>false</code> if not.
	 */
	public synchronized boolean contains(Object object) {
		for (SListNode node = mFront; node != null; node = node.mNext) {
			if (node.mObject.equals(object)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * Remove the first occurrance of the given object from the list.
	 * @return <code>true</code> if the object was removed, or
	 *         <code>false</code> if the object wasn't in the list.
	 */
	public synchronized boolean remove(Object object) {
		if (mSize == 0) {
			return false;
		}
		if (mFront.mObject.equals(object)) {
			mFront = mFront.mNext;
			if (--mSize == 0) {
				//   DebugWin.printc((mFront == null) + "list inconsistent");
				mBack = null;
			}
			return true;
		}
		SListNode prev = mFront, node = mFront.mNext;
		while (node != null) {
			if (node.mObject.equals(object)) {
				prev.mNext = node.mNext;
				if (node == mBack) {
					mBack = prev;
				}
				mSize--;
				return true;
			}
			prev = node;
			node = node.mNext;
		}
		return false;
	}
	/**
	 * Remove every element from the list.
	 */
	public synchronized void empty() {
		mFront = null;
		mBack = null;
		mSize = 0;
	}
	/**
	 * Return the number of elements in the list.
	 */
	public int size() {
		return mSize;
	}
	public Enumeration elements() {
		return new Enumerator(mFront);
	}
}