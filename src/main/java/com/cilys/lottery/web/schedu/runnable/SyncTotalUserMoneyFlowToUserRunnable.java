package com.cilys.lottery.web.schedu.runnable;

import com.cilys.lottery.web.model.UserMoneyFlowModel;
import com.cilys.lottery.web.model.impl.UserMoneyFlowImpl;
import com.cilys.lottery.web.schedu.ScheduUtils;
import com.cilys.lottery.web.schedu.TaskBean;
import com.cilys.lottery.web.schedu.TaskType;
import com.cilys.lottery.web.schedu.ThreadPools;

import java.util.List;

/**
 * Created by admin on 2020/6/25.
 */
public class SyncTotalUserMoneyFlowToUserRunnable implements Runnable {

    @Override
    public void run() {
        List<UserMoneyFlowModel> ls = UserMoneyFlowImpl.queryNoneOfAddToUser();
        if (ls != null && ls.size() > 0){
            for (UserMoneyFlowModel m : ls) {
                if (m != null){
                    ScheduUtils.putTask(TaskBean.createTask(TaskType.SYNC_SINGLE_USER_MONEY_FLOW_TO_USER, m));
                }
            }
        }
    }
}
