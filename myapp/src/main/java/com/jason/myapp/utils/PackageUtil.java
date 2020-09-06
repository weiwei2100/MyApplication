package com.jason.myapp.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import com.meiguan.ipsplayer.base.common.pm.IPackageDeleteObserver;
import com.meiguan.ipsplayer.base.common.pm.IPackageInstallObserver;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;

/**
 * Created by qiuzi on 15/9/8.
 */
public class PackageUtil {

    private Method installPackageMethod;

    private Method deletePackageMethod;

    private Object packageManager;

    private IPackageInstallObserver installObserver;

    private IPackageDeleteObserver deleteObserver;

    public PackageUtil() {

        try {

            Class activityThread = Class.forName("android.app.ActivityThread");
            packageManager = activityThread.getMethod("getPackageManager", getParamTypes(activityThread, "getPackageManager")).invoke(activityThread, new Object[0]);
            Class pm = packageManager.getClass();
            installPackageMethod = pm.getMethod("installPackage", getParamTypes(pm, "installPackage"));

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                deletePackageMethod = pm.getMethod("deletePackage", getParamTypes(pm, "deletePackage"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Class[] getParamTypes(Class clz, String methodName) {
        Class[] classes = null;
        Method[] methods = clz.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                classes = method.getParameterTypes();
            }
        }
        return classes;
    }

    public void deletePackage(String packageName) throws InvocationTargetException, IllegalAccessException {
        Method method = deletePackageMethod;
        Object clz = packageManager;
        Object[] params = new Object[3];
        params[0] = packageName;
        params[1] = deleteObserver;
        params[2] = Integer.valueOf(0);
        method.invoke(clz, params);
    }

    public void installPackage(Uri uri)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Method method = installPackageMethod;
        Object clz = packageManager;
        Object[] params = new Object[4];
        params[0] = uri;
        params[1] = installObserver;
        params[2] = Integer.valueOf(2);
        params[3] = null;
        method.invoke(clz, params);
    }

    public void installPackage(File file)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (!file.exists()) {
            throw new IllegalArgumentException();
        } else {
            installPackage(Uri.fromFile(file));
            return;
        }
    }

    public void installPackage(String path)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        installPackage(new File(path));
    }

    public void setInstallObserver(IPackageInstallObserver ipackageinstallobserver) {
        installObserver = ipackageinstallobserver;
    }

    public void setDeleteObserver(IPackageDeleteObserver ipackagedeleteobserver) {
        deleteObserver = ipackagedeleteobserver;
    }

    public boolean isPkgInstalled(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        android.content.pm.ApplicationInfo info = null;
        try {
            info = context.getPackageManager().getApplicationInfo(packageName, 0);
            return info != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public PackageInfo getPackageInfo(Context context, String packageName) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return packageInfo;
    }

    public PackageInfo getPackageArchiveInfo(Context context, String path) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageArchiveInfo(path, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return packageInfo;
    }

//    public void uninstallPackage(String packageName) {
//        Process process = null;
//        try {
//            process = Runtime.getRuntime().exec("system/bin/pm uninstall " + packageName);
//            process.waitFor();
//        } catch (Exception e) {
//            Log.e("APK卸载失败", packageName + e.getMessage(), e);
//        } finally {
//            try {
//                if (process != null) {
//                    process.destroy();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }


    /**
     * 静默卸载
     */
    public boolean uninstallPackage(String packageName) {
        PrintWriter PrintWriter = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            PrintWriter = new PrintWriter(process.getOutputStream());
            PrintWriter.println("LD_LIBRARY_PATH=/vendor/lib:/system/lib ");
            PrintWriter.println("pm uninstall " + packageName);
            PrintWriter.flush();
            PrintWriter.close();
            int value = process.waitFor();
            return returnResult(value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return false;
    }

    private boolean returnResult(int value) {
        // 代表成功
        if (value == 0) {
            return true;
        } else if (value == 1) { // 失败
            return false;
        } else { // 未知情况
            return false;
        }
    }

    /**
     * 静默卸载
     */
    public boolean uninstallToPackage(String packageName) {
        PrintWriter PrintWriter = null;
        Process process = null;
        try {
            String[] args = {"pm", "uninstall", packageName};
            ProcessBuilder processBuilder = new ProcessBuilder(args);
            process = processBuilder.start();
            PrintWriter = new PrintWriter(process.getOutputStream());
            PrintWriter.flush();
            PrintWriter.close();
            int value = process.waitFor();
            return returnResult(value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public boolean installToCommand(String apkPath) {
        boolean result = false;
        try {
            String[] args = {"pm", "install", "-r", apkPath};
            ProcessBuilder processBuilder = new ProcessBuilder(args);
            Process process = null;
            BufferedReader successResult = null;
            BufferedReader errorResult = null;
            StringBuilder successMsg = new StringBuilder();
            StringBuilder errorMsg = new StringBuilder();
            try {
                process = processBuilder.start();
                InputStreamReader in1 = new InputStreamReader(process.getInputStream());
                successResult = new BufferedReader(in1);
                InputStreamReader in2 = new InputStreamReader(process.getErrorStream());
                errorResult = new BufferedReader(in2);
                String s;
                while ((s = successResult.readLine()) != null) {
                    successMsg.append(s);
                }
                while ((s = errorResult.readLine()) != null) {
                    errorMsg.append(s);
                }
            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                try {
                    if (successResult != null) {
                        successResult.close();
                    }
                    if (errorResult != null) {
                        errorResult.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (process != null) {
                    process.destroy();
                }
            }
            if (successMsg.toString().contains("success") ||
                    successMsg.toString().contains("Success")) {
                result = true;
            } else {
                result = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public boolean installWithCommand(String apkPath) {
        boolean result = false;
        DataOutputStream dataOutputStream = null;
        BufferedReader errorStream = null;
        try {
            // 申请su权限
            Process process = Runtime.getRuntime().exec("su");
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            // 执行pm install命令
            String command = "pm install -r " + apkPath + "\n";
            dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));
            dataOutputStream.flush();
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            process.waitFor();
            errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String msg = "";
            String line;
            // 读取命令的执行结果
            while ((line = errorStream.readLine()) != null) {
                msg += line;
            }
            Log.d("TAG", "install msg is " + msg);
            // 如果执行结果中包含Failure字样就认为是安装失败，否则就认为安装成功
            if (!msg.contains("Failure")) {
                result = true;
            }

        } catch (Exception e) {
            Log.e("TAG", e.getMessage(), e);
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (errorStream != null) {
                    errorStream.close();
                }
            } catch (IOException e) {
                Log.e("TAG", e.getMessage(), e);
            }
        }
        return result;
    }
}
