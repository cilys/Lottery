package com.cilys.lottery.web.controller;

import com.cily.utils.base.StrUtils;
import com.cily.utils.base.log.Logs;
import com.cilys.lottery.web.conf.Param;
import com.cilys.lottery.web.conf.SQLParam;
import com.cilys.lottery.web.interceptor.SchemeIdInterceptor;
import com.cilys.lottery.web.log.LogUtils;
import com.cilys.lottery.web.model.impl.OrderImpl;
import com.cilys.lottery.web.schedu.ScheduUtils;
import com.cilys.lottery.web.schedu.TaskBean;
import com.cilys.lottery.web.schedu.TaskType;
import com.cilys.lottery.web.utils.ParamUtils;
import com.jfinal.aop.Before;
import com.jfinal.kit.HttpKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;

import java.sql.SQLException;
import java.util.Map;

/**
 * Created by admin on 2020/6/22.
 */
public class OrderController extends BaseController {

    /**
     * 购买
     */
    public void add() {
        Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                try {
                    String str = HttpKit.readData(getRequest());
                    LogUtils.info(getClass().getSimpleName(), null, getRequest().getRequestURI(), str, getUserId());
                    Map<String, Object> m = ParamUtils.parseJson(str);
                    String customId = (String)m.get(SQLParam.CUSTOMER_ID);
                    if (StrUtils.isEmpty(customId)){
                        customId = getUserId();
                        m.put(SQLParam.CUSTOMER_ID, customId);
                    }

                    String result = OrderImpl.addOrder(m, getUserId());
                    renderJson(result, null);

                    return Param.C_SUCCESS.equals(result);

                } catch (Exception e) {
                    Logs.printException(e);
                    renderJsonFailed(Param.C_ORDER_ADD_FAILED, null);
                    return false;
                }

            }
        });
    }


    @Before({SchemeIdInterceptor.class})
    public void query(){
        String orderStatus = getParam(SQLParam.ORDER_STATUS);
        String payType = getParam(SQLParam.PAY_TYPE);
        int schemeId = getInt(SQLParam.SCHEME_ID, -1);
        String customerId = getParam(SQLParam.CUSTOMER_ID, null);
        String sortColumn = getParam(SQLParam.SORT_COLUMN, SQLParam.CREATE_TIME);
        String sort = getParam(SQLParam.SORT, SQLParam.DESC);

        renderJsonSuccess(OrderImpl.query(schemeId, customerId, orderStatus,
                payType, getPageNumber(), getPageSize(), sortColumn, sort));
    }


}