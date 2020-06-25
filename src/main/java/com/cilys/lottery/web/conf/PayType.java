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
}
