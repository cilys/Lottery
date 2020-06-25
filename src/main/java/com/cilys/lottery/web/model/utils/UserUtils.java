package com.cilys.lottery.web.model.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cily.utils.base.StrUtils;
import com.cilys.lottery.web.conf.Param;
import com.cilys.lottery.web.conf.SQLParam;
import com.cilys.lottery.web.controller.BaseController;
import com.cilys.lottery.web.model.UserModel;
import com.cilys.lottery.web.utils.ResUtils;
import com.jfinal.kit.HttpKit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by admin on 2018/2/5.
 */
public class UserUtils {

    public static void regist(BaseController c, String userId, String status){
        if(c == null){
            throw new NullPointerException("The Controller is null.");
        }

        String token = c.getHeader(SQLParam.TOKEN);
        String deviceImei = c.getAttr(Param.DEVICE_IMEI);

        String userName = c.getPara(SQLParam.USER_NAME);
        String pwd = c.getPara(SQLParam.PWD);
        String realName = c.getPara(SQLParam.REAL_NAME, null);
        String sex = c.getPara(SQLParam.SEX, null);
        String phone = c.getPara(SQLParam.PHONE, null);
        String address = c.getPara(SQLParam.ADDRESS, null);
        String idCard = c.getPara(SQLParam.ID_CARD, null);

        if (StrUtils.isEmpty(status)){
            status = SQLParam.STATUS_DISABLE;
        }

        if (UserModel.getUserByUserName(userName) != null){
            c.renderJson(ResUtils.res(Param.C_RESIGT_USER_EXISTS,
                    TokenUtils.createToken(userId, deviceImei, token), null));
            return;
        }

        if (UserModel.insert(userName, pwd, realName,
                sex, phone, address, idCard, status)){
            c.renderJson(ResUtils.success(TokenUtils.createToken(
                    userId, deviceImei, token), null));
        }else {
            c.renderJson(ResUtils.res(Param.C_REGIST_FAILURE,
                    TokenUtils.createToken(userId, deviceImei, token), null));
        }
    }

    public static void registByJsonData(BaseController c, String userId){
        String token = c.getHeader(SQLParam.TOKEN);
        String deviceImei = c.getAttr(Param.DEVICE_IMEI);

        String params = HttpKit.readData(c.getRequest());
        Logger.getLogger(UserUtils.class.getSimpleName()).info("params = " + params);
        System.out.println("params = " + params);
        Map<String, Object> m = JSON.parseObject(params,  new TypeReference<Map<String, Object>>(){}.getType());
        UserModel um = new UserModel();
        um.put(m);
        um.removeNullValueAttrs();
//        um.set(SQLParam.USER_ID, UUIDUtils.getUUID());
        if (UserModel.getUserByUserName(um.getStr(SQLParam.USER_NAME)) != null){
            c.renderJson(ResUtils.res(Param.C_RESIGT_USER_EXISTS,
                    TokenUtils.createToken(userId, deviceImei, token), null));
            return;
        }

        if (um.save()) {
            c.renderJsonSuccess(um);
        }else {
            c.renderJson(ResUtils.res(Param.C_REGIST_FAILURE,
                    TokenUtils.createToken(userId, deviceImei, token), null));
        }
    }

    public static void updateUserInfo(BaseController c, String rootUserId , String userId, String status){
        if(c == null){
            throw new NullPointerException("The Controller is null.");
        }
        String token = c.getHeader(SQLParam.TOKEN);
        String deviceImei = c.getAttr(Param.DEVICE_IMEI);

        String pwd = c.getPara(SQLParam.PWD);
        String realName = c.getPara(SQLParam.REAL_NAME, null);
        String sex = c.getPara(SQLParam.SEX, null);
        String phone = c.getPara(SQLParam.PHONE, null);
        String address = c.getPara(SQLParam.ADDRESS, null);
        String idCard = c.getPara(SQLParam.ID_CARD, null);

        if (StrUtils.isEmpty(userId)){
            c.renderJson(ResUtils.res(Param.C_USER_ID_NULL, TokenUtils.createToken(rootUserId, deviceImei, token), null));
            return;
        }

        int updateResult = UserModel.updateUserInfo(userId, pwd, realName,
                sex, phone, address, idCard, status, null);
        if (updateResult == UserModel.USER_INFO_UPDATE_SUCCESS){
            c.renderJson(ResUtils.success(TokenUtils.createToken(
                    rootUserId, deviceImei, token), null));
        }else if (updateResult == UserModel.USER_NOT_EXIST){
            c.renderJson(ResUtils.res(Param.C_USER_NOT_EXIST,
                    TokenUtils.createToken(rootUserId, deviceImei, token), null));
        }else if (updateResult == UserModel.USER_INFO_NO_UPDATE){
            c.renderJson(ResUtils.res(Param.C_USER_INFO_NO_UPDATE,
                    TokenUtils.createToken(rootUserId, deviceImei, token), null));
        }else {
            c.renderJson(ResUtils.res(Param.C_USER_INFO_UPDATE_FAILURE,
                    TokenUtils.createToken(rootUserId, deviceImei, token), null));
        }
    }

