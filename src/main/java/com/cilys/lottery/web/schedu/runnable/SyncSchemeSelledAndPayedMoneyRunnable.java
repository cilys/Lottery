package com.cilys.lottery.web.schedu.runnable;

import com.cily.utils.base.log.Logs;
import com.cily.utils.base.time.TimeUtils;
import com.cilys.lottery.web.model.impl.SchemeImpl;

/**
 * Created by admin on 2020/6/29.
 * 方案表，同步已售金额、已支付金额
 */
public class SyncSchemeSelledAndPayedMoneyRunnable implements Runnable {
    @Override
    public void run() {
//        Logs.sysErr(TimeUtils.milToStr(System.currentTimeMillis(), null) + "开始同步方案表里已购买金额、已经支付金额...");

        SchemeImpl.syncSchemeSelledAndPayedMoney();

//        Logs.sysErr(TimeUtils.milToStr(System.currentTimeMillis(), null) + "开始同步方案表里已购买金额、已经支付金额...");


    }
}
