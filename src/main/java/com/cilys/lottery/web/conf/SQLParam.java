package com.cilys.lottery.web.conf;

/**
 * Created by admin on 2018/1/30.
 */
public interface SQLParam {
    String T_USER = "t_user";

    String T_TOKEN = "t_token";



    String USER_ID = "userId";
    String USER_NAME = "userName";
    String PWD = "pwd";
    String REAL_NAME = "realName";
    String SEX = "sex";
    String PHONE = "phone";
    String ADDRESS = "address";
    String ID_CARD = "idCard";
    String STATUS = "status";
    String CREATE_TIME = "createTime";
    String TOKEN = "token";
    String UPDATE_TIME = "updateTime";
    String LEFT_MONEY = "leftMoney";    //余额


    String STATUS_ENABLE = "0";
    String STATUS_DISABLE = "1";


    String ID = "id";

    String T_UNION_SCHEME = "t_union_scheme";
    String NAME = "name";
    String TOTAL_MONEY = "totalMoney";
    String MAX_USERS = "maxUsers";
    String DESCPTION = "descption";
    String CREATORID = "creatorId";
    String OUT_OF_TIME = "outOfTime";
    String PAYED_MONEY = "payedMoney";
    String SELLED_MONEY = "selledMoney";
    String TOTAL_BONUS = "totalBonus";      //中奖奖金总额
    String BONUS_RATE = "bonusRate";        //奖金税率
    String CAN_USE_BONUS = "canUseBonus";   //可用奖金，即缴税后的奖金余额



    String T_ORDER = "t_order";
    String SCHEME_ID = "schemeId";
    String CUSTOMER_ID = "customerId";
    String MONEY = "money";
    String PAY_TYPE = "payType";
    String ORDER_STATUS = "orderStatus";
    String OPERATOR = "operator";//购买操作者，默认为客户id
    String ORDER_OPERATOR = "orderOperator";    //支付操作者，默认为客户id
    String PAYED_RATE = "payedRate";    //已付款的用户支付金额，占全部已支付的百分比
    String BONUS_MONEY = "bonusMoney";    //该方案中奖后，用户所得的奖金。可分配奖金乘以该用户出资占比，即为该用户的中奖金额
    String BONUS_STATUS = "bonusStatus";    //奖金状态，0该奖金已经到用户账户里，1奖金已计算但未分配给用户账户里，2奖金到账后又重新退款给系统，3默认状态，表示该方案的奖金未经过任何操作


    String T_USER_MONEY_FLOW = "t_user_money_flow";
    String ORDER_ID = "orderId";
    String SOURCE_USER_ID = "sourceUserId";
    String IS_ADD_TO_USER = "isAddToUser";

    String T_LOG = "t_log";
    String OPERATION_TIME = "operationTime";
    String ACTION_URL = "actionUrl";
    String PARAM = "param";

    String SYSTEM = "system";
    String SORT_COLUMN = "sortColumn";
    String SORT = "sort";
    String AES = "aes";
    String DESC = "desc";
}