package com.cilys.lottery.web.model;

import com.cily.utils.base.StrUtils;
import com.cilys.lottery.web.conf.SQLParam;
import com.jfinal.plugin.activerecord.Page;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by admin on 2020/6/24.
 * 用户账户资金流水
 */
public class UserMoneyFlowModel extends BaseModel<UserMoneyFlowModel> {
    private static UserMoneyFlowModel dao = new UserMoneyFlowModel();

    public synchronized static boolean insert(String userId, Integer orderId, BigDecimal money,
                                 String sourceUserId, String payType) throws Exception {
        UserMoneyFlowModel m = new UserMoneyFlowModel();
        m.set(SQLParam.USER_ID, userId);
        if (orderId != null){
            m.set(SQLParam.ORDER_ID, orderId);
        }
        m.set(SQLParam.MONEY, money);
        m.set(SQLParam.SOURCE_USER_ID, sourceUserId);
        m.set(SQLParam.PAY_TYPE, payType);
        m.set(SQLParam.IS_ADD_TO_USER, SQLParam.STATUS_DISABLE);

        return m.save();
    }

    public synchronized static boolean isAddToUser(UserMoneyFlowModel m) throws Exception{
        m.set(SQLParam.IS_ADD_TO_USER, SQLParam.STATUS_ENABLE);
        return m.update();
    }

    public static List<UserMoneyFlowModel> query(String query) {
        if (StrUtils.isEmpty(query)){
            query = "";
        }
        query = "select * from " + SQLParam.T_USER_MONEY_FLOW + " " + query;
        return dao.find(query);
    }

    public static UserMoneyFlowModel queryFirst(String query){
        if (StrUtils.isEmpty(query)){
            query = "";
        }
        query = "select * from " + SQLParam.T_USER_MONEY_FLOW + " " + query;
        return dao.findFirst(query);
    }

    public static Page<UserMoneyFlowModel> query(int pageNumber, int pageSize, String query){
        if (StrUtils.isEmpty(query)){
            return dao.paginate(pageNumber, pageSize, "select * ", " from " + SQLParam.T_USER_MONEY_FLOW);
        }else {
            return dao.paginate(pageNumber, pageSize, "select * ",
                    " from " + SQLParam.T_USER_MONEY_FLOW + " where " + query);
        }
    }

    public static UserMoneyFlowModel queryById(int id) {
        return dao.findById(id);
    }
}