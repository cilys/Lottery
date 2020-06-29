package com.cilys.lottery.web.controller;

import com.cily.utils.base.StrUtils;
import com.cily.utils.base.time.TimeUtils;
import com.cilys.lottery.web.conf.SQLParam;
import com.cilys.lottery.web.model.impl.SchemeImpl;

/**
 * Created by admin on 2020/6/27.
 */
public class SchemeController extends BaseController {

    /**
     * 查询全部方案
     */
    public void queryAll(){
        String status = getParam(SQLParam.STATUS, null);
        String outTimeType = getParam("outTimeType", null);

        String currentTime = null;

        if (StrUtils.isEmpty(outTimeType) || "0".equals(outTimeType)){
            currentTime = TimeUtils.milToStr(System.currentTimeMillis(), null);
        } else if ("1".equals(outTimeType)){
            currentTime = TimeUtils.milToStr(System.currentTimeMillis(), null);
        } else if ("2".equals(outTimeType)){
            currentTime = null;
        }
        String name = getParam(SQLParam.NAME);
        String orderColunm = SQLParam.OUT_OF_TIME;
        String order = "DESC";
//        renderJsonSuccess(SchemeModel.query(getPageNumber(), getPageSize(), status,
//                currentTime, outTimeType, orderColunm, order));
        renderJsonSuccess(SchemeImpl.query(getPageNumber(), getPageSize(), status,
                currentTime, outTimeType, name, orderColunm, order));
    }
}