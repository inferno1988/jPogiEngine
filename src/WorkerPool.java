import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class WorkerPool {
	private static ConcurrentHashMap<Long, PaintThread> workerPool = new ConcurrentHashMap<Long, PaintThread>();
	public static void addWorker(Long threadId, PaintThread thread) {
		workerPool.put(threadId, thread);
	};

	public static Integer getWorkerCount() {
		return workerPool.size();
	}

	public static void removeWorker(Long threadId) {
		workerPool.remove(threadId);
	}

	public static boolean hasWorkers() {
		if (getWorkerCount() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public static void interruptAll() {
		Collection<PaintThread> threadList = workerPool.values();
		JobGenerator.getJobList().clear();
		for (Thread thread: threadList) {
			thread.interrupt();
		}
	}
}
