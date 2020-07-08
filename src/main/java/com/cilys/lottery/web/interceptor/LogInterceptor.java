package com.cilys.lottery.web.interceptor;

import com.cily.utils.base.StrUtils;
import com.cilys.lottery.web.log.LogUtils;
import com.cilys.lottery.web.utils.ParamUtils;
import com.jfinal.aop.Invocation;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by admin on 2018/2/6.
 */
public class LogInterceptor extends BaseInterceptor {
    @Override
    public void intercept(Invocation inv) {
        Map<String, Object> map = new HashMap<>();
        Enumeration<String> names = inv.getController().getParaNames();
        while (names.hasMoreElements()){
            String name = names.nextElement();
            map.put(name, inv.getController().getPara(name));
        }

        String actionUrl = inv.getActionKey();

        System.err.println("请求路径：" + actionUrl);

        if (StrUtils.isEmpty(actionUrl) || actionUrl.equals("/")){

        }else {
            LogUtils.info(this.getClass().getSimpleName(), null, actionUrl, ParamUtils.string(map), getUserId(inv));
        }
        inv.invoke();
    }
}