package com.jason.myapp.utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.Build;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import com.ips.terminal.log.ReportingDevice;
import com.meiguan.ipsplayer.base.common.board.BoardUtilFactory;
import com.meiguan.ipsplayer.base.common.db.model.Config;
import org.apache.http.conn.util.InetAddressUtils;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by qiuzi on 15/4/23.
 */
public class SystemUtil {

    private Context context;
    private final String TAG = "SystemUtil";

    public SystemUtil(Context context) {
        this.context = context;
    }

    /**
     * 获取终端型号
     *
     * @return
     */
    public String getTerminalType() {
        String type = Build.MODEL;
        return type;
    }

    /**
     * 获取系统信息
     *
     * @return
     */
    public String getOS() {
        String version = Build.VERSION.RELEASE;
        return "Android " + version;
    }

    /**
     * 获取MAC地址
     *
     * @return
     */
    @TargetApi(9)
    public String getMAC() {
        String mac_s = "";
        try {
            byte[] mac;
            NetworkInterface ne =
                    NetworkInterface.getByInetAddress(InetAddress.getByName(getIP()));
            mac = ne.getHardwareAddress();
            mac_s = byte2hex(mac);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mac_s;
    }

    /**
     * 获取IP地址
     *
     * @return
     */
    public String getIP() {
        try {
            String ipv4;
            List<NetworkInterface> nilist = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface ni : nilist) {
                List<InetAddress> ialist = Collections.list(ni.getInetAddresses());
                for (InetAddress address : ialist) {
                    if (!address.isLoopbackAddress() &&
                            InetAddressUtils.isIPv4Address(ipv4 = address.getHostAddress())) {
                        return ipv4;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("SystemUtil.getIP", "error");
        }
        return null;
    }

    /**
     * 获取网关地址
     *
     * @return
     */
    public String getGateway() {
//        WifiManager wm = (WifiManager) this.ctx.getSystemService(Context.WIFI_SERVICE);
//        DhcpInfo di = wm.getDhcpInfo();
//        return long2Ip(di.gateway);
        return "";
    }

    /**
     * 获取子网掩码地址
     *
     * @return
     */
    public String getNetmask() {
//        WifiManager wm = (WifiManager) this.ctx.getSystemService(Context.WIFI_SERVICE);
//        DhcpInfo di = wm.getDhcpInfo();
//        return long2Ip(di.netmask);
        return "";
    }

    /**
     * 获取CPU信息
     *
     * @return
     */
    public String getCPUInfo() {
        String path = "/proc/cpuinfo";
        String cpuInfo = "";
        FileReader fr = null;
        BufferedReader br = null;

        try {
            fr = new FileReader(path);
            br = new BufferedReader(fr, 8192);

            String name = "";
            String value = "";

            //优先查询一遍，是否存在获取信息异常的情况
            while ((cpuInfo = br.readLine()) != null) {
                Log.d(TAG, "readLine cpuInfo:" + cpuInfo);
                name = String.valueOf(cpuInfo.split(":")[0]);
                value = String.valueOf(cpuInfo.split(":")[1]);
                if (name.toLowerCase().contains("processor")) {//"processor" || //"cpu architecture"
                    cpuInfo = value;
                    break;
                }
            }

//            (AIDIWEI-3328 Android7.1.2板卡问题)
            if (value.equals(" 0")) {
                br.mark(0);
                while ((cpuInfo = br.readLine()) != null) {
                    name = String.valueOf(cpuInfo.split(":")[0]);
                    value = String.valueOf(cpuInfo.split(":")[1]);

                    if (name.toLowerCase().contains("cpu architecture")) {//"processor" || //"cpu architecture"
                        cpuInfo = value;
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {

            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (fr != null) {

                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d(TAG, "board ABI:" + cpuInfo);
        return cpuInfo;
    }

    /**
     * 获取内存剩余空间（MB）
     *
     * @return
     */
    public long getMemoryUsed() {
        long memUnused;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        memUnused = mi.availMem / 1024 / 1024;
        return getMemTotal() - memUnused;
    }

    /**
     * 获取总内存空间（单位：MB）
     *
     * @return
     */
    public long getMemTotal() {
        long memTotal = 0;
        // 通过/proc/meminfo读出内核信息进行解析
        String path = "/proc/meminfo";
        String content = "";
        BufferedReader br = null;
        FileReader fr = null;

        try {
            fr = new FileReader(path);
            br = new BufferedReader(fr, 8);
            String line = "";
            if ((line = br.readLine()) != null) {
                content = line;
                int begin = content.indexOf(':');
                int end = content.indexOf('k');
                content = content.substring(begin + 1, end).trim();
                memTotal = Long.valueOf(content) / 1024;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                    new FileReader(path).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return memTotal;
    }

    /**
     * 获取当前存储总空间（单位：MB）
     *
     * @return
     */
    public long getSDCardTotal() {
        long total;
        String root = Environment.getCurrentStoragePath(context);
        StatFs stat = new StatFs(root);
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        total = (blockSize * totalBlocks) / 1024 / 1024;
        return total;
    }

    /**
     * 获取当前存储已用空间（单位：MB）
     *
     * @return
     */
    public long getSDCardUsed() {
        long used;
        String root = Environment.getCurrentStoragePath(context);
        StatFs stat = new StatFs(root);
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        long availableBlocks = stat.getAvailableBlocks();
        used = (totalBlocks - availableBlocks) * blockSize / 1024 / 1024;
        return used;
    }

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    public int getScreenWidth() {
        int widthPixels;
        WindowManager w = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
        widthPixels = metrics.widthPixels;

        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
            try {
                widthPixels = (Integer) Display.class
                        .getMethod("getRawWidth").invoke(d);
            } catch (Exception e) {
            }
        } else if (Build.VERSION.SDK_INT >= 17)
            try {
                android.graphics.Point realSize = new android.graphics.Point();
                Display.class.getMethod("getRealSize",
                        android.graphics.Point.class).invoke(d, realSize);
                widthPixels = realSize.x;
            } catch (Exception e) {
            }
        return widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @return
     */
    public int getScreenHeight() {
        int heightPixels;
        WindowManager w = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
        heightPixels = metrics.heightPixels;

        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
            try {
                heightPixels = (Integer) Display.class
                        .getMethod("getRawHeight").invoke(d);
            } catch (Exception e) {
            }
        } else if (Build.VERSION.SDK_INT >= 17)
            try {
                android.graphics.Point realSize = new android.graphics.Point();
                Display.class.getMethod("getRealSize",
                        android.graphics.Point.class).invoke(d, realSize);
                heightPixels = realSize.y;
            } catch (Exception e) {
                Log.e("Exception ", e + "");
            }
        return heightPixels;
    }

    //终端固件版本
    public String getDisplay() {
        return Build.DISPLAY;
    }

    /**
     * 获取CPU最大频率（KB）
     *
     * @return
     */
    public long getMaxCPUFreq() {
        long freq = 0;
        String path = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq";
        File file = new File(path);
        if (!file.exists()) {
            return 0;
        }
        ADBShellUtil util = new ADBShellUtil();
        try {
            String result = util.exeShellCommand("cat " + path);
            if (result != null) {
                String[] data = result.split("/n");
                if (data.length > 0) {
                    result = data[0];
                    freq = Long.parseLong(result);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
        return freq;
    }

    /**
     * 获取ANDROID_ID
     *
     * @return
     */
    public String getSN() {
        Method getSystemProperties = null;

        String board = BoardUtilFactory.getInstance().getBoard();
        Log.d("updater_n", "getSN-- :" + board + "> sn:" + Build.SERIAL);

        if (!TextUtils.isEmpty(board) && board.equals("PHILIPS-V551")) {
            return Build.SERIAL;
        }

        try {
            getSystemProperties = Class.forName("android.os.SystemProperties").getMethod("get", String.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String sn = null;
        try {
            if (getSystemProperties != null) {
                sn = (String) getSystemProperties.invoke(null, "ro.serialno");

            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        Log.d("updater_n", "getSN-- :" + board);
        if (TextUtils.isEmpty(sn) && !TextUtils.isEmpty(board) && !board.equals("AIDIWEI-3188")) {
            sn = getMAC();
        }
        Log.d("updater_n", "getSN==:" + sn);
        return sn;
    }

    /**
     * 获取屏幕旋转角度设置（"0", "90", "180", "270"）
     *
     * @return
     */
    public String getRotation() {
        return BoardUtilFactory.getInstance().getBoardUtil().getRotation(context);
    }

    private String byte2hex(byte[] b) {
        String hex = "";
        try {
            StringBuffer hs = new StringBuffer(b.length);
            String stmp = "";
            int len = b.length;
            for (int n = 0; n < len; n++) {
                stmp = Integer.toHexString(b[n] & 0xFF);
                if (stmp.length() == 1) {
                    hs = hs.append("0").append(stmp);
                } else {
                    hs = hs.append(stmp);
                }
                if (n < len - 1) {
                    hs.append(":");
                }
            }
            hex = String.valueOf(hs).toUpperCase();
        } catch (Exception e) {
            hex = "";
        }
        return hex;
    }

    /**
     * 获取终端CPU使用率
     *
     * @return
     */
    public float getCpuRate() {
        float cpuUsed = 0.00f;
        ADBShellUtil shellUtil = new ADBShellUtil();
        try {
            String result = shellUtil.exeShellCommand("vmstat -n 1");
            if (result != null) {
                String[] data = result.split("/n");
                if (data.length > 2) {
                    String line = data[2];
                    String[] value = line.trim().split(" +");
                    if (value.length > 12) {
                        float rate = (100 - Float.parseFloat(value[12])) / 100;
                        cpuUsed = new BigDecimal(rate).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0.00f;
        }
        return cpuUsed;
    }

    /**
     * 本应用占用CPU百分比
     *
     * @return
     */
    public float getCpuAppRate() {
        float cpuUsed = 0.00f;
        ADBShellUtil shellUtil = new ADBShellUtil();
        try {
            String result = shellUtil.exeShellCommand("top -d 0 -n 1 | grep com.meiguan.ipsplayer");
            if (result != null) {
                String[] lines = result.split("/n");
                for (String line : lines) {
                    String[] data = line.trim().split(" +");
                    if (data.length > 2) {
                        float rate = Float.parseFloat(data[2].substring(0, data[2].indexOf("%"))) / 100;
                        cpuUsed += new BigDecimal(rate).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0.00f;
        }
        return cpuUsed;
    }

    /**
     * 本应用占用内存（单位：MB）
     *
     * @return
     */
    public long getMemoryAppUsed() {
        long memUsed = 0;
        ADBShellUtil shellUtil = new ADBShellUtil();
        try {
            String result = shellUtil.exeShellCommand("top -d 0 -n 1 | grep com.meiguan.ipsplayer");
            if (result != null) {
                String[] lines = result.split("/n");
                for (String line : lines) {
                    String[] data = line.trim().split(" +");
                    if (data.length > 6) {
                        if (data[6].toLowerCase().endsWith("k")) {
                            memUsed += (Long.valueOf(data[6].substring(0, data[6].length() - 1))) / 1024;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return memUsed;
    }

    /**
     * 获取素材占用SD卡空间（MB）
     *
     * @return
     */
    public long getSDCardSourceUsed() {
        long sourceUsed = 0;
        String concertPath = Environment.getCurrentStoragePath(context) + "/concert";
        File file = new File(concertPath);
        if (!file.exists()) {
            return 0;
        }
        ADBShellUtil util = new ADBShellUtil();
        try {
            String result = util.exeShellCommand("du -sm " + concertPath);
            if (result != null) {
                String[] data = result.split("\t");
                if (data.length > 0) {
                    sourceUsed = Long.parseLong(data[0]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return sourceUsed;
    }

    public String getDeviceId() {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = telephonyManager.getDeviceId();
        return deviceId == null ? "" : deviceId;
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    public long getMobileRx() {
        return TrafficStats.getMobileRxBytes() / 1024;
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    public long getMobileTx() {
        return TrafficStats.getMobileTxBytes() / 1024;
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    public long getTotalRx() {
        return TrafficStats.getTotalRxBytes() / 1024;
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    public long getTotalTx() {
        return TrafficStats.getTotalTxBytes() / 1024;
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    public long getWifiRx() {
        return (TrafficStats.getTotalRxBytes() - TrafficStats.getMobileRxBytes()) / 1024;
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    public long getWifiTx() {
        return (TrafficStats.getTotalTxBytes() - TrafficStats.getMobileTxBytes()) / 1024;
    }


    public void reboot() {
        BoardUtilFactory.getInstance().getBoardUtil().reboot(context);
    }

    public void sleep() {
        BoardUtilFactory.getInstance().getBoardUtil().sleep(context);
    }

    public void setBrightness(int brightness) {
        BoardUtilFactory.getInstance().getBoardUtil().setBrightness(context, brightness);
    }

    public void setVolume(int volume) {
        BoardUtilFactory.getInstance().getBoardUtil().setVolume(context, volume);
    }

    public void setTime(Date date) {
        if (date == null) {
            return;
        }
        BoardUtilFactory.getInstance().getBoardUtil().setTime(context, date);
    }

    public int getBrightness() {
        int brightness = 0;
        ContentResolver cr = context.getContentResolver();
        try {
            brightness = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return brightness;
    }

    //网络连接类型
    public String updateConnectedFlags(Context mContext) {
        ConnectivityManager connManager =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI); // wifi
        NetworkInfo gprs = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE); // gprs
        if (wifi != null && wifi.getState() == NetworkInfo.State.CONNECTED) {
            return "WIFI";
        } else if (gprs != null && gprs.getState() == NetworkInfo.State.CONNECTED) {
            return "移动网络";
        }
        return "以太网";
    }

    /**
     * 2:wifi 3:移动网络 1:以太网
     *
     * @param mContext
     * @return
     */
    public int getNetWorkStatus(Context mContext) {
        ConnectivityManager connManager =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI); // wifi
        NetworkInfo gprs = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE); // gprs
        if (wifi != null && wifi.getState() == NetworkInfo.State.CONNECTED) {
            return 2;
        } else if (gprs != null && gprs.getState() == NetworkInfo.State.CONNECTED) {
            return 3;
        }
        return 1;
    }

    public int getVolume() {
        int volume = 0;
        try {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return volume;
    }

    public int getMaxVolume() {
        int maxVolume = 0;
        try {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maxVolume;
    }

    public void hideStatusBar() {
        BoardUtilFactory.getInstance().getBoardUtil().hideSystemBar(context);
    }

    public String setSDCardPath() {
        String sdCardPath = BoardUtilFactory.getInstance().getBoardUtil().getSDCardPath(context);
        return sdCardPath;
    }

    public boolean isSettingKey(int keyCode) {
        return BoardUtilFactory.getInstance().getBoardUtil().isSettingKey(keyCode);
    }


    /**
     * 获取设备信息
     *
     * @return
     */
    public ReportingDevice getReportingDevice(Context context) {
        ConfigUtil util = new ConfigUtil(context);
        Config config = util.getConfig();
        SystemUtil systemUtil = new SystemUtil(context);
        String board = BoardUtilFactory.getInstance().getBoard();

        ReportingDevice reportingDevice = new ReportingDevice();
        reportingDevice.setBoardType(board);
        reportingDevice.setFirmwareVersion(systemUtil.getDisplay());
        reportingDevice.setSerialNumber(systemUtil.getSN());
        reportingDevice.setSoftwareVersionCode(VersionUtil.getVersionCode(context));
        reportingDevice.setSoftwareVersionName(VersionUtil.getVersionName(context));//versionUtil.getVersionString(LogService.getContext())
        reportingDevice.setTerminalId(config.getTerminalId());

        return reportingDevice;
    }

    /**
     * Android : 反射机制获取系统属性（SystemProperties）
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public String getProperty(String key, String defaultValue) {
        String value = defaultValue;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            value = (String) (get.invoke(c, key, "unknown"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return value;
        }
    }

    /**
     * Android : 反射机制设置系统属性（SystemProperties）
     *
     * @param key
     * @param value
     */
    public void setProperty(String key, String value) {
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method set = c.getMethod("set", String.class, String.class);
            set.invoke(c, key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getHDMIStatus() {
        return BoardUtilFactory.getInstance().getBoardUtil().getHDMIStatus();
    }

}
