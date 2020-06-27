package com.cilys.lottery.web.conf;

import com.cily.utils.base.StrUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2018/1/30.
 */
public class Param {
    public final static String DEVICE_IMEI = "deviceImei";

    public final static String PAGE_NUMBER = "pageNumber";
    public final static String PAGE_SIZE = "pageSize";
    public final static String SEARCH_TEXT = "searchText";
    public final static String OS_TYPE = "osType";


    //错误码规则：0成功；1~99系统错误；1001~1999用户相关错误
    public final static String C_SUCCESS = "0";

    public final static String C_SERVER_ERROR = "11";

    public final static String C_404 = "404";
    public final static String C_500 = "500";

    public final static String C_USER_NOT_EXIST = "1001";
    public final static String C_USER_NOT_LOGIN = "1002";
    public final static String C_USER_LOGIN_ON_OTHER = "1003";
    public final static String C_USER_OR_PWD_ERROR = "1006";
    public final static String C_USER_NAME_NULL = "1011";
    public final static String C_USER_NAME_ILLAGLE = "1012";

    public final static String C_USER_DEL_FAILED = "1021";
    public final static String C_USER_LEFT_MONEY_NOT_ENTHOH = "5022";

    public final static String C_PWD_NULL = "1031";
    public final static String C_PWD_ILLAGLE = "1032";
    public final static String C_PWD_NEW_NULL = "1033";
    public final static String C_PWD_NOT_EQUAL = "1034";
    public final static String C_PWD_CHANGE_FAILED = "1035";

    public final static String C_REAL_NAME_TOO_LONG = "1041";

    public final static String C_PHONE_ILLAGLE = "1051";

    public final static String C_ID_CARD_ILLAGLE = "1061";

    public final static String C_RECHARGE_FAILED = "1071";


    public final static String C_RESIGT_USER_EXISTS = "1101";
    public final static String C_REGIST_FAILURE = "1106";


    public final static String C_USER_ID_NULL = "1111";
    public final static String C_TOKEN_NULL = "1112";
    public final static String C_TOKEN_ERROR = "1113";

    public final static String C_USER_INFO_UPDATE_FAILURE = "1121";
    public final static String C_USER_INFO_NO_UPDATE = "1122";

    public final static String C_ADD_MONEY_NULL = "1131";
    public final static String C_ADD_MONEY_ILLAGE = "1132";


    public final static String C_PARAM_ERROR = "2111";
    public final static String C_STATUS_ERROR = "2112";

    public final static String C_INSERT_FAILED = "2201";
    public final static String C_UPDATE_FAILED = "2202";
    public final static String C_QUERY_FAILED = "2203";
    public final static String C_DEL_FAILED = "2204";
    public final static String C_NONE_FOR_UPDATE = "2205";

    public final static String C_SEARCH_TEXT_NULL = "3001";
    public final static String C_SEARCH_TEXT_TOO_LONG = "3002";

    public final static String C_RIGHT_LOW = "4001";
    public final static String C_SCHEME_NAME_NULL = "5001";
    public final static String C_SCHEME_NAME_EXIST = "5002";
    public final static String C_SCHEME_NOT_EXIST = "5003";
    public final static String C_SCHEME_DISABLE = "5004";
    public final static String C_SCHEME_OUT_OF_TIME = "5005";
    public final static String C_SCHEME_LEFT_MONEY_NOT_EOUTH = "5006";
    public final static String C_SCHEME_HAS_SELLED = "5007";
    public final static String C_SCHEME_ID_NULL = "5008";
    public final static String C_SCHEME_BONUS_PAYED_TO_USER = "5009";
    public final static String C_SCHEME_MONEY_ERROR = "5010";

    public final static String C_CUSTOMER_ID_NULL = "5061";

    public final static String C_CUSTOMER_ID_NOT_EXIST = "5062";
    public final static String C_ADMIN_CAN_NOT_BUY_FOR_OTHER = "5021";
    public final static String C_NORMAL_CAN_NOT_BUY_FOR_OTHER = "5022";

    public final static String C_BUY_MONEY_ZERO = "5031";
    public final static String C_BUY_MONEY_ILLAGE = "5032";
    public final static String C_BUY_FAILED = "5041";


    public final static String C_ORDER_NOT_EXIST = "5051";
    public final static String C_ORDER_ADD_FAILED = "5052";

    public final static String C_NONE_OF_CAN_USE_BONUS = "5061";







    private final static Map<String, String> failureInfo = new HashMap<>();

