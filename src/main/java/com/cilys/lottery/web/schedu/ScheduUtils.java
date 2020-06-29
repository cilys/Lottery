package com.cilys.lottery.web.schedu;

import com.cilys.lottery.web.model.OrderModel;
import com.cilys.lottery.web.model.UserMoneyFlowModel;
import com.cilys.lottery.web.schedu.runnable.*;

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
            } else if (TaskType.SYNC_BONUS_ADD_USER_MONEY_FLOW.equals(taskType)){
                Object data = task.getData();
                if (data != null && data instanceof OrderModel){
                    ThreadPools.executeTask(new OrderBonusAddToUserMoneyFlowRunnable((OrderModel) data));
                }
            } else if (TaskType.CLEAR_PUT_OF_TIME_LOG.equals(taskType)){
                ThreadPools.executeTask(new ClearOutOfTimeLogRunnabl());
            } else if (TaskType.SYNC_USER_INFO_TO_CACHE.equals(taskType)){
                ThreadPools.executeTask(new SyncUserInfoRunnable());
            } else if (TaskType.SYNC_SCHEME_SELLED_AND_PAYED_MONEY.equals(taskType)){
                ThreadPools.executeTask(new SyncSchemeSelledAndPayedMoneyRunnable());
            }
        }
    }
}