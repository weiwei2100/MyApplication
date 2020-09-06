package com.jason.myapp.utils;

import android.content.Context;
import android.os.StatFs;
import com.meiguan.ipsplayer.base.common.db.dao.StorageConfigDao;
import com.meiguan.ipsplayer.base.common.db.model.StorageConfig;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by qiuzi on 15/12/2.
 */
public class Environment {

    /**
     * 获取本机存储路径
     *
     * @return
     */
    public static String getExternalStorageDirectory() {
        try {
            Class<?> klass = Class.forName("android.os.Environment$UserEnvironment");
            Method method = klass.getDeclaredMethod("getExternalStorageDirectory");
            Object object = method.invoke(klass.getConstructor(int.class).newInstance(new Object[]{0}));
            if (object != null) {
                return object.toString();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getSDCardPath() {
        String sdcardPath;
        File internal = new File(getExternalStorageDirectory());
        File externalFolder = new File(internal.getParent() + "/extsd");
        if (externalFolder.exists()) {
            sdcardPath = externalFolder.getAbsolutePath();
        } else {
            sdcardPath = internal.getParent() + "/external_sd";
        }
        return sdcardPath;
    }

    public static String getCurrentStoragePath(Context context) {
        String concertStoragePath;
        StorageConfigDao dao = new StorageConfigDao(context);
        StorageConfig config = dao.getStorageConfig();
        if (config != null && config.getStorageType() == StorageConfig.STORAGE_SDCARD) {
            concertStoragePath = getSDCardPath();
        } else {
            concertStoragePath = getExternalStorageDirectory();
        }
        return concertStoragePath;
    }

    public static int getCurrentStorageType(Context context) {
        int type = 0;
        StorageConfigDao dao = new StorageConfigDao(context);
        StorageConfig config = dao.getStorageConfig();
        if (config != null && config.getStorageType() == StorageConfig.STORAGE_SDCARD) {
            type = StorageConfig.STORAGE_SDCARD;
        } else {
            type = StorageConfig.STORAGE_INTERNAL;
        }
        return type;
    }

    public static long getInternalTotalSize() {
        long total;
        String root = getExternalStorageDirectory();
        StatFs stat = new StatFs(root);
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        total = (blockSize * totalBlocks) / 1024 / 1024;
        return total;
    }

    public static long getInternalAvailableSize() {
        long available;
        String root = getExternalStorageDirectory();
        StatFs stat = new StatFs(root);
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        available = (availableBlocks * blockSize) / 1024 / 1024;
        return available;
    }

    public static long getSDCardTotalSize() {
        long total;
        String root = getSDCardPath();
        StatFs stat = new StatFs(root);
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        total = (blockSize * totalBlocks) / 1024 / 1024;
        return total;
    }

    public static long getSDCardAvailableSize() {
        long available;
        String root = getSDCardPath();
        StatFs stat = new StatFs(root);
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        available = (availableBlocks * blockSize) / 1024 / 1024;
        return available;
    }

}
