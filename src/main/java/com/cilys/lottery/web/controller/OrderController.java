package com.cilys.lottery.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cily.utils.base.StrUtils;
import com.cily.utils.base.log.Logs;
import com.cilys.lottery.web.conf.Param;
import com.cilys.lottery.web.conf.PayStatus;
import com.cilys.lottery.web.conf.PayType;
import com.cilys.lottery.web.conf.SQLParam;
import com.cilys.lottery.web.model.OrderModel;
import com.cilys.lottery.web.model.SchemeModel;
import com.cilys.lottery.web.model.UserModel;
import com.cilys.lottery.web.model.impl.OrderImpl;
import com.cilys.lottery.web.model.utils.QueryParam;
import com.cilys.lottery.web.model.utils.RootUserIdUtils;
import com.cilys.lottery.web.model.utils.SchemeUtils;
import com.cilys.lottery.web.model.utils.UserUtils;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by admin on 2020/6/22.
 */
public class OrderController extends BaseController {

    /**
     * 购买
     */
    public void add(){
        String params = HttpKit.readData(getRequest());
        Logger.getLogger(getClass().getSimpleName()).info("params = " + params);
        Logs.sysOut("params = " + params);

        try{
            Map<String, Object> m = JSON.parseObject(params,  new TypeReference<Map<String, Object>>(){}.getType());

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

            String headUserId = getUserId();    //从head请求头里取出来的userId，也就是当前用户

            String customerId = sm.getStr(SQLParam.CUSTOMER_ID);    //客户id
            String operator = sm.getStr(SQLParam.OPERATOR);         //购买操作者id
            String payOperator = sm.getStr(SQLParam.ORDER_OPERATOR);  //支付操作者id

            if (StrUtils.isEmpty(customerId)){
                renderJsonFailed(Param.C_CUSTOMER_ID_NULL, null);
                return;
            }
            if (!UserUtils.userExist(customerId)){
                renderJsonFailed(Param.C_CUSTOMER_ID_NOT_EXIST, null);
                return;
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
                renderJsonFailed(Param.C_BUY_MONEY_ZERO, null);
                return;
            }
            if (money.compareTo(zeroBigDecimal) == -1){
                renderJsonFailed(Param.C_BUY_MONEY_ILLAGE, null);
                return;
            }

            //检测方案是否存在
            SchemeModel schemeModel = SchemeModel.queryById(schemeId);
            if (schemeModel == null){
                renderJsonFailed(Param.C_SCHEME_NOT_EXIST, null);
                return;
            }
            //检测方案状态是否禁售
            if (SchemeUtils.checkSchemeStatus(schemeModel)){
                renderJsonFailed(Param.C_SCHEME_DISABLE, null);
                return;
            }

            //检测方案是否过期
            if (SchemeUtils.checkOutOfTime(schemeModel)){
                renderJsonFailed(Param.C_SCHEME_OUT_OF_TIME, null);
                return;
            }


            //查询是否超卖
            List<OrderModel> ls = OrderModel.queryBySchemeId(schemeId);
            BigDecimal selledMoney = OrderModel.selled(ls);
            BigDecimal payedMoney = OrderModel.payed(ls);

            if (OrderModel.isOutOfSell(schemeModel.getBigDecimal(SQLParam.TOTAL_MONEY), money, selledMoney)){
                renderJsonFailed(Param.C_SCHEME_LEFT_MONEY_NOT_EOUTH, null);
                return;
            }

            //账号余额支付，需判断余额是否足够
            if (PayType.PAY_YU_E.equals(payType)){
                if (headUserId.equals(customerId)){
                    //自己购买，判断账号余额是否足够
                    if (selfMoney.compareTo(money) == 1){
                        sm.set(SQLParam.ORDER_STATUS, PayStatus.PAYED);
                        if (OrderModel.insert(sm)){
                            um.set(SQLParam.LEFT_MONEY, selfMoney.subtract(money));
                            UserModel.updateUserLeftMoney(um);

                            //TODO 更新方案里的已购买的份额、已支付的份额
                            BigDecimal selledM = selledMoney.add(money);
                            schemeModel.set(SQLParam.SELLED_MONEY, selledM);

                            BigDecimal payedM = payedMoney.add(payedMoney);
                            schemeModel.set(SQLParam.PAYED_MONEY, payedM);

                            SchemeModel.updateInfo(schemeModel);


                            renderJsonSuccess(null);
                            return;
                        }else {
                            renderJsonFailed(Param.C_INSERT_FAILED, null);
                            return;
                        }
                    }else {
                        renderJsonFailed(Param.C_USER_LEFT_MONEY_NOT_ENTHOH, null);
                        return;
                    }
                }else {
                    //检查系统是否开通管理员代购开关
                    boolean adminCanBuyOther = PropKit.getBoolean("adminCanBuyOther", false);
                    if (!adminCanBuyOther){
                        renderJsonFailed(Param.C_ADMIN_CAN_NOT_BUY_FOR_OTHER, null);
                        return;
                    }

                    if (RootUserIdUtils.isRootUser(getUserId())) {
                        //管理员代买，则需要判断被代买者的账号余额是否足够
                        if (selfMoney.compareTo(money) == 1){
                            sm.set(SQLParam.ORDER_STATUS, PayStatus.PAYED);
                            if (OrderModel.insert(sm)){
                                um.set(SQLParam.LEFT_MONEY, selfMoney.subtract(money));
                                UserModel.updateUserLeftMoney(um);
                                //TODO 更新方案里的已购买的份额、已支付的份额

                                //TODO 更新方案里的已购买的份额、已支付的份额
                                BigDecimal selledM = selledMoney.add(money);
                                schemeModel.set(SQLParam.SELLED_MONEY, selledM);
                                BigDecimal payedM = payedMoney.add(payedMoney);
                                schemeModel.set(SQLParam.PAYED_MONEY, payedM);
                                SchemeModel.updateInfo(schemeModel);

                                renderJsonSuccess(null);
                                return;
                            }else {
                                renderJsonFailed(Param.C_INSERT_FAILED, null);
                                return;
                            }
                        }else {
                            renderJsonFailed(Param.C_USER_LEFT_MONEY_NOT_ENTHOH, null);
                            return;
                        }
                    } else {
                        //普通用户不可代购
                        renderJsonFailed(Param.C_NORMAL_CAN_NOT_BUY_FOR_OTHER, null);
                        return;
                    }
                }
            }else {
                //非余额支付，普通用户默认是未支付状态，管理员需从参数里获取是否已支付
                String orderStatus = SQLParam.STATUS_DISABLE;

                if (RootUserIdUtils.isRootUser(getUserId())) {
                    orderStatus = sm.getStr(SQLParam.ORDER_STATUS);
                }

                sm.set(SQLParam.ORDER_STATUS, orderStatus);

                if (OrderModel.insert(sm)){
                    //TODO 更新方案里的已购买的份额
                    BigDecimal selledM = selledMoney.add(money);
                    schemeModel.set(SQLParam.SELLED_MONEY, selledM);
                    SchemeModel.updateInfo(schemeModel);

                    renderJsonSuccess(null);
                }else {
                    renderJsonFailed(Param.C_BUY_FAILED, null);
                }
            }

        }catch (Exception e){
            Logger.getLogger(UserUtils.class.getSimpleName()).info("params = " + params);
            Logs.printException(e);
            renderJsonFailed(Param.C_PARAM_ERROR, null);
        }
    }

    public void query(){
        String orderStatus = getParam(SQLParam.ORDER_STATUS);
        String payType = getParam(SQLParam.PAY_TYPE);
        int schemeId = getInt(SQLParam.SCHEME_ID);

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
        Page<OrderModel> datas = OrderModel.query(getPageNumber(), getPageSize(), queryParam.string());
        if (datas != null){
            List<OrderModel> ls = datas.getList();
            if (ls != null && ls.size() > 0){
                for (OrderModel m : ls){
                    String customerId = m.getStr(SQLParam.CUSTOMER_ID);
                    String operator = m.getStr(SQLParam.OPERATOR);
                    String orderOperator = m.getStr(SQLParam.ORDER_OPERATOR);

                    String cusertomerName = UserUtils.getUserRealNameFromCache(customerId);
                    String operatorName = UserUtils.getUserRealNameFromCache(operator);
                    String payOperatorName = UserUtils.getUserRealNameFromCache(orderOperator);

                    m.setCusertomerName(cusertomerName);
                    m.setOperatorName(operatorName);
                    m.setPayOperatorName(payOperatorName);
                }
            }
        }
        renderJsonSuccess(datas);
    }

    /**
     * 更新订单支付状态
     */
    public void updateOrderStatus(){
        final int id = getIntWithDefaultValue(SQLParam.ID);

        final String newOrderStatus = getParam(SQLParam.ORDER_STATUS);

        Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                try{
                    String result = OrderImpl.updatePayStatus(id, newOrderStatus);
                    if (Param.C_SUCCESS.equals(result)){
                        renderJsonSuccess(null);
                        return true;
                    }else {
                        renderJsonFailed(result, null);
                        return false;
                    }
                }catch (Exception e){
                    renderJsonFailed(Param.C_SERVER_ERROR, null);
                    Logs.printException(e);
                    return false;
                }


            }
        });

    }
}