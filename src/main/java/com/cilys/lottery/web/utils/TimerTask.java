package com.cilys.lottery.web.utils;

import com.cily.utils.base.TimerUtils;

import java.util.concurrent.TimeUnit;

/**
 * Created by admin on 2020/6/23.
 */
public class TimerTask {

    public static void startTimer(boolean initSys){
        TimerUtils.task(new TimerRunnable(initSys), 1, 60, TimeUnit.MINUTES);
        initSys = false;
    }
}
