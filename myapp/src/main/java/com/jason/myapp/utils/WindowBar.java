package com.jason.myapp.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 弹窗辅助类
 * <p>
 * Created by sunjainchao.
 */
public class WindowBar {

    private static View mView = null;
    private static WindowManager mWindowManager = null;
    private static Context mContext = null;

    public static Boolean isShown = false;

    private int duration = 10 * 1000;

    /**
     * 显示弹出框
     *
     * @param context
     */
    public static void makeBar(final Context context, Object message, int duration) {
        if (isShown) {
            hide();
        }
        isShown = true;

        final WindowBar windowBar = new WindowBar();

        windowBar.duration = duration;

        mContext = context;
        // 获取WindowManager
        mWindowManager = (WindowManager) mContext.getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);

        mView = setUpView(context, message);

        final LayoutParams params = new LayoutParams();

        // 类型
        params.type = LayoutParams.TYPE_SYSTEM_ALERT;

        int flags = LayoutParams.FLAG_ALT_FOCUSABLE_IM | LayoutParams.FLAG_NOT_FOCUSABLE;
        // 如果设置了WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE，弹出的View收不到Back键的事件
        params.flags = flags;
        // 不设置这个弹出框的透明遮罩显示为黑色
        params.format = PixelFormat.TRANSLUCENT;

        params.width = LayoutParams.WRAP_CONTENT;
        params.height = LayoutParams.WRAP_CONTENT;

        params.gravity = Gravity.CENTER;

//        params.windowAnimations = R.style.windowbar_anim;

        Handler addViewhandler = new Handler(mContext.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                mWindowManager.addView(mView, (LayoutParams) msg.obj);
                timer = new Timer();
                task = new TimerTask() {
                    public void run() {
                        hide();
                    }
                };
                timer.schedule(task, windowBar.duration);

            }
        };
        Message msg = addViewhandler.obtainMessage(1);
        msg.obj = params;
        addViewhandler.sendMessage(msg);
    }

    static Timer timer;
    static TimerTask task;

    /**
     * 隐藏弹出框
     */
    private static void hide() {
        if (isShown && null != mView) {
            mWindowManager.removeView(mView);
            isShown = false;
            if (null != timer && null != task) {
                task.cancel();
                timer.cancel();
                task = null;
                timer = null;
            }
        }
    }

    private static View setUpView(final Context context, Object text) {
        TextView textView = new TextView(context);
        textView.setText(null == text ? "null" : String.valueOf(text));
        GradientDrawable gd = new GradientDrawable();//创建drawable
        gd.setColor(Color.argb(30, 0, 0, 0));
        gd.setCornerRadius(10);

        textView.setBackground(gd);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(30);
        textView.setPadding(10, 10, 10, 10);

        return textView;

    }
}