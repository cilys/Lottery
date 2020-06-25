package com.cilys.lottery.web.interceptor;

import com.cily.utils.base.StrUtils;
import com.cilys.lottery.web.conf.Param;
import com.cilys.lottery.web.conf.SQLParam;
import com.cilys.lottery.web.model.UserModel;
import com.jfinal.aop.Invocation;

/**
 * Created by admin on 2018/2/23.
 * 请求参数中，userId是否null，是否存在该用户
 */
public class UserIdInterceptor extends BaseInterceptor {
    @Override
    public void intercept(Invocation inv) {
        String userId = getParam(inv, SQLParam.USER_ID);
        if (StrUtils.isEmpty(userId)){
            renderJson(inv, Param.C_USER_ID_NULL, createTokenByOs(inv), null);
            return;
        }
        if (UserModel.getUserByUserId(userId) != null){
            inv.invoke();
        }else {
            renderJson(inv, Param.C_USER_NOT_EXIST, createTokenByOs(inv), null);
        }
    }
}