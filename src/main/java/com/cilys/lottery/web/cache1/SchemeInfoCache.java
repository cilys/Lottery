package com.cilys.lottery.web.cache1;

import com.cilys.lottery.web.conf.SQLParam;
import com.cilys.lottery.web.model.SchemeModel;
import com.cilys.lottery.web.model.impl.SchemeImpl;
import com.cilys.lottery.web.schedu.ScheduUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2020/6/27.
 * 缓存方案信息
 */
public class SchemeInfoCache {
    private static Map<Integer, SchemeModel> cacheMap;

    public synchronized static void clear(){
        if (cacheMap != null){
            cacheMap.clear();
        }
    }

    private static long lastCacheUserTime;
    public static SchemeModel get(int schemeId, boolean reset){
        //上次同步的数据如果是1天（默认）之前的数据，强制清空后，从数据库里更新
        if (System.currentTimeMillis() - lastCacheUserTime > 24 * 60 * 60 *1000){
            clear();
        }
        if (cacheMap == null || cacheMap.size() < 1){
            List<SchemeModel> ls = SchemeModel.queryAll();
            if (ls != null && ls.size() > 0){
                if (reset){
                    clear();
                }
                cacheMap = new HashMap<>();

                for (SchemeModel u : ls){
                    cacheMap.put(u.getInt(SQLParam.ID), u);
                }
                lastCacheUserTime = System.currentTimeMillis();
            }
        }
        if (schemeId < 0){
            return null;
        }

        if (cacheMap != null && cacheMap.size() > 0){

            return cacheMap.get(schemeId);
        }
        return null;
    }

    public static String getSchemeName(int schemeId){
        SchemeModel m = get(schemeId, false);
        if (m == null){
            return null;
        }
        return m.getStr(SQLParam.NAME);
    }
}
