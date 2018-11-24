package net.imoran.tv.sdk.network.requestdata;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by bobge on 2017/7/12.
 */
public class CoreExecutorService {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int MAXIMUM_POOL_SIZE = 50;
    private static final int KEEP_ALIVE = CPU_COUNT;
    /*private static HashMap<String , ExecutorService> mExecutorServiceMap = new HashMap<>();*/
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "CoreExecutorService #" + mCount.getAndIncrement());
        }
    };
    private static BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<>(128);
    private static ExecutorService THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE,
            KEEP_ALIVE,
            TimeUnit.SECONDS,
            sPoolWorkQueue,
            sThreadFactory);

    public static ExecutorService getDefaultExecutorService() {
        return THREAD_POOL_EXECUTOR;
    }

    public static void cancel(Runnable task) {
        sPoolWorkQueue.remove(task);
    }

}