    public static void updateUserInfoByJsonData(BaseController c, String rootUserId){
        if(c == null){
            throw new NullPointerException("The Controller is null.");
        }
        String token = c.getHeader(SQLParam.TOKEN);
        String deviceImei = c.getAttr(Param.DEVICE_IMEI);

        String params = HttpKit.readData(c.getRequest());
        Logger.getLogger(UserUtils.class.getSimpleName()).info("params = " + params);
        System.out.println("params = " + params);
        Map<String, Object> m = JSON.parseObject(params,  new TypeReference<Map<String, Object>>(){}.getType());
        UserModel um = new UserModel();
        um.put(m);
        um.removeNullValueAttrs();

        String userId = um.getStr(SQLParam.USER_ID);
        String pwd = um.getStr(SQLParam.PWD);
        String realName = um.getStr(SQLParam.REAL_NAME);
        String sex = um.getStr(SQLParam.SEX);
        String phone = um.getStr(SQLParam.PHONE);
        String address = um.getStr(SQLParam.ADDRESS);
        String idCard = um.getStr(SQLParam.ID_CARD);
        String status = um.getStr(SQLParam.STATUS);

        if (StrUtils.isEmpty(userId)){
            c.renderJson(ResUtils.res(Param.C_USER_ID_NULL, TokenUtils.createToken(rootUserId, deviceImei, token), null));
            return;
        }

        int updateResult = UserModel.updateUserInfo(userId, pwd, realName,
                sex, phone, address, idCard, status, null);
        if (updateResult == UserModel.USER_INFO_UPDATE_SUCCESS){
            c.renderJson(ResUtils.success(TokenUtils.createToken(
                    rootUserId, deviceImei, token), null));
        }else if (updateResult == UserModel.USER_NOT_EXIST){
            c.renderJson(ResUtils.res(Param.C_USER_NOT_EXIST,
                    TokenUtils.createToken(rootUserId, deviceImei, token), null));
        }else if (updateResult == UserModel.USER_INFO_NO_UPDATE){
            c.renderJson(ResUtils.res(Param.C_USER_INFO_NO_UPDATE,
                    TokenUtils.createToken(rootUserId, deviceImei, token), null));
        }else {
            c.renderJson(ResUtils.res(Param.C_USER_INFO_UPDATE_FAILURE,
                    TokenUtils.createToken(rootUserId, deviceImei, token), null));
        }
    }

    private static boolean isEmpty(String... strs){
        if (strs == null){
            return true;
        }
        if (strs.length < 1){
            return true;
        }
        for (String s : strs){
            if (!StrUtils.isEmpty(s)){
                return false;
            }
        }
        return true;
    }

    public static boolean userExist(String userId) {
        if (StrUtils.isEmpty(userId)){
            return false;
        }
        UserModel um = UserModel.getUserByUserId(userId);
        return um != null;
    }

    private static Map<String, UserModel> cacheUserMap;
    private static long lastCacheUserTime;

    public static String getUserRealNameFromCache(String userId){
        return getUserRealNameFromCache(userId, false);
    }
    public static String getUserRealNameFromCache(String userId, boolean reset){
        //上次同步的数据如果是1天（默认）之前的数据，强制清空后，从数据库里更新
        if (System.currentTimeMillis() - lastCacheUserTime > 24 * 60 * 60 *1000){
            clearUserCache();
        }

        if (cacheUserMap == null || cacheUserMap.size() < 1){
            List<UserModel> ls = UserModel.queryAll();
            if (ls != null && ls.size() > 0){
                if (reset){
                    clearUserCache();
                }
                cacheUserMap = new HashMap<>();

                for (UserModel u : ls){
                    cacheUserMap.put(u.getStr(SQLParam.USER_ID), u);
                }
                lastCacheUserTime = System.currentTimeMillis();
            }
        }
        if (StrUtils.isEmpty(userId)){
            return userId;
        }

        if (cacheUserMap != null && cacheUserMap.size() > 0){
            UserModel m = cacheUserMap.get(userId);
            String realName = m.getStr(SQLParam.REAL_NAME);
            if (!StrUtils.isEmpty(realName)){
                return realName;
            }
            String userName = m.getStr(SQLParam.USER_NAME);
            if (!StrUtils.isEmpty(userName)){
                return userName;
            }
        }
        return userId;
    }

    public static void clearUserFromCache(String userId){
        if (StrUtils.isEmpty(userId)){
            return;
        }
        if (cacheUserMap != null){
            cacheUserMap.remove(userId);
        }
    }

    public static void clearUserCache(){
        if (cacheUserMap != null){
            cacheUserMap.clear();
        }
    }


}
