package com.cilys.lottery.web;

import com.cilys.lottery.web.conf.SQLParam;
import com.cilys.lottery.web.controller.*;
import com.cilys.lottery.web.controller.sys.SchemeController;
import com.cilys.lottery.web.controller.sys.SysUserController;
import com.cilys.lottery.web.interceptor.LogInterceptor;
import com.cilys.lottery.web.interceptor.OptionMethodInterceptor;
import com.cilys.lottery.web.model.*;
import com.cilys.lottery.web.model.impl.UserMoneyFlowImpl;
import com.cilys.lottery.web.utils.TimerTask;
import com.jfinal.config.*;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.jfinal.template.Engine;

/**
 * Created by admin on 2020/6/16.
 */
public class Conf extends JFinalConfig {
    @Override
    public void configConstant(Constants me) {
        PropKit.use("conf.properties");
    }

    @Override
    public void configRoute(Routes me) {
        me.add("/", IndexController.class);
        me.add("user", UserController.class);
        me.add("sys/user", SysUserController.class);
        me.add("sys/scheme", SchemeController.class);
        me.add("order", OrderController.class);
//        me.add("station", StationController.class);
//        me.add("line", RoadLineController.class);
//        me.add("task", CarTaskController.class);
    }

    @Override
    public void configEngine(Engine me) {

    }

    @Override
    public void configPlugin(Plugins me) {
        C3p0Plugin c3p0 = new C3p0Plugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("pwd"));
        me.add(c3p0);

        ActiveRecordPlugin arp = new ActiveRecordPlugin(c3p0);
        arp.setShowSql(PropKit.getBoolean("showSql"));
        me.add(arp);
        arp.addMapping(SQLParam.T_USER, SQLParam.USER_ID, UserModel.class);

        arp.addMapping(SQLParam.T_TOKEN, SQLParam.USER_ID, TokenModel.class);

        arp.addMapping(SQLParam.T_UNION_SCHEME, SchemeModel.class);
        arp.addMapping(SQLParam.T_ORDER, OrderModel.class);
        arp.addMapping(SQLParam.T_USER_MONEY_FLOW, UserMoneyFlowModel.class);
//        arp.addMapping("t_road_line", RoadLineModel.class);
//        arp.addMapping("t_car_task", CarTaskModel.class);
    }

    @Override
    public void configInterceptor(Interceptors me) {
        me.add(new LogInterceptor());
        me.add(new OptionMethodInterceptor());
    }

    @Override
    public void configHandler(Handlers me) {

    }

    @Override
    public void afterJFinalStart() {
        super.afterJFinalStart();

//        InitStationLine.initStation();

        TimerTask.startTimer(true);
    }
}
