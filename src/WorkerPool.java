import java.util.concurrent.ConcurrentHashMap;

public class WorkerPool {
	private static ConcurrentHashMap<Integer, String> workerPool = new ConcurrentHashMap<Integer, String>();
	public static void addWorker(Integer threadId, String action) {
		workerPool.put(threadId, action);
	};
	
	public static Integer getWorkerCount() {
		return workerPool.size();
	}
	
	public static void removeWorker(Integer threadId) {
		workerPool.remove(threadId);
	}
	
	public static boolean hasWorkers() {
		if (getWorkerCount() > 0) {
			return true;
		} else {
			return false;
		}
	}
}
