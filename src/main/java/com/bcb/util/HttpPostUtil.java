package com.bcb.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by kx on 2018/1/20.
 */
public class HttpPostUtil {
    public static JSONObject gainPostJson(String appkey,JSONObject paramObj) {
        List<String> keys = new ArrayList<>(paramObj.keySet());
        Collections.sort(keys);
        StringBuilder sb = new StringBuilder();
        sb.append(appkey);
        for(String key:keys) {
            sb.append(key+"="+paramObj.getString(key));
        }
        sb.append(appkey);
        paramObj.put("sign",MD5Util.string2MD5(MD5Util.string2MD5(sb.toString()).toUpperCase()).toUpperCase());
        return paramObj;
    }
    public static String httpPostWithJSON(String url, String param)
            throws Exception {
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpClient client = HttpClients.createDefault();
        String respContent = null;
        StringEntity entity = new StringEntity(param, "utf-8");
        entity.setContentEncoding("UTF-8");
        httpPost.setEntity(entity);
        HttpResponse resp = client.execute(httpPost);
        if (resp.getStatusLine().getStatusCode() == 200) {
            HttpEntity httpEntity = resp.getEntity();
            respContent = EntityUtils.toString(httpEntity, "UTF-8");
        }
        return respContent;
    }

}
