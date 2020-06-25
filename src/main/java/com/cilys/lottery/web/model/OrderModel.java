package com.cilys.lottery.web.model;


import com.cily.utils.base.StrUtils;
import com.cilys.lottery.web.conf.SQLParam;
import com.jfinal.plugin.activerecord.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class OrderModel extends BaseModel<OrderModel> {
    private static OrderModel dao = new OrderModel();

    public static boolean insert(OrderModel b) {
        if (b == null) {
            return false;
        }

        return b.save();
    }

    public static List<OrderModel> queryAll() {
        return dao.find("select * from " + SQLParam.T_ORDER);
    }

    public static Page<OrderModel> query(int pageNumber, int pageSize, String whereParam){
        if (whereParam != null && whereParam.length() > 0){
            return dao.paginate(pageNumber, pageSize, "select * ",
                    StrUtils.join(" from ", SQLParam.T_ORDER, " where ", whereParam));
        }else {
            return dao.paginate(pageNumber, pageSize, "select * ",
                    StrUtils.join(" from ", SQLParam.T_ORDER));
        }
    }

    public static boolean delById(Object id) {
        return dao.deleteById(id);
    }

    public static boolean updateInfo(OrderModel b) {
        if (b == null) {
            return false;
        }

        return b.update();
    }

    public static OrderModel queryById(Object id) {
        return dao.findById(id);
    }

    public static List<OrderModel> queryBySchemeId(int schemeId){
        return dao.find("select * from " + SQLParam.T_ORDER
                + " where " + SQLParam.SCHEME_ID + " = " + schemeId);
    }

    /**
     * 判断是否已经超卖，即已卖的钱 + 即将购买的钱，加起来，是否超过总额度
     * @param schemeId
     * @return
     */
    public static boolean isOutOfSell(int schemeId, BigDecimal totalMoney, BigDecimal forMoney){
        BigDecimal selled = selled(queryBySchemeId(schemeId));

        return isOutOfSell(totalMoney, forMoney, selled);
    }

    public static boolean isOutOfSell(BigDecimal totalMoney, BigDecimal forMoney, BigDecimal selledMoney){
        BigDecimal zero = new BigDecimal("0.00");
        int to = totalMoney.compareTo(zero);
        if (to == 0 || to == -1){
            return true;
        }
        int pa = forMoney.compareTo(zero);
        if (pa == 0 || pa == -1){
            return true;
        }

        BigDecimal s = forMoney.add(selledMoney);
        if (totalMoney.compareTo(s) == -1){
            return true;
        }
        return false;
    }

    /**
     * 已支付
     * @param ms
     * @return
     */
    public static BigDecimal payed(List<OrderModel> ms){
        if (ms == null || ms.size() < 1){
            return new BigDecimal("0.00");
        }

        BigDecimal b0 = new BigDecimal("0.00");
        for (OrderModel m : ms){
            String payStatus = m.getStr(SQLParam.ORDER_STATUS);
            if ("0".equals(payStatus)) {
                BigDecimal b = m.getBigDecimal(SQLParam.MONEY);
                b0 = b0.add(b);
            }
        }

        return b0;
    }

    /**
     * 已购买，但不一定支付成功
     */
    public static BigDecimal selled(List<OrderModel> ms){
        if (ms == null || ms.size() < 1){
            return new BigDecimal("0.00");
        }

        BigDecimal b0 = new BigDecimal("0.00");
        for (OrderModel m : ms){
            BigDecimal b = m.getBigDecimal(SQLParam.MONEY);
            b0 = b0.add(b);
        }

        return b0;
    }

    @Override
    protected Map<String, Object> _getAttrs() {
        Map<String, Object> map = super._getAttrs();
        map.put("cusertomerName", cusertomerName);
        map.put("operatorName", operatorName);
        map.put("payOperatorName", payOperatorName);
        return map;
    }
    private String cusertomerName;
    private String operatorName;
    private String payOperatorName;

    public void setCusertomerName(String cusertomerName) {
        this.cusertomerName = cusertomerName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public void setPayOperatorName(String payOperatorName) {
        this.payOperatorName = payOperatorName;
    }

    public static boolean updateOrderStatus(OrderModel m) throws Exception {
        if (m == null){
            return false;
        }
        return m.update();
    }
}