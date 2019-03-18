package com.bcb.util;

import com.alibaba.fastjson.JSONObject;
import com.bcb.bean.SystemProperties;
import com.bcb.helper.market.BbexHelper;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.jdbc.Expectation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

@Slf4j
public class CommonUtil {

    /**
     * @author qiang wen
     * @description   获取最新 bcb2usdx 的价格
     */

    public static BigDecimal getCurrentBcb2UsdxPrice(){
        Map<String,String> map = BbexHelper.niuXEthBcBMapValue("BCB");
        String bcbUsdxPrice = map.get("BCB_USDX");//获取BCB_USDX最新价格
        return new BigDecimal(bcbUsdxPrice);
    }

    //执行打币操作
    public static String sendBcB(String toAddress, BigDecimal amount, String url) throws Exception {
        try{
            String appid = "DTSYSTEM";
            String appkey = "SK2006TOERL17TENYEA";
            JSONObject obj = new JSONObject();
            obj.put("appid", appid);
            obj.put("timestamp", System.currentTimeMillis());
            obj.put("toAddress", toAddress);
            obj.put("coinType", "3");
            obj.put("amount", amount);
            HttpPostUtil.gainPostJson(appkey, obj);
            String response = HttpPostUtil.httpPostWithJSON(url + "midware/trans/bcb/trans", obj.toString());
            log.debug("中间件返回消息："+response);
            return response;
        }catch (Exception e){
            log.error("中间件调用失败：", e);
            throw new Exception("中间件调用失败：", e);
        }
    }

    //获取两个时间之间相差的天数
    public static Integer surplusDay(Object recordCreateTime,Object expireTime) throws Exception {
        try{
            if(ObjectUtils.isEmpty(recordCreateTime) || ObjectUtils.isEmpty(expireTime)){
                throw new Exception("时间格式不正确");
            }
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            double dateTime = (formatter.parse(expireTime.toString()).getTime() - formatter.parse(recordCreateTime.toString()).getTime());
            return  (int)(dateTime / (1000*3600*24));
        }catch (Exception e){
            log.error("时间格式不正确");
            throw new Exception("时间格式不正确");
        }
    }


    //UTC时间转化为本地时间
    public static Date utcToLocal(Timestamp utcTime){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeStr = sdf.format(utcTime);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date utcDate = null;
        try {
            utcDate = sdf.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf.setTimeZone(TimeZone.getDefault());
        Date locatlDate = null;
        String localTime = sdf.format(utcDate.getTime());
        try {
            locatlDate = sdf.parse(localTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return locatlDate;
    }

}
