package com.cilys.lottery.web.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cily.utils.base.log.Logs;
import com.cilys.lottery.web.model.utils.UserUtils;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by admin on 2020/6/25.
 */
public class ParamUtils {

    public static Map<String, Object> parseJson(String jsonParam) throws Exception {
        Logs.sysOut("json参数：" + jsonParam);
        return JSON.parseObject(jsonParam, new TypeReference<Map<String, Object>>(){}.getType());
    }
    public static Map<String, String> parseJsonToStr(String jsonParam) throws Exception {
        Logs.sysOut("json参数：" + jsonParam);
        return JSON.parseObject(jsonParam, new TypeReference<Map<String, Object>>(){}.getType());
    }

    public static String string(Object obj){
        if (obj == null){
            return null;
        }
        try{
            return JSON.toJSONString(obj);
        }catch (Exception e){
            Logs.printException(e);
            return null;
        }
    }

}
