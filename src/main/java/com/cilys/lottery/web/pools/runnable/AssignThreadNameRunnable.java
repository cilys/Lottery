package com.cilys.lottery.web.pools.runnable;

/**
 * Created by admin on 2020/6/25.
 */
public abstract class AssignThreadNameRunnable implements Runnable {
    private String threadName;

    public AssignThreadNameRunnable(){

    }
    public AssignThreadNameRunnable(String threadName){
        this.threadName = threadName;
    }

    public String getThreadName() {
        return threadName;
    }
}
