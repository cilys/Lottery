package com.cilys.lottery.web.interceptor;

import com.cily.utils.base.StrUtils;
import com.cilys.lottery.web.conf.Param;
import com.cilys.lottery.web.conf.SQLParam;
import com.jfinal.aop.Invocation;

/**
 * Created by 123 on 2018/5/4.
 */
public class PhoneInterceptor extends BaseInterceptor {
    @Override
    public void intercept(Invocation inv) {
        String phone = getParam(inv, SQLParam.PHONE);
        if (!StrUtils.isEmpty(phone)){
            if (phone.matches(Param.REGX_PHONE)){
                inv.invoke();
            }else {
                renderJson(inv, Param.C_PHONE_ILLAGLE);
                return;
            }
        }else {
            inv.invoke();
        }
    }
}
