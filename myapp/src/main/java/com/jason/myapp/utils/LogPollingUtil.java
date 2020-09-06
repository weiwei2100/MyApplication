package com.jason.myapp.utils;

import android.content.Context;
import android.util.Log;
import com.meiguan.ipsplayer.base.common.db.model.Config;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by micrown on 16/12/19.
 */
public class LogPollingUtil {

    private static LogPollingUtil instance;
    private boolean logFlag;
    private String logPath = Environment.getExternalStorageDirectory() + "/log/";
    private final String LOGCAT = "logcat";
    private static final int MAX_POST_SIZE = 200 * 1024 * 1024;
    private Config config;
    private ConfigUtil configUtil;


    public static LogPollingUtil getInstance() {

        if (instance == null) {
            instance = new LogPollingUtil();
        }
        return instance;
    }

    private LogPollingUtil() {

    }

    /**
     * 开启日志
     *
     * @param context
     */
    public void startThreadExecute(final Context context) {

        /**
         * 开始记录logcat
         */

        ThreadPoolUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                Log.d("logPoll", "开始记录log");

                ShellUtils.execCommand("logcat -f " +
                        Environment.getCurrentStoragePath(context) +
                        "/log/log.txt" + " -v time &", true);

                LogUtils.getInstance().d("logPoll", "logcat进程已经关闭");

            }
        });

    }

    public void zip(Context context) throws Exception {

        if (config == null) {
            configUtil = new ConfigUtil(context);
            if (!configUtil.isRegistered()) {
                return;
            }
            config = configUtil.getConfig();
            Log.e("logPoll", "config == null,config对象:" + config);
        }

        Log.e("logPoll", "开始压缩");
        //压缩文件
        XZip.ZipFolder(logPath + "log.txt", Environment.getExternalStorageDirectory()
                + "/log/" + config.getTerminalId() + "_"
                + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date())
                + "_log.zip");
        Log.e("logPoll", "压缩完成,准备删除log.txt");

    }

    /**
     * 删除log.txt文件
     *
     * @param path
     * @throws Exception
     */
    public void removeLogFile(String path) throws Exception {

        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        Log.e("logPoll", "删除log.txt文件");

    }


//    /**
//     * 停止log
//     */
//    public void stopThreadExecute(Context context) throws Exception {
//
//        logFlag = false;
//
//        if (config == null) {
//            configUtil = new ConfigUtil(context);
//            if (!configUtil.isRegistered()) {
//                return;
//            }
//            config = configUtil.getConfig();
//            Log.e("logPoll", "config == null,config对象:"+config);
//
//        }
//
//        //ToDo 停止logcat的进程
//        IntentActionUtil.killProcess(LOGCAT);
//
//        /**
//         * 调用check方法 判断进程是否停止掉
//         *
//         */
//        boolean logRinningFlag = IntentActionUtil.isProcessRunning(LOGCAT);
//        Log.d("logPoll", "判断log进程是否停止掉:" + logRinningFlag);
//        if (logRinningFlag) {
//            /**
//             * remove log.txt,继续开启记录日志
//             */
//            Log.d("logPoll", "log进程停掉,删除log.txt");
//            //压缩文件
//            XZip.ZipFolder(logPath + "log.txt", Environment.getExternalStorageDirectory()
//                    + "/log/" + config.getTerminalId() + "_"
//                    + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date())
//                    + "_log.zip");
//            Log.e("logPoll", "走if压缩完成,准备删除log.txt");
//            removeLogFile(logPath + "log.txt");
//            //ToDo 继续记录log
//            Log.d("logPoll", "准备继续记录log,标记是否正在记录--" + logFlag);
//            startThreadExecute(context);
//        } else {
//            /**
//             * 没有停止掉,remove log.txt
//             */
//            Log.d("logPoll", "没有停止掉进程,尝试删除进程3次");
//            for (int i = 0; i < 3; i++) {
//
//                Log.d("logPoll", "尝试停止log进程--" + i);
//                IntentActionUtil.killProcess(LOGCAT);
//                boolean logRinningFlagTwo = IntentActionUtil.isProcessRunning(LOGCAT);
//                Log.d("logPoll", "尝试判断log进程是否停止掉:" + logRinningFlagTwo);
//                if (logRinningFlagTwo) {
//                    //ToDo 继续记录log
//                    Log.d("logPoll", "尝试成功,准备继续记录log,标记是否正在记录--" + logFlag);
//                    XZip.ZipFolder(logPath + "log.txt", Environment.getExternalStorageDirectory()
//                            + "/log/" + config.getTerminalId() + "_"
//                            + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date())
//                            + "_log.zip");
//                    Log.e("logPoll", "走for压缩完成,准备删除log.txt");
//                    removeLogFile(logPath + "log.txt");
//                    startThreadExecute(context);
//                    break;
//                } else {
//                    continue;
//                }
//
//            }
//            XZip.ZipFolder(logPath + "log.txt", Environment.getExternalStorageDirectory()
//                    + "/log/" + config.getTerminalId() + "_"
//                    + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date())
//                    + "_log.zip");
//            Log.e("logPoll", "走else压缩完成,准备删除log.txt");
//            removeLogFile(logPath + "log.txt");
//        }
//    }


//    /**
//     * 文件不存在,判断是否在记录log
//     */
//    public void isRecordLog(File file, Context context) throws Exception {
//        /**
//         * 判断是否正在记录log
//         */
//        Log.d("logPoll", "文件不存在,判断是否正在记录log:" + logFlag);
//        if (!logFlag) {
//            /**
//             * 没有记录log,开始记录log
//             */
//            Log.d("logPoll","没有记录log,开始记录log,创建文件txt");
//            file.createNewFile();
//            //txt不存在,开始记录log
//            startThreadExecute(context);
//
//        } else {
//            /**
//             * 已经开始记录log,执行stop
//             */
//            Log.d("logPoll", "已经开始记录log,执行stop");
//            stopThreadExecute(context);
//        }
//    }


//    /**
//     * 文件存在,判断是否在记录log,判断大小是否到100M
//     */
//    public void logFile(File f, Context context) throws Exception {
//        /**
//         * 文件存在,判断大小是否到100M
//         */
//
//        Log.d("logPoll", "文件存在,判断log日志是否超出限额,标记是否正在记录:" + logFlag);
//
//        if (logFlag && f.length() != 0 && f.length() > MAX_POST_SIZE) {
//            /**
//             * 达到100M,执行stop
//             */
//
//            Log.d("logPoll", "文件存在,检测文件大小为:"+f.length()+",达到"+MAX_POST_SIZE+",执行stop");
//            LogPollingUtil.getInstance().stopThreadExecute(context);
//
//
//        } else if (!logFlag && f.length() != 0) {
//            /**
//             * 检测文件未达到100M,没有记录日志,继续记录日志
//             */
//            Log.d("logPoll", "检测文件大小为:"+f.length()+",未达到"+MAX_POST_SIZE+",没有记录日志,继续记录日志");
//            startThreadExecute(context);
//
//        } else {
//            /**
//             * 未达到100M,继续记录日志
//             */
//            Log.d("logPoll", "检测文件大小为:"+f.length()+",未达到"+MAX_POST_SIZE+",继续记录日志");
//        }
//    }


}
