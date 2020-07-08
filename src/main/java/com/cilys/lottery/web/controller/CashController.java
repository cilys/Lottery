package com.cilys.lottery.web.controller;

import com.cily.utils.base.StrUtils;
import com.cily.utils.base.log.Logs;
import com.cilys.lottery.web.conf.Param;
import com.cilys.lottery.web.conf.SQLParam;
import com.cilys.lottery.web.log.LogUtils;
import com.cilys.lottery.web.model.impl.CashImpl;
import com.cilys.lottery.web.model.utils.QueryParam;
import com.cilys.lottery.web.utils.ParamUtils;
import com.jfinal.kit.HttpKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.sql.SQLException;
import java.util.Map;

/**
 * Created by Administrator on 2020/7/7.
 */
public class CashController extends BaseController {

    public void add() {
        try{
            String params = HttpKit.readData(getRequest());
            LogUtils.info(getClass().getSimpleName(), null, getRequest().getRequestURI(), params, getUserId());

            Map<String, String> m = ParamUtils.parseJsonToStr(params);
            final String userId = getCustomerId(getUserId(), m.get(SQLParam.USER_ID));
            if (Param.C_RIGHT_LOW.equals(userId)){
                renderJsonFailed(Param.C_RIGHT_LOW, null);
                return;
            }

            final String money = m.get(SQLParam.MONEY);
            final String msg = m.get(SQLParam.MSG);

            Db.tx(new IAtom() {
                @Override
                public boolean run() throws SQLException {
                    try{
                        String result = CashImpl.insert(userId, money, msg);
                        renderJson(result, null);
                        if (Param.C_SUCCESS.equals(result)){
                            return true;
                        }else {
                            return false;
                        }
                    }catch (Exception e){
                        Logs.printException(e);
                        renderJsonFailed(Param.C_INSERT_FAILED, null);
                        return false;
                    }
                }
            });

        }catch (Exception e){
            renderJsonFailed(Param.C_PARAM_ERROR, null);
        }
    }

    public void query() {
        String userId = getCustomerId(getUserId(), getParam(SQLParam.USER_ID));
        if (Param.C_RIGHT_LOW.equals(userId)){
            renderJsonFailed(Param.C_RIGHT_LOW, null);
            return;
        }

        String sortColumn = getParam(SQLParam.SORT_COLUMN, SQLParam.APPLY_TIME);
        String sort = getParam(SQLParam.SORT, SQLParam.DESC);
        String status = getParam(SQLParam.STATUS);

        QueryParam query = new QueryParam();
        if (!StrUtils.isEmpty(userId)){
            query.equal(SQLParam.USER_ID, userId);
        }
        if (!StrUtils.isEmpty(status)){
            query.and();
            query.equal(SQLParam.STATUS, status);
        }
        String sql = query.string();
        sql = sql.trim();
        if (sql.startsWith("and")) {
            sql = sql.replaceFirst("and", "");
        }
        if (!StrUtils.isEmpty(sortColumn) && !StrUtils.isEmpty(sort)){
            sql = sql + " order by " + sortColumn + " " + sort;
        }
        renderJsonSuccess(CashImpl.query(getPageNumber(), getPageSize(), sql));
    }

    protected String getCustomerId(String headerUser, String paramUserId){
        return headerUser;
    }
}