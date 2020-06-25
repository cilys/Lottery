package com.cilys.lottery.web.pools;

import com.cilys.lottery.web.pools.thread.AssignNameThreadFactory;

import java.util.concurrent.*;

/**
 * Created by admin on 2020/6/25.
 */
public class FixedThreadPools {
    private static ExecutorService fixedThradPool = createFixedThreadPool(3);

    public static void executeTask(Runnable runnable){
        if (runnable == null){
            return;
        }
        if (fixedThradPool == null){
            fixedThradPool = createFixedThreadPool(3);
        }
        fixedThradPool.execute(runnable);
    }

    public static void shutdown(){
        if (fixedThradPool != null) {
            fixedThradPool.shutdown();
        }
        fixedThradPool = null;
    }

    private static final RejectedExecutionHandler defaultHandler = new ThreadPoolExecutor.AbortPolicy();
    private static ExecutorService createFixedThreadPool(int poolSize){
        return new ThreadPoolExecutor(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), new AssignNameThreadFactory(), defaultHandler);
    }



}