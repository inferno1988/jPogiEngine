package net.ifno.com.ua;

public class PaintThread extends Thread {
	private boolean interrupted = false;

	public PaintThread() {
	}

	@Override
	public void interrupt() {
		this.interrupted = true;
	}

	@Override
	public boolean isInterrupted() {
		return this.interrupted;
	}
}
