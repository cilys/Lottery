package com.cilys.lottery.web.interceptor;

import com.cilys.lottery.web.conf.Param;
import com.cilys.lottery.web.conf.SQLParam;
import com.cilys.lottery.web.utils.ResUtils;
import com.cilys.lottery.web.model.utils.TokenUtils;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

/**
 * Created by admin on 2018/2/22.
 */
public abstract class BaseInterceptor implements Interceptor {

    protected String getParam(Invocation inv, String paramKey, String defValue){
        if (inv == null){
            return null;
        }
        return inv.getController().getPara(paramKey, defValue);
    }

    protected String getParam(Invocation inv, String paramKey){
        return getParam(inv, paramKey, null);
    }

    protected String getDeviceImei(Invocation inv){
        return getHead(inv, Param.DEVICE_IMEI);
    }
    protected String getDeviceImeiFromAttr(Invocation inv){
        return inv.getController().getAttr(Param.DEVICE_IMEI);
    }
    protected String getUserId(Invocation inv){
        return getHead(inv, SQLParam.USER_ID);
    }
    protected String getToken(Invocation inv){
        return getHead(inv, SQLParam.TOKEN);
    }

    protected String getHead(Invocation inv, String key){
        if (inv == null){
            return null;
        }
        return inv.getController().getHeader(key);
    }

    protected String createTokenByOs(Invocation inv){
        return TokenUtils.createToken(getUserId(inv), getDeviceImei(inv), getToken(inv));
    }

    protected void renderJson(Invocation inv, Object o){
        if (inv != null ){
            inv.getController().getResponse().setHeader("Access-Control-Allow-Origin", "*");
            inv.getController().getResponse().setHeader("Access-Control-Allow-Headers", "osType, userId, token, roomNumber, Content-Type, Accept");
            inv.getController().renderJson(o);
        }
    }

    protected void renderJson(Invocation inv, String errorCode, String token, Object data){
        renderJson(inv, ResUtils.res(errorCode, token, data));

    }
}