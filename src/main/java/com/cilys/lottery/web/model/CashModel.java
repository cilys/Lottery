package com.cilys.lottery.web.model;

import com.cily.utils.base.StrUtils;
import com.cily.utils.base.time.TimeUtils;
import com.cilys.lottery.web.conf.SQLParam;
import com.jfinal.plugin.activerecord.Page;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by Administrator on 2020/7/7.
 * 提现申请
 */
public class CashModel extends BaseModel<CashModel> {
    private static CashModel dao = new CashModel();
    public final static String STATUS_APPLY_PROCESS = "1";  //申请中
    public final static String STATUS_APPLY_SUCCESS = "0";  //成功
    public final static String STATUS_APPLY_REFUSE = "2";   //拒绝

    public static boolean insert(CashModel m) throws Exception{
        if (m == null){
            return false;
        }

        return m.save();
    }

    public static boolean updateStatus(CashModel m){
        if (m == null){
            return false;
        }
        return m.update();
    }

    public static CashModel queryById(int id){
        return dao.findById(id);
    }

    public static Page<CashModel> query(int pageNumber, int pageSize, String queryWhere){
        if (StrUtils.isEmpty(queryWhere)){
            return dao.paginate(pageNumber, pageSize, "select * ",
                    " from " + SQLParam.T_CASH);
        }else {
            return dao.paginate(pageNumber, pageSize, "select * ",
                    " from " + SQLParam.T_CASH + " where " + queryWhere);
        }
    }

    public static boolean del(int id){
        return dao.deleteById(id);
    }

    private String realName;
    private String operatorName;
    public void setRealName(String realName) {
        this.realName = realName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    @Override
    protected Map<String, Object> _getAttrs() {
        Map<String, Object> map = super._getAttrs();
        if (map != null){
            map.put(SQLParam.REAL_NAME, realName);
            map.put(SQLParam.OPERATOR_NAME, operatorName);
        }
        return map;
    }
}
