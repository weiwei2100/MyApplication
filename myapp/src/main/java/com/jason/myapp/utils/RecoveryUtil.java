package com.jason.myapp.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import com.meiguan.ipsplayer.base.common.db.dao.*;
import com.meiguan.ipsplayer.base.common.db.dao.sync.*;

import java.io.File;
import java.sql.SQLException;

/**
 * Created by qiuzi on 16/5/7.
 */
public class RecoveryUtil {

    private final static String TAG = "RecoveryUtil";

    public static void recovery(Context context) throws Exception {
        // 清除计划表
        clearPlan(context);

        // 清除同步相关
        clearSync(context);

        // 清除素材
        clearSourceFile(context);

        // 清除播放缓存
        clearCache(context, "com.meiguan.ipsplayer.updater");

        // 清除日志
        clearLogs(context);

        //清除优惠券
        clearCoupon(context);

        //清除卖品
        clearSaleGoods(context);

        // 清除广告素材
        clearAdSources(context);

        clearAdItems(context);

        //取消广告标记
//        clearAdTerminalSetting(context);

        clearAdCycle(context);

        //清除led播放区参数设置
//        clearLEDParameters(context);

        //清除工作时间
        clearWorkTime(context);

        clearSyncServerAds(context);

        //清空ips/upgrade/temp文件夹
        FileUtil.delAllFile(Environment.getExternalStorageDirectory() + "/ips/upgrade/temp");

        //清空ips/upgrade/backup文件夹
        FileUtil.delAllFile(Environment.getExternalStorageDirectory() + "/ips/upgrade/backup");

        //清空/data/data/com.meiguan.ipsplayer/xx 路径下的内容
        clearDataCacheDir(context);

        // 重启
        reboot(context);


    }

    private static void clearDataCacheDir(Context context) {
        boolean isCleanSuccess = deleteDir(context.getCacheDir());
        Log.d(TAG, "data path clean:" + (isCleanSuccess ? "success" : "error"));
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    private static void clearCache(Context context, String packageName) throws PackageManager.NameNotFoundException {
        Context appContext = context.createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY);

        File path = appContext.getCacheDir();
        if (path == null) {
            return;
        }
        ShellUtils.execCommand("rm -r " + path.getAbsolutePath(), true);
    }

    private static void clearLogs(Context context) {
        ShellUtils.execCommand("rm -r " +
                Environment.getCurrentStoragePath(context) +
                "/ips/log", true);
    }

    private static void clearPlan(Context context) {
        TerminalPlanDao terminalPlanDao = new TerminalPlanDao(context);
        terminalPlanDao.removeAll();

        DownloadPlanDao downloadPlanDao = new DownloadPlanDao(context);
        downloadPlanDao.removeAll();

        DefaultPlanDao defaultPlanDao = new DefaultPlanDao(context);
        defaultPlanDao.removeAll();

        DownloadDefaultPlanDao downloadDefaultPlanDao = new DownloadDefaultPlanDao(context);
        downloadDefaultPlanDao.removeAll();
    }

    /**
     * 清除同步相关
     *
     * @param context
     */
    private static void clearSync(Context context) throws SQLException {
        TimHostConfigDAO timHostConfigDAO = new TimHostConfigDAO(context);
        timHostConfigDAO.removeAll();

        TimClientConfigDAO timClientConfigDAO = new TimClientConfigDAO(context);
        timClientConfigDAO.removeAll();

        TimHostSceneDAO timHostSceneDAO = new TimHostSceneDAO(context);
        timHostSceneDAO.removeAll();

        TimClientSceneDAO timClientSceneDAO = new TimClientSceneDAO(context);
        timClientSceneDAO.removeAll();

        SeqHostConfigDAO seqHostConfigDAO = new SeqHostConfigDAO(context);
        seqHostConfigDAO.removeAll();

        SeqClientConfigDAO seqClientConfigDAO = new SeqClientConfigDAO(context);
        seqClientConfigDAO.removeAll();

        SyncSettingDao syncSettingDao = new SyncSettingDao(context);
        syncSettingDao.removeAll();

        SyncProgramSettingDao syncProgramSettingDao = new SyncProgramSettingDao(context);
        syncProgramSettingDao.removeAll();

    }

    private static void clearSourceFile(Context context) {
        CleanUpUtil cleanUpUtil = new CleanUpUtil(context);
        cleanUpUtil.cleanUp();
    }

    private static void reboot(Context context) {
        SystemUtil systemUtil = new SystemUtil(context);
        systemUtil.reboot();
    }

    private static void clearCoupon(Context context) {
        TerminalTicketCouponConfigDao terminalTicketCouponConfigDao = new TerminalTicketCouponConfigDao(context);
        terminalTicketCouponConfigDao.removeAll();

    }

    private static void clearSaleGoods(Context context) {
        TerminalSaleConfigDao terminalSaleConfigDao = new TerminalSaleConfigDao(context);
        terminalSaleConfigDao.removeAll();

    }

    private static void clearAdSources(Context context) {
        AdSourceDao adSourceDao = new AdSourceDao(context);
        adSourceDao.removeAll();
    }

    private static void clearAdItems(Context context) {
        AdItemDAO adItemDAO = new AdItemDAO(context);
        adItemDAO.removeAll();
    }

    private static void clearAdTerminalSetting(Context context) {
        AdTerminalSettingDAO adTerminalSettingDAO = new AdTerminalSettingDAO(context);
        adTerminalSettingDAO.removeAll();
    }

    private static void clearAdCycle(Context context) {
        AdCycleDAO adCycleDAO = new AdCycleDAO(context);
        adCycleDAO.removeAll();
    }

    private static void clearWorkTime(Context context) {
        TerminalWorkTimeDao workTimeDao = new TerminalWorkTimeDao(context);
        workTimeDao.deleteAll();
    }

    private static void clearSyncServerAds(Context context) {
        SyncServerAdDAO syncServerAdDAO = new SyncServerAdDAO(context);
        syncServerAdDAO.removeAll();
    }

    private static void clearLEDParameters(Context context) {
        LEDParametersConfigDAO ledParametersConfigDAO = new LEDParametersConfigDAO(context);
        ledParametersConfigDAO.removeAll();
    }


}