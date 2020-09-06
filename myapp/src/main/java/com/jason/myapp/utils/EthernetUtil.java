package com.jason.myapp.utils;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ips.system.control.model.NetworkConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunjianchao on 17/3/4.
 */
public class EthernetUtil {

    private String TAG = "EthernetUtil";
    private static EthernetUtil ethernetUtil = new EthernetUtil();

    public static EthernetUtil getInstance() {

        return ethernetUtil;
    }

    /**
     * 设置静态ip 子网掩码 网关 dns
     * settings put system ethernet_static_ip 192.168.1.222    ip
     * settings put system ethernet_static_netmask 192.168.0.1  子网掩码
     * settings put system ethernet_static_gateway 255.255.255.0  默认网关
     * settings put system ethernet_static_dns1 192.168.1.222
     * <p>
     * ethernet_static_ip
     * ethernet_static_gateway
     * ethernet_static_netmask
     * ethernet_static_dns1
     * ethernet_static_dns2
     */

    public void setEthernetStaticIP(Context context, String ip, String netMask, String gateway,
                                    String dns1, String dns2) throws Exception {
//        ShellUtils.execCommand("settings put system ethernet_static_ip " + ip, true);
//        ShellUtils.execCommand("settings put system ethernet_static_netmask " + netMask, true);
//        ShellUtils.execCommand("settings put system ethernet_static_gateway " + gateway, true);
//        ShellUtils.execCommand("settings put system ethernet_static_dns1 " + dns1, true);
//        ShellUtils.execCommand("settings put system ethernet_static_dns2 " + dns2, true);

        if (TextUtils.isEmpty(ip) || !NetworkUtil.isIPAddress(ip)) {
            throw new RuntimeException("您的ip格式不正确,请立即检查");

        }
        if (TextUtils.isEmpty(netMask) || !NetworkUtil.isIPAddress(netMask)) {
            throw new RuntimeException("您的子网掩码格式不正确,请立即检查");

        }
        if (TextUtils.isEmpty(gateway) || !NetworkUtil.isIPAddress(gateway)) {
            throw new RuntimeException("您的默认网关格式不正确,请立即检查");

        }
        if (TextUtils.isEmpty(dns1) || !NetworkUtil.isIPAddress(dns1)) {
            throw new RuntimeException("您的dns1格式不正确,请立即检查");

        }
        Settings.System.putString(context.getContentResolver(), "ethernet_static_ip", ip);
        Settings.System.putString(context.getContentResolver(), "ethernet_static_netmask", netMask);
        Settings.System.putString(context.getContentResolver(), "ethernet_static_gateway", gateway);
        Settings.System.putString(context.getContentResolver(), "ethernet_static_dns1", dns1);
        Settings.System.putString(context.getContentResolver(), "ethernet_static_dns2", dns2);

    }

    /**
     * 打开以太网
     *
     * @param context
     */
    public void openEthernet(Context context) throws Exception {

        setEthernet(context, true);
    }

    /**
     * 关闭以太网
     *
     * @param context
     */
    public void closeEthernet(Context context) throws Exception {

        setEthernet(context, false);

    }

    private void setEthernet(Context context, boolean flag) throws Exception {

        //获取ETHERNET_SERVICE参数
        String ETHERNET_SERVICE = (String) Context.class.getField("ETHERNET_SERVICE").get(null);

        Class<?> ethernetManagerClass = Class.forName("android.net.ethernet.EthernetManager");

        //获取ethernetManager服务对象
        Object ethernetManager = context.getSystemService(ETHERNET_SERVICE);

        Method ethernetManagerClassMethod = ethernetManagerClass.getMethod("setEthernetEnabled", getParamTypes(ethernetManagerClass, "setEthernetEnabled"));
        Object[] params = new Object[1];
        if (flag) {
            params[0] = Boolean.TRUE;
        } else {
            params[0] = Boolean.FALSE;
        }

        Object object = ethernetManagerClassMethod.invoke(ethernetManager, params);
        Log.e("etherNet", "close object------ : " + object.toString());

    }

