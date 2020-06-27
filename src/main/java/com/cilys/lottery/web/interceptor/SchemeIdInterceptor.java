package com.cilys.lottery.web.interceptor;

import com.jfinal.aop.Invocation;

/**
 * Created by admin on 2020/6/25.
 */
public class SchemeIdInterceptor extends BaseInterceptor {
    @Override
    public void intercept(Invocation inv) {
        inv.invoke();
    }
}
