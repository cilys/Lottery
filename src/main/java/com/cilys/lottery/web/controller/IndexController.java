package com.cilys.lottery.web.controller;

import com.cilys.lottery.web.interceptor.LoginedInterceptor;
import com.jfinal.aop.Clear;

/**
 * Created by 123 on 2018/4/29.
 */
public class IndexController extends BaseController {
    @Clear({LoginedInterceptor.class})
    public void index(){
        render("./login.html");
    }

}