    /**
     * 打开以太网静态ip
     * settings put system ethernet_use_static_ip 1
     */
    public void openEthernetStaticIP(Context context) throws Exception {

        Settings.System.putString(context.getContentResolver(), "ethernet_use_static_ip", "1");

    }

    /**
     * 关闭以太网静态ip
     * settings put system ethernet_use_static_ip 0
     */
    public void closeEthernetStaticIP(Context context) throws Exception {

        Settings.System.putString(context.getContentResolver(), "ethernet_use_static_ip", "0");

    }

    private Class[] getParamTypes(Class clz, String methodName) {
        Class[] classes = null;
        Method[] methods = clz.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                classes = method.getParameterTypes();
            }
        }
        return classes;
    }

    /**
     * 获取静态以太网设置
     *
     * @param context
     * @throws Exception
     */
    public NetworkConfig getEthernetStaticInfo(Context context) throws Exception {
        String ethernetStaticIP = Settings.System.getString(context.getContentResolver(), "ethernet_static_ip");
        String ethernetStaticNetmask = Settings.System.getString(context.getContentResolver(), "ethernet_static_netmask");
        String ethernetStaticGateway = Settings.System.getString(context.getContentResolver(), "ethernet_static_gateway");
        String ethernetStaticDns1 = Settings.System.getString(context.getContentResolver(), "ethernet_static_dns1");
        String ethernetStaticDns2 = Settings.System.getString(context.getContentResolver(), "ethernet_static_dns2");

        NetworkConfig networkConfig = new NetworkConfig();
        networkConfig.setIpAddress(ethernetStaticIP);
        networkConfig.setNetmask(ethernetStaticNetmask);
        networkConfig.setGateway(ethernetStaticGateway);
        networkConfig.setDns1(ethernetStaticDns1);
        networkConfig.setDns2(ethernetStaticDns2);
        return networkConfig;
    }

    /**
     * 以太网获取方式 1：静态 0：动态
     * 实际返回 1：动态 2：静态
     *
     * @param context
     * @return
     */
    public int getEthernetSetting(Context context) {
        int useStaticIp = Settings.System.getInt(context.getContentResolver(), "ethernet_use_static_ip", 0);
        Log.d(TAG, "以太网获取方式：" + useStaticIp);
        if (useStaticIp == 0) {
            useStaticIp = 1;
        } else {
            useStaticIp = 2;
        }
        return useStaticIp;
    }

    /**
     * android7.0获取静态IP、网关、子网掩码、DNS
     *
     * @param context
     * @return
     */
    public Map<String, String> getIps(Context context) {
        Map<String, String> ipMaps = new HashMap<String, String>();
        try {
            // 获取ETHERNET_SERVICE参数
            String ETHERNET_SERVICE = (String) Context.class.getField("ETHERNET_SERVICE").get(null);
            Class<?> ethernetManagerClass = Class.forName("android.net.EthernetManager");
            // 获取ethernetManager服务对象
            Object ethernetManager = context.getSystemService(ETHERNET_SERVICE);
            // 获取在EthernetManager中的抽象类mService成员变量
            Field mService = ethernetManagerClass.getDeclaredField("mService");
            // 设置访问权限
            mService.setAccessible(true);
            // 获取抽象类的实例化对象
            Object mServiceObject = mService.get(ethernetManager);
            Class<?> iEthernetManagerClass = Class.forName("android.net.IEthernetManager");
            Method[] methods = iEthernetManagerClass.getDeclaredMethods();

            for (Method ms : methods) {
                String methodName = ms.getName();
                if ("getGateway".equals(methodName)) {   // 网关
                    String gate = (String) ms.invoke(mServiceObject);
                    ipMaps.put("gateWay", gate);
                } else if ("getNetmask".equals(methodName)) {  // 子网掩码
                    String mask = (String) ms.invoke(mServiceObject);
                    ipMaps.put("maskAddress", mask);
                } else if ("getIpAddress".equals(methodName)) {  // IP地址
                    String ipAddr = (String) ms.invoke(mServiceObject);
                    ipMaps.put("ipAddress", ipAddr);
                } else if ("getDns".equals(methodName)) {  // DNS(注意解析)
                    String dnss = (String) ms.invoke(mServiceObject);
                    String[] arrDns = dnss.split("\\,");
                    String dns = null;
                    if (arrDns != null) {
                        dns = arrDns[0];
                        ipMaps.put("dns", dns);
                    }
                } else if ("getConfiguration".equals(methodName)) {
                    Object invoke = ms.invoke(mServiceObject);
                    JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(invoke));
                    String ipAssignment = jsonObject.getString("ipAssignment");
                    ipMaps.put("ipAssignment", ipAssignment);
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception : ", e);
        }
        return ipMaps;
    }

    /**
     * A83获取dns
     *
     * @return
     */
    public String getA83LocalDns() {
        Process cmdProcess = null;
        BufferedReader reader = null;
        String dnsIP = "";
        try {
            cmdProcess = Runtime.getRuntime().exec("getprop net.dns1");
            reader = new BufferedReader(new InputStreamReader(cmdProcess.getInputStream()));
            dnsIP = reader.readLine();
            return dnsIP;
        } catch (IOException e) {
            return null;
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
            }
            cmdProcess.destroy();
        }
    }

    /**
     * A83获取子网掩码
     *
     * @return
     */
    public String getA83LocalMask() {
        Process cmdProcess = null;
        BufferedReader reader = null;
        String dnsIP = "";
        try {
            cmdProcess = Runtime.getRuntime().exec("ifconfig eth0");
            reader = new BufferedReader(new InputStreamReader(cmdProcess.getInputStream()));
            dnsIP = reader.readLine();
            return dnsIP;
        } catch (IOException e) {
            return null;
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
            }
            cmdProcess.destroy();
        }
    }


    /**
     * 获取子网掩码
     *
     * @param name
     * @return
     */
    public String getLocalMask(String name) {
        Process cmdProcess = null;
        BufferedReader reader = null;
        String dnsIP = "";
        try {
            cmdProcess = Runtime.getRuntime().exec("getprop dhcp." + name + ".mask");
            reader = new BufferedReader(new InputStreamReader(cmdProcess.getInputStream()));
            dnsIP = reader.readLine();
            return dnsIP;
        } catch (IOException e) {
            return null;
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
            }
            cmdProcess.destroy();
        }
    }

    /**
     * 获取网关地址
     *
     * @param name
     * @return
     */
    public String getLocalGATE(String name) {
        Process cmdProcess = null;
        BufferedReader reader = null;
        String dnsIP = "";
        try {
            cmdProcess = Runtime.getRuntime().exec("getprop dhcp." + name + ".gateway");
            reader = new BufferedReader(new InputStreamReader(cmdProcess.getInputStream()));
            dnsIP = reader.readLine();
            return dnsIP;
        } catch (IOException e) {
            return null;
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
            }
            cmdProcess.destroy();
        }
    }

    /**
     * 获取DNS地址
     *
     * @param name
     * @return
     */
    public String getLocalDNS(String name) {
        Process cmdProcess = null;
        BufferedReader reader = null;
        String dnsIP = "";
        try {
            cmdProcess = Runtime.getRuntime().exec("getprop dhcp." + name + ".dns1");
            reader = new BufferedReader(new InputStreamReader(cmdProcess.getInputStream()));
            dnsIP = reader.readLine();
            return dnsIP;
        } catch (IOException e) {
            return null;
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
            }
            cmdProcess.destroy();
        }
    }


    /**
     * @return 获取所有有效的网卡
     */
    public String[] getAllNetInterface() {
        ArrayList<String> availableInterface = new ArrayList<String>();
        String[] interfaces = null;
        try {
            //获取本地设备的所有网络接口
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    // 过滤掉127段的ip地址
                    if (!"127.0.0.1".equals(ip)) {
                        if (ni.getName().substring(0, 3).equals("eth")) {//筛选出以太网
                            availableInterface.add(ni.getName());
                        }
                    }
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
        }
        int size = availableInterface.size();
        if (size > 0) {
            interfaces = new String[size];
            for (int i = 0; i < size; i++) {
                interfaces[i] = availableInterface.get(i);
            }
        }
        return interfaces;
    }


}
