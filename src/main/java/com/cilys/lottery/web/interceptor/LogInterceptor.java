package com.cilys.lottery.web.interceptor;

import com.jfinal.aop.Invocation;

import java.util.Enumeration;
import java.util.logging.Logger;

/**
 * Created by admin on 2018/2/6.
 */
public class LogInterceptor extends BaseInterceptor {
    @Override
    public void intercept(Invocation inv) {
        Enumeration<String> names = inv.getController().getParaNames();
        while (names.hasMoreElements()){
            String name = names.nextElement();
            Logger.getLogger(this.getClass().getSimpleName()).info(
                    "<--->name = " + name + "<--->value = "
                            + inv.getController().getPara(name));
            System.out.println("name = " + name + "<-->value = " + inv.getController().getPara(name));
        }
        System.out.println("requestUrl = " + inv.getActionKey());
        Logger.getLogger(this.getClass().getSimpleName()).info(
                "requestUrl = " + inv.getActionKey());

        inv.invoke();
    }
}
