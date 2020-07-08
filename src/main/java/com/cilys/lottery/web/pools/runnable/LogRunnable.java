package com.cilys.lottery.web.pools.runnable;

import com.cilys.lottery.web.log.LogUtils;
import com.cilys.lottery.web.model.LogModel;
import com.cilys.lottery.web.model.impl.LogImpl;

/**
 * Created by admin on 2020/6/29.
 */
public class LogRunnable extends AssignThreadNameRunnable {
    private LogModel logModel;

    public LogRunnable(LogModel logModel) {
        super("input-log");
        this.logModel = logModel;
    }

    @Override
    public void run() {
//        LogUtils.info("LogRunnable执行线程名称：" + Thread.currentThread().getName());
        LogImpl.insert(logModel);
    }
}
