package com.cilys.lottery.web.model.impl;

import com.cilys.lottery.web.conf.BonusStatus;
import com.cilys.lottery.web.conf.Param;
import com.cilys.lottery.web.conf.PayStatus;
import com.cilys.lottery.web.conf.SQLParam;
import com.cilys.lottery.web.model.OrderModel;
import com.cilys.lottery.web.utils.BigDecimalUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by admin on 2020/6/24.
 */
public class OrderImpl {


    /**
     * 检查奖金是否已经分配到用户的账户里
     * @param schemeId
     * @return true 已经分配到用户的账户里
     */
    public static boolean checkBonusHasToUser(int schemeId){
        List<OrderModel> ls = OrderModel.queryBySchemeId(schemeId);
        if (ls == null || ls.size() < 1){
            return false;
        }
        for (OrderModel m : ls){
            String bonusStatus = m.get(SQLParam.BONUS_STATUS);
            if (BonusStatus.BEEN_TO_USER.equals(bonusStatus)){
                return true;
            }
        }
        return false;
    }

    public static String updatePayStatus(int id, String newOrderStatus) throws Exception{
        OrderModel m = OrderModel.queryById(id);
        if (m == null){
            return Param.C_ORDER_NOT_EXIST;
        }
        String oldOrderStatus = m.get(SQLParam.ORDER_STATUS);
        if (oldOrderStatus.equals(newOrderStatus)){
            return Param.C_NONE_FOR_UPDATE;
        }
        BigDecimal money = m.get(SQLParam.MONEY);
        if (money == null){
            return Param.C_SCHEME_MONEY_ERROR;
        }

        m.set(SQLParam.ORDER_STATUS, newOrderStatus);
        boolean updateOrderStatus = OrderModel.updateOrderStatus(m);
        if (!updateOrderStatus) {
            return Param.C_UPDATE_FAILED;
        }
        String payType = m.get(SQLParam.PAY_TYPE);
        String userId = m.get(SQLParam.CUSTOMER_ID);

        if (PayStatus.PAYED.equals(newOrderStatus)){
            //新状态为支付状态，对用户是付款，金钱减少
            //n - n * 2 = -n，整数变复数
            BigDecimal doubleMoney = BigDecimalUtils.multiply(money, BigDecimalUtils.toBigDecimal(2), true);
            BigDecimal flowMoney = BigDecimalUtils.subtract(money, doubleMoney);
            if (UserMoneyFlowImpl.addToMoneyFlow(userId, id, flowMoney, SQLParam.SYSTEM, payType)){
                return Param.C_SUCCESS;
            }else{
                return Param.C_UPDATE_FAILED;
            }
        } else if (PayStatus.UN_PAY.equals(newOrderStatus)){
            //新状态是未支付状态
            return Param.C_SUCCESS;
        } else if (PayStatus.SYSTEM_BACK.equals(newOrderStatus)){
            //新状态是退款，增加用户账户的余额。只要是退款，无论之前是哪种方式，增加到用户账户里的方式，统统都是系统退款
            if (UserMoneyFlowImpl.addToMoneyFlow(userId, id, money, SQLParam.SYSTEM, PayStatus.SYSTEM_BACK)){
                return Param.C_SUCCESS;
            }else{
                return Param.C_UPDATE_FAILED;
            }
        } else {
            return Param.C_STATUS_ERROR;
        }
    }
}