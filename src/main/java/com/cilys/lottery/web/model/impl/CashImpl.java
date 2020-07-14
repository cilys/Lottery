package com.cilys.lottery.web.model.impl;

import com.cily.utils.base.StrUtils;
import com.cily.utils.base.time.TimeUtils;
import com.cilys.lottery.web.cache1.UserInfoCache;
import com.cilys.lottery.web.conf.Param;
import com.cilys.lottery.web.conf.PayType;
import com.cilys.lottery.web.conf.SQLParam;
import com.cilys.lottery.web.model.CashModel;
import com.cilys.lottery.web.utils.BigDecimalUtils;
import com.jfinal.plugin.activerecord.Page;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2020/7/7.
 */
public class CashImpl {

    public static String insert(String userId, String money, String msg) throws Exception {
        if (StrUtils.isEmpty(userId)) {
            return Param.C_USER_ID_NULL;
        }
        if (StrUtils.isEmpty(money)) {
            return Param.C_MONEY_NULL;
        }
        BigDecimal m = BigDecimalUtils.toBigDecimal(money);
        if (m == null){
            return Param.C_MONEY_ILLAGE;
        }
        if (BigDecimalUtils.noMoreThan(m, BigDecimalUtils.zero())) {
            return Param.C_MONEY_ILLAGE;
        }

        CashModel model = new CashModel();
        model.set(SQLParam.USER_ID, userId);
        model.set(SQLParam.MONEY, money);
        if (!StrUtils.isEmpty(msg)) {
            model.set(SQLParam.MSG, msg);
        }
        String currentTime = TimeUtils.milToStr(System.currentTimeMillis(), null);
        model.set(SQLParam.APPLY_TIME, currentTime);
        model.set(SQLParam.CREATE_TIME, currentTime);

        boolean inserResult = CashModel.insert(model);
        if (inserResult) {
            Integer cashId = model.getInt(SQLParam.ID, null);
            String result = UserImpl.applyCash(userId, money);    //原来的实现方式，直接修改用户表里的可用余额和冻结余额
//            String result = UserImpl.applyCash(cashId, userId, money);  //新方式，通过资金流水表，实现资金同步
            return result;
        }else {
            return Param.C_INSERT_FAILED;
        }
    }

    public static String updateStatus(int id, String operator, String operatorResult,
                                      String status) throws Exception{
        if (StrUtils.isEmpty(operator)){
            return Param.C_OPERATOR_NULL;
        }

        if (CashModel.STATUS_APPLY_SUCCESS.equals(status)
                || CashModel.STATUS_APPLY_REFUSE.equals(status)
                || CashModel.STATUS_APPLY_PROCESS.equals(status)){

        } else {
            return Param.C_STATUS_ERROR;
        }

        CashModel m = CashModel.queryById(id);
        if (m == null){
            return Param.C_CASH_NOT_EXIST;
        }

        String userId = m.getStr(SQLParam.USER_ID);
        BigDecimal money = m.getBigDecimal(SQLParam.MONEY);

        m.set(SQLParam.OPERATOR, operator);
        if (!StrUtils.isEmpty(operatorResult)){
            m.set(SQLParam.OPERATOR_RESULT, operatorResult);
        }
        m.set(SQLParam.STATUS, status);
        if (CashModel.updateStatus(m)){
//            return Param.C_SUCCESS;
            if (CashModel.STATUS_APPLY_SUCCESS.equals(status)){
                //提现成功，添加到资金流水里
                BigDecimal newMoney = BigDecimalUtils.subtract(money, BigDecimalUtils.add(money, money));
                boolean f = UserMoneyFlowImpl.addToMoneyFlow(userId, null,
                        null, id, newMoney, SQLParam.SYSTEM, PayType.PAY_APPLY_CASH_SUCCESS);
                if (f){
                    return Param.C_SUCCESS;
                }else {
                    return Param.C_UPDATE_FAILED;
                }
            } else if (CashModel.STATUS_APPLY_REFUSE.equals(status)){
                //拒绝，则把冻结中的钱，还回到可用余额里
                return UserImpl.applyRefuse(userId, money);
            } else {
                return Param.C_SUCCESS;
            }
        }else {
            return Param.C_UPDATE_FAILED;
        }
    }

    public static Page<CashModel> query(int pageNumber, int pageSize, String queryWhere, String order){
        Page<CashModel> result = CashModel.query(pageNumber, pageSize, queryWhere, order);
        if (result != null){
            if (result.getList() != null){
                for (CashModel m : result.getList()) {
                    String userId = m.getStr(SQLParam.USER_ID);
                    String operator = m.getStr(SQLParam.OPERATOR);
                    m.setRealName(UserInfoCache.getUserRealNameFromCache(userId));
                    m.setOperatorName(UserInfoCache.getUserRealNameFromCache(operator));
                }
            }
        }
        return result;
    }
}