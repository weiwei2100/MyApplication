package com.jason.myapp.board;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.KeyEvent;
import com.meiguan.ipsplayer.base.common.utils.EthernetUtil;
import com.meiguan.ipsplayer.base.common.utils.ImageCompressUtil;
import com.meiguan.ipsplayer.base.common.utils.ShellUtils;

import java.util.Date;

/**
 * Created by qiuzi on 2017/5/16.
 */
public class Aidiwei3288Util implements IBoardUtil {

    @Override
    public void sleep(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        pm.goToSleep(SystemClock.uptimeMillis());
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    @Override
    public void reboot(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        pm.reboot("");
    }

    @Override
    public void setBrightness(Context context, int brightness) {
        Settings.System.putInt(context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, brightness);
        Uri uri = Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
        context.getContentResolver().notifyChange(uri, null);
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
        Intent intent = new Intent();
        intent.setAction("com.outform.hidebar");
        context.sendBroadcast(intent);
    }

    @Override
    public String getRotation(Context context) {
        String rotationStr = "0";
        int rotation = Settings.System.getInt(context.getContentResolver(), "screen_display_orientation", 0);
        rotationStr = String.valueOf(rotation);
        return rotationStr;
    }

    @Override
    public String getSDCardPath(Context context) {
        String path = "udisk0";
        return path;
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
