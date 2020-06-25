package com.cilys.lottery.web.schedu;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by admin on 2020/6/24.
 * 任务队列
 */
public class TaskQueue {
    private static ConcurrentLinkedQueue<TaskBean> queue = new ConcurrentLinkedQueue();

    protected static void add(TaskBean bean){
        if (queue == null){
            queue = new ConcurrentLinkedQueue();
        }
        if (bean == null){
            return;
        }
        queue.add(bean);
    }

    protected static TaskBean poll(){
        if (queue != null){
            return queue.poll();
        }
        return null;
    }
}