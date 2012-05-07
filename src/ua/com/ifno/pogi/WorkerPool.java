package ua.com.ifno.pogi;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class WorkerPool {
	private static final ConcurrentHashMap<Long, PaintThread> workerPool = new ConcurrentHashMap<Long, PaintThread>();
	public static void addWorker(Long threadId, PaintThread thread) {
		workerPool.put(threadId, thread);
	}

    private static Integer getWorkerCount() {
		return workerPool.size();
	}

	public static void removeWorker(Long threadId) {
		workerPool.remove(threadId);
	}

	public static boolean hasWorkers() {
        return getWorkerCount() > 0;
	}

	public static void interruptAll() {
		Collection<PaintThread> threadList = workerPool.values();
		JobGenerator.getJobList().clear();
		for (PaintThread thread: threadList) {
			thread.interrupt();
		}
	}
}
