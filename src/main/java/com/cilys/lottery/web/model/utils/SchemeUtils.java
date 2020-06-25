package com.cilys.lottery.web.model.utils;

import com.cily.utils.base.log.Logs;
import com.cily.utils.base.time.TimeUtils;
import com.cilys.lottery.web.conf.SQLParam;
import com.cilys.lottery.web.model.OrderModel;
import com.cilys.lottery.web.model.SchemeModel;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by admin on 2020/6/23.
 */
public class SchemeUtils {

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
