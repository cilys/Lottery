package com.cilys.lottery.web.controller;

import com.cily.utils.base.StrUtils;
import com.cily.utils.base.log.Logs;
import com.cilys.lottery.web.cache1.UserInfoCache;
import com.cilys.lottery.web.conf.Param;
import com.cilys.lottery.web.conf.SQLParam;
import com.cilys.lottery.web.interceptor.LoginedInterceptor;
import com.cilys.lottery.web.interceptor.UserIdInterceptor;
import com.cilys.lottery.web.log.LogUtils;
import com.cilys.lottery.web.model.UserModel;
import com.cilys.lottery.web.model.impl.UserImpl;
import com.cilys.lottery.web.model.utils.RootUserIdUtils;
import com.cilys.lottery.web.utils.ParamUtils;
import com.cilys.lottery.web.utils.ResUtils;
import com.cilys.lottery.web.model.utils.TokenUtils;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.kit.HttpKit;

import java.util.Map;

/**
 * Created by admin on 2018/1/30.
 */

public class UserController extends BaseController {

    @Before({UserIdInterceptor.class})
    public void userInfo(){
        String userId = getParam(SQLParam.USER_ID);
        UserModel um = UserModel.getUserByUserId(userId);
        String osType = getHeader("osType");
//        um.remove(SQLParam.PWD);
        if (!"1".equals(osType)) {
            um.set(SQLParam.REAL_NAME, UserModel.formcatRealName(um.getStr(SQLParam.REAL_NAME)));
            um.set(SQLParam.PHONE, UserModel.formcatPhone(um.getStr(SQLParam.PHONE)));
            um.set(SQLParam.ID_CARD, UserModel.formcatIdCard(um.getStr(SQLParam.ID_CARD)));
            um.set(SQLParam.ADDRESS, UserModel.formcatAddress(um.getStr(SQLParam.ADDRESS)));
        }
        renderJsonSuccess(um);
    }
//
//    @Before({PwdInterceptor.class})
    public void changePwd(){
        String pwd = getParam(SQLParam.PWD);
        String newPwd = getParam("newPwd");
        if (StrUtils.isEmpty(newPwd)){
            renderJsonFailed(Param.C_PWD_NEW_NULL, null);
            return;
        }
        if (newPwd.length() > 32){
            renderJsonFailed(Param.C_PWD_ILLAGLE, null);
            return;
        }
        String userId = getUserId();
        UserModel um = UserModel.getUserByUserId(userId);
        if (um == null){
            renderJsonFailed(Param.C_USER_NOT_EXIST, null);
            return;
        }
        if (!pwd.equals(um.get(SQLParam.PWD))){
            renderJsonFailed(Param.C_PWD_NOT_EQUAL, null);
            return;
        }
        if (UserModel.updateUserInfo(userId, newPwd, null, null,
                null, null, null, null, null) == UserModel.USER_INFO_UPDATE_SUCCESS){
            renderJsonSuccess(null);
        }else {
            renderJsonFailed(Param.C_PWD_CHANGE_FAILED, null);
        }
    }

    @Clear({LoginedInterceptor.class})
    public void login(){
        String userName = getParam(SQLParam.USER_NAME);
        String pwd = getParam(SQLParam.PWD);
        String deviceImei = getDeviceImei();

        UserModel um = UserImpl.queryByUserName(userName);
        if (um == null){
            renderJson(ResUtils.res(Param.C_USER_NOT_EXIST, null, null));
            return;
        }

        if (pwd.equals(um.getStr(SQLParam.PWD))){
            um.remove(SQLParam.PWD);

            if (Param.REQUEST_SOURCE_WEB.equals(getHeader(Param.OS_TYPE))) {
                if (RootUserIdUtils.isRootUser(um.getStr(SQLParam.USER_ID))) {
//                    String status = um.getStr(SQLParam.STATUS, SQLParam.STATUS_DISABLE);
//                    if (SQLParam.STATUS_ENABLE.equals(status)){
                        String token = TokenUtils.createToken(um.getStr(SQLParam.USER_ID), deviceImei, null);
                        renderJson(ResUtils.success(token, um));
//                    } else {
//                        renderJsonFailed(Param.C_USER_DISABLE, null);
//                    }
                    return;
                }else {
                    renderJsonFailed(Param.C_RIGHT_LOW, null);
                }
            }else {
                String status = um.getStr(SQLParam.STATUS, SQLParam.STATUS_DISABLE);
                if (SQLParam.STATUS_ENABLE.equals(status)){
                    String token = TokenUtils.createToken(um.getStr(SQLParam.USER_ID), deviceImei, null);
                    renderJson(ResUtils.success(token, um));
                } else {
                    renderJsonFailed(Param.C_USER_DISABLE, null);
                }
            }
            return;
        }else {
            renderJson(ResUtils.res(Param.C_USER_OR_PWD_ERROR, null, null));
            return;
        }
    }

    protected boolean checkUserIsRoot(String userId){
        return false;
    }

    @Clear({LoginedInterceptor.class})
    public void regist(){
        try {
            String str = HttpKit.readData(getRequest());
            LogUtils.info(getClass().getSimpleName(), null, getRequest().getRequestURI(), str, getUserId());

            Map<String, Object> params = ParamUtils.parseJson(str);
            params.remove(SQLParam.STATUS);
            params.remove(SQLParam.LEFT_MONEY);

            params.put(SQLParam.STATUS, SQLParam.STATUS_DISABLE);

            String result = UserImpl.addUser(params);
            if (Param.C_SUCCESS.equals(result)) {
                UserInfoCache.clearUserCache();
            }
            renderJson(result, null);
        } catch (Exception e) {
            Logs.printException(e);
            renderJsonFailed(Param.C_PARAM_ERROR, null);
        }
    }

    public void updateInfo(){
//        UserUtils.updateUserInfoByJsonData(this, getUserId());
        try {
            String str = HttpKit.readData(getRequest());
            LogUtils.info(getClass().getSimpleName(), null, getRequest().getRequestURI(), str, getUserId());
            Map<String, Object> params = ParamUtils.parseJson(str);

            params.remove(SQLParam.LEFT_MONEY);

            String result = UserImpl.updateUserInfo(params, getUserId());

            if (Param.C_SUCCESS.equals(result)) {
                UserInfoCache.clearUserCache();
            }

            renderJson(result, null);
        } catch (Exception e) {
            Logs.printException(e);
            renderJsonFailed(Param.C_PARAM_ERROR, null);
        }
    }
}