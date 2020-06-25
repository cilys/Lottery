package com.cilys.lottery.web.schedu;

import java.io.Serializable;

/**
 * Created by admin on 2020/6/24.
 */
public class TaskBean<T> implements Serializable {
    private String type;
    private T data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static TaskBean syncUserMoneyFlowToUser(int id){
        return createTask(TaskType.SYNC_SINGLE_USER_MONEY_FLOW_TO_USER, id);
    }
    public static TaskBean syncUserMoneyFlowToUser(){
        return syncUserMoneyFlowToUser(-1);
    }
    public static <T>TaskBean createTask(String taskType, T data){
        TaskBean<T> t = new TaskBean<>();
        t.setType(taskType);
        t.setData(data);
        return t;
    }
    public static TaskBean createTask(String taskType){
        return createTask(taskType, null);
    }
}
