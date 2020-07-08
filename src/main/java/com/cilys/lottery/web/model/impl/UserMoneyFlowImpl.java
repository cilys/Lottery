package com.cilys.lottery.web.model.impl;

import com.cily.utils.base.StrUtils;
import com.cilys.lottery.web.conf.PayType;
import com.cilys.lottery.web.conf.SQLParam;
import com.cilys.lottery.web.model.UserModel;
import com.cilys.lottery.web.model.UserMoneyFlowModel;
import com.cilys.lottery.web.schedu.ScheduUtils;
import com.cilys.lottery.web.schedu.TaskBean;
import com.cilys.lottery.web.utils.BigDecimalUtils;
import com.cilys.lottery.web.model.utils.QueryParam;
import com.jfinal.plugin.activerecord.Page;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by admin on 2020/6/24.
 */
public class UserMoneyFlowImpl {

    /**
     * 资金流水入库
     * @param userId
     * @param money
     * @param sourceUserId
     * @param payType
     * @return
     * @throws Exception
     */
    public static boolean addToMoneyFlow(String userId, Integer SchemeId, Integer orderId,
                                         BigDecimal money,
                                         String sourceUserId, String payType) throws Exception {
        return addToMoneyFlow(userId, SchemeId, orderId, null,
                money, sourceUserId, payType);
    }

    /**
     * 资金流水入库
     * @param userId    用户id
     * @param SchemeId  方案id
     * @param orderId   订单id
     * @param applyCashId   申请提现id
     * @param money         金额
     * @param sourceUserId
     * @param payType
     * @return
     * @throws Exception
     */
    public static boolean addToMoneyFlow(String userId, Integer SchemeId, Integer orderId,
                                         Integer applyCashId, BigDecimal money,
                                         String sourceUserId, String payType) throws Exception {
        if (StrUtils.isEmpty(userId)) {
            return false;
        }
        if (money == null) {
            money = BigDecimalUtils.zero();
        }
        if (StrUtils.isEmpty(sourceUserId)){
            return false;
        }
        if (StrUtils.isEmpty(payType)){
            return false;
        }
        ScheduUtils.putTask(TaskBean.syncUserMoneyFlowToUser());
        return UserMoneyFlowModel.insert(userId, SchemeId, orderId, applyCashId,
                money, sourceUserId, payType);
    }

    /**
     * 如果用户资金已经到账，则修改此流水记录的状态
     * @param m
     * @return
     * @throws Exception
     */
    public static boolean updateIsAddToUser(UserMoneyFlowModel m) throws Exception{
        if (m == null){
            return false;
        }
        return UserMoneyFlowModel.isAddToUser(m);
    }

    /**
     * 同步一条资金流水到用户账户里
     * @return
     * @throws Exception
     */
    public static boolean updateUserLeftMoney() throws Exception {
        UserMoneyFlowModel m = UserMoneyFlowModel.queryFirst(getQueueNoneOfAddToUser());
        return updateUserLeftMoney(m);
    }

    /**
     * 查找所有未同步到用户账户的流水记录
     * @return
     */
    public static List<UserMoneyFlowModel> queryNoneOfAddToUser(){
        return UserMoneyFlowModel.query(getQueueNoneOfAddToUser());
    }

    private static String getQueueNoneOfAddToUser(){
        QueryParam param = new QueryParam();
        param.append(" where ");
        param.equal(SQLParam.IS_ADD_TO_USER, SQLParam.STATUS_DISABLE);
        param.and();
        param.append(SQLParam.PAY_TYPE);
        param.append(" in(");
        param.append(PayType.PAY_YU_E);         //余额支付
        param.append(", ");
        param.append(PayType.PAY_SYSTEM_BACK);  //系统退款
        param.append(", ");
        param.append(PayType.PAY_SYSTEM_RECHARGE);  //系统充值
        param.append(", ");
        param.append(PayType.PAY_SYSTEM_UPDATE_USER_LEFT_MONEY);  //系统更新用户的余额
        param.append(", ");
        param.append(PayType.PAY_SYSTEM_BONUS);  //系统下发奖金
        param.append(", ");
        param.append(PayType.PAY_APPLY_CASH_SUCCESS);  //提现成功
        param.append(")");
        param.append(" ");
        param.append("order by ");
        param.append(SQLParam.ID);
        param.append(" desc");
        return param.string();
    }

    public static boolean updateUserLeftMoney(int id) throws Exception {
        return updateUserLeftMoney(UserMoneyFlowModel.queryById(id));
    }

    /**
     * 将流水里的数据，同步到用户的账户里
     * @param flowModel
     * @return
     */
    public static boolean updateUserLeftMoney(UserMoneyFlowModel flowModel) throws Exception{

        if (flowModel == null) {
            return false;
        }
        String payType = flowModel.getStr(SQLParam.PAY_TYPE);
        if (!PayType.PAY_YU_E.equals(payType)
                && !PayType.PAY_SYSTEM_BACK.equals(payType)
                && !PayType.PAY_SYSTEM_RECHARGE.equals(payType)
                && !PayType.PAY_SYSTEM_UPDATE_USER_LEFT_MONEY.equals(payType)
                && !PayType.PAY_SYSTEM_BONUS.equals(payType)
                && !PayType.PAY_APPLY_CASH_SUCCESS.equals(payType)){
            // 不是余额支付
            // 不是系统退款
            // 不是系统充值
            // 不是系统更新用户余额，
            // 系统下发奖金
            // 提现成功
            // 无需更新用户余额，直接返回true
            return true;
        }
        //流水已经同步到账户余额里，则不需要继续同步
        String isAddToUser = flowModel.getStr(SQLParam.IS_ADD_TO_USER);
        if (SQLParam.STATUS_ENABLE.equals(isAddToUser)){
            return true;
        }

        String userId = flowModel.getStr(SQLParam.USER_ID);
        UserModel um = UserModel.getUserByUserId(userId);
        if (um == null){
            return false;
        }
        BigDecimal flowMoney = flowModel.getBigDecimal(SQLParam.MONEY);
        if (flowMoney == null){
            return false;
        }

        //如果是系统更新用户余额，则直接把新的余额更新到表里
        if (PayType.PAY_SYSTEM_UPDATE_USER_LEFT_MONEY.equals(payType)){
            um.set(SQLParam.LEFT_MONEY, flowMoney);
        } else if (PayType.PAY_APPLY_CASH_SUCCESS.equals(payType)){
            //如果是提现，则从冻结余额里扣除
            BigDecimal coldMoney = um.getBigDecimal(SQLParam.COLD_MONEY);
            if (coldMoney == null){
                coldMoney = BigDecimalUtils.zero();
            }
            BigDecimal newLeftMoney = BigDecimalUtils.add(coldMoney, flowMoney);
            um.set(SQLParam.COLD_MONEY, newLeftMoney);
            System.out.println("执行同步提现：coldMoney = " + coldMoney.toString() + "\tflowMoney = " + flowMoney);
        } else if (PayType.PAY_APPLY_CASH.equals(payType)){
            //新方式，提现申请，资金流水里增加了两条记录，一条是减少可用余额的记录，一条是增加冻结余额的记录

        } else {
            BigDecimal userMoney = um.getBigDecimal(SQLParam.LEFT_MONEY);
            if (userMoney == null){
                userMoney = BigDecimalUtils.zero();
            }
            BigDecimal newLeftMoney = BigDecimalUtils.add(userMoney, flowMoney);
            um.set(SQLParam.LEFT_MONEY, newLeftMoney);
        }
        //更新用户账户余额
        boolean updateUserMoney = UserModel.updateUserMoney(um);
        if (updateUserMoney == false){
            return false;
        }
        //更新该流水的状态
        return updateIsAddToUser(flowModel);
    }

    public static Page<UserMoneyFlowModel> query(int pageNumber, int pageSize,
                                                 String userId, String payType,
                                                 String isAddToUser, String sortColumn, String sort){
        QueryParam param = new QueryParam();
        if (!StrUtils.isEmpty(userId)){
            param.and();
            param.equal(SQLParam.USER_ID, userId);
        }
        if (!StrUtils.isEmpty(payType)){
            param.and();
            param.equal(SQLParam.PAY_TYPE, payType);
        }
        if (!StrUtils.isEmpty(isAddToUser)){
            param.and();
            param.equal(SQLParam.IS_ADD_TO_USER, isAddToUser);
        }
        String sql = param.string().trim();
        sql = sql.replaceFirst("and", "");
        String orderBy = null;
        if (!StrUtils.isEmpty(sortColumn) && !StrUtils.isEmpty(sort)) {
            orderBy = " order by " + sortColumn + " " + sort;
        }
        return UserMoneyFlowModel.query(pageNumber, pageSize, sql, orderBy);
    }
}