package com.jason.myapp.utils;

import android.util.Log;
import android.webkit.JavascriptInterface;
import com.alibaba.fastjson.JSON;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lizhiqi on 15/2/10.
 */
public class HttpSendUtil {

    @JavascriptInterface
    public String sendGetRequest(String endpoint, Map<String, String> params) {

//        Log.i("endpoint is : ",endpoint);
        if (!(endpoint != null && endpoint.startsWith("http://"))) {
            return "";
        }
        String requestUrl = buildGetParams(endpoint, params);
        String result = getRequest(requestUrl);
        return result;
    }

    @JavascriptInterface
    public String sendPostRequest(String endpoint, String params) {

        if (!(endpoint != null && endpoint.startsWith("http://"))) {
            return "";
        }

        Map<String, String> paramMap = JSON.parseObject(params, Map.class);

        StringBuffer sb = new StringBuffer("");
        for (Map.Entry<String, String> it : paramMap.entrySet()) {
            if (!sb.toString().equals("")) {
                sb.append("&");
            }
            sb.append(it.getKey()).append("=").append(it.getValue());

        }

        String result = getPostRequest(endpoint, sb.toString());
        return result;
    }


    private String getPostRequest(String requestUrl, String params) {
        String result = "";
        PrintWriter out = null;
        BufferedReader br = null;
        try {

            URL url = new URL(requestUrl);
            URLConnection connection = url.openConnection();

            connection.setDoOutput(true);
            connection.setDoOutput(true);

            connection.setConnectTimeout(10000);

            OutputStream os = connection.getOutputStream();
            out = new PrintWriter(os);
            out.print(params);
            out.flush();

            br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            result = sb.toString();
        } catch (MalformedURLException e) {
            Log.e("create request url error : ", e.getMessage());
        } catch (IOException e) {
            Log.e("create request url connection error : ", e.getMessage());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                out.close();
            }
        }
//        Log.i("response result is :",result);

        return result;
    }

    private String getRequest(String requestUrl) {
        String result = "";
        try {

            URL url = new URL(requestUrl);
            URLConnection connection = url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            result = sb.toString();
        } catch (MalformedURLException e) {
//            Log.e("create request url error : ", e.getMessage());
        } catch (IOException e) {
//            Log.e("create request url connection error : ",e.getMessage());
        }
//        Log.i("response result is :",result);

        return result;
    }

    private String buildGetParams(String endpoint, Map<String, String> params) {
        String paramUrls = "";
        if (params != null) {
            for (Map.Entry<String, String> it : params.entrySet()) {
                paramUrls += it.getKey() + "=" + it.getValue() + "&";
            }
            if (paramUrls != "") {
                paramUrls = paramUrls.substring(0, paramUrls.length() - 1);
            }
        }

        String requestUrl = endpoint;

        if (paramUrls != null && paramUrls != "") {
            requestUrl += "?" + paramUrls;
        }
        return requestUrl;
    }

    public static void main(String[] args) {
        HttpSendUtil sendUtil = new HttpSendUtil();
        Map<String, String> map = new HashMap<String, String>();
        map.put("access", "1");
        System.out.println(sendUtil.sendGetRequest("http://www.baidu.com", map));
    }

}
