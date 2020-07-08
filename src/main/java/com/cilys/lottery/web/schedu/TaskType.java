package com.cilys.lottery.web.schedu;

/**
 * Created by admin on 2020/6/24.
 */
public interface TaskType {
    //单条流水同步到用户表的余额里
    String SYNC_SINGLE_USER_MONEY_FLOW_TO_USER = "SYNC_SINGLE_USER_MONEY_FLOW_TO_USER";
    //全部流水同步到用户表的余额里
    String SYNC_TOTAL_USER_MONEY_FLOW_TO_USER = "SYNC_TOTAL_USER_MONEY_FLOW_TO_USER";
    //一个订单的奖金同步到资金流水里
    String SYNC_BONUS_ADD_USER_MONEY_FLOW = "SYNC_BONUS_ADD_USER_MONEY_FLOW";
    //把一个方案下所有订单的奖金，同步到资金流水里
    String SYNC_SCHEME_BONUS_ADD_USER_MONEY_FLOW = "SYNC_SCHEME_BONUS_ADD_USER_MONEY_FLOW";

    //删除过期日志
    String CLEAR_PUT_OF_TIME_LOG = "CLEAR_PUT_OF_TIME_LOG";

    //同步用户信息到缓存里
    String SYNC_USER_INFO_TO_CACHE = "SYNC_USER_INFO_TO_CACHE";

    //同步方案表中的，已售金额、已付款金额
    String SYNC_SCHEME_SELLED_AND_PAYED_MONEY = "SYNC_SCHEME_SELLED_AND_PAYED_MONEY";

    //申请提现成功
    String APPLY_CASH_SUCCESS = "APPLY_CASH_SUCCESS";
    //拒绝提现
//    String APPLY_CASH_REFUSE = "APPLY_CASH_REFUSE";
}
