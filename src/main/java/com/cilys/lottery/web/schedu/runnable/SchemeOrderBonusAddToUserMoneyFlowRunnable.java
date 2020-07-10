package com.cilys.lottery.web.schedu.runnable;

import com.cilys.lottery.web.conf.BonusStatus;
import com.cilys.lottery.web.conf.PayStatus;
import com.cilys.lottery.web.conf.SQLParam;
import com.cilys.lottery.web.model.OrderModel;
import com.cilys.lottery.web.model.impl.OrderImpl;
import com.cilys.lottery.web.schedu.ScheduUtils;
import com.cilys.lottery.web.schedu.TaskBean;
import com.cilys.lottery.web.schedu.TaskType;

import java.util.List;

/**
 * Created by admin on 2020/6/26.
 * 把一个方案的所有订单的奖金，添加到资金流水里
 */
public class SchemeOrderBonusAddToUserMoneyFlowRunnable implements Runnable{
    private Integer schemeId;

    public SchemeOrderBonusAddToUserMoneyFlowRunnable() {
    }

    public SchemeOrderBonusAddToUserMoneyFlowRunnable(Integer schemeId) {
        this.schemeId = schemeId;
    }

    @Override
    public void run() {
        List<OrderModel> ls = null;
        if (schemeId != null && schemeId > -1){
            ls = OrderImpl.queryOrders(schemeId);
        }else {
            ls = OrderImpl.queryWaitBonusOrders();
        }

        if (ls != null && ls.size() > 0){
            for (OrderModel m : ls){
                if (m != null) {
                    String orderStatus = m.getStr(SQLParam.ORDER_STATUS);
                    String bonusStatus = m.getStr(SQLParam.BONUS_STATUS);

                    //已付款、并且奖金已计算的订单，才能分奖金
                    if (PayStatus.PAYED.equals(orderStatus)
                            && BonusStatus.CALULATED.equals(bonusStatus)) {

                        TaskBean<OrderModel> taskBean = new TaskBean<>();
                        taskBean.setType(TaskType.SYNC_BONUS_ADD_USER_MONEY_FLOW);
                        taskBean.setData(m);

                        ScheduUtils.putTask(taskBean);
                    }
                }
            }
        }
    }
}
