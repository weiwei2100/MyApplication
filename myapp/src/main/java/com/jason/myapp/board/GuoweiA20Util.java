package com.jason.myapp.board;

import android.content.Context;
import android.media.AudioManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import com.meiguan.ipsplayer.base.common.utils.EthernetUtil;
import com.meiguan.ipsplayer.base.common.utils.ImageCompressUtil;
import com.meiguan.ipsplayer.base.common.utils.ShellUtils;
import com.meiguan.ipsplayer.base.common.utils.SystemUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by qiuzi on 16/5/3.
 */
public class GuoweiA20Util implements IBoardUtil {

    @Override
    public void sleep(Context context) {
        List<String> commnandList = new ArrayList<String>();
        commnandList.add("mount -o rw,remount /sys/power");
        commnandList.add("echo mem > /sys/power/state");
        commnandList.add("chmod 644 /sys/power/state");
        ShellUtils.execCommand(commnandList, true);
    }

    @Override
    public void reboot(Context context) {
        ShellUtils.execCommand("su -c reboot", true);
    }

    @Override
    public void setBrightness(Context context, int brightness) {
        List<String> commnandList = new ArrayList<String>();
        commnandList.add("mount -o rw,remount /sys/devices/virtual/disp/disp/attr");
        commnandList.add("echo " + String.valueOf(brightness) + " > /sys/devices/virtual/disp/disp/attr/screen_bright");
        commnandList.add("chmod 644 /sys/devices/virtual/disp/disp/attr/screen_bright");
        ShellUtils.execCommand(commnandList, true);
    }

    @Override
    public void setVolume(Context context, int volume) {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        manager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_PLAY_SOUND);
    }

    @Override
    public void setTime(Context context, Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd.HHmmss");
        String time = sdf.format(date);
        List<String> commnandList = new ArrayList<String>();
        commnandList.add("mount -o rw,remount /system/bin");
        commnandList.add("/system/bin/date -s \"" + time + "\"");
        commnandList.add("chmod 644 /system/bin");
        ShellUtils.execCommand(commnandList, true);
    }

    @Override
    public void hideSystemBar(Context context) {

    }

    @Override
    public String getRotation(Context context) {
        Method getSystemProperties = null;
        try {
            getSystemProperties = Class.forName("android.os.SystemProperties").getMethod("get", String.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String rotation = "0";
        try {
            if (getSystemProperties != null) {
                rotation = (String) getSystemProperties.invoke(null, "persist.sys.hwrotation");

            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return rotation == null ? "0" : rotation;
    }

    @Override
    public String getSDCardPath(Context context) {
        return "";
    }

    @Override
    public boolean isSettingKey(int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }

        return false;
    }

    @Override
    public void screenshot(String screenshotPath, String name, Context context, int width, int height) {
        ShellUtils.CommandResult commandResult = ShellUtils.execCommand("screencap -p " + screenshotPath, true);
        int result = commandResult.result;
        if (result == 0 && TextUtils.isEmpty(commandResult.errorMsg)) {
            try {
                ImageCompressUtil.compress(screenshotPath, screenshotPath, width, height);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("图片压缩失败.");
            }
        } else {
            throw new RuntimeException("截屏命令执行失败.");
        }
    }

    @Override
    public int getEthernetIpProperty(Context context) {
        //1：动态ip 2：静态ip
        int status;
        SystemUtil systemUtil = new SystemUtil(context);
        String dhcpResult = systemUtil.getProperty("dhcp.eth0.result", "unknown");
        if ("ok".equals(dhcpResult)) {
            status = 1;
        } else {
            status = 2;
        }
        return status;
    }

    @Override
    public String getGateway(Context context) {
        String[] allNetInterface = EthernetUtil.getInstance().getAllNetInterface();
        String gateway = EthernetUtil.getInstance().getLocalGATE(allNetInterface[0]);
        return gateway;
    }


    @Override
    public String getSubnetMask(Context context) {
        String subnetMask = EthernetUtil.getInstance().getA83LocalMask();
        String mask = subnetMask.substring(subnetMask.indexOf("mask") + 4, subnetMask.indexOf("flags"));
        return mask.trim();
    }

    @Override
    public String getDns(Context context) {
        String dns = EthernetUtil.getInstance().getA83LocalDns();
        return dns;
    }

    @Override
    public int getHDMIStatus() {
        int status = 0;
        try {
            ShellUtils.CommandResult commandResult = ShellUtils.execCommand("cat /sys/class/switch/hdmi/state", true);
            if (commandResult.result == 0 && TextUtils.isEmpty(commandResult.errorMsg)) {
                String successMsg = commandResult.successMsg;
                status = Integer.parseInt(successMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }
}
