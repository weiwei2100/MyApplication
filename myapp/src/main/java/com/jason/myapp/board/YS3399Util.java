package com.jason.myapp.board;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;
import android.view.KeyEvent;
import com.meiguan.ipsplayer.base.common.utils.ImageCompressUtil;
import com.ys.rkapi.MyManager;

import java.util.Date;

/**
 * Created by qiuzi on 2019-06-28 16:14.
 */
public class YS3399Util implements IBoardUtil {

    @Override
    public void sleep(Context context) {
        MyManager manager = MyManager.getInstance(context);
        manager.turnOffBacklight();
    }

    @Override
    public void reboot(Context context) {
        MyManager manager = MyManager.getInstance(context);
        manager.reboot();
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
        MyManager manager = MyManager.getInstance(context);
        manager.setTime(date.getTime());
    }

    @Override
    public void hideSystemBar(Context context) {
        MyManager manager = MyManager.getInstance(context);
        manager.hideNavBar(false);
        Log.d("YS-3399", "hide navigation bar");
    }

    @Override
    public String getRotation(Context context) {
        return "0";
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
    public String getGateway(Context context) {
        return "";
    }

    @Override
    public String getSubnetMask(Context context) {
        return "";
    }

    @Override
    public String getDns(Context context) {
        return "";
    }

    @Override
    public int getHDMIStatus() {
        return 0;
    }

    @Override
    public void screenshot(String screenshotPath, String name, Context context, int width, int height) {
        MyManager manager = MyManager.getInstance(context);
        manager.takeScreenshot(screenshotPath);
        try {
            Thread.sleep(2000L);
            ImageCompressUtil.compress(screenshotPath, screenshotPath, width, height);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("图片压缩失败.");
        }
    }

    @Override
    public int getEthernetIpProperty(Context context) {
        MyManager manager = MyManager.getInstance(context);
        String ethMode = manager.getEthMode();

        int mode = 0;

        if ("StaticIp".equals(ethMode)) {
            mode = 2;
        }

        if ("DHCP".equals(ethMode)) {
            mode = 1;
        }

        return mode;
    }
}
