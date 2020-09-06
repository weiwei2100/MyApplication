package com.jason.myapp;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by qiuzi on 15/5/20.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private String TAG = "CrashHandler";
    private static final String LOG_ROOT_PATH = "ips/log";
    private static CrashHandler instance;
    private Context context;
    private Thread.UncaughtExceptionHandler handler;

    private CrashHandler() {
    }

    public synchronized static CrashHandler getInstance() {
        if (instance == null) {
            instance = new CrashHandler();
        }
        return instance;
    }

    public void init(Context ctx) {
        context = ctx;
        handler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        String exceptions = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + ":" + ex.toString() + " stack trace:\n";
        StackTraceElement[] stackElements = ex.getStackTrace(); // 异常堆栈
        if (stackElements != null) {
            String exception = "";
            for (int i = 0; i < stackElements.length; i++) {
                exception += stackElements[i].getClassName() + ":";
                exception += stackElements[i].getMethodName() + " at line:";
                exception += stackElements[i].getLineNumber() + "\n";
                exceptions += exception;
            }
        }
        FileUtil.getInstance().writeFile(LOG_ROOT_PATH + "/" + context.getPackageName() + ".log", exceptions);
//        int pid = android.os.Process.myPid();
//        String name = getProcessName(pid);
//        Log.d(TAG, "进程id:"+pid+",进程名："+name);
//
//        //TODO 记录日志
//
//        if (IntentActionUtil.PKG_COLLECTION.equals(name)) {
//            try {
//                Thread.sleep(60000);
//                //TODO 重启
//                SystemUtil systemUtil = new SystemUtil(context);
//                systemUtil.reboot();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
        //TODO 向总线广播异常信息
//        android.os.Process.killProcess(android.os.Process.myPid());

//        // 结束当前进程并于3秒后重启应用
//        Intent intent = new Intent(ipsContext, MyActivity.class);
//        PendingIntent restartIntent = PendingIntent.getActivity(
//                ipsContext, 0, intent,
//                PendingIntent.FLAG_CANCEL_CURRENT);
//        //退出程序
//        AlarmManager mgr = (AlarmManager)ipsContext.getSystemService(Context.ALARM_SERVICE);
//        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 3000,
//                restartIntent); // 3秒钟后重启应用
//        android.os.Process.killProcess(android.os.Process.myPid());

    }

    public String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Exception e) {
            Log.e(TAG, "getProcessName read is fail. exception=" + e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "getProcessName close is fail. exception=" + e);
            }
        }
        return null;
    }
}
