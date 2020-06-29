package com.cilys.lottery.web.log;

import com.cily.utils.base.log.Logs;
import com.cily.utils.base.time.TimeUtils;
import com.cilys.lottery.web.model.LogModel;
import com.cilys.lottery.web.pools.FixedThreadPools;
import com.cilys.lottery.web.pools.runnable.LogRunnable;
import com.cilys.lottery.web.schedu.ThreadPools;

/**
 * Created by admin on 2020/6/29.
 */
public class LogUtils {

    public static void info(String className, String name, String actionUrl, String param, String userId){
        info(className, param);

        LogModel logModel = LogModel.createLogModel(name, actionUrl, param, userId,
                TimeUtils.milToStr(System.currentTimeMillis(), null));

        FixedThreadPools.executeTask(new LogRunnable(logModel));
    }

    public static void info(String className, String param){
        info("[" + className + "]\t" + param);
    }

    public static void info(String name, String actionUrl, String param, String userId){
        info(null, name, actionUrl, param, userId);
    }

    public static void info(String param){
        Logs.sysOut(param);
    }
}
