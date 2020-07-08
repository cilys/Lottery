package com.cilys.lottery.web.interceptor;

import com.cily.utils.base.StrUtils;
import com.cilys.lottery.web.conf.Param;
import com.cilys.lottery.web.model.TokenModel;
import com.jfinal.aop.Invocation;

/**
 * Created by admin on 2018/2/6.
 */
public class LoginedInterceptor extends BaseInterceptor {
    @Override
    public void intercept(Invocation inv) {
        String userId = getUserId(inv);
        String token = getToken(inv);
        String deviceImei = getDeviceImeiFromAttr(inv);
        if (StrUtils.isEmpty(userId)){
            renderJson(inv, Param.C_USER_ID_NULL, null, null);
            return;
        }
        if (StrUtils.isEmpty(token)){
            renderJson(inv, Param.C_TOKEN_NULL, null, null);
            return;
        }
        //TODO 数据库验证token是否有效
        String checkTokenResult = TokenModel.checkTokenByUserId(userId, token);
        if (Param.C_SUCCESS.equals(checkTokenResult)){
            inv.invoke();
        }else {
            renderJson(inv, checkTokenResult, null, null);
            return;
        }
    }
}