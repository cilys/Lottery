package com.cilys.lottery.web.model.impl;

import com.cily.utils.base.StrUtils;
import com.cily.utils.base.log.Logs;
import com.cily.utils.base.time.TimeUtils;
import com.cilys.lottery.web.conf.Param;
import com.cilys.lottery.web.conf.SQLParam;
import com.cilys.lottery.web.model.OrderModel;
import com.cilys.lottery.web.model.SchemeModel;
import com.cilys.lottery.web.utils.BigDecimalUtils;
import com.cilys.lottery.web.model.utils.QueryParam;
import com.jfinal.plugin.activerecord.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2020/6/23.
 */
public class SchemeImpl {

    /**
     * 更新奖金
     * @param id
     * @param params
     * @return
     */
    public static String updateBonus(int id, Map<String, String> params){
        if (params == null || params.size() < 1){
            return Param.C_PARAM_ERROR;
        }
        SchemeModel m = SchemeModel.queryById(id);
        if (m == null){
            return Param.C_SCHEME_NOT_EXIST;
        }

        //判断奖金是否已经分配给各个用户，如果已经分配给各个用户了，则不可再修改奖金及税率
        if (OrderImpl.checkBonusHasToUser(id)){
            return Param.C_SCHEME_BONUS_PAYED_TO_USER;
        }

        String totalBonus = params.get(SQLParam.TOTAL_BONUS);
        if (StrUtils.isEmpty(totalBonus)){
            return Param.C_NONE_FOR_UPDATE;
        }
        BigDecimal total = BigDecimalUtils.toBigDecimal(totalBonus);
        if (total == null){
            return Param.C_PARAM_ERROR;
        }
        if (!BigDecimalUtils.moreThan(total, BigDecimalUtils.zero())){
            return Param.C_NONE_FOR_UPDATE;
        }
        m.set(SQLParam.TOTAL_BONUS, total);

        String canUseBonusStr = params.get(SQLParam.CAN_USE_BONUS);
        BigDecimal canUse = null;
        if (!StrUtils.isEmpty(canUseBonusStr)){
            canUse = BigDecimalUtils.toBigDecimal(canUseBonusStr);
        }

        String bonusRate = params.get(SQLParam.BONUS_RATE);
        if (!StrUtils.isEmpty(bonusRate)){
            BigDecimal rate = BigDecimalUtils.toBigDecimal(bonusRate, true);

            if (rate != null){
                BigDecimal bg100 = BigDecimalUtils.toBigDecimal(100);
                if (BigDecimalUtils.noLessThan(rate, BigDecimalUtils.zero())
                        && BigDecimalUtils.noMoreThan(rate, bg100)){
                    m.set(SQLParam.BONUS_RATE, rate);

                    String result = calRate(canUse, m);
                    if (result != null){
                        return result;
                    }

                    //税率在0 ~ 100之间
                    BigDecimal leftRate = BigDecimalUtils.subtract(bg100, rate, true);

                    //可用奖金额度，税率是0~100，xx%
                    BigDecimal canUseBonus = BigDecimalUtils.multiply(total, leftRate, true);

                    //真实可用奖金额度，除以了100（即百分号）
                    BigDecimal realCanUseBonus = BigDecimalUtils.divide(canUseBonus, bg100);

                    m.set(SQLParam.CAN_USE_BONUS, realCanUseBonus);

                    if (SchemeModel.updateInfo(m)){
                        return Param.C_SUCCESS;
                    }else {
                        return Param.C_UPDATE_FAILED;
                    }
                }else {
                    String result = calRate(canUse, m);
                    if (result != null){
                        return result;
                    }
                }
            } else {
                String result = calRate(canUse, m);
                if (result != null){
                    return result;
                }
            }
        }

        if (SchemeModel.updateInfo(m)){
            return Param.C_SUCCESS;
        }else {
            return Param.C_UPDATE_FAILED;
        }
    }
    private static String calRate(BigDecimal canUse, SchemeModel m){
        //如果提交的可以分配的金额不为空，并且大于等于0，则直接入库，无需计算税率
        boolean noLessThan0 = BigDecimalUtils.noLessThan(canUse, BigDecimalUtils.zero());
        if (canUse != null && BigDecimalUtils.noLessThan(canUse, BigDecimalUtils.zero())){
            m.set(SQLParam.CAN_USE_BONUS, canUse);
            if (SchemeModel.updateInfo(m)){
                return Param.C_SUCCESS;
            }else {
                return Param.C_UPDATE_FAILED;
            }
        }
        return null;
    }


    public static Page<SchemeModel> query(int pageNumber, int pageSize,
                                             String status, String outOfTime,
                                             String outTimeType,
                                             String name,
                                             String orderColunm, String order){

        String symbol = " > ";

        //空、0、当前时间以后，1历史（当前时间之前），2全部
        if (StrUtils.isEmpty(outTimeType) || "0".equals(outTimeType)){
            symbol = " > ";
        }else if ("1".equals(outTimeType)){
            symbol = " < ";
        } else if ("2".equals(outTimeType)){
            symbol = null;
        }

        QueryParam query = new QueryParam();
        query.append("where ");
        if (!StrUtils.isEmpty(status)){
            query.equal(SQLParam.STATUS, status);
            query.and();
        }
        if (!StrUtils.isEmpty(outOfTime) && !StrUtils.isEmpty(symbol)){
            query.append(SQLParam.OUT_OF_TIME);
            query.append(symbol);
            query.append("'");
            query.append(outOfTime);
            query.append("'");
            query.and();
        }
        if (!StrUtils.isEmpty(name)){
            query.like(SQLParam.NAME, name);
            query.and();
        }
        String sql = query.string();
        sql = sql.trim();
        if (sql.equalsIgnoreCase("where")){
            sql = "";
        } else if (sql.equalsIgnoreCase("and")){
            sql = "";
        } else {
            if (sql.endsWith("and") || sql.endsWith("AND")){
                sql = sql.substring(0, sql.length() - 3);
            }
        }
        if (!StrUtils.isEmpty(orderColunm)) {
            if (StrUtils.isEmpty(order)){
                order = "ASC";
            }
            sql = sql + " order by " + orderColunm + " " + order;
        }

        return SchemeModel.query(pageNumber, pageSize, sql);
    }


    /**
     * 同步方便表，已售金额、已付款的金额
     */
    public static void syncSchemeSelledAndPayedMoney(){
        List<SchemeModel> ls = SchemeModel.queryNoramlScheme();
        if (ls == null || ls.size() < 1){
            Logs.sysErr("未过期、正常售卖的方案为空..");
            return;
        }

        for (SchemeModel m : ls) {
            try {
                int schemeId = m.get(SQLParam.ID);
                BigDecimal selledM = m.get(SQLParam.SELLED_MONEY);
                BigDecimal payedM = m.get(SQLParam.PAYED_MONEY);
                Logs.sysErr("方案id：" + schemeId + "，方案名称：" + m.get(SQLParam.NAME));
                Logs.sysErr("方案表里已售卖的金额：" + selledM.toString() + "\t已支付的金额：" + payedM.toString());
                List<OrderModel> pays = OrderModel.queryBySchemeId(schemeId);
                if (pays != null && pays.size() > 0) {
                    BigDecimal selledMoney = OrderModel.selled(pays);
                    BigDecimal payedMoney = OrderModel.payed(pays);
                    Logs.sysErr("购买记录表里已售卖的金额：" + selledMoney.toString() + "\t已支付的金额：" + payedMoney.toString());

                    if (selledM.compareTo(selledMoney) == 0 && payedM.compareTo(payedMoney) == 0){
                        Logs.sysErr("已售卖金额、已支付金额已同步，无需同步...");
                    }else {
                        m.set(SQLParam.SELLED_MONEY, selledMoney);
                        m.set(SQLParam.PAYED_MONEY, payedMoney);

                        if (SchemeModel.updateInfo(m)) {
                            Logs.sysErr("同步方案表里已售卖金额、已支付金额成功..");
                        } else {
                            Logs.sysErr("更新方案表里已售卖金额、已支付金额失败了......");
                        }
                    }
                } else {
                    Logs.sysErr("该方案没有购买记录..");
                }

                Logs.sysErr("****************同步完成*******************");
            }catch (Exception e) {
                Logs.printException(e);
                Logs.sysErr("同步方案表里已售卖金额、已支付金额出错..");
            }
        }

    }


    /**
     * 检查方案是否禁用
     * @param m
     * @return true禁用，
     */
    public static boolean checkSchemeStatus(SchemeModel m){
        if (m == null){
            return false;
        }
        String status = m.get(SQLParam.STATUS);

        return !SQLParam.STATUS_ENABLE.equals(status);
    }

    /**
     * 检查方案是否过期
     * @param m
     * @return true已过期
     */
    public static boolean checkOutOfTime(SchemeModel m){
        if (m == null){
            return false;
        }
        String outOfTime = m.get(SQLParam.OUT_OF_TIME);

        long oft = TimeUtils.strToMil(outOfTime, null, System.currentTimeMillis());
        return oft < System.currentTimeMillis();
    }

    /**
     * 检查方案是否已经被卖出去了
     * @param schemeId
     * @return true有人购买过
     */
    public static boolean checkSelled(int schemeId){
        List<OrderModel> ls = OrderModel.queryBySchemeId(schemeId);
        return ls != null && ls.size() > 0;
    }











































































}
