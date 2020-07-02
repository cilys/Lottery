package com.cilys.lottery.web.controller;

import com.cily.utils.base.StrUtils;
import com.cilys.lottery.web.cache.SchemeInfoCache;
import com.cilys.lottery.web.cache.UserInfoCache;
import com.cilys.lottery.web.conf.Param;
import com.cilys.lottery.web.conf.SQLParam;
import com.cilys.lottery.web.model.UserMoneyFlowModel;
import com.cilys.lottery.web.model.impl.UserMoneyFlowImpl;
import com.cilys.lottery.web.model.utils.RootUserIdUtils;
import com.cilys.lottery.web.model.utils.UserUtils;
import com.jfinal.plugin.activerecord.Page;

/**
 * Created by admin on 2020/6/27.
 */
public class UserMoneyFlowController extends BaseController {

    /**
     * 查询资金流水
     */
    public void query(){
        String userId = getParam(SQLParam.USER_ID);

        if (StrUtils.isEmpty(userId)){
            //查询所有用户的资金流水

            //系统用户可以查询
            if (RootUserIdUtils.isRootUser(getUserId())){

            } else {
                renderJsonFailed(Param.C_RIGHT_LOW, null);
                return;
            }
        } else {
            if (userId.equals(getUserId())){
                //只能查自己的
            }else {
                //查别人的，判断是否是系统用户
                if (RootUserIdUtils.isRootUser(getUserId())){

                } else {
                    renderJsonFailed(Param.C_RIGHT_LOW, null);
                    return;
                }
            }
        }

        String sortColumn = getParam(SQLParam.SORT_COLUMN, SQLParam.CREATE_TIME);
        String sort = getParam(SQLParam.SORT, SQLParam.DESC);

        String isAddToUser = getParam(SQLParam.IS_ADD_TO_USER);
        String payType = getParam(SQLParam.PAY_TYPE);


        Page<UserMoneyFlowModel> datas = UserMoneyFlowImpl.query(getPageNumber(), getPageSize(),
                userId, payType, isAddToUser, sortColumn, sort);
        if (datas != null && datas.getList() != null){
            for (UserMoneyFlowModel m : datas.getList()){
                String userRealName = UserInfoCache.getUserRealNameFromCache(m.getStr(SQLParam.USER_ID));
                String sourceUserId = m.get(SQLParam.SOURCE_USER_ID);
                String sourceUserName = "系统";
                if (SQLParam.SYSTEM.equalsIgnoreCase(sourceUserId)){

                } else {
                    sourceUserName = UserInfoCache.getUserRealNameFromCache(sourceUserId);
                }
                Integer schemeId = m.get(SQLParam.SCHEME_ID);
                m.setUserRealName(userRealName);
                m.setSourceUserName(sourceUserName);
                if (schemeId != null) {
                    m.setSchemeName(SchemeInfoCache.getSchemeName(schemeId));
                }
            }
        }
        renderJsonSuccess(datas);
    }
}