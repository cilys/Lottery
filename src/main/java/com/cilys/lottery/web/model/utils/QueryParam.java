package com.cilys.lottery.web.model.utils;

/**
 * Created by admin on 2020/6/22.
 */
public class QueryParam {
    private StringBuilder su;

    public QueryParam(){
        su = new StringBuilder();
    }

    public StringBuilder equal(String key, String value){
        su.append(" ");
        su.append(key);
        su.append(" = '");
        su.append(value);
        su.append("'");
        su.append(" ");
        return su;
    }

    public StringBuilder equal(String key, int value){
        su.append(" ");
        su.append(key);
        su.append(" = ");
        su.append(value);
        su.append("");
        su.append(" ");
        return su;
    }

    public StringBuilder like(String key, String like){
        su.append(" ");
        su.append(key);
        su.append(" like '%");
        su.append(like);
        su.append("%'");
        su.append(" ");
        return su;
    }

    public StringBuilder append(String str){
        su.append(str);
        return su;
    }

    public StringBuilder and(){
        su.append(" and ");
        return su;
    }

    public StringBuilder or(){
        su.append(" or ");
        return su;
    }

    public String string(){
        return su.toString();
    }
}
