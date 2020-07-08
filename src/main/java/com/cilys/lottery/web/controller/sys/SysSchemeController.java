package com.cilys.lottery.web.controller.sys;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cily.utils.base.StrUtils;
import com.cily.utils.base.log.Logs;
import com.cilys.lottery.web.cache1.SchemeInfoCache;
import com.cilys.lottery.web.conf.Param;
import com.cilys.lottery.web.conf.SQLParam;
import com.cilys.lottery.web.controller.SchemeController;
import com.cilys.lottery.web.interceptor.SysUserInterceptor;
import com.cilys.lottery.web.log.LogUtils;
import com.cilys.lottery.web.model.SchemeModel;
import com.cilys.lottery.web.model.impl.OrderImpl;
import com.cilys.lottery.web.model.impl.SchemeImpl;
import com.cilys.lottery.web.model.utils.UserUtils;
import com.cilys.lottery.web.utils.BigDecimalUtils;
import com.cilys.lottery.web.utils.ParamUtils;
import com.jfinal.aop.Before;
import com.jfinal.kit.HttpKit;

import java.math.BigDecimal;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by admin on 2020/6/16.
 */
@Before({SysUserInterceptor.class})
public class SysSchemeController extends SchemeController {

    public void add(){
        try{
            String params = HttpKit.readData(getRequest());
            LogUtils.info(getClass().getSimpleName(), null, getRequest().getRequestURI(), params, getUserId());

            Map<String, Object> m = ParamUtils.parseJson(params);

            SchemeModel sm  = new SchemeModel();
            sm.put(m);
            sm.removeNullValueAttrs();

            String name = sm.getStr(SQLParam.NAME);
            if (StrUtils.isEmpty(name)){
                renderJsonFailed(Param.C_SCHEME_NAME_NULL, null);
                return;
            }
            if (SchemeModel.queryByName(name) != null){
                renderJsonFailed(Param.C_SCHEME_NAME_EXIST, null);
                return;
            }

            if (SchemeModel.insert(sm)){
                SchemeInfoCache.clear();

                renderJsonSuccess(null);
            }else {
                renderJsonFailed(Param.C_INSERT_FAILED, null);
            }
        }catch (Exception e){
            Logs.printException(e);
            renderJsonFailed(Param.C_PARAM_ERROR, null);
        }
    }

    public void del(){
        int id = getInt(SQLParam.ID);

        //判断是否有购买记录，如果有购买记录，则不可删除
        if (SchemeImpl.checkSelled(id)){
            renderJsonFailed(Param.C_SCHEME_HAS_SELLED, null);
            return;
        }
        if (SchemeModel.delById(id)){
            SchemeInfoCache.clear();

            renderJsonSuccess(null);
        }else {
            renderJsonFailed(Param.C_DEL_FAILED, null);
        }
    }

