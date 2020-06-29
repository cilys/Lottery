package com.cilys.lottery.web.pools.thread;

import com.cily.utils.base.Sys;
import com.cilys.lottery.web.pools.runnable.AssignThreadNameRunnable;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by admin on 2020/6/25.
 */
public class AssignNameThreadFactory extends BaseThreadFactory {
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    public AssignNameThreadFactory() {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
        namePrefix = "pool-" +
                poolNumber.getAndIncrement() +
                "-thread-";
    }

    public Thread newThread(Runnable r) {
        Thread t;
        System.out.println("---" + (r instanceof AssignThreadNameRunnable));
        System.out.println("---" + r.getClass().getSimpleName());
        if (r != null && r instanceof AssignThreadNameRunnable){
            String threadName = ((AssignThreadNameRunnable) r).getThreadName();

            if (threadName != null && threadName.length() > 0){
                t = new Thread(group, r, threadName, 0);
            }else {
                t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            }
        }else {
            t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
        }

        if (t.isDaemon())
            t.setDaemon(false);
        if (t.getPriority() != Thread.NORM_PRIORITY)
            t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }
}
