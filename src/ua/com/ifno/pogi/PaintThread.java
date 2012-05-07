package ua.com.ifno.pogi;
abstract class PaintThread extends Thread {
	private boolean interrupted = false;

	PaintThread() {
	}

	public void interrupt() {
		this.interrupted = true;
	}

	public boolean isInterrupted() {
		return this.interrupted;
	}
}
