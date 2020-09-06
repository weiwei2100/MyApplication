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
import java.util.Map;

/**
 * Created by sunjianchao on 18/9/11.
 */
public class Aidiwei3328Util implements IBoardUtil {

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
        intent.setAction("elc_hide_systembar");
        context.sendBroadcast(intent);
    }

    @Override
    public String getRotation(Context context) {
        String rotationStr = "0";
        return rotationStr;
    }

    @Override
    public String getSDCardPath(Context context) {
        return "";
    }

    @Override
    public boolean isSettingKey(int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
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
        int ipAssignment = 0;
        Map<String, String> map = EthernetUtil.getInstance().getIps(context);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if ("ipAssignment".equals(entry.getKey())) {
                if ("DHCP".equals(entry.getValue())) {
                    ipAssignment = 1;
                } else if ("STATIC".equals(entry.getValue())) {
                    ipAssignment = 2;
                }
            }
        }
        return ipAssignment;
    }

    @Override
    public String getGateway(Context context) {
        String gateway = "";
        Map<String, String> map = EthernetUtil.getInstance().getIps(context);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if ("gateWay".equals(entry.getKey())) {
                gateway = entry.getValue();
            }
        }
        return gateway;
    }

    @Override
    public String getSubnetMask(Context context) {

        String maskAddress = "";
        Map<String, String> map = EthernetUtil.getInstance().getIps(context);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if ("maskAddress".equals(entry.getKey())) {
                maskAddress = entry.getValue();
            }
        }
        return maskAddress;
    }

    @Override
    public String getDns(Context context) {
        String dns = "";
        Map<String, String> map = EthernetUtil.getInstance().getIps(context);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if ("dns".equals(entry.getKey())) {
                dns = entry.getValue();
            }
        }
        return dns;
    }

    @Override
    public int getHDMIStatus() {
        int status = 0;
        try {
            ShellUtils.CommandResult commandResult = ShellUtils.execCommand("cat /sys/class/display/HDMI/connect", true);
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
