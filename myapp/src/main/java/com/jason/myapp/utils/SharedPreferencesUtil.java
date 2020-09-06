package com.jason.myapp.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Base64;

import java.io.*;


/**
 * @author sunjianchao
 * SharedPreference的工具类
 */
public class SharedPreferencesUtil {

    public static final String TICKETCOORDINATE = "ticketCoordinate";

    public static final String ORISTAR_IP = "oristar_ip";
    public static final String ORISTAR_PORT = "oristar_port";
    public static final String ORISTAR_CINEMA_CODE = "oristar_cinema_code";

    public static boolean getBoolean(Context context, String key) {
        return getSharedPreferences(context).getBoolean(key, false);
    }

    public static boolean getBoolean(Context context, String prefName, String prefKey,
                                     boolean defaultValue) {
        return getSharedPreferences(context, prefName).getBoolean(prefKey, defaultValue);
    }

    public static void putBoolean(Context context, String key, boolean b) {
        getSharedPreferences(context).edit().putBoolean(key, b).commit();
    }

    public static void putBoolean(Context context, String prefName, String prefKey, boolean value) {
        getSharedPreferences(context, prefName).edit().putBoolean(prefKey, value).commit();
    }

    public static String getString(Context context, String key) {
        return getSharedPreferences(context).getString(key, "");
    }

    public static String getString(Context context, String key, String defaultValue) {
        return getSharedPreferences(context).getString(key, defaultValue);
    }

    public static String getString(Context context, String prefName, String prefKey,
                                   String defaultValue) {
        return getSharedPreferences(context, prefName).getString(prefKey, defaultValue);
    }

    public static void putString(Context context, String key, String value) {
        getSharedPreferences(context).edit().putString(key, value).commit();
    }

    public static void putString(Context context, String prefName, String prefKey, String value) {
        getSharedPreferences(context, prefName).edit().putString(prefKey, value).commit();
    }

    public static long getLong(Context context, String key) {
        return getSharedPreferences(context).getLong(key, -1L);
    }

    public static long getLong(Context context, String key, long defalutValue) {
        return getSharedPreferences(context).getLong(key, defalutValue);
    }

    public static long getLong(Context context, String prefName, String prefKey, long defaultValue) {
        return getSharedPreferences(context, prefName).getLong(prefKey, defaultValue);
    }

    public static void putLong(Context context, String key, long value) {
        getSharedPreferences(context).edit().putLong(key, value).commit();
    }

    public static void putLong(Context context, String prefName, String prefKey, long value) {
        getSharedPreferences(context, prefName).edit().putLong(prefKey, value).commit();
    }

    public static int getInt(Context context, String key) {
        return getSharedPreferences(context).getInt(key, -1);
    }

    public static int getInt(Context context, String key, int defaultValue) {
        return getSharedPreferences(context).getInt(key, defaultValue);
    }

    public static int getInt(Context context, String prefName, String prefKey, int defaultValue) {
        return getSharedPreferences(context, prefName).getInt(prefKey, defaultValue);
    }

    public static void putInt(Context context, String key, int value) {
        getSharedPreferences(context).edit().putInt(key, value).commit();
    }

    public static void putInt(Context context, String prefName, String prefKey, int value) {
        getSharedPreferences(context, prefName).edit().putInt(prefKey, value).commit();
    }

    public static float getFloat(Context context, String prefName, String prefKey,
                                 float defaultValue) {
        return getSharedPreferences(context, prefName).getFloat(prefKey, defaultValue);
    }

    public static void putFloat(Context context, String prefName, String prefKey, float value) {
        getSharedPreferences(context, prefName).edit().putFloat(prefKey, value).commit();
    }

    public static void putFloat(Context context, String prefKey, float value) {
        getSharedPreferences(context).edit().putFloat(prefKey, value).commit();
    }


    public static void remove(Context context, String prefKey) {
        getSharedPreferences(context).edit().remove(prefKey).commit();
    }


    public static void clear(Context context, String preName) {
        getSharedPreferences(context, preName).edit().clear().commit();
    }


    public static SharedPreferences getSharedPreferences(Context context) {

        return getSharedPreferences(context, null);
    }


    public static SharedPreferences getSharedPreferences(Context context, String prefName) {
        if (TextUtils.isEmpty(prefName)) {
            return PreferenceManager.getDefaultSharedPreferences(context);
        } else {
            return context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        }
    }

    public static boolean setObjectToShare(Context context, Object object,
                                           String key) {
        // TODO Auto-generated method stub
        SharedPreferences share = PreferenceManager
                .getDefaultSharedPreferences(context);
        if (object == null) {
            SharedPreferences.Editor editor = share.edit().remove(key);
            return editor.commit();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        // 将对象放到OutputStream中
        // 将对象转换成byte数组，并将其进行base64编码
        String objectStr = new String(Base64.encode(baos.toByteArray(),
                Base64.DEFAULT));
        try {
            baos.close();
            oos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        SharedPreferences.Editor editor = share.edit();
        // 将编码后的字符串写到base64.xml文件中
        editor.putString(key, objectStr);
        return editor.commit();
    }


    public static Object getObjectFromShare(Context context, String key) {
        SharedPreferences sharePre = PreferenceManager
                .getDefaultSharedPreferences(context);
        try {
            String wordBase64 = sharePre.getString(key, "");
            // 将base64格式字符串还原成byte数组
            if (wordBase64 == null || wordBase64.equals("")) { // 不可少，否则在下面会报java.io.StreamCorruptedException
                return null;
            }
            byte[] objBytes = Base64.decode(wordBase64.getBytes(),
                    Base64.DEFAULT);
            ByteArrayInputStream bais = new ByteArrayInputStream(objBytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            // 将byte数组转换成product对象
            Object obj = ois.readObject();
            bais.close();
            ois.close();
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
