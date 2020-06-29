package com.cilys.lottery.web.model.impl;

import com.cily.utils.base.time.TimeUtils;
import com.cilys.lottery.web.conf.SQLParam;
import com.cilys.lottery.web.model.LogModel;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Page;

/**
 * Created by admin on 2020/6/29.
 */
public class LogImpl {

    public static boolean insert(LogModel b){
        return LogModel.insert(b);
    }

    public static Page<LogModel> query(int pageNumber, int pageSize){
        return LogModel.query(pageNumber, pageSize, SQLParam.OPERATION_TIME, SQLParam.DESC);
    }

    public static boolean clearOutOfTimeLog(){
        long outOfTime = PropKit.getLong("outOfTimeLog", 30 * 24 * 60 * 60 * 1000L);
        return LogModel.clearOutOfTime(TimeUtils.milToStr(System.currentTimeMillis() - outOfTime, null));
    }
}