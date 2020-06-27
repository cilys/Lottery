package com.cilys.lottery.web.model.impl;

import com.cily.utils.base.StrUtils;
import com.cilys.lottery.web.conf.Param;
import com.cilys.lottery.web.conf.SQLParam;
import com.cilys.lottery.web.model.SchemeModel;
import com.cilys.lottery.web.utils.BigDecimalUtils;
import com.cilys.lottery.web.model.utils.QueryParam;
import com.jfinal.plugin.activerecord.Page;

import java.math.BigDecimal;
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














































































}
