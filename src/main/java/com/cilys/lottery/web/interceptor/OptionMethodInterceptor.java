package com.cilys.lottery.web.interceptor;

import com.cilys.lottery.web.conf.Param;
import com.jfinal.aop.Invocation;

public class OptionMethodInterceptor extends BaseInterceptor {
    @Override
    public void intercept(Invocation inv) {
        if ("OPTIONS".equalsIgnoreCase(inv.getController().getRequest().getMethod())){
            renderJson(inv, Param.C_SUCCESS, null, null);
            return;
        }
        inv.invoke();
    }
}