    public void updateInfo(){
        int id = getInt(SQLParam.ID);

        String params = HttpKit.readData(getRequest());
        LogUtils.info(getClass().getSimpleName(), null, getRequest().getRequestURI(), params, getUserId());

        try{
            Map<String, Object> m = JSON.parseObject(params,  new TypeReference<Map<String, Object>>(){}.getType());

            SchemeModel sm  = SchemeModel.queryById(id);
//            //检查方案是否被禁用
//            if (SchemeUtils.checkSchemeStatus(sm)){
//                renderJsonFailed(Param.C_SCHEME_DISABLE, null);
//                return;
//            }
//            //检查方案是否已经过期
//            if (SchemeUtils.checkOutOfTime(sm)){
//                renderJsonFailed(Param.C_SCHEME_OUT_OF_TIME, null);
//                return;
//            }
            //检查方案是否已经被售卖过
            if (SchemeImpl.checkSelled(id)){
                renderJsonFailed(Param.C_SCHEME_HAS_SELLED, null);
                return;
            }

//            sm.put(m);
//            sm.removeNullValueAttrs();

            sm.set(SQLParam.NAME, m.get(SQLParam.NAME));
            sm.set(SQLParam.TOTAL_MONEY, m.get(SQLParam.TOTAL_MONEY));
            sm.set(SQLParam.OUT_OF_TIME, m.get(SQLParam.OUT_OF_TIME));
            sm.set(SQLParam.STATUS, m.get(SQLParam.STATUS));
            sm.set(SQLParam.DESCPTION, m.get(SQLParam.DESCPTION));

            String name = sm.getStr(SQLParam.NAME);
            if (StrUtils.isEmpty(name)){
                renderJsonFailed(Param.C_SCHEME_NAME_NULL, null);
                return;
            }

            SchemeModel dm = SchemeModel.queryByName(name);

            if (dm != null) {
                int qId = dm.getInt(SQLParam.ID);
                if (id == qId) {

                } else {
                    renderJsonFailed(Param.C_SCHEME_NAME_EXIST, null);
                    return;
                }
            }

            if (SchemeModel.updateInfo(sm)){
                SchemeInfoCache.clear();

                renderJsonSuccess(null);
            }else {
                renderJsonFailed(Param.C_UPDATE_FAILED, null);
            }
        }catch (Exception e){
            Logger.getLogger(UserUtils.class.getSimpleName()).info("params = " + params);
            Logs.printException(e);
            renderJsonFailed(Param.C_PARAM_ERROR, null);
        }
    }

    /**
     * 更新方案状态
     */
    public void updateStatus(){
        String status = getParam(SQLParam.STATUS);
        int id = getInt(SQLParam.ID);


        if (StrUtils.isEmpty(status)){
            renderJsonFailed(Param.C_STATUS_ERROR, null);
            return;
        }
        if (SQLParam.STATUS_ENABLE.equals(status)
                || SQLParam.STATUS_DISABLE.equals(status)){
            SchemeModel m = SchemeModel.queryById(id);
            if (m == null){
                renderJsonFailed(Param.C_SCHEME_NOT_EXIST, null);
                return;
            }
            m.set(SQLParam.STATUS, status);
            if (SchemeModel.updateInfo(m)){
                renderJsonSuccess(null);
            }else {
                renderJsonFailed(Param.C_UPDATE_FAILED, null);
            }

        }else {
            renderJsonFailed(Param.C_STATUS_ERROR, null);
        }
    }

    /**
     * 根据id查询方案
     */
    public void queryById(){
        int id = getInt(SQLParam.ID);

        renderJsonSuccess(SchemeModel.queryById(id));
    }


    /**
     * 更新奖金
     */
    public void updateBonus() {
        int id = getInt(SQLParam.ID);

        try {
            String params = HttpKit.readData(getRequest());
            LogUtils.info(getClass().getSimpleName(), null, getRequest().getRequestURI(), params, getUserId());

            Map<String, String> m = ParamUtils.parseJsonToStr(params);
            renderJsonFailed(SchemeImpl.updateBonus(id, m), null);
        } catch (Exception e) {
            Logs.printException(e);
            renderJsonFailed(Param.C_PARAM_ERROR, null);
        }
    }

    /**
     * 查询奖金状态
     */
    public void queryBonusStatus(){
        int schemeId = getInt(SQLParam.ID, -1);

        SchemeModel sm = SchemeModel.queryById(schemeId);
        if (sm == null){
            renderJsonFailed(Param.C_SCHEME_NOT_EXIST, null);
            return;
        }

        BigDecimal canUseBonus = sm.getBigDecimal(SQLParam.CAN_USE_BONUS);
        if (canUseBonus == null
                || BigDecimalUtils.noMoreThan(canUseBonus, BigDecimalUtils.zero())) {

            renderJsonFailed(Param.C_NONE_OF_CAN_USE_BONUS, null);
            return;
        }

        //订单中，只要有一个人的资金到流水里，就不可修改奖金的状态
        renderJsonSuccess(OrderImpl.queryBonusStatus(schemeId));
    }
}