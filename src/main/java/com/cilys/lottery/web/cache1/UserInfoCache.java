package com.cilys.lottery.web.cache1;

import com.cily.utils.base.StrUtils;
import com.cilys.lottery.web.conf.SQLParam;
import com.cilys.lottery.web.model.UserModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2020/6/27.
 * 缓存用户信息
 */
public class UserInfoCache {

    private static Map<String, UserModel> cacheUserMap;
    private static long lastCacheUserTime;
    public static String getUserRealNameFromCache(String userId){
        return getUserRealNameFromCache(userId, false);
    }
    public static String getUserRealNameFromCache(String userId, boolean reset){
        //上次同步的数据如果是1天（默认）之前的数据，强制清空后，从数据库里更新
        if (System.currentTimeMillis() - lastCacheUserTime > 24 * 60 * 60 *1000){
            clearUserCache();
        }

        if (cacheUserMap == null || cacheUserMap.size() < 1){
            List<UserModel> ls = UserModel.queryAll();
            if (ls != null && ls.size() > 0){
                if (reset){
                    clearUserCache();
                }
                cacheUserMap = new HashMap<>();

                for (UserModel u : ls){
                    cacheUserMap.put(u.getStr(SQLParam.USER_ID), u);
                }
                lastCacheUserTime = System.currentTimeMillis();
            }
        }
        if (StrUtils.isEmpty(userId)){
            return userId;
        }

        if (cacheUserMap != null && cacheUserMap.size() > 0){
            UserModel m = cacheUserMap.get(userId);
            String realName = m.getStr(SQLParam.REAL_NAME);
            if (!StrUtils.isEmpty(realName)){
                return realName;
            }
            String userName = m.getStr(SQLParam.USER_NAME);
            if (!StrUtils.isEmpty(userName)){
                return userName;
            }
        }
        return userId;
    }

    public static void clearUserFromCache(String userId){
        if (StrUtils.isEmpty(userId)){
            return;
        }
        if (cacheUserMap != null){
            cacheUserMap.remove(userId);
        }
    }

    public static void clearUserCache(){
        if (cacheUserMap != null){
            cacheUserMap.clear();
        }
    }

}
