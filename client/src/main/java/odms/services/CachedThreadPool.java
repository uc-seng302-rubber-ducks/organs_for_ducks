package odms.services;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CachedThreadPool {

    private ExecutorService executor;

    private static CachedThreadPool pool = null;

    private CachedThreadPool() {
        executor = Executors.newCachedThreadPool();
    }
}
