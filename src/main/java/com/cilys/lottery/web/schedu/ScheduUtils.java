package com.cilys.lottery.web.schedu;

import com.cilys.lottery.web.model.UserMoneyFlowModel;
import com.cilys.lottery.web.schedu.runnable.SyncSingleUserMoneyFlowToUserRunnable;
import com.cilys.lottery.web.schedu.runnable.SyncTotalUserMoneyFlowToUserRunnable;

/**
 * Created by admin on 2020/6/24.
 * 队列调度器
 */
public class ScheduUtils {

    public static void putTask(TaskBean taskBean){
        TaskQueue.add(taskBean);
        excuteTask();
    }

    public static void putTask(String taskType){
        putTask(TaskBean.createTask(taskType));
    }

    private static TaskBean pollTask(){
        return TaskQueue.poll();
    }

    public static void excuteTask(){
        TaskBean task = pollTask();
        if (task != null) {
            String taskType = task.getType();
            if (TaskType.SYNC_SINGLE_USER_MONEY_FLOW_TO_USER.equals(taskType)) {
                Object data = task.getData();
                if (data == null) {
                    ThreadPools.executeTask(new SyncSingleUserMoneyFlowToUserRunnable());
                } else {
                    if (data instanceof Integer) {
                        ThreadPools.executeTask(new SyncSingleUserMoneyFlowToUserRunnable((Integer) task.getData()));
                    } else if (data instanceof UserMoneyFlowModel){
                        ThreadPools.executeTask(new SyncSingleUserMoneyFlowToUserRunnable((UserMoneyFlowModel)task.getData()));
                    }
                }
            } else if (TaskType.SYNC_TOTAL_USER_MONEY_FLOW_TO_USER.equals(taskType)) {
                ThreadPools.executeTask(new SyncTotalUserMoneyFlowToUserRunnable());
            }
        }
    }
}