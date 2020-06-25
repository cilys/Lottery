package com.cilys.lottery.web.schedu;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by admin on 2020/6/24.
 */
public class ThreadPools {

    private static ExecutorService fixedThradPool = Executors.newFixedThreadPool(3);

    public static void executeTask(Runnable runnable){
        if (runnable == null){
            return;
        }
        if (fixedThradPool == null){
            fixedThradPool = Executors.newFixedThreadPool(3);
        }
        fixedThradPool.execute(runnable);
    }

    public static void shutdown(){
        if (fixedThradPool != null) {
            fixedThradPool.shutdown();
        }
        fixedThradPool = null;
    }

}
