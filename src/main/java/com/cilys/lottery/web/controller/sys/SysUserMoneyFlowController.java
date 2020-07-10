package com.cilys.lottery.web.controller.sys;

import com.cilys.lottery.web.conf.BonusStatus;
import com.cilys.lottery.web.conf.Param;
import com.cilys.lottery.web.conf.SQLParam;
import com.cilys.lottery.web.controller.UserMoneyFlowController;
import com.cilys.lottery.web.interceptor.SysUserInterceptor;
import com.cilys.lottery.web.model.impl.OrderImpl;
import com.cilys.lottery.web.schedu.ScheduUtils;
import com.cilys.lottery.web.schedu.TaskBean;
import com.cilys.lottery.web.schedu.TaskType;
import com.jfinal.aop.Before;

/**
 * Created by admin on 2020/6/27.
 */
@Before({SysUserInterceptor.class})
public class SysUserMoneyFlowController extends UserMoneyFlowController {

    /**
     * 分配奖金给中奖用户
     */
    public void distributionBonus(){
        int schemeId = getInt(SQLParam.SCHEME_ID, -1);

//        String bonusStatus = OrderImpl.queryBonusStatus(schemeId);
//        if (BonusStatus.BEEN_TO_USER.equals(bonusStatus)){
//            renderJsonFailed(Param.C_BONUS_HAVE_BEEN_ENTER_USER, null);
//            return;
//        }

        ScheduUtils.putTask(TaskBean.createTask(TaskType.SYNC_SCHEME_BONUS_ADD_USER_MONEY_FLOW, schemeId));
        renderJsonSuccess(null);
    }
}