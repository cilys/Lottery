package com.cilys.lottery.web.controller;

import com.cily.utils.base.log.Logs;
import com.cilys.lottery.web.conf.Param;
import com.cilys.lottery.web.conf.SQLParam;
import com.cilys.lottery.web.interceptor.SchemeIdInterceptor;
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
                    Map<String, Object> m = ParamUtils.parseJson(HttpKit.readData(getRequest()));
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
        int schemeId = getInt(SQLParam.SCHEME_ID);

        renderJsonSuccess(OrderImpl.query(schemeId, orderStatus,
                payType, getPageNumber(), getPageSize()));
    }

    /**
     * 更新订单支付状态
     */
    public void updatePayStatus(){
        final int id = getIntWithDefaultValue(SQLParam.ID);

        final String newOrderStatus = getParam(SQLParam.ORDER_STATUS);

        Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                try{
                    String result = OrderImpl.updatePayStatus(id, newOrderStatus);
                    renderJson(result, null);
                    return Param.C_SUCCESS.equals(result);
                }catch (Exception e){
                    renderJsonFailed(Param.C_SERVER_ERROR, null);
                    Logs.printException(e);
                    return false;
                }
            }
        });
    }

    /**
     * 计算奖金分配额度
     */
    public void calculateBonus(){
        int schemeId = getInt(SQLParam.SCHEME_ID, -1);

        if (OrderImpl.calculateBonus(schemeId)) {
            renderJsonSuccess(null);
        }else {
            renderJsonFailed(Param.C_UPDATE_FAILED, null);
        }
    }

    /**
     * 分配奖金给中奖用户
     */
    public void distributionBonus(){
        int schemeId = getInt(SQLParam.SCHEME_ID, -1);
        ScheduUtils.putTask(TaskBean.createTask(TaskType.SYNC_SCHEME_BONUS_ADD_USER_MONEY_FLOW, schemeId));
        renderJsonSuccess(null);
    }
}