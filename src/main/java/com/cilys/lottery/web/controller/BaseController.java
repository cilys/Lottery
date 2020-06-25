package com.cilys.lottery.web.controller;

import com.cily.utils.base.log.Logs;
import com.cilys.lottery.web.conf.Param;
import com.cilys.lottery.web.conf.SQLParam;
import com.cilys.lottery.web.utils.ResUtils;
import com.cilys.lottery.web.model.utils.TokenUtils;
import com.jfinal.core.Controller;

/**
 * Created by admin on 2018/2/22.
 */
public class BaseController extends Controller {

    protected String getDeviceImei(){
        return getAttr(Param.DEVICE_IMEI);
    }

    protected String getParam(String paramKey, String defValue){
        return getPara(paramKey, defValue);
    }
    protected String getParam(String paramKey){
        return getParam(paramKey, null);
    }

    protected Integer getInt(String paramKey){
        return getInt(paramKey, null);
    }

    protected Integer getIntWithDefaultValue(String paramKey){
        return getInt(paramKey, -1);
    }

    protected Integer getInt(String paramKey, Integer defValue){
        return getParaToInt(paramKey, defValue);
    }

    protected Float getFloat(String paramKey){
        return getFloat(paramKey, null);
    }

    protected Float getFloat(String paramKey, Float defValue){
        String v = getParam(paramKey);
        if (v == null){
            return defValue;
        }
        try{
            Float d = Float.valueOf(v);
            return d;
        }catch (NumberFormatException e){
            Logs.printException(e);
            return defValue;
        }
    }

    protected Double getDouble(String paramKey){
        return getDouble(paramKey, 0d);
    }

    protected Double getDouble(String paramKey, Double defValue) {
        String v = getParam(paramKey);
        if (v == null){
            return defValue;
        }
        try{
            Double d = Double.valueOf(v);
            return d;
        }catch (NumberFormatException e){
            Logs.printException(e);
            return defValue;
        }
    }

    protected String getToken(){
        return getHeader(SQLParam.TOKEN);
    }

    protected String getUserId(){
        String userId = getHeader(SQLParam.USER_ID);
        return userId;
    }

    protected String createTokenByOs(){
        return TokenUtils.createToken(getUserId(), getDeviceImei(), getToken());
    }

    @Override
    public void renderJson(Object object) {
//        getResponse().setHeader("Access-Control-Allow-Origin", "*");
//        getResponse().setHeader("Access-Control-Allow-Headers", "Content-Type,Access-Token");
        getResponse().setHeader("Access-Control-Allow-Origin", "*");
        getResponse().setHeader("Access-Control-Allow-Headers", "osType, userId, token, roomNumber, Content-Type, Accept");
        super.renderJson(object);
    }

    public void renderJsonSuccess(Object data){
        renderJson(ResUtils.success(createTokenByOs(), data));
    }

    public void renderJsonFailed(String code, Object data){
        renderJson(ResUtils.res(code, createTokenByOs(), data));
    }

    public void renderJson(String code, Object data){
        renderJsonFailed(code, data);
    }


    protected int getPageNumber(){
        return getInt(Param.PAGE_NUMBER, 1);
    }

    protected int getPageSize(){
        return getInt(Param.PAGE_SIZE, 10);
    }
}
