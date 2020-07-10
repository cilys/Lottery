package com.cilys.lottery.web.controller.sys;

import com.cily.utils.base.log.Logs;
import com.cilys.lottery.web.conf.BonusStatus;
import com.cilys.lottery.web.conf.Param;
import com.cilys.lottery.web.conf.SQLParam;
import com.cilys.lottery.web.controller.OrderController;
import com.cilys.lottery.web.interceptor.SysUserInterceptor;
import com.cilys.lottery.web.model.impl.OrderImpl;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;

import java.sql.SQLException;

/**
 * Created by Administrator on 2020/7/9.
 */
@Before({SysUserInterceptor.class})
public class SysOrderController extends OrderController {

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

        String bonusStatus = OrderImpl.queryBonusStatus(schemeId);
        if (BonusStatus.BEEN_TO_USER.equals(bonusStatus)){
            renderJsonFailed(Param.C_BONUS_HAVE_BEEN_ENTER_USER, null);
            return;
        }

        if (OrderImpl.calculateBonus(schemeId)) {
            renderJsonSuccess(null);
        }else {
            renderJsonFailed(Param.C_UPDATE_FAILED, null);
        }
    }

}
