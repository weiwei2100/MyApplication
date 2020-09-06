package com.jason.myapp.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.ips.terminal.report.machine.Version;
import com.meiguan.ipsplayer.base.common.log.VersionInfoCollector;

import java.util.List;

/**
 * Created by qiuzi on 16/6/6.
 */
public class VersionUtil {

    public static String getVersionString(Context context) {
        String versionLog = "";
        List<Version> versions = VersionInfoCollector.getVersionsInfo(context);
        for (Version version : versions) {
            String name = "";
            if (IntentActionUtil.PKG_BUS.equals(version.getPackageName())) {
                name = "b";
            } else if (IntentActionUtil.PKG_DOWNLOAD.equals(version.getPackageName())) {
                name = "d";
            } else if (IntentActionUtil.PKG_LAUNCHER.equals(version.getPackageName())) {
                name = "l";
            } else if (IntentActionUtil.PKG_PLAY.equals(version.getPackageName())) {
                name = "p";
            } else if (IntentActionUtil.PKG_SETTINGS.equals(version.getPackageName())) {
                name = "s";
            } else if (IntentActionUtil.PKG_UPGRADE.equals(version.getPackageName())) {
                name = "u";
            } else if (IntentActionUtil.PKG_LOG.equals(version.getPackageName())) {
                name = "log";
            } else if (IntentActionUtil.PKG_PRINT.equals(version.getPackageName())) {
                name = "p";
            } else if (IntentActionUtil.PKG_COLLECTION.equals(version.getPackageName())) {
                versionLog = version.getVersionCode() + "";
                return versionLog;
            } else if ("com.meiguan.ipsplayer.updater".equals(version.getPackageName())){
                versionLog = version.getVersionCode() + "";
                return versionLog;
            }
            versionLog += name + "-" + version.getVersionCode() + ",";
        }

        if (versionLog.endsWith(",")) {
            versionLog = versionLog.substring(0, versionLog.length() - 1);
        }
        return versionLog;
    }

    /**
     * android版本名
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    /**
     * 版本号
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;
        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pi;
    }
}
