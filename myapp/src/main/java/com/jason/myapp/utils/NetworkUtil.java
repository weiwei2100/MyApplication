package com.jason.myapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import com.meiguan.ipsplayer.base.common.board.BoardUtilFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by qiuzi on 15/8/17.
 */
public class NetworkUtil {

    public static final String NETWORK_TYPE_WIFI = "WIFI";
    public static final String NETWORK_TYPE_3G = "3G";
    public static final String NETWORK_TYPE_2G = "2G";
    public static final String NETWORK_TYPE_WAP = "wap";
    public static final String ETHERNET = "Ethernet";
    public static final String NETWORK_TYPE_UNKNOWN = "未知";
    public static final String NETWORK_TYPE_DISCONNECT = "Disconnect";

    /**
     * Get network type
     *
     * @param context
     * @return
     */
    public static String getNetworkType(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        String type;
        if (networkInfo == null || !networkInfo.isConnected()) {
            type = null;
        } else {
            type = networkInfo.getTypeName();
        }
        return type;
    }

    public static String getNetworkOperator(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getNetworkOperatorName();
    }

    public static int getWifiSignalStrength(Context context) {
        int strength = 0;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null) {
            strength = wifiInfo.getRssi();
        }
        return strength;
    }

    public static boolean isInternetConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return (info != null && info.isAvailable());
    }

    public static boolean isHostReachable(String host) {
        if (TextUtils.isEmpty(host)) {
            return false;
        }
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("ping -c 1 -w 100 " + host);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int status = 0;
        try {

            if (p != null) {
                status = p.waitFor();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (status == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get network type name
     *
     * @param context
     * @return
     */
    public static String getNetworkTypeName(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo;
        String type = NETWORK_TYPE_DISCONNECT;
        if (manager == null || (networkInfo = manager.getActiveNetworkInfo()) == null) {
            return type;
        }

        if (networkInfo.isConnected()) {
            String typeName = networkInfo.getTypeName();
            if ("WIFI".equalsIgnoreCase(typeName)) {
                type = NETWORK_TYPE_WIFI;
            } else if ("MOBILE".equalsIgnoreCase(typeName)) {
                String proxyHost = android.net.Proxy.getDefaultHost();
                type = TextUtils.isEmpty(proxyHost) ? (isFastMobileNetwork(context) ? NETWORK_TYPE_3G : NETWORK_TYPE_2G)
                        : NETWORK_TYPE_WAP;
            } else {
                type = ETHERNET;
            }
        }
        String internet = "未连接互联网";
        if (isInternetConnected(context)) {
            internet = "已连接互联网";
        }
        return type + "     " + internet;
    }

    /**
     * Whether is fast mobile network
     *
     * @param context
     * @return
     */
    private static boolean isFastMobileNetwork(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager == null) {
            return false;
        }

        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return false;
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return false;
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return true;
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return true;
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return false;
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return true;
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return true;
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return true;
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return true;
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return true;
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return true;
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return true;
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return false;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return true;
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return false;
            default:
                return false;
        }
    }

    public static final boolean ping() {

        String result = null;
        try {
            String ip = "www.baidu.com";// ping �ĵ�ַ�����Ի����κ�һ�ֿɿ�������
            Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);// ping��ַ3��
            // ��ȡping�����ݣ����Բ���
            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer stringBuffer = new StringBuffer();
            String content = "";
            while ((content = in.readLine()) != null) {
                stringBuffer.append(content);
            }
            Log.d("------ping-----", "result content : " + stringBuffer.toString());
            // ping��״̬
            int status = p.waitFor();
            if (status == 0) {
                result = "success";

                return true;
            } else {
                result = "failed";
            }
        } catch (IOException e) {
            result = "IOException";
        } catch (InterruptedException e) {
            result = "InterruptedException";
        } finally {
            Log.d("----result---", "result = " + result);
        }
        return false;
    }

    /*验证Ip是否合法*/
    public static boolean isIPAddress(String ipaddr) {
        boolean flag = false;
        Pattern pattern = Pattern.compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
        Matcher m = pattern.matcher(ipaddr);
        flag = m.matches();
        return flag;
    }

    /**
     * 1：以太网动态 ip 2:以太网静态 ip
     *
     * @param context
     * @return
     */
    public static int isUsingEthernetStaticIp(Context context) {
        return BoardUtilFactory.getInstance().getBoardUtil().getEthernetIpProperty(context);
    }

}
