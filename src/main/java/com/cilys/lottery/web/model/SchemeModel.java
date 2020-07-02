package com.cilys.lottery.web.model;


import com.cily.utils.base.StrUtils;
import com.cily.utils.base.time.TimeUtils;
import com.cilys.lottery.web.conf.Param;
import com.cilys.lottery.web.conf.SQLParam;
import com.jfinal.plugin.activerecord.Page;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SchemeModel extends BaseModel<SchemeModel> {
    private static SchemeModel dao = new SchemeModel();


    public static boolean insert(SchemeModel b) {
        if (b == null) {
            return false;
        }
        String currentTime = TimeUtils.milToStr(System.currentTimeMillis(), null);
        b.set(SQLParam.CREATE_TIME, currentTime);
        b.set(SQLParam.UPDATE_TIME, currentTime);
        return b.save();
    }

    public static List<SchemeModel> queryAll() {
        return dao.find("select * from " + SQLParam.T_UNION_SCHEME);
    }

    public static Page<SchemeModel> query(int pageNumber, int pageSize, String sql){
        sql = " from " + SQLParam.T_UNION_SCHEME + " " + sql;
        return dao.paginate(pageNumber, pageSize, "select * ", sql);
    }

    public static Page<SchemeModel> query(int pageNumber, int pageSize,
                                          String status, String outOfTime,
                                          String outTimeType, String orderColunm, String order){
        String sql = " from " + SQLParam.T_UNION_SCHEME;
        String symbol = " > ";

        //空、0、当前时间以后，1历史（当前时间之前），2全部
        if (StrUtils.isEmpty(outTimeType) || "0".equals(outTimeType)){
            symbol = " > ";
        }else if ("1".equals(outTimeType)){
            symbol = " < ";
        } else if ("2".equals(outTimeType)){
            symbol = null;
        }


        if (!StrUtils.isEmpty(status)){
            sql = sql + " where " + SQLParam.STATUS + " = '" + status + "'";

            if (!StrUtils.isEmpty(outOfTime) && !StrUtils.isEmpty(symbol)){
                sql = sql + " and " + SQLParam.OUT_OF_TIME + symbol + "'" + outOfTime + "'";
            }
        }else {
            if (!StrUtils.isEmpty(outOfTime) && !StrUtils.isEmpty(symbol)){
                sql = sql + " where " + SQLParam.OUT_OF_TIME + symbol + "'" + outOfTime + "'";
            }
        }
        if (!StrUtils.isEmpty(orderColunm)) {
            if (StrUtils.isEmpty(order)){
                order = "ASC";
            }
            sql = sql + " order by " + orderColunm + " " + order;
        }
        return dao.paginate(pageNumber, pageSize, "select * ", sql);
    }

    public static boolean delById(Object id) {
        return dao.deleteById(id);
    }

    public static boolean updateInfo(SchemeModel b) {
        if (b == null) {
            return false;
        }
        b.set(SQLParam.UPDATE_TIME, TimeUtils.milToStr(System.currentTimeMillis(), null));
        return b.update();
    }

    public static SchemeModel queryByName(String name) {
        if (StrUtils.isEmpty(name)) {
            return null;
        }
        return dao.findFirst("select * from "
                + SQLParam.T_UNION_SCHEME + " where name = '" + name + "'");
    }

    public static SchemeModel queryById(Object id) {
        return dao.findById(id);
    }

    public static boolean updateSelledAndPayedMoney(int schemeId, BigDecimal selledMoney,
                                                    BigDecimal payedMoney){
        SchemeModel m = queryById(schemeId);
        if (m != null){
            m.set(SQLParam.PAYED_MONEY, payedMoney);
            m.set(SQLParam.SELLED_MONEY, selledMoney);
            m.set(SQLParam.UPDATE_TIME, TimeUtils.milToStr(System.currentTimeMillis(), null));
            return m.update();
        }
        return false;
    }

    //查询所有的未过期、未被禁用的方案
    public static List<SchemeModel> queryNoramlScheme(){
        String currentTime = TimeUtils.milToStr(System.currentTimeMillis(), null);
        return dao.find(StrUtils.join(
                "select * from ", SQLParam.T_UNION_SCHEME,
                " where ", SQLParam.STATUS, " = '", SQLParam.STATUS_ENABLE, "' ",
                " and ", SQLParam.OUT_OF_TIME, " > '", currentTime, "'"
        ));
    }

//    @Override
//    public SchemeModel put(Map<String, Object> map) {
//        if (map != null){
//            for (Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator(); it.hasNext(); ) {
//                Map.Entry<String, Object> item = it.next();
//                String key = item.getKey();
//                if (SQLParam.NAME.equals(key)
//                        || SQLParam.TOTAL_MONEY.equals(key)
//                        || SQLParam.OUT_OF_TIME.equals(key)
//                        || SQLParam.STATUS.equals(key)) {
//
//                } else {
//                    it.remove();
//                }
//            }
//        }
//
//        return super.put(map);
//    }
}