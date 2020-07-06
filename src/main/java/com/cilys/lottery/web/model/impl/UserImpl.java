package com.cilys.lottery.web.model.impl;

import com.cily.utils.base.StrUtils;
import com.cily.utils.base.UUIDUtils;
import com.cily.utils.base.log.Logs;
import com.cilys.lottery.web.conf.Param;
import com.cilys.lottery.web.conf.PayType;
import com.cilys.lottery.web.conf.SQLParam;
import com.cilys.lottery.web.model.UserModel;
import com.cilys.lottery.web.utils.BigDecimalUtils;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by admin on 2020/6/25.
 * 用户信息处理
 */
public class UserImpl {

    public static String addUser(Map<String, Object> params){
        if (params == null || params.size() < 1){
            return Param.C_PARAM_ERROR;
        }
        params.remove("rePwd");
        UserModel um = new UserModel();
        um.put(params);
        um.removeNullValueAttrs();


        //初始增加用户，则没有资金余额
        um.remove(SQLParam.LEFT_MONEY);
        um.set(SQLParam.USER_ID, UUIDUtils.getUUID());

        if (UserModel.getUserByUserName(um.getStr(SQLParam.USER_NAME)) != null){
            return Param.C_RESIGT_USER_EXISTS;
        }

        if (um.save()) {
            return Param.C_SUCCESS;
        }else {
            return Param.C_REGIST_FAILURE;
        }
    }

    public static String updateUserStatus(String userId, String status){
        if (StrUtils.isEmpty(userId)){
            return Param.C_USER_ID_NULL;
        }
        UserModel um = UserModel.getUserByUserId(userId);
        if (um == null){
            return Param.C_USER_NOT_EXIST;
        }
        if (SQLParam.STATUS_ENABLE.equals(status) || SQLParam.STATUS_DISABLE.equals(status)){
            um.set(SQLParam.STATUS, status);
            if (um.update()){
                return Param.C_SUCCESS;
            }else {
                return Param.C_UPDATE_FAILED;
            }
        }else {
            return Param.C_STATUS_ERROR;
        }
    }

    public static String updateUserInfo(Map<String, Object> params, String targetUserId){
        if (params == null || params.size() < 1){
            return Param.C_PARAM_ERROR;
        }
        if (StrUtils.isEmpty(targetUserId)){
            return Param.C_USER_ID_NULL;
        }

        UserModel um = UserModel.getUserByUserId(targetUserId);
        if (um == null){
            return Param.C_USER_NOT_EXIST;
        }
        params.remove("rePwd");
        params.remove(SQLParam.USER_ID);

        BigDecimal oldLeftMoney = um.getBigDecimal(SQLParam.LEFT_MONEY);
        String newLm = (String)params.get(SQLParam.LEFT_MONEY);
        BigDecimal newLeftMoney = BigDecimalUtils.toBigDecimal(newLm);
        if (newLeftMoney == null){
            um.remove(SQLParam.LEFT_MONEY);
            params.remove(SQLParam.LEFT_MONEY);
        }else {
            if (oldLeftMoney != null){
                if (BigDecimalUtils.equal(oldLeftMoney, newLeftMoney)){
                    //新旧余额一致，则无需添加到资金流水里
                    um.remove(SQLParam.LEFT_MONEY);
                    params.remove(SQLParam.LEFT_MONEY);
                }else {

                }
            } else {
                //旧余额为空，直接使用新余额
            }
        }
        um.put(params);
        um.removeNullValueAttrs();

        Object refreshLm = um.get(SQLParam.LEFT_MONEY);
        BigDecimal refreshLeftMoney = null;
        if (refreshLm == null){

        }else {
            if (refreshLm instanceof String){
                refreshLeftMoney = BigDecimalUtils.toBigDecimal((String)refreshLm);
            } else if (refreshLm instanceof BigDecimal) {
                refreshLeftMoney = (BigDecimal)refreshLm;
            }
        }

        if (refreshLeftMoney == null){
            //为空，表示没有余额需要更新
        }else {
            //不为空，表示需要更新余额
            try{
                UserMoneyFlowImpl.addToMoneyFlow(targetUserId, null, null,
                        refreshLeftMoney, SQLParam.SYSTEM, PayType.PAY_SYSTEM_UPDATE_USER_LEFT_MONEY);
            }catch (Exception e){
                Logs.printException(e);
            }
        }
        um.remove(SQLParam.LEFT_MONEY);

        boolean result = um.update();

        return result ? Param.C_SUCCESS : Param.C_UPDATE_FAILED;

    }

    public static UserModel queryByUserName(String userName){
        if (StrUtils.isEmpty(userName)){
            return null;
        }
        return UserModel.getUserByUserName(userName);
    }















}