package com.cilys.lottery.web.schedu.runnable;


import com.cilys.lottery.web.cache.UserInfoCache;

/**
 * Created by admin on 2020/6/29.
 * 同步用户信息到缓存里
 */
public class SyncUserInfoRunnable implements Runnable {

    @Override
    public void run() {
//同步缓存里的用户数据
//        Logs.sysErr(TimeUtils.milToStr(System.currentTimeMillis(), null) + "开始更新用户缓存数据...");
        UserInfoCache.getUserRealNameFromCache(null, true);
//        Logs.sysErr(TimeUtils.milToStr(System.currentTimeMillis(), null) + "更新用户缓存数据完成...");
    }
}
