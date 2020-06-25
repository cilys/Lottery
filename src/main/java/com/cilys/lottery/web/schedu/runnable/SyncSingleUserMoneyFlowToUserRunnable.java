package com.cilys.lottery.web.schedu.runnable;

import com.cily.utils.base.log.Logs;
import com.cilys.lottery.web.conf.Param;
import com.cilys.lottery.web.model.UserMoneyFlowModel;
import com.cilys.lottery.web.model.impl.UserMoneyFlowImpl;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.activerecord.IAtom;

import java.sql.SQLException;

/**
 * Created by admin on 2020/6/24.
 * 资金流水，同步到用户账户
 */
public class SyncSingleUserMoneyFlowToUserRunnable implements Runnable {
    private int userMoneyFlowId;

    public SyncSingleUserMoneyFlowToUserRunnable(){
        this.userMoneyFlowId = -1;
    }

    public SyncSingleUserMoneyFlowToUserRunnable(int userMoneyFlowId) {
        this.userMoneyFlowId = userMoneyFlowId;
    }

    private UserMoneyFlowModel userMoneyFlowModel;
    public SyncSingleUserMoneyFlowToUserRunnable(UserMoneyFlowModel userMoneyFlowModel){
        this.userMoneyFlowModel = userMoneyFlowModel;
    }

    @Override
    public void run() {
        Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                try {
                    if (userMoneyFlowModel != null){
                        return UserMoneyFlowImpl.updateUserLeftMoney(userMoneyFlowModel);
                    }
                    if (userMoneyFlowId < 0) {
                        return UserMoneyFlowImpl.updateUserLeftMoney();
                    }
                    return UserMoneyFlowImpl.updateUserLeftMoney(userMoneyFlowId);
                } catch (Exception e) {
                    Logs.printException(e);
                }

                return false;
            }
        });
    }
}
