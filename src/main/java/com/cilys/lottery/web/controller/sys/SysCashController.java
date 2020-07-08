package com.cilys.lottery.web.controller.sys;

import com.cily.utils.base.log.Logs;
import com.cilys.lottery.web.conf.Param;
import com.cilys.lottery.web.conf.SQLParam;
import com.cilys.lottery.web.controller.CashController;
import com.cilys.lottery.web.interceptor.SysUserInterceptor;
import com.cilys.lottery.web.log.LogUtils;
import com.cilys.lottery.web.model.impl.CashImpl;
import com.cilys.lottery.web.utils.ParamUtils;
import com.jfinal.aop.Before;
import com.jfinal.kit.HttpKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;

import java.sql.SQLException;
import java.util.Map;

/**
 * Created by Administrator on 2020/7/7.
 */
@Before({SysUserInterceptor.class})
public class SysCashController extends CashController {

    @Override
    protected String getCustomerId(String headerUser, String paramUserId) {
        return paramUserId;
    }

    public void updateStatus(){
        try {
            String params = HttpKit.readData(getRequest());
            LogUtils.info(getClass().getSimpleName(), null, getRequest().getRequestURI(), params, getUserId());

            Map<String, String> m = ParamUtils.parseJsonToStr(params);

//            final int id = getInt(SQLParam.ID, -1);
//            final String status = getParam(SQLParam.STATUS);
//            final String operator = getUserId();
//            final String operatorResult = getParam(SQLParam.OPERATOR_RESULT);
            String strId = m.get(SQLParam.ID);
            final Integer id = Integer.parseInt(strId);
            final String operator = getUserId();
            final String operatorResult = m.get(SQLParam.OPERATOR_RESULT);
            final String status = m.get(SQLParam.STATUS);

            Db.tx(new IAtom() {
                @Override
                public boolean run() throws SQLException {
                    try {
                        String result = CashImpl.updateStatus(id, operator, operatorResult, status);
                        renderJson(result, null);
                        if (Param.C_SUCCESS.equals(result)) {
                            return true;
                        } else {
                            return false;
                        }
                    } catch (Exception e) {
                        Logs.printException(e);
                        return false;
                    }
                }
            });
        }catch (Exception e){
            Logs.printException(e);
            renderJsonFailed(Param.C_PARAM_ERROR, null);
        }
    }
}