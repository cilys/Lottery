package com.cilys.lottery.web.model;

import com.cily.utils.base.StrUtils;
import com.cily.utils.base.log.Logs;
import com.jfinal.plugin.activerecord.Model;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by admin on 2020/6/16.
 */
public class BaseModel<M extends BaseModel> extends Model<M> {

    protected static boolean isEmpty(String... strs){
        if (strs == null){
            return true;
        }
        if (strs.length < 1){
            return true;
        }
        for (String s : strs){
            if (!StrUtils.isEmpty(s)){
                return false;
            }
        }
        return true;
    }

    @Override
    public M removeNullValueAttrs() {
        String[] names  = _getAttrNames();
        Map<String, Object> attrs = _getAttrs();
        for (Iterator<Map.Entry<String, Object>> it = attrs.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, Object> e = it.next();
            if (e.getValue() == null) {
                it.remove();
                remove(e.getKey());
            }else {
                if (e.getValue() instanceof String){
                    if (StrUtils.isEmpty((String)e.getValue())){
                        it.remove();
                        remove(e.getKey());
                    }
                }
            }
        }
        return (M)this;
    }

    @Override
    public M put(Map<String, Object> map) {
        if (map != null){
            for (String name : map.keySet()){
                this.set(name, map.get(name));
            }
        }
        return super.put(map);
    }

    public String getStr(String attr, String defValue) {
        String str = super.getStr(attr);
        if (str == null){
            return defValue;
        }
        return str;
    }

    @Override
    public Integer getInt(String attr) {
        return getInt(attr, -1);
    }

    public Integer getInt(String attr, Integer defValue) {
        String str = getStr(attr, null);
        if (str == null){
            return defValue;
        }
        try {
            return Integer.parseInt(str);
        }catch (NumberFormatException e){
            Logs.printException(e);
            return defValue;
        }
    }
}