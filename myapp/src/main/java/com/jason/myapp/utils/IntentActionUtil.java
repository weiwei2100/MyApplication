package com.jason.myapp.utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by qiuzi on 15/6/16.
 */
public class IntentActionUtil {

    /**
     * SyncProgramSenderReceiver
     */
    public static final String ACTION_SYNCPROGRAMSENDER_RECEIVER = "ips.action.player.SyncProgramSenderReceiver";

    /**
     * SyncListenerReceiver
     */
    public static final String ACTION_SYNCLISTENERR_RECEIVER = "ips.action.player.SyncListenerReceiver";

    /**
     * Setings 更新同步数据库数据
     */
    public static final String ACTION_UPDATASYNCSETTINGS = "ips.action.settings.updatasyncsettings";


    /**
     * bus通知download有级别更高的计划下发
     */
    public static final String ACTION_STOPDEFAULTDOWNLOAD = "ips.action.download.stop.downdefaultplan";

    /**
     * bus通知download有级别更高的计划下发
     */
    public static final String ACTION_STOPDOWNLOAD = "ips.action.download.stop.downplan";

    /**
     * usb设置同步通知play开启同步
     */
    public static final String ACTION_OPENSYNC = "ips.action.play.opensync";

    /**
     * play同步缓存更新
     */
    public static final String ACTION_SYNCSETTING_UPDATA = "ips.action.play.syncsettingupdata";

    /**
     * 开启同步测试
     */
    public static final String ACTION_OPEN_SYNCCHECK = "ips.action.play.opensyncCheck";

    /**
     * 下载计划
     */
    public static final String ACTION_DOWNLOAD_PLAN = "ips.action.download.plan";

    /**
     * USB下载播放计划
     */
    public static final String ACTION_DOWNLOAD_STORAGE = "ips.action.download.storage";

    /**
     * 下载状态报告
     */
    public static final String ACTION_DOWNLOAD_REPORT = "ips.action.download.report";

    public static final String ACTION_CONTROL_TIME_ADJUST = "ips.action.time.adjust";

    /**
     * 应用升级
     */
    public static final String ACTION_UPGRADE = "ips.action.upgrade.collection";

    /**
     * Android主板固件升级
     */
    public static final String ACTION_UPGRADE_FIRMWARE = "ips.action.upgrade.firmware.collection";

    /**
     * 播放计划切换
     */
    public static final String ACTION_PLAY_PLAN_SWITCH = "ips.action.play.plan.switch";

    /**
     * 终端控制——开机
     */
    public static final String ACTION_CONTROL_POWER_ON = "ips.action.control.power.on";

    /**
     * 终端控制——关机
     */
    public static final String ACTION_CONTROL_POWER_OFF = "ips.action.control.power.off";

    /**
     * 终端控制——定时开机
     */
    public static final String ACTION_CONTROL_POWER_ON_TIMING = "ips.action.control.power.on.timing";

    /**
     * 终端控制——定时关机
     */
    public static final String ACTION_CONTROL_POWER_OFF_TIMING = "ips.action.control.power.off.timing";

    /**
     * 终端控制——亮度调节
     */
    public static final String ACTION_CONTROL_BRIGHT = "ips.action.control.bright";

    /**
     * 终端控制——音量调节
     */
    public static final String ACTION_CONTROL_VOLUME = "ips.action.control.volume";

    /**
     * 终端控制——重启
     */
    public static final String ACTION_CONTROL_RESTART = "ips.action.control.restart";

    /**
     * 终端控制——截屏
     */
    public static final String ACTION_CONTROL_SCREENSHOT = "ips.action.control.screenshot";

    /**
     * 终端控制——运行时长
     */
    public static final String ACTION_CONTROL_ELAPSED_TIME = "ips.action.control.elapsed.time";

    /**
     * 插播
     */
    public static final String ACTION_INTERCUT = "ips.action.intercut";

    /**
     * 清空播放list
     */
    public static final String ACTION_CLEAN_PALY_LIST = "clean.show.play.list";
    /**
     * 播放
     */
    public static final String ACTION_PLAY = "cinemad.play";
    /**
     * 暂停
     */
    public static final String ACTION_PAUSE = "cinemad.pause";
    /**
     * 恢复
     */
    public static final String ACTION_RESUME = "cinemad.resume";
    /**
     * 停止
     */
    public static final String ACTION_STOP = "cinemad.stop";
    /**
     * 节目单更新
     */
    public static final String ACTION_PROGRAM_UPDATE = "ips.action.program.update";

    /**
     * 发送同步消息
     */
    public static final String ACTION_SYNC_SEND = "ips.action.sync.send";

    /**
     * 上传终端日志
     */
    public static final String ACTION_UPLOAD_LOG = "ips.action.upload.log";

    /**
     * USB应用升级
     */
    public static final String ACTION_UPGRADE_STORAGE = "ips.action.upgrade.storage";

    /**
     * 启动日志
     */
    public static final String ACTION_LOG_BOOT = "ips.action.log.boot";

    /**
     * 启动日志
     */
    public static final String ACTION_LOG_REPORT = "ips.action.log.report";

    /**
     * 心跳日志
     */
    public static final String ACTION_LOG_HEARTBEAT = "ips.action.log.heartbeat";

    /**
     * 下载日志
     */
    public static final String ACTION_LOG_DOWNLOAD = "ips.action.log.downlaod";

    /**
     * 播放素材日志
     */
    public static final String ACTION_LOG_PLAYSOURCE = "ips.action.log.playsource";

    /**
     * 通知Bus启动MQ
     */
    public static final String ACTION_START_MQ = "ips.action.mq.start";

    /**
     * 通知Bus发送命令执行结果
     */
    public static final String ACTION_COMMAND_RESPONSE = "ips.action.command.response";

    /**
     * 通知Bus发送下载进度
     */
    public static final String ACTION_DOWNLOAD_PROGRESS = "ips.action.download.progress";

    /**
     * 通知Bus发送广告素材下载进度
     */
    public static final String ACTION_AD_DOWNLOAD_PROGRESS = "ips.action.ad.download.progress";

    /**
     * 通知Bus发送上传结果
     */
    public static final String ACTION_SCREENSHOT_UPLOAD_RESULT = "ips.action.screenshot.upload.result";

    /**
     * 通知play启动SyncServer
     */
    public static final String ACTION_SYNC_SERVER_START = "ips.action.sync.server.start";

    /**
     * 通知play停止SyncServer
     */
    public static final String ACTION_SYNC_SERVER_STOP = "ips.action.sync.server.stop";

    /**
     * 通知play启动SyncClient
     */
    public static final String ACTION_SYNC_CLIENT_START = "ips.action.sync.client.start";

    /**
     * 通知play执行指定场景
     */
    public static final String ACTION_SYNC_EXECUTE_SCENE = "ips.action.sync.execute.scene";

    /**
     * 通知play，通过主机发布删除同步场景命令
     */
    public static final String ACTION_SYNC_SCENE_CANCEL = "ips.action.sync.scene.cancel";

    /**
     * 通知play通知bus 上报播放log
     *
     */
    public static final String ACTION_PLAY_INFO_REPORT = "ips.action.play.info.report";

    /**
     * 通知同步设置发生变化
     */
    public static final String ACTION_SYNC_CONFIG_CHANGED = "ips.action.sync.config.changed";

    public static final String ACTION_SYNC_STATE_CHANGED = "ips.action.sync.state.changed";

    //试投
    public static final String ACTION_SYNC_PRE_EXECUTE = "ips.action.sync.pre.execute";

    //广告试投日志上报
    public static final String ACTION_LOG_SYNC_PRE_EXECUTE = "ips.action.log.sync.pre.execute";

    //广告播放上报
    public static final String ACTION_LOG_AD_PLAY_TERMINAL = "ips.action.log.ad.play.terminal";
    // 放映服务器日志上报
    public static final String ACTION_SEND_DCSERVER_LOG = "ips.action.send.dcserver.log";
    public static final String ACTION_SEND_PLAYBACK_STATUS = "ips.action.send.playbackstatus";

    /**
     * 局域网配置
     */
    public static final String ACTION_LAN_CONFIG = "ips.action.lan.config";

    /**
     * ips.android.download应用包名
     */
    public static final String PKG_DOWNLOAD = "com.meiguan.ipsplayer.download";

    /**
     * ips.android.launcher应用包名
     */
    public static final String PKG_LAUNCHER = "com.meiguan.ipsplayer.launcher";

    /**
     * ips.android.download应用主服务名
     */
    public static final String SERVICE_DOWNLOAD = "com.meiguan.ipsplayer.download.DownloadService";

    public static final String PKG_PLAY = "com.meiguan.ipsplayer.play";

    public static final String PKG_BUS = "com.meiguan.ipsplayer.bus";

    public static final String PKG_SETTINGS = "com.meiguan.ipsplayer.settings";

    public static final String PKG_UPGRADE = "com.meiguan.ipsplayer.upgrader";

    public static final String PKG_LOG = "com.meiguan.ipsplayer.log";

    public static final String PKG_INSTALL = "com.meiguan.ipsplayer.installer";

    public static final String SERVICE_BUS = "com.meiguan.ipsplayer.bus.BusService";

    public static final String SERVICE_LAUNCHER = "com.meiguan.ipsplayer.launcher.LauncherService";

    public static final String SERVICE_SETTINGS = "com.meiguan.ipsplayer.settings.SettingsService";

    public static final String ACTIVITY_SETTINGS = "com.meiguan.ipsplayer.settings.SettingsActivity";

    public static final String ACTIVITY_PLAY = "com.meiguan.ipsplayer.play.PlayActivity";

    public static final String ACTIVITY_VIEWPAGER = "com.meiguan.ipsplayer.play.ViewPagerActivity";

    public static final String SERVICE_PLAY = "com.meiguan.ipsplayer.play.PlayService";

    public static final String SERVICE_UPGRADE = "com.meiguan.ipsplayer.upgrade.UpgradeService";

    public static final String SERVICE_LOG = "com.meiguan.ipsplayer.log.LogService";

    public static final String PKG_UPGRADE_N = "com.meiguan.ipsplayer.updater";

    public static final String PKG_COLLECTION = "com.meiguan.ipsplayer.collection";

    public static final String PKG_FIRMWARE_MASUNG = "com.meiguan.ipsplayer.printer.firmware.meisong";

    public static final String ACTIVITY_INSTALL = "com.meiguan.ipsplayer.installer.InstallActivity";
    public static final String ACTIVITY_CHECKNET = "com.meiguan.ipsplayer.settings.CheckNetActivity";

    public static final String ACTIVITY_UPGRADE_N = "com.meiguan.ipsplayer.updater.UpgradeActivity";
    public static final String ACTIVITY_COLLECTION = "com.meiguan.ipsplayer.collection.CollectionActivity";

    /**
     * 打印的广播
     */
    public static final String ACTION_PRINT_SOON = "ips.action.print.soon";
    public static final String ACTION_PRINT_SOON_TRANSIT = "ips.action.print.soon.transit";

    public static final String ACTION_PRINT_RESULT = "ips.action.print.result";
    public static final String ACTION_PRINT_RESULT_TRANSIT = "ips.action.print.result.transit";

    public static final String ACTION_PRINT_EXCEPTION = "ips.action.print.exception";
    public static final String ACTION_PRINT_EXCEPTION_TRANSIT = "ips.action.print.exception.transit";

    public static final String ACTION_PRINT_EXCEPTION_DESCRIPTION = "ips.action.print.exception.description";
    public static final String ACTION_PRINT_EXCEPTION_DESCRIPTION_TRANSIT = "ips.action.print.exception.description.transit";

    public static final String ACTION_PRINT_CHECKPRINTSTATUS = "ips.action.print.checkstatus";

    public static final String ACTION_PRINTER_FIRMWARE_VERSION = "ips.action.printer.firmware.version";

    /**
     * 影票切纸广播
     */
    public static final String ACTION_PRINT_CUTTICKETPAPER = "ips.action.print.cutpaper";
    public static final String ACTION_PRINT_CUTTICKETPAPER_TRANSIT = "ips.action.print.cutpaper.transit";

    /**
     * 下发优惠券通知广播
     */
    public static final String ACTION_COUPON_REPORT = "ips.action.coupon.report";


    /**
     * 取票机设置
     */
    public static final String ACTION_TICKET_CONFIG = "ips.action.print.ticketconfig";

    public static final String ACTIVITY_TICKETCONFIG = "com.meiguan.ipsplayer.settings.ticket.PeripheralsActivity";

    /**
     * 票纸模板
     */
    public static final String ACTION_TICKET_TEMPLATE = "ips.action.print.ticketTemplate";

    /**
     * 动态票纸模板
     */
    public static final String ACTION_TICKET_TEMPLATE_FLAG = "ips.action.print.ticketTemplate.flag";

    /**
     * 打印卖品(大地)
     */
    public static final String ACTION_TICKET_SALEGOODS = "ips.action.print.ticket.saleGoods";
    /**
     * 打印卖品(大地)
     */
    public static final String ACTION_TICKET_SALEGOODS_TRANSIT = "ips.action.print.ticket.saleGoods.transit";


    /**
     * print应用包名
     */
    public static final String PKG_PRINT = "com.meiguan.ipsplayer.print";
    /**
     * 打印机固件应用包名
     */
    public static final String PKG_PRINTER_FIRMWARE = "com.meiguan.ipsplayer.printer.firmware.meisong";

    /**
     * printservice
     */
    public static final String SERVICE_PRINT = "com.meiguan.ipsplayer.print.PrintService";

    public static final String ACTIVITY_FACTIRTEST = "com.meiguan.ipsplayer.test.factory.FactoryTestActivity";
    public static final String PKG_TEST = "com.meiguan.ipsplayer.test";

    public static final String ACTION_PRINTER_TEST = "ips.action.printer.test";

    public static final String ACTIVITY_CINEMA_TICKET_BUY = "com.meiguan.ipsplayer.cinema.module.buy.ui.activity.MovieChooseActivity";
    public static final String ACTIVITY_CINEMA_TICKET_VIP = "com.meiguan.ipsplayer.cinema.module.buy.ui.activity.VipCardActivity";
    public static final String ACTIVITY_CINEMA_TICKET_PRINT = "com.meiguan.ipsplayer.cinema.module.buy.ui.activity.PrintTicketActivity";
    public static final String PKG_CINEMA_TICKET = "com.meiguan.ipsplayer.cinema";


    public static final String SERVICE_CINEMA_TICKET = "com.meiguan.ipsplayer.cinema.module.buy.service.OristarDataService";
    public static final String ACTIVITY_ORISTAR_CINEMA_TICKET_VIP = "com.meiguan.ipsplayer.cinema.module.buy.ui.activity.oristar.OristarVipCardActivity";
    public static final String ACTIVITY_ORISTAR_CINEMA_TICKET_PRINT = "com.meiguan.ipsplayer.cinema.module.buy.ui.activity.oristar.OristarPrintTicketActivity";

    /**
     * 购买影票
     */
    public static final String ACTION_PRINT_VIP = "ips.action.print.vip";
    public static final String ACTION_PRINT_VIP_RESULT = "ips.action.print.vip.result";

    /**
     * 购买影票打印异常
     */
    public static final String ACTION_PRINT_VIP_EXCEPTION = "ips.action.print.vip.exception";

    //打印机设置
    public static final String SETPRINTER = "ips.action.printer.setup";

    //取票机缺纸广播
    public static final String ACTION_PRINT_CHECK_PRINT_PAPERSTATUS = "ips.action.print.check.paper.status";

    //清空json数据
    public static final String ACTION_CLEAR_JSON_DATA = "ips.action.clear.ticket.data";

    //play 获取影票配置信息
    public static final String ACTION_TICKET_CONFIG_INFO = "ips.action.print.ticketconfig_info";

    //网络设置配置信息
    public static final String ACTION_NETWORK_SETTING_CONFIG = "ips.action.network.setting";


    //新日志上报
    public static final String ACTION_LOG_RECORD = "ips.action.log.logRecord";

    //终端同步日志上报
    public static final String ACTION_LOG_SYNC_TERMINAL = "ips.action.log.sync.terminal";

    public static final String ACTION_LOG_SYNC_STATE = "ips.action.log.sync.state";
    public static final String ACTION_SEND_PROBEDEVMAC = "ips.action.send.probedevmac";

    /**
     * 判断应用是否处于运行状态
     *
     * @param packageName
     * @param context
     * @return
     */
    @TargetApi(3)
    public static boolean isAppRunning(String packageName, Context context) {
        boolean isAppRunning = false;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> list = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : list) {
            if (info.processName.equals(packageName)) {
                isAppRunning = true;
                break;
            }
        }
        return isAppRunning;
    }

    /**
     * 启动Activity,根据打包配置采用不同的启动方式
     *
     * @param packageName
     * @param activityName
     * @param context
     */
    public static void startExternalActivity(String packageName, String activityName, Context context) {
        startActivityInApp(packageName, activityName, context);
    }

    /**
     * 启动本应用内Activity
     *
     * @param packageName
     * @param activityName
     * @param context
     */
    private static void startActivityInApp(String packageName, String activityName, Context context) {
        try {
            Class activityClass = Class.forName(activityName);
            Intent intent = new Intent(context, activityClass);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动其他应用Activity
     *
     * @param packageName
     * @param activityName
     * @param context
     */
    public static void startActivityOutOfApp(String packageName, String activityName, Context context) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName, activityName));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动Service,根据打包配置采用不同的启动方式
     *
     * @param packageName
     * @param serviceName
     * @param context
     */
    public static void startExternalService(String packageName, String serviceName, Context context) {
        startServiceInApp(packageName, serviceName, context);
    }

    /**
     * 启动本应用内Service
     *
     * @param packageName
     * @param serviceName
     * @param context
     */
    private static void startServiceInApp(String packageName, String serviceName, Context context) {
        try {
            Class serviceClass = Class.forName(serviceName);
            Intent intent = new Intent(context, serviceClass);
            context.startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动其他应用Service
     *
     * @param packageName
     * @param serviceName
     * @param context
     */
    public static void startServiceOutOfApp(String packageName, String serviceName, Context context) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName, serviceName));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public static void killProcess(String processName, Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo runningProcess : runningProcesses) {
            if (processName.equals(runningProcess.processName)) {
                Process process = null;
                try {
                    process = Runtime.getRuntime().exec("kill -9 " + runningProcess.pid);
                    process.waitFor();


                } catch (Exception e) {
                    Log.e("关闭进程异常", processName + e.getMessage(), e);
                } finally {
                    try {
                        if (process != null) {
                            process.destroy();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    public static void stopApp(String pkgName, Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        try {
            Method method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
            method.invoke(am, pkgName);
        } catch (Exception e) {
            Log.e("stop app error", pkgName);
        }
    }

    /**
     * 判断应用是否在前台运行
     *
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public static boolean isAppOnForeground(String packageName, Context context) {
        ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(
                Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    public static boolean isActivityRunning(Context context, String activityClassName) {
        boolean result = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> infos = activityManager.getRunningTasks(20);
        for (ActivityManager.RunningTaskInfo info : infos) {
            ComponentName topActivity = info.topActivity;
            ComponentName baseActivity = info.baseActivity;
            if (activityClassName.equals(topActivity.getClassName())
                    || activityClassName.equals(baseActivity.getClassName())) {
                result = true;
                break;
            }
        }
        return result;
    }


    /**
     * 强制停止散包所有进程
     */
    public static void forceStopAPKs(Context context) throws Exception {

        forceStopPackage(IntentActionUtil.PKG_LAUNCHER, context);
        forceStopPackage(IntentActionUtil.PKG_BUS, context);
        forceStopPackage(IntentActionUtil.PKG_DOWNLOAD, context);
        forceStopPackage(IntentActionUtil.PKG_SETTINGS, context);
        forceStopPackage(IntentActionUtil.PKG_UPGRADE, context);
        forceStopPackage(IntentActionUtil.PKG_LOG, context);
        forceStopPackage(IntentActionUtil.PKG_PLAY, context);
    }

    /**
     * 强制停止collection所有进程
     */
    public static void forceStopCollection(Context context) {
        try {

            forceStopPackage(IntentActionUtil.PKG_COLLECTION, context);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 强制停止打印机固件
     */
    public static void forceStopPrinterFirmware(Context context) {
        try {
            forceStopPackage(IntentActionUtil.PKG_PRINTER_FIRMWARE, context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 强制停止应用程序
     *
     * @param pkgName
     */
    private static void forceStopPackage(String pkgName, Context context)
            throws Exception {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        Method method = Class.forName("android.app.ActivityManager").getMethod(
                "forceStopPackage", String.class);
        method.invoke(am, pkgName);
    }

    /**
     * 根据进程名称判断进程是否运行
     *
     * @param processName
     * @return
     */
    public static boolean isProcessRunning(String processName) {
        boolean isRunning = false;

        ShellUtils.CommandResult commandResult = ShellUtils.execCommand("procrank |grep " + processName, true);
//        Log.d("CommandResult", commandResult.result + "");
        if (commandResult != null) {
            String resultStr = commandResult.successMsg;
//            Log.d("CommandResult", resultStr);
            if (!TextUtils.isEmpty(resultStr) && resultStr.contains(processName)) {
                isRunning = true;
            }
        }

        return isRunning;
    }

    /**
     * 根据进程名称Kill进程
     *
     * @param processName
     */
    public static void killProcess(String processName) {
        ShellUtils.CommandResult commandResult = ShellUtils.execCommand("procrank |grep " + processName, true);

        if (commandResult != null) {
            String resultStr = commandResult.successMsg;
            String[] results = resultStr.split(processName);
            for (String result : results) {
                if (!TextUtils.isEmpty(result)) {
                    String[] dataArray = result.split(" + ");
                    if (dataArray.length > 0 && !TextUtils.isEmpty(dataArray[0])) {
                        String processId = dataArray[0];
                        Log.d("CommandResult", "kill process:" + processId);
                        ShellUtils.execCommand("kill -9 " + processId, true);
                    }
                }
            }
        }
    }

    /**
     * 根据进程名称判断大小
     *
     * @param processName
     */
    public static int processSize(String processName) {
        int logcatSize = 0;
        ShellUtils.CommandResult commandResult = ShellUtils.execCommand("procrank |grep " + processName, true);
        if (commandResult != null) {
            String resultStr = commandResult.successMsg;
            logcatSize = count(resultStr, "logcat");
        }
        return logcatSize;
    }

    /**
     * 判断1个字符串中出现了几次其他字符串
     *
     * @param text
     * @param sub
     * @return
     */
    public static int count(String text, String sub) {
        int count = 0, start = 0;
        while ((start = text.indexOf(sub, start)) >= 0) {
            start += sub.length();
            count++;
        }
        return count;
    }

}