    static {
        failureInfo.put(C_SUCCESS, "操作成功");
        failureInfo.put(C_SERVER_ERROR, "系统内部异常");
        failureInfo.put(C_USER_NOT_EXIST, "用户不存在");
        failureInfo.put(C_USER_OR_PWD_ERROR, "用户名或密码错误");
        failureInfo.put(C_USER_NAME_NULL, "用户名为空");
        failureInfo.put(C_RESIGT_USER_EXISTS, "该账号已存在");
        failureInfo.put(C_REGIST_FAILURE, "注册失败");
        failureInfo.put(C_USER_ID_NULL, "用户id为空");
        failureInfo.put(C_TOKEN_NULL, "token为空");
        failureInfo.put(C_TOKEN_ERROR, "用户未登录或登录已失效");
        failureInfo.put(C_USER_INFO_UPDATE_FAILURE, "更新用户信息失败");
        failureInfo.put(C_USER_INFO_NO_UPDATE, "用户信息无更新");
        failureInfo.put(C_USER_NOT_LOGIN, "该用户未登录或登录已失效");
        failureInfo.put(C_USER_LOGIN_ON_OTHER, "该用户已在其他地方登录");
        failureInfo.put(C_USER_DEL_FAILED, "删除用户失败");
        failureInfo.put(C_USER_NAME_ILLAGLE, "用户名必须是2至30位的字母或数字或中划线或下划线");
        failureInfo.put(C_PWD_NULL, "密码为空");
        failureInfo.put(C_PWD_ILLAGLE, "密码不合法");

        failureInfo.put(C_404, "找不到路径");
        failureInfo.put(C_500, "系统内部异常");
        failureInfo.put(C_PHONE_ILLAGLE, "手机号码不合法");
        failureInfo.put(C_ID_CARD_ILLAGLE, "身份证号不合法");
        failureInfo.put(C_SEARCH_TEXT_NULL, "待搜索的内容为空");
        failureInfo.put(C_SEARCH_TEXT_TOO_LONG, "搜索内容过长");
        failureInfo.put(C_PARAM_ERROR, "参数错误");

        failureInfo.put(C_PWD_NEW_NULL, "新密码为空");
        failureInfo.put(C_PWD_NOT_EQUAL, "原密码错误");
        failureInfo.put(C_PWD_CHANGE_FAILED, "修改密码失败");

        failureInfo.put(C_RIGHT_LOW, "权限不足");

        failureInfo.put(C_INSERT_FAILED, "添加失败，请重试..");
        failureInfo.put(C_UPDATE_FAILED, "更新失败，请重试..");
        failureInfo.put(C_QUERY_FAILED, "查询失败，请重试..");
        failureInfo.put(C_DEL_FAILED, "删除失败，请重试..");

        failureInfo.put(C_SCHEME_NAME_NULL, "方案名称为空");
        failureInfo.put(C_SCHEME_NAME_EXIST, "方案名称已存在");
        failureInfo.put(C_STATUS_ERROR, "状态值为空或非法状态值");
        failureInfo.put(C_SCHEME_NOT_EXIST, "组合方案不存在");
        failureInfo.put(C_CUSTOMER_ID_NULL, "客户id为空");
        failureInfo.put(C_CUSTOMER_ID_NOT_EXIST,"客户不存在");
        failureInfo.put(C_ADMIN_CAN_NOT_BUY_FOR_OTHER, "暂未开通管理员代购权限，请联系管理员处理");
        failureInfo.put(C_NORMAL_CAN_NOT_BUY_FOR_OTHER, "普通用户暂不支持代购");
        failureInfo.put(C_BUY_MONEY_ZERO, "购买份额为0，请重新购买");
        failureInfo.put(C_BUY_MONEY_ILLAGE, "购买份额不合法，请重新购买");
        failureInfo.put(C_USER_LEFT_MONEY_NOT_ENTHOH, "账号余额不足");
        failureInfo.put(C_BUY_FAILED, "购买失败，请稍后重试..");
        failureInfo.put(C_SCHEME_DISABLE, "该方案已停售");
        failureInfo.put(C_SCHEME_OUT_OF_TIME, "该方案已超过截止时间");
        failureInfo.put(C_SCHEME_LEFT_MONEY_NOT_EOUTH, "该方案剩余份额不足");
        failureInfo.put(C_ADD_MONEY_NULL, "充值金额为空");
        failureInfo.put(C_ADD_MONEY_ILLAGE, "充值金额非法");
        failureInfo.put(C_SCHEME_HAS_SELLED, "该方案已经被售卖给客户，不可继续操作");
        failureInfo.put(C_SCHEME_ID_NULL, "方案id为空");
        failureInfo.put(C_NONE_FOR_UPDATE, "没有数据需要更新");
        failureInfo.put(C_SCHEME_BONUS_PAYED_TO_USER, "该方案的奖金已支付给用户，无法继续操作");
        failureInfo.put(C_ORDER_NOT_EXIST, "该订单不存在");
        failureInfo.put(C_SCHEME_MONEY_ERROR, "方案总金额不合法");
        failureInfo.put(C_RECHARGE_FAILED, "充值失败，请稍后重试..");
        failureInfo.put(C_ORDER_ADD_FAILED, "购买失败，请稍后重试..");
        failureInfo.put(C_NONE_OF_CAN_USE_BONUS, "该方案未分配奖金或可用奖金余额不足");
//        failureInfo.put(, );
//        failureInfo.put(, );
//        failureInfo.put(, );
//        failureInfo.put(, );
//        failureInfo.put(, );
//        failureInfo.put(, );
//        failureInfo.put(, );
//        failureInfo.put(, );
//        failureInfo.put(, );
//        failureInfo.put(, );
//        failureInfo.put(, );
//        failureInfo.put(, );
//        failureInfo.put(, );
//        failureInfo.put(, );
//        failureInfo.put(, );
//        failureInfo.put(, );
//        failureInfo.put(, );
//        failureInfo.put(, );
//        failureInfo.put(, );
//        failureInfo.put(, );
//        failureInfo.put(, );
//        failureInfo.put(, );
//        failureInfo.put(, );
//        failureInfo.put(, );
//        failureInfo.put(, );
//        failureInfo.put(, );
//        failureInfo.put(, );
//        failureInfo.put(, );
//        failureInfo.put(, );
//        failureInfo.put(, );
//        failureInfo.put(, );
//        failureInfo.put(, );
//        failureInfo.put(, );
//        failureInfo.put(, );
//        failureInfo.put(, );
//        failureInfo.put(, );
//        failureInfo.put(, );
    }

    public final static String getMsg(String code) {
        if (StrUtils.isEmpty(code)) {
            code = C_SERVER_ERROR;
        }
        return failureInfo.get(code);
    }

    public final static String REGX_PHONE = "1[0-9]{10}";
    public final static String REQUEST_SOURCE_WEB = "1";
    public final static String REQUEST_SOURCE_ANDROID = "2";
    public final static String REQUEST_SOURCE_IOS = "3";
}