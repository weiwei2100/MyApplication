package com.jason.myapp.utils;

import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Toast 封装
 */
public class ToastUtil {

    private Context mContext;
    private Resources mResources;

    private ToastUtil() {
        super();
    }

    private ToastUtil(Context context) {
        this.mContext = context;
        this.mResources = context.getResources();
    }

    public static void showCenterToast(Context context, String msg, int duration) {
        showCustomToast(context, msg, duration, Gravity.CENTER);
    }

    public static void showCustomToast(Context context, String msg, int duration, int gravity) {
        Toast toast = Toast.makeText(context, msg, duration);
        toast.setGravity(gravity, 0, 0);
        toast.show();
    }

    public static void showToast(Context context, String msg, int duration) {
        Toast toast = Toast.makeText(context, msg, duration);
        toast.show();
    }


    public static void ShowTimeToast(Context context, String msg, final int cnt) {
        final Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        final Timer timer = new Timer();

        timer.schedule(new TimerTask() {

            @Override

            public void run() {

                toast.show();

            }

        }, 0, Toast.LENGTH_LONG);

        new Timer().schedule(new TimerTask() {

            @Override

            public void run() {

                toast.cancel();

                timer.cancel();

            }

        }, cnt);

    }


}

