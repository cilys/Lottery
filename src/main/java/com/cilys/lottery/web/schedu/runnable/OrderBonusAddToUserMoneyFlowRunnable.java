package com.cilys.lottery.web.schedu.runnable;

import com.cily.utils.base.log.Logs;
import com.cilys.lottery.web.model.OrderModel;
import com.cilys.lottery.web.model.impl.OrderImpl;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;

import java.sql.SQLException;

/**
 * Created by admin on 2020/6/26.
 * 一个订单里奖金，添加到资金流水里
 */
public class OrderBonusAddToUserMoneyFlowRunnable implements Runnable {
    private OrderModel orderModel;

    public OrderBonusAddToUserMoneyFlowRunnable(OrderModel orderModel) {
        this.orderModel = orderModel;
    }


    @Override
    public void run() {
        if (orderModel != null){
            Db.tx(new IAtom() {
                @Override
                public boolean run() throws SQLException {
                    try {
                        return OrderImpl.bonusAddToUserMoneyFlow(orderModel);
                    } catch (Exception e) {
                        Logs.printException(e);
                        return false;
                    }
                }
            });
        }
    }
}
