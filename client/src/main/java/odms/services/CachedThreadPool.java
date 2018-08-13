package odms.services;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CachedThreadPool {

    private ExecutorService executor;

    private static CachedThreadPool pool = null;

    private CachedThreadPool() {
        executor = Executors.newCachedThreadPool();
    }

    public static CachedThreadPool getThreadPool() {
        if (pool == null) {
            pool = new CachedThreadPool();
        }
        return pool;
    }

    public ExecutorService getExecutor() {
        return executor;
    }
}
