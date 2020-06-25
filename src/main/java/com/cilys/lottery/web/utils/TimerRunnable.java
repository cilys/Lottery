package com.cilys.lottery.web.utils;

import com.cily.utils.base.log.Logs;
import com.cily.utils.base.time.TimeUtils;
import com.cilys.lottery.web.model.utils.SchemeUtils;
import com.cilys.lottery.web.model.utils.UserUtils;
import com.cilys.lottery.web.schedu.ScheduUtils;
import com.cilys.lottery.web.schedu.TaskType;

/**
 * Created by admin on 2020/6/23.
 */
public class TimerRunnable implements Runnable {
    private boolean initSys;

    public TimerRunnable() {
        this.initSys = false;
    }

    public TimerRunnable(boolean initSys) {
        this.initSys = initSys;
    }

    @Override
    public void run() {
        Logs.sysOut(TimeUtils.milToStr(System.currentTimeMillis(), null) + "进入了timer runnable..");

        String time = TimeUtils.milToStr(System.currentTimeMillis(), "HH");
        //凌晨2点，同步缓存里的数据
        if (initSys || time.equals("02")){
            initSys = false;

            //同步缓存里的用户数据
            Logs.sysErr(TimeUtils.milToStr(System.currentTimeMillis(), null) + "开始更新用户缓存数据...");
            UserUtils.getUserRealNameFromCache(null, true);
            Logs.sysErr(TimeUtils.milToStr(System.currentTimeMillis(), null) + "更新用户缓存数据完成...");
            //同步方案的已购买的金额、已支付的金额
            Logs.sysErr(TimeUtils.milToStr(System.currentTimeMillis(), null) + "开始同步方案表里已购买金额、已经支付金额...");
            SchemeUtils.syncSchemeSelledAndPayedMoney();
            Logs.sysErr(TimeUtils.milToStr(System.currentTimeMillis(), null) + "开始同步方案表里已购买金额、已经支付金额...");


            ScheduUtils.putTask(TaskType.SYNC_TOTAL_USER_MONEY_FLOW_TO_USER);
        }
    }
}
