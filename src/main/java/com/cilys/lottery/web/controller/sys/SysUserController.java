package com.cilys.lottery.web.controller.sys;

import com.cily.utils.base.StrUtils;
import com.cily.utils.base.log.Logs;
import com.cilys.lottery.web.conf.Param;
import com.cilys.lottery.web.conf.PayType;
import com.cilys.lottery.web.conf.SQLParam;
import com.cilys.lottery.web.controller.BaseController;
import com.cilys.lottery.web.interceptor.*;
import com.cilys.lottery.web.model.UserModel;
import com.cilys.lottery.web.model.impl.UserImpl;
import com.cilys.lottery.web.model.impl.UserMoneyFlowImpl;
import com.cilys.lottery.web.utils.BigDecimalUtils;
import com.cilys.lottery.web.model.utils.UserUtils;
import com.cilys.lottery.web.utils.ParamUtils;
import com.jfinal.aop.Before;
import com.jfinal.kit.HttpKit;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2018/2/5.
 */
//@Before({SysUserInterceptor.class})
public class SysUserController extends BaseController {

    @Before({OptionMethodInterceptor.class})
    public void addUser(){
        try {
            Map<String, Object> params = ParamUtils.parseJson(HttpKit.readData(getRequest()));
            String result = UserImpl.addUser(params);
            if (Param.C_SUCCESS.equals(result)) {
                UserUtils.clearUserCache();
            }
            renderJson(result, null);
        } catch (Exception e) {
            Logs.printException(e);
            renderJsonFailed(Param.C_PARAM_ERROR, null);
        }
    }

    @Before({UserIdInterceptor.class})
    public void updateUserStatus(){
        renderJson(UserImpl.updateUserStatus(getParam(SQLParam.USER_ID), getParam(SQLParam.STATUS)), null);
    }

    @Before({OptionMethodInterceptor.class})
    public void updateUserInfo(){
//        UserUtils.updateUserInfoByJsonData(this, getUserId());
        try {
            Map<String, Object> params = ParamUtils.parseJson(HttpKit.readData(getRequest()));
            String result = UserImpl.updateUserInfo(params, getParam(SQLParam.USER_ID));

            if (Param.C_SUCCESS.equals(result)) {
                UserUtils.clearUserCache();
            }

            renderJson(result, null);
        } catch (Exception e) {
            Logs.printException(e);
            renderJsonFailed(Param.C_PARAM_ERROR, null);
        }
    }

    public void getUsers(){
        String osType = getHeader("osType");

        renderJsonSuccess(UserModel.getUsersByStatus(getParam(SQLParam.STATUS),
                getPageNumber(), getPageSize(), null, "asc", !"1".equals(osType)));
    }

    public void getUserCount(){
        long enableUserCount = UserModel.getEnableUserCount();
        long disableUserCount = UserModel.getDisableUserCount();

        Map<String, Long> map = new HashMap<>();
        map.put("enableUserCount", enableUserCount);
        map.put("disableUserCount", disableUserCount);
        renderJsonSuccess(map);
    }

    @Before({UserIdInterceptor.class})
    public void delUser() {
        String userId = getParam(SQLParam.USER_ID);
        if (UserModel.delByUserId(userId)) {

            UserUtils.clearUserFromCache(userId);

            renderJsonSuccess(null);
        }else {
            renderJsonFailed(Param.C_USER_DEL_FAILED, null);
        }
    }

    @Before({UserIdInterceptor.class})
    public void updateLeftMoney(){
        String userId = getParam(SQLParam.USER_ID);
        //修改用户的账户余额
        String lm = getParam(SQLParam.LEFT_MONEY);
        if (StrUtils.isEmpty(lm)) {
            renderJsonFailed(Param.C_ADD_MONEY_NULL, null);
            return;
        }

        BigDecimal addMoney = BigDecimalUtils.toBigDecimal(lm);
        if (addMoney == null){
            renderJsonFailed(Param.C_ADD_MONEY_ILLAGE, null);
            return;
        }

        //直接入库，无法记录用户资金流水，改成先入库资金流水，系统同步资金流水库的数据到用户账户里
//        UserModel um = UserModel.getUserByUserId(userId);
//        BigDecimal leftMoney = um.get(SQLParam.LEFT_MONEY);
//        if (leftMoney == null){
//            leftMoney = BigDecimalUtils.zero();
//        }
//        BigDecimal newLeftMoney = BigDecimalUtils.add(leftMoney, addMoney);
//        um.set(SQLParam.LEFT_MONEY, newLeftMoney);
//        if (UserModel.updateUserLeftMoney(um)){
//            renderJsonSuccess(null);
//        }else {
//            renderJsonFailed(Param.C_UPDATE_FAILED, null);
//        }
        try{
            if (UserMoneyFlowImpl.addToMoneyFlow(userId,
                    null, addMoney, SQLParam.SYSTEM, PayType.PAY_SYSTEM_RECHARGE)){
                renderJsonSuccess(null);
            }else {
                renderJsonFailed(Param.C_RECHARGE_FAILED, null);
            }
        }catch (Exception e){
            Logs.printException(e);
            renderJsonFailed(Param.C_SERVER_ERROR, null);
        }
    }
}