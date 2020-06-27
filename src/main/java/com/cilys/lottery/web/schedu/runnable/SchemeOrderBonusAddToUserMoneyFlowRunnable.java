package com.cilys.lottery.web.schedu.runnable;

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

    public SchemeOrderBonusAddToUserMoneyFlowRunnable(Integer schemeId) {
        this.schemeId = schemeId;
    }

    @Override
    public void run() {
        if (schemeId != null && schemeId > -1){
            List<OrderModel> ls = OrderImpl.queryOrders(schemeId);
            if (ls != null && ls.size() > 0){
                for (OrderModel m : ls){
                    TaskBean<OrderModel> taskBean = new TaskBean<>();
                    taskBean.setType(TaskType.SYNC_BONUS_ADD_USER_MONEY_FLOW);
                    taskBean.setData(m);

                    ScheduUtils.putTask(taskBean);
                }
            }
        }
    }
}
