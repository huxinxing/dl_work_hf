package com.bcb.util;

/**
 * Created by kx on 2018/1/28.
 */
public class StringValueUtil {

    public static String retVal(String value) {
        if(!value.contains("."))
            return value;
        String _value = new StringBuffer(value).reverse().toString();
        String _v = _value.substring(0,_value.indexOf("."));
        if(_v.length()>0) {
            boolean hasNumber=false;
            for(int i=0;i<=_v.length()-1;i++) {
                if(Integer.valueOf(String.valueOf(_v.charAt(i))).compareTo(0)>0){
                    hasNumber=true;
                }
            }
            if(!hasNumber) {
                String _fv = _value.substring(_value.indexOf(".")+1);
                return new StringBuilder(_fv).reverse().toString();
            }
        }
        StringBuffer fval = new StringBuffer();
        boolean stop =false;
        for (int i =0;i<=_value.length() - 1;i++) {
            if(_value.charAt(i)=='0'||_value.charAt(i)=='.'){
            }else {
                stop =true;
            }
            if(stop) {
                fval.append(_value.charAt(i));
            }
        }
        if(fval.length()==0) return "0";
        return fval.reverse().toString();
    }

    public static String gainTargetTypeDesc(String targetType) {
        String desc = "";
        switch (targetType) {
            case "1":
                desc="BTC";
                break;
            case "2":
                desc ="LTC";
                break;
            case "3":
                desc = "ETH";
                break;
        }
        return desc;
    }
    public static String gainStatusDesc(Integer status) {
        String desc ="";
        switch (status){
            case 0:
                desc="初始状态";
                break;
            case 1:
                desc="审核通过";
                break;
            case -1:
                desc="审核不通过";
                break;
            case 2:
                desc="待发布";
                break;
            case 10:
                desc = "已公开";
                break;
            case 20:
                desc = "募集开始";
                break;
            case 30:
                desc = "募集结束";
                break;
            case 40:
                desc = "已关闭";
                break;
        }
        return desc;
    }

    public static String gainTradeStatusDesc(Integer status) {
        String desc = "";
        switch(status){
            case 0:
                desc = "代投已确认";
                break;
            case 1:
                desc = "代投已成功";
                break;
            case 2:
                desc = "代币已转账";
                break;
            case -1:
                desc = "代投已失败";
                break;
        }
        return desc;
    }

    public static String activeStatusDesc(Integer status){
        String desc= "";
        switch(status){
            case 0:
                desc = "初始状态";
                break;
            case 1:
                desc = "上线";
                break;
            case 2:
                desc = "结束";
                break;
            case -1:
                desc = "下线";
                break;
        }
        return desc;
    }
}
