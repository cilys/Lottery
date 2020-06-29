package com.cilys.lottery.web.schedu.runnable;

import com.cilys.lottery.web.model.impl.LogImpl;

/**
 * Created by admin on 2020/6/29.
 * 清除日志
 */
public class ClearOutOfTimeLogRunnabl implements Runnable {
    @Override
    public void run() {
        LogImpl.clearOutOfTimeLog();
    }
}
