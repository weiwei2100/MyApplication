package com.jason.myapp.board;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.KeyEvent;
import com.meiguan.ipsplayer.base.common.utils.EthernetUtil;
import com.meiguan.ipsplayer.base.common.utils.ImageCompressUtil;
import com.meiguan.ipsplayer.base.common.utils.ShellUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * Created by qiuzi on 16/5/3.
 */
public class ShixinA20Util implements IBoardUtil {

    @Override
    public void sleep(Context context) {
        context.sendBroadcast(new Intent("android.action.adtv.sleep"));
    }

    @Override
    public void reboot(Context context) {
        context.sendBroadcast(new Intent("android.intent.action.reboot"));
    }

    @Override
    public void setBrightness(Context context, int brightness) {

    }

    @Override
    public void setVolume(Context context, int volume) {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        manager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_PLAY_SOUND);
    }

    @Override
    public void setTime(Context context, Date date) {
        long time = date.getTime();
        SystemClock.setCurrentTimeMillis(time);
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
        int ethernetSetting = EthernetUtil.getInstance().getEthernetSetting(context);
        return ethernetSetting;
    }

    @Override
    public String getGateway(Context context) {
        int ethernetSetting = getEthernetIpProperty(context);
        String gateway = "";
        if (ethernetSetting == 1) {
            String[] allNetInterface = EthernetUtil.getInstance().getAllNetInterface();
            gateway = EthernetUtil.getInstance().getLocalGATE(allNetInterface[0]);
        } else {
            gateway = Settings.System.getString(context.getContentResolver(), "ethernet_static_gateway");
        }
        return gateway;
    }

    @Override
    public String getSubnetMask(Context context) {
        int ethernetSetting = getEthernetIpProperty(context);
        String subnetMask = "";
        if (ethernetSetting == 1) {
            String[] allNetInterface = EthernetUtil.getInstance().getAllNetInterface();
            subnetMask = EthernetUtil.getInstance().getLocalMask(allNetInterface[0]);
        } else {
            subnetMask = Settings.System.getString(context.getContentResolver(), "ethernet_static_netmask");

        }
        return subnetMask;
    }

    @Override
    public String getDns(Context context) {
        int ethernetSetting = getEthernetIpProperty(context);
        String dns = "";
        if (ethernetSetting == 1) {
            String[] allNetInterface = EthernetUtil.getInstance().getAllNetInterface();
            dns = EthernetUtil.getInstance().getLocalDNS(allNetInterface[0]);
        } else {
            dns = Settings.System.getString(context.getContentResolver(), "ethernet_static_dns1");
        }
        return dns;
    }

    @Override
    public int getHDMIStatus() {
        return 0;
    }
}
