package com.cilys.lottery.web.model;

import com.cily.utils.base.StrUtils;
import com.cilys.lottery.web.conf.SQLParam;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;

import java.util.Map;

/**
 * Created by admin on 2020/6/29.
 */
public class LogModel extends BaseModel<LogModel> {
    private static LogModel dao = new LogModel();

    public static boolean insert(LogModel b){
        if (b == null){
            return false;
        }
        return b.save();
    }

    public static Page<LogModel> query(int pageNumber, int pageSize, String coumlnName, String order){
        if (!StrUtils.isEmpty(coumlnName) && !StrUtils.isEmpty(order)){
            return dao.paginate(pageNumber, pageSize, "select * ",
                    " from " + SQLParam.T_LOG + " order by "
                            + coumlnName + " " + order);
        } else {
            return dao.paginate(pageNumber, pageSize, "select * ",
                    " from " + SQLParam.T_LOG);
        }
    }

    public static boolean clearOutOfTime(String outOfTime){
        return Db.delete("delete from " + SQLParam.T_LOG + " where "
                + SQLParam.OPERATION_TIME + " < '" + outOfTime + "'") > 0;
    }

    public static LogModel createLogModel(String name, String actionUrl, String param,
                                          String userId, String operationTime){
        LogModel m = new LogModel();
        if (!StrUtils.isEmpty(name)) {
            m.set(SQLParam.NAME, name);
        }
        if (!StrUtils.isEmpty(actionUrl)) {
            m.set(SQLParam.ACTION_URL, actionUrl);
        }
        if (!StrUtils.isEmpty(param)) {
            m.set(SQLParam.PARAM, param);
        }
        if (!StrUtils.isEmpty(userId)){
            m.set(SQLParam.USER_ID, userId);
        }
        if (!StrUtils.isEmpty(operationTime)){
            m.set(SQLParam.OPERATION_TIME, operationTime);
        }
        return m;
    }

}
