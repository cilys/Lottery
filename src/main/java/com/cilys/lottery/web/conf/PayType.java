package com.cilys.lottery.web.conf;

/**
 * Created by admin on 2020/6/22.
 */
public interface PayType {
    String PAY_YU_E = "1";      //账号余额
    String PAY_WECHAT = "2";    //微信
    String PAY_ALI_PAY = "3";   //支付宝
    String PAY_BANK = "4";      //银联
    String PAY_MONEY = "5";     //现金

    String PAY_SYSTEM_BACK = "6";       //系统退款
    String PAY_SYSTEM_RECHARGE = "7";   //系统充值
    String PAY_SYSTEM_UPDATE_USER_LEFT_MONEY = "8";     //系统更新用户的余额

    String PAY_SYSTEM_BONUS = "9";      //系统下发奖金

    @Deprecated
    String PAY_APPLY_CASH_SUCCESS = "10";   //申请提现成功
    String PAY_APPLY_CASH_REFUSE = "11";

    String PAY_APPLY_CASH_REDUCE_LEFT_MONEY = "12"; //申请提现，产生一条减少可用余额的交易记录
    String PAY_APPLY_CASH_ADD_COLD_MONEY = "13";    //申请提现，并且产生一条增加冻结余额的交易记录

    String PAY_APPLY_CASH_REFUSE_REDUCE_COLD_MONEY = "14";  //拒绝提现，产生一条减少冻结余额的交易记录
    String PAY_APPLY_CASH_REFUSE_ADD_LEFT_MONEY = "15";     //拒绝提现，产生一条增加可用余额的交易记录
    String PAY_APPLY_CASH_AGREE = "16";                     //同意提现，产生一条减少冻结余额的交易记录

}
