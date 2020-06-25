package com.cilys.lottery.web.utils;

import com.cily.utils.base.log.Logs;

import java.math.BigDecimal;

/**
 * Created by admin on 2020/6/23.
 */
public class BigDecimalUtils {

    public static BigDecimal toBigDecimal(String str){
        return toBigDecimal(str, false);
    }

    public static BigDecimal toBigDecimal(String str, boolean sourceResult){
        try{
            BigDecimal b = new BigDecimal(str);
            if (sourceResult){

            }else {
                b = setScale(b);
            }
            return b;
        }catch (Exception e){
            Logs.printException(e);
            return null;
        }
    }
    public static BigDecimal setScale(BigDecimal b){
        return setScale(b, 2, BigDecimal.ROUND_DOWN);
    }

    public static BigDecimal setScale(BigDecimal b, int newScale, int round){
        if (b == null){
            return b;
        }
        b = b.setScale(newScale, round);
        return b;
    }

    public static BigDecimal toBigDecimal(int num){
        return toBigDecimal(num, false);
    }

    public static BigDecimal toBigDecimal(int num, boolean sourceResult){
        return toBigDecimal(String.valueOf(num), sourceResult);
    }

    public static BigDecimal zero() {
        return toBigDecimal("0.00");
    }

    public static BigDecimal add(BigDecimal b1, BigDecimal b2){
        return add(b1, b2, false);
    }

    public static BigDecimal add(BigDecimal b1, BigDecimal b2, boolean sourceResult){
        BigDecimal b = b1.add(b2);
        if (sourceResult){

        }else {
            b = setScale(b);
        }
        return b;
    }

    public static BigDecimal subtract(BigDecimal b1, BigDecimal b2){
        return subtract(b1, b2, false);
    }

    public static BigDecimal subtract(BigDecimal b1, BigDecimal b2, boolean sourceResult){
        BigDecimal b = b1.subtract(b2);
        if (sourceResult){

        }else {
            b = setScale(b);
        }
        return b;
    }

    public static BigDecimal multiply(BigDecimal b1, BigDecimal b2){
        return multiply(b1, b2, false);
    }

    public static BigDecimal multiply(BigDecimal b1, BigDecimal b2, boolean sourceResult){
        BigDecimal b = b1.multiply(b2);
        if (sourceResult){

        }else {
            b = setScale(b);
        }
        return b;
    }

    public static BigDecimal divide(BigDecimal b1, BigDecimal b2){
        return divide(b1, b2, false);
    }

    public static BigDecimal divide(BigDecimal b1, BigDecimal b2, boolean sourceResult){
        if (equal(b2, zero())){
            return b1;
        }

        BigDecimal b = b1.divide(b2);
        if (sourceResult){

        }else {
            b = setScale(b);
        }
        return b;
    }

    public static boolean equal(BigDecimal b1, BigDecimal b2){
        if (b1 == null || b2 == null){
            return false;
        }
        return b1.compareTo(b2) == 0;
    }

    public static boolean moreThan(BigDecimal b1, BigDecimal b2){
        if (b1 == null || b2 == null){
            return false;
        }
        return b1.compareTo(b2) == 1;
    }
    public static boolean lessThan(BigDecimal b1, BigDecimal b2){
        if (b1 == null || b2 == null){
            return false;
        }
        return b1.compareTo(b2) == -1;
    }

    public static boolean noMoreThan(BigDecimal b1, BigDecimal b2){
        return lessThan(b1, b2) || equal(b1, b2);
    }

    public static boolean noLessThan(BigDecimal b1, BigDecimal b2){
        return moreThan(b1, b2) || equal(b1, b2);
    }
}
