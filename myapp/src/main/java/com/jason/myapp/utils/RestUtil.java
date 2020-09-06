package com.jason.myapp.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by qiuzi on 15/6/15.
 */
public class RestUtil {

    private static final RestUtil restUtil = new RestUtil();

    private HttpClient client;

    private RestUtil() {
        client = new DefaultHttpClient();
    }

    public static RestUtil getInstance() {
        return restUtil;
    }

    public String get(String uri) {
        if (!(uri != null && uri.startsWith("http://"))) {
            return "";
        }
        String result = "";
        HttpGet httpGet = new HttpGet(uri);
        BufferedReader br = null;
        try {
            HttpResponse response = client.execute(httpGet);
            br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            result = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}
