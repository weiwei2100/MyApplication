package com.jason.myapp.board;

import android.content.Context;

import java.util.Date;

/**
 * Created by qiuzi on 16/5/3.
 */
public interface IBoardUtil {

    void sleep(Context context);

    void reboot(Context context);

    void setBrightness(Context context, int brightness);

    void setVolume(Context context, int volume);

    void setTime(Context context, Date date);

    void hideSystemBar(Context context);

    String getRotation(Context context);

    String getSDCardPath(Context context);

    boolean isSettingKey(int keyCode);

    void screenshot(String screenshotPath, String name, Context context, int width, int height);

    int getEthernetIpProperty(Context context);

    //获取以太网网关
    String getGateway(Context context);

    //获取以太网子网掩码
    String getSubnetMask(Context context);

    //获取以太网dns
    String getDns(Context context);

    int getHDMIStatus();

}
