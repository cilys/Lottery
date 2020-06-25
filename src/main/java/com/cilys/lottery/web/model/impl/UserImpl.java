package com.cilys.lottery.web.model.impl;

import com.cilys.lottery.web.conf.Param;
import com.cilys.lottery.web.conf.SQLParam;
import com.cilys.lottery.web.model.UserModel;

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
        UserModel um = new UserModel();
        um.put(params);
        um.removeNullValueAttrs();

        //初始增加用户，则没有资金余额
        um.remove(SQLParam.LEFT_MONEY);

        if (UserModel.getUserByUserName(um.getStr(SQLParam.USER_NAME)) != null){
            return Param.C_RESIGT_USER_EXISTS;
        }

        if (um.save()) {
            return Param.C_SUCCESS;
        }else {
            return Param.C_REGIST_FAILURE;
        }
    }





















}