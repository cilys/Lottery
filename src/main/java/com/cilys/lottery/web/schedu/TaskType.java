package com.cilys.lottery.web.schedu;

/**
 * Created by admin on 2020/6/24.
 */
public interface TaskType {
    //单条流水同步到用户表的余额里
    String SYNC_SINGLE_USER_MONEY_FLOW_TO_USER = "SYNC_SINGLE_USER_MONEY_FLOW_TO_USER";
    //全部流水同步到用户表的余额里
    String SYNC_TOTAL_USER_MONEY_FLOW_TO_USER = "SYNC_TOTAL_USER_MONEY_FLOW_TO_USER";
}
