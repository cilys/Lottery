package com.cilys.lottery.web.model.impl;

import com.cily.utils.base.StrUtils;
import com.cily.utils.base.log.Logs;
import com.cilys.lottery.web.cache.UserInfoCache;
import com.cilys.lottery.web.conf.*;
import com.cilys.lottery.web.model.OrderModel;
import com.cilys.lottery.web.model.SchemeModel;
import com.cilys.lottery.web.model.UserModel;
import com.cilys.lottery.web.model.utils.QueryParam;
import com.cilys.lottery.web.model.utils.RootUserIdUtils;
import com.cilys.lottery.web.model.utils.UserUtils;
import com.cilys.lottery.web.schedu.ScheduUtils;
import com.cilys.lottery.web.schedu.TaskType;
import com.cilys.lottery.web.utils.BigDecimalUtils;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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

    /**
     * 更新订单的支付状态
     * @param id
     * @param newOrderStatus
     * @return
     * @throws Exception
     */
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
            if (UserMoneyFlowImpl.addToMoneyFlow(userId, m.getInt(SQLParam.SCHEME_ID), id, flowMoney, SQLParam.SYSTEM, payType)){
                return Param.C_SUCCESS;
            }else{
                return Param.C_UPDATE_FAILED;
            }
        } else if (PayStatus.UN_PAY.equals(newOrderStatus)){
            //新状态是未支付状态
            return Param.C_SUCCESS;
        } else if (PayStatus.SYSTEM_BACK.equals(newOrderStatus)){
            //新状态是退款，增加用户账户的余额。只要是退款，无论之前是哪种方式，增加到用户账户里的方式，统统都是系统退款
            if (UserMoneyFlowImpl.addToMoneyFlow(userId, m.getInt(SQLParam.SCHEME_ID), id, money, SQLParam.SYSTEM, PayStatus.SYSTEM_BACK)){
                return Param.C_SUCCESS;
            }else{
                return Param.C_UPDATE_FAILED;
            }
        } else {
            return Param.C_STATUS_ERROR;
        }
    }



    public static String addOrder(Map<String, Object> m, String headUserId) throws Exception {
        if (m == null){
            return Param.C_PARAM_ERROR;
        }

        OrderModel sm  = new OrderModel();
        sm.put(m);
        sm.removeNullValueAttrs();

        String sId = sm.getStr(SQLParam.SCHEME_ID);
        int schemeId = -1;
        if (sId != null){
            try {
                schemeId = Integer.valueOf(sId);
            }catch (NumberFormatException e){
                Logs.printException(e);
            }
        }

        String payType = sm.getStr(SQLParam.PAY_TYPE);

//        String headUserId = getUserId();    //从head请求头里取出来的userId，也就是当前用户

        String customerId = sm.getStr(SQLParam.CUSTOMER_ID);    //客户id
        String operator = sm.getStr(SQLParam.OPERATOR);         //购买操作者id
        String payOperator = sm.getStr(SQLParam.ORDER_OPERATOR);  //支付操作者id

        if (StrUtils.isEmpty(customerId)){
            return Param.C_CUSTOMER_ID_NULL;
        }
        if (!UserUtils.userExist(customerId)){
            return Param.C_CUSTOMER_ID_NOT_EXIST;
        }
        if (StrUtils.isEmpty(operator)){
            operator = headUserId;
            sm.set(SQLParam.OPERATOR, operator);
        }

        BigDecimal zeroBigDecimal = new BigDecimal("0.00");
        UserModel um = UserModel.getUserByUserId(customerId);
        //账号余额
        BigDecimal selfMoney = um.getBigDecimal(SQLParam.LEFT_MONEY);
        if (selfMoney == null){
            selfMoney = zeroBigDecimal;
        }

        //购买份额
        BigDecimal money = new BigDecimal(sm.getStr(SQLParam.MONEY));
        if (money == null){
            money = zeroBigDecimal;
        }
        if (money.compareTo(zeroBigDecimal) == 0){
            return Param.C_BUY_MONEY_ZERO;
        }
        if (money.compareTo(zeroBigDecimal) == -1){
            return Param.C_BUY_MONEY_ILLAGE;
        }

        //检测方案是否存在
        SchemeModel schemeModel = SchemeModel.queryById(schemeId);
        if (schemeModel == null){
            return Param.C_SCHEME_NOT_EXIST;
        }
        //检测方案状态是否禁售
        if (SchemeImpl.checkSchemeStatus(schemeModel)){
            return Param.C_SCHEME_DISABLE;
        }

        //检测方案是否过期
        if (SchemeImpl.checkOutOfTime(schemeModel)){
            return Param.C_SCHEME_OUT_OF_TIME;
        }


        //查询是否超卖
        List<OrderModel> ls = OrderModel.queryBySchemeId(schemeId);
        BigDecimal selledMoney = OrderModel.selled(ls);
        BigDecimal payedMoney = OrderModel.payed(ls);

        if (OrderModel.isOutOfSell(schemeModel.getBigDecimal(SQLParam.TOTAL_MONEY), money, selledMoney)){
            return Param.C_SCHEME_LEFT_MONEY_NOT_EOUTH;
        }

        //账号余额支付，需判断余额是否足够
        if (PayType.PAY_YU_E.equals(payType)){
            if (headUserId.equals(customerId)){
                //自己购买，判断账号余额是否足够
                if (selfMoney.compareTo(money) == 1){
                    sm.set(SQLParam.ORDER_STATUS, PayStatus.PAYED);


                    if (OrderModel.insert(sm)){
//                        um.set(SQLParam.LEFT_MONEY, selfMoney.subtract(money));
//                        UserModel.updateUserLeftMoney(um);

                        BigDecimal flowMoney = BigDecimalUtils.subtract(money,
                                BigDecimalUtils.multiply(BigDecimalUtils.toBigDecimal(2), money));

                        UserMoneyFlowImpl.addToMoneyFlow(um.getStr(SQLParam.USER_ID), sm.getInt(SQLParam.SCHEME_ID), sm.getInt(SQLParam.ID),
                                flowMoney, SQLParam.SYSTEM, PayStatus.PAYED);

                        //TODO 更新方案里的已购买的份额、已支付的份额
                        BigDecimal selledM = selledMoney.add(money);
                        schemeModel.set(SQLParam.SELLED_MONEY, selledM);

                        BigDecimal payedM = payedMoney.add(payedMoney);
                        schemeModel.set(SQLParam.PAYED_MONEY, payedM);

                        SchemeModel.updateInfo(schemeModel);
                        return Param.C_SUCCESS;
                    }else {
                        return Param.C_INSERT_FAILED;
                    }
                }else {
                    return Param.C_USER_LEFT_MONEY_NOT_ENTHOH;
                }
            }else {
                //检查系统是否开通管理员代购开关
                boolean adminCanBuyOther = PropKit.getBoolean("adminCanBuyOther", false);
                if (!adminCanBuyOther){
                    return Param.C_ADMIN_CAN_NOT_BUY_FOR_OTHER;
                }

                if (RootUserIdUtils.isRootUser(headUserId)) {
                    //管理员代买，则需要判断被代买者的账号余额是否足够
                    if (selfMoney.compareTo(money) == 1){
                        sm.set(SQLParam.ORDER_STATUS, PayStatus.PAYED);
                        if (OrderModel.insert(sm)){
//                            um.set(SQLParam.LEFT_MONEY, selfMoney.subtract(money));
//                            UserModel.updateUserLeftMoney(um);
                            UserMoneyFlowImpl.addToMoneyFlow(um.getStr(SQLParam.USER_ID), sm.getInt(SQLParam.SCHEME_ID), sm.getInt(SQLParam.ID),
                                    BigDecimalUtils.subtract(money, BigDecimalUtils.multiply(BigDecimalUtils.toBigDecimal(2), money)),
                                    SQLParam.SYSTEM, PayStatus.PAYED);
                            //TODO 更新方案里的已购买的份额、已支付的份额

                            //TODO 更新方案里的已购买的份额、已支付的份额
                            BigDecimal selledM = selledMoney.add(money);
                            schemeModel.set(SQLParam.SELLED_MONEY, selledM);
                            BigDecimal payedM = payedMoney.add(payedMoney);
                            schemeModel.set(SQLParam.PAYED_MONEY, payedM);
                            SchemeModel.updateInfo(schemeModel);

                            return Param.C_SUCCESS;
                        }else {
                            return Param.C_INSERT_FAILED;
                        }
                    }else {
                        return Param.C_USER_LEFT_MONEY_NOT_ENTHOH;
                    }
                } else {
                    //普通用户不可代购
                    return Param.C_NORMAL_CAN_NOT_BUY_FOR_OTHER;
                }
            }
        }else {
            //非余额支付，普通用户默认是未支付状态，管理员需从参数里获取是否已支付
            String orderStatus = SQLParam.STATUS_DISABLE;

            if (RootUserIdUtils.isRootUser(headUserId)) {
                orderStatus = sm.getStr(SQLParam.ORDER_STATUS);
            }

            sm.set(SQLParam.ORDER_STATUS, orderStatus);

            if (OrderModel.insert(sm)){
                //TODO 更新方案里的已购买的份额
                BigDecimal selledM = selledMoney.add(money);
                schemeModel.set(SQLParam.SELLED_MONEY, selledM);
                SchemeModel.updateInfo(schemeModel);

                return Param.C_SUCCESS;
            }else {
                return Param.C_BUY_FAILED;
            }
        }
    }

    /**
     * 根据方案id，查询该方案下的订单列表
     * @param schemeId      方案id
     * @param orderStatus
     * @param payType
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public static Page<OrderModel> query(int schemeId, String orderStatus, String payType,
                                         int pageNumber, int pageSize){


        QueryParam queryParam = new QueryParam();
        queryParam.equal(SQLParam.SCHEME_ID, schemeId);

        if (!StrUtils.isEmpty(orderStatus)){
            queryParam.and();
            queryParam.equal(SQLParam.ORDER_STATUS, orderStatus);
            if (!StrUtils.isEmpty(payType)){
                queryParam.and();
                queryParam.equal(SQLParam.PAY_TYPE, payType);
            }
        }else {
            if (!StrUtils.isEmpty(payType)){
                queryParam.and();
                queryParam.equal(SQLParam.PAY_TYPE, payType);
            }
        }
        Page<OrderModel> datas = OrderModel.query(pageNumber, pageSize, queryParam.string());
        if (datas != null){
            List<OrderModel> ls = datas.getList();
            if (ls != null && ls.size() > 0){
                for (OrderModel m : ls){
                    String customerId = m.getStr(SQLParam.CUSTOMER_ID);
                    String operator = m.getStr(SQLParam.OPERATOR);
                    String orderOperator = m.getStr(SQLParam.ORDER_OPERATOR);

                    String cusertomerName = UserInfoCache.getUserRealNameFromCache(customerId);
                    String operatorName = UserInfoCache.getUserRealNameFromCache(operator);
                    String payOperatorName = UserInfoCache.getUserRealNameFromCache(orderOperator);

                    m.setCusertomerName(cusertomerName);
                    m.setOperatorName(operatorName);
                    m.setPayOperatorName(payOperatorName);
                }
            }
        }
        return datas;
    }


    /**
     * 查询一个方案下的所有的订单
     * @param schemeId
     * @return
     */
    public static List<OrderModel> queryOrders(int schemeId){
        return OrderModel.queryBySchemeId(schemeId);
    }

    /**
     * 计算各个订单所应该分配的奖金
     * @param schemeId
     * @return
     */
    public static boolean calculateBonus(int schemeId){
        SchemeModel sm = SchemeModel.queryById(schemeId);
        if (sm == null){
            return false;
        }
        BigDecimal canUseBonus = sm.get(SQLParam.CAN_USE_BONUS);
        if (canUseBonus == null){
            return false;
        }
        if (BigDecimalUtils.noMoreThan(canUseBonus, BigDecimalUtils.zero())){
            return false;
        }

        List<OrderModel> ls = OrderModel.queryBySchemeId(schemeId);
        if (ls == null || ls.size() > 0) {
            BigDecimal bd0 = BigDecimalUtils.zero();
            //所有人的出资总额
            BigDecimal totalPayedMoney = OrderModel.payed(ls);
            if (BigDecimalUtils.noMoreThan(totalPayedMoney, bd0)){
                return false;
            }
            for (OrderModel m : ls){
                BigDecimal payed = m.get(SQLParam.MONEY);
                //计算出资人的出资比例
                if (BigDecimalUtils.moreThan(payed, bd0)){
//                    BigDecimal rate = BigDecimalUtils.divide(payed, totalPayedMoney, true);
                    BigDecimal n = BigDecimalUtils.multiply(canUseBonus, payed, true);
                    BigDecimal bonus = BigDecimalUtils.divide(n, totalPayedMoney);

                    BigDecimal rate = BigDecimalUtils.divide(payed, totalPayedMoney);
                    if (BigDecimalUtils.noMoreThan(rate, BigDecimalUtils.zero())){
                        rate = BigDecimalUtils.toBigDecimal("0.01");
                    }
                    m.set(SQLParam.PAYED_RATE, rate);
                    m.set(SQLParam.BONUS_MONEY, bonus);
                }
                m.set(SQLParam.BONUS_STATUS, BonusStatus.CALULATED);
            }
            Db.batchUpdate(ls, ls.size());

            ScheduUtils.putTask(TaskType.SYNC_SCHEME_BONUS_ADD_USER_MONEY_FLOW);

            return true;
        }else {
            return false;
        }
    }

    /**
     * 奖金进入到账户流水中
     * @return
     */
    public static boolean bonusAddToUserMoneyFlow(OrderModel m) throws Exception {
        if (m == null){
            return false;
        }
        String bonusStatus = m.get(SQLParam.BONUS_STATUS);
        if (!BonusStatus.CALULATED.equals(bonusStatus)){
            return false;
        }
        BigDecimal bonusMoney = m.get(SQLParam.BONUS_MONEY);
        //应得奖金

        if (bonusMoney == null || BigDecimalUtils.equal(bonusMoney, BigDecimalUtils.zero())){
            return false;
        }
        m.set(SQLParam.BONUS_STATUS, BonusStatus.BEEN_TO_USER);
        if (OrderModel.updateBonusStatus(m)){
            return UserMoneyFlowImpl.addToMoneyFlow(m.getStr(SQLParam.USER_ID), m.getInt(SQLParam.SCHEME_ID),
                    m.getInt(SQLParam.ID), bonusMoney, SQLParam.SYSTEM, PayType.PAY_SYSTEM_BONUS);
        }
        return false;
    }


    public static String queryBonusStatus(int schemeId){
        List<OrderModel> ls = OrderModel.queryBySchemeId(schemeId);
        if (ls == null || ls.size() < 0){
            return "-1";
        }
        int status = 3;
        for (OrderModel m : ls){
            String bonusStatus = m.get(SQLParam.BONUS_STATUS);
            if (BonusStatus.BEEN_TO_USER.equals(bonusStatus)){
                return bonusStatus;
            }
            int b = 3;
            try{
                b = Integer.valueOf(bonusStatus);
            }catch (Exception e){
                Logs.printException(e);
            }
            if (b < status){
                status = b;
            }
        }
        return String.valueOf(status);
    }


}