package odms.services;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Singleton to hold a pool of threads, such that tasks can be run off one service without degrading performance
 */
public class CachedThreadPool {

    private ExecutorService executor;

    private static CachedThreadPool pool = null;

    /**
     * Private constructor to prevent multiple instances being created
     */
    private CachedThreadPool() {
        executor = Executors.newCachedThreadPool(t -> {
            Thread thread = new Thread();
            thread.setDaemon(true);
            return thread;
        });
    }

    /**
     * Gets the thread pool instance
     *
     * @return the pool instance
     */
    public static CachedThreadPool getThreadPool() {
        if (pool == null) {
            pool = new CachedThreadPool();
        }
        return pool;
    }

    /**
     * Gets the executor service
     * @return the service to execute tasks on
     */
    public ExecutorService getExecutor() {
        return executor;
    }
}
