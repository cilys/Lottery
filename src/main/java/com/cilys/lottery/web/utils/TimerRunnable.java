package com.cilys.lottery.web.utils;

import com.cily.utils.base.log.Logs;
import com.cily.utils.base.time.TimeUtils;
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

            //同步用户信息到缓存里
            ScheduUtils.putTask(TaskType.SYNC_USER_INFO_TO_CACHE);

            //同步方案的已购买的金额、已支付的金额
            ScheduUtils.putTask(TaskType.SYNC_SCHEME_SELLED_AND_PAYED_MONEY);

            //资金流水同步到账户里
            ScheduUtils.putTask(TaskType.SYNC_TOTAL_USER_MONEY_FLOW_TO_USER);

            //清除日志
            ScheduUtils.putTask(TaskType.CLEAR_PUT_OF_TIME_LOG);

        }
    }
}
