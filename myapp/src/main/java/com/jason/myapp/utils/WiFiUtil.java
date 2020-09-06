package com.jason.myapp.utils;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import com.ips.system.control.model.NetworkConfig;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by micrown on 17/3/4.
 */
public class WiFiUtil {

    private String TAG = "WiFiUtil";
    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;

    private static final int WIFICIPHER_NOPASS = 0;
    private static final int WIFICIPHER_WEP = 1;
    private static final int WIFICIPHER_WPA = 2;

    private static final int WIFI_NEED_PASSWORD = 0;
    private static final int WIFI_NO_PASSWORD = 1;
    private static final int WIFI_NOT_CONNECTED = 2;

    public static final String WIFI_AUTH_OPEN = "";
    public static final String WIFI_AUTH_ROAM = "[ESS]";

    public WiFiUtil(Context context) {
        // 取得WifiManager对象
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    // 打开WIFI
    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    // 关闭WIFI
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    /**
     * 判断Wifi高级设置是静态IP配置方式还是DHCP配置方式
     *
     * @return
     */
    public int getWifiSetting() {
        DhcpInfo dhcpInfo = mWifiManager.getDhcpInfo();
        if (dhcpInfo.leaseDuration == 0) {//静态IP配置方式
            Log.d(TAG, "静态IP配置方式");
            return NetworkConfig.IP_SETTINGS_STATIC;
        } else {
            Log.d(TAG, "动态IP配置方式");//动态IP配置方式
            return NetworkConfig.IP_SETTINGS_DHCP;
        }
    }

    /**
     * 获取wifi信息
     *
     * @return
     * @throws Exception
     */
    public NetworkConfig getWifiInfo() throws Exception {
        DhcpInfo dhcpInfo = mWifiManager.getDhcpInfo();

        NetworkConfig networkConfig = new NetworkConfig();
        networkConfig.setIpAddress(intToIp(dhcpInfo.ipAddress));
        networkConfig.setNetmask(intToIp(dhcpInfo.netmask));
        networkConfig.setGateway(intToIp(dhcpInfo.gateway));
        networkConfig.setDns1(intToIp(dhcpInfo.dns1));
        networkConfig.setDns2(intToIp(dhcpInfo.dns2));
        return networkConfig;
    }

    private String intToIp(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
                + (0xFF & paramInt >> 24);
    }

    /**
     * 设置wifi信息的方法
     * 静态：STATIC  自动获取：DHCP
     */
    public boolean setStaticIp(String setting, NetworkConfig networkConfig) {
        WifiConfiguration wifiConfig = null;
        WifiInfo connectionInfo = mWifiManager.getConnectionInfo(); //得到连接的wifi网络
        List<WifiConfiguration> configuredNetworks = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration conf : configuredNetworks) {

            if (conf.networkId == connectionInfo.getNetworkId()) {
                wifiConfig = conf;
                break;
            }
        }
        // 如果是android3.x版本及以上的话
        try {
            setIpAssignment(setting, wifiConfig);
            setIpAddress(InetAddress.getByName(networkConfig.getIpAddress()), 24, wifiConfig);
            setGateway(InetAddress.getByName(networkConfig.getGateway()), wifiConfig);
            setDNS(InetAddress.getByName(networkConfig.getDns1()), wifiConfig);
            setDNS(InetAddress.getByName(networkConfig.getDns2()), wifiConfig);
            mWifiManager.updateNetwork(wifiConfig); // apply the setting
            Log.d(TAG, setting + "ip设置成功！");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, setting + "ip设置失败！");
            return false;

        }

    }

    private static void setIpAssignment(String assign, WifiConfiguration wifiConf)
            throws SecurityException, IllegalArgumentException,
            NoSuchFieldException, IllegalAccessException {

        setEnumField(wifiConf, assign, "ipAssignment");
    }

    private static void setIpAddress(InetAddress addr, int prefixLength, WifiConfiguration wifiConf) throws Exception {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if (linkProperties == null) {
            return;
        }
        Class<?> laClass = Class.forName("android.net.LinkAddress");

        Constructor<?> laConstructor = laClass.getConstructor(new Class[]{
                InetAddress.class, int.class});

        Object linkAddress = laConstructor.newInstance(addr, prefixLength);
        ArrayList<Object> mLinkAddresses = (ArrayList<Object>) getDeclaredField(
                linkProperties, "mLinkAddresses");

        mLinkAddresses.clear();
        mLinkAddresses.add(linkAddress);

    }

    private static Object getField(Object obj, String name)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {

        Field f = obj.getClass().getField(name);
        Object out = f.get(obj);

        return out;
    }

    private static Object getDeclaredField(Object obj, String name)

            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getDeclaredField(name);

        f.setAccessible(true);
        Object out = f.get(obj);
        return out;

    }

    @SuppressWarnings({"unchecked", "rawtypes"})

    private static void setEnumField(Object obj, String value, String name)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getField(name);
        f.set(obj, Enum.valueOf((Class<Enum>) f.getType(), value));
    }


    private static void setGateway(InetAddress gateway, WifiConfiguration wifiConf) throws Exception {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if (linkProperties == null) {
            return;
        }
        if (android.os.Build.VERSION.SDK_INT >= 14) { // android4.x版本
            Class<?> routeInfoClass = Class.forName("android.net.RouteInfo");

            Constructor<?> routeInfoConstructor = routeInfoClass
                    .getConstructor(new Class[]{InetAddress.class});

            Object routeInfo = routeInfoConstructor.newInstance(gateway);

            ArrayList<Object> mRoutes = (ArrayList<Object>) getDeclaredField(

                    linkProperties, "mRoutes");
            mRoutes.clear();
            mRoutes.add(routeInfo);
        } else { // android3.x版本
            ArrayList<InetAddress> mGateways = (ArrayList<InetAddress>) getDeclaredField(
                    linkProperties, "mGateways");
            mGateways.clear();
            mGateways.add(gateway);

        }
    }

    private static void setDNS(InetAddress dns, WifiConfiguration wifiConf) throws Exception {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if (linkProperties == null) {
            return;
        }
        ArrayList<InetAddress> mDnses = (ArrayList<InetAddress>)
                getDeclaredField(linkProperties, "mDnses");
        mDnses.clear(); // 清除原有DNS设置（如果只想增加，不想清除，词句可省略）
        mDnses.add(dns);
    }


    public WifiConfiguration createWifiConfig(String ssid, String password, int type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + ssid + "\"";

        WifiConfiguration tempConfig = isExsits(ssid);
        if (tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }

        if (type == WIFICIPHER_NOPASS) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        } else if (type == WIFICIPHER_WEP) {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == WIFICIPHER_WPA) {
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }

        return config;
    }

    private WifiConfiguration isExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }

        }
        return null;
    }

    public boolean addNetWork(WifiConfiguration wifiConfiguration) {
        int wcgID = mWifiManager.addNetwork(wifiConfiguration);
        Log.d(TAG, wcgID + "true");
        mWifiManager.enableNetwork(wcgID, true);
        mWifiManager.saveConfiguration();
        boolean reconnect = mWifiManager.reconnect();
        return reconnect;

    }

    //得到连接的名称SSID
    public String getConnectedSSID() {
        return (mWifiInfo == null) ? "null" : mWifiInfo.getSSID();
    }

    //得到连接的IP地址
    public int getConnectedIPAddr() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    //得到连接的ID
    public int getConnectedID() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    /**
     * 检测wifi是否需要密码和wifi是否连接
     *
     * @param context
     * @return
     */
    public int checkWifiPassword(Context context) {
        WifiInfo wifiInfo = null;
        WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        try {
            mWifiManager.startScan();
            // 得到当前连接的wifi热点的信息
            wifiInfo = mWifiManager.getConnectionInfo();
        } catch (SecurityException e) {
            return WIFI_NEED_PASSWORD;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (wifiInfo == null) {
            Log.i(TAG, "wifi not connected");
            return WIFI_NOT_CONNECTED;
        }

        String ssid = wifiInfo.getSSID();
        if (ssid == null) {
            return WIFI_NOT_CONNECTED;
        } else if (ssid.length() <= 2) {
            return WIFI_NOT_CONNECTED;
        }
        if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
            ssid = ssid.substring(1, ssid.length() - 1);
        }

        List<ScanResult> mWifiList = null;
        try {
            // 得到扫描结果
            mWifiList = mWifiManager.getScanResults();
        } catch (SecurityException e) {
            return WIFI_NEED_PASSWORD;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mWifiList != null && mWifiList.size() > 0) {
            try {
                for (ScanResult sr : mWifiList) {
                    if (sr.SSID.equals(ssid) && sr.BSSID.equals(wifiInfo.getBSSID())) {
                        if (sr.capabilities != null) {
                            String capabilities = sr.capabilities.trim();
                            if (capabilities != null && (capabilities.equals(WIFI_AUTH_OPEN) || capabilities.equals(WIFI_AUTH_ROAM))) {
                                return WIFI_NO_PASSWORD;
                            }
                        }
                    }
                }
            } catch (Throwable e) {
                return WIFI_NEED_PASSWORD;
            }
        }

        return WIFI_NEED_PASSWORD;
    }


    /**
     * 获取密码 不为空，返回密码，""空为无密码
     *
     * @param wifiname
     * @return
     * @throws Exception
     */
    public String getPSW(String wifiname) throws Exception {

        String PSW = null;
        Process process = null;
        DataOutputStream dataOutputStream = null;
        DataInputStream dataInputStream = null;
        StringBuffer wifiPSW = new StringBuffer();

        try {

            process = Runtime.getRuntime().exec("su");

            dataOutputStream = new DataOutputStream(process.getOutputStream());

            dataInputStream = new DataInputStream(process.getInputStream());

            dataOutputStream.writeBytes("cat /data/misc/wifi/wpa_supplicant.conf\n");

            dataOutputStream.writeBytes("exit\n");

            dataOutputStream.flush();

            InputStreamReader inputStreamReader = new InputStreamReader(dataInputStream, "UTF-8");

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
                wifiPSW.append(line);
            }

            bufferedReader.close();
            inputStreamReader.close();
            process.waitFor();

        } catch (Exception e) {
            Log.e(TAG, "not root");
            throw e;

        } finally {

            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }

                if (dataInputStream != null) {
                    dataInputStream.close();
                }
                process.destroy();

            } catch (Exception e) {
                throw e;

            }

        }

        /*匹配password*/
        Log.d(TAG, "PATTERN the password:" + wifiPSW);
        Pattern network = Pattern.compile("network=\\{([^\\}]+)\\}", Pattern.DOTALL);
        Matcher networkMatcher = network.matcher(wifiPSW.toString());

        while (networkMatcher.find()) {

            String networkBlock = networkMatcher.group();

            Pattern ssid = Pattern.compile("ssid=\"" + wifiname + "\"");

            Matcher ssidMatcher = ssid.matcher(networkBlock);

            if (ssidMatcher.find()) {

                Pattern psk = Pattern.compile("psk=\"([^\"]+)\"");

                Matcher pskMatcher = psk.matcher(networkBlock);

                if (pskMatcher.find()) {
                    PSW = pskMatcher.group(1);
                } else {
                    PSW = "";//无密码
                }

            } else {
                Log.d(TAG, "do not find the ssid");
            }

        }
        Log.d(TAG, PSW);
        return PSW;
    }

    /**
     * These values are matched in string arrays -- changes must be kept in sync
     */
    static final int SECURITY_NONE = 0;
    static final int SECURITY_WEP = 1;
    static final int SECURITY_PSK = 2;
    static final int SECURITY_EAP = 3;

    private int getSecurity(WifiConfiguration config) {
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
            return SECURITY_PSK;
        }
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP) || config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X)) {
            return SECURITY_EAP;
        }
        return (config.wepKeys[0] != null) ? SECURITY_WEP : SECURITY_NONE;
    }


    /**
     * 获取热点的加密类型,此方法获取不到隐藏的网络加密方式
     *
     * @param ssid
     */
    public Integer getCipherType(String ssid) {
        Integer type = null;
        List<ScanResult> list = mWifiManager.getScanResults();

        for (ScanResult scResult : list) {

            if (!TextUtils.isEmpty(scResult.SSID) && scResult.SSID.equals(ssid)) {
                String capabilities = scResult.capabilities;
                Log.i(TAG, "capabilities=" + capabilities);

                if (!TextUtils.isEmpty(capabilities)) {

                    if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
                        Log.d(TAG, "wpa");
                        type = WIFICIPHER_WPA;

                    } else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
                        Log.d(TAG, "wep");
                        type = WIFICIPHER_WEP;
                    } else {
                        type = WIFICIPHER_NOPASS;
                        Log.d(TAG, "no");
                    }
                }
            }
        }
        return type;
    }

    /**
     * 获取热点的加密类型,可获取到隐藏网络的加密方式
     *
     * @param ssid
     * @return
     */
    public Integer getType(String ssid) {
        Integer type = null;
        // 得到配置好的网络连接
        List<WifiConfiguration> wifiConfigList = mWifiManager.getConfiguredNetworks();

        for (WifiConfiguration wifiConfiguration : wifiConfigList) {

            //配置过的SSID
            String configSSid = wifiConfiguration.SSID;
            configSSid = configSSid.replace("\"", "");

            //当前连接SSID
            String currentSSid = mWifiInfo.getSSID();
            currentSSid = currentSSid.replace("\"", "");

            //比较networkId，防止配置网络保存相同的SSID
            if (currentSSid.equals(configSSid) && mWifiInfo.getNetworkId() == wifiConfiguration.networkId) {
                if (ssid.equals(currentSSid)) {
                    type = getSecurity(wifiConfiguration);
                    break;
                }
            }
        }
        return type;
    }
}
