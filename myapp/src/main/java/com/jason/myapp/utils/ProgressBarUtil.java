package com.jason.myapp.utils;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * 圆形进度条封装
 */
public class ProgressBarUtil {

    public static ProgressDialog dialog;

    private Context context = null;

    private ProgressBarUtil() {
    }

    private ProgressBarUtil init(Context context) {
        this.context = context;
        return this;
    }

    public static ProgressBarUtil create(Context context) {
        ProgressBarUtil dialog = new ProgressBarUtil().init(context);
        return dialog;
    }


    public static ProgressDialog showProgressDialog(Context context, String message) {
        dialog = ProgressDialog.show(context, "", message);
        dialog.setCancelable(false);
        return dialog;
    }

    public static ProgressDialog showProgressDialog(Context context, String title, String message) {
        dialog = ProgressDialog.show(context, title, message);
        dialog.setCancelable(false);
        return dialog;
    }

    public static void updateMessage(String message) {
        if (dialog != null) {
            dialog.setMessage(message);
        }
    }

    public ProgressDialog showProgressDialog() {
        return showProgressDialog("加载中...");
    }

    public ProgressDialog showProgressDialog(String message) {
        ProgressDialog dialog = ProgressDialog.show(context, "", message);
        dialog.setCancelable(true);
        return dialog;
    }

    public static void dismissLoadingDialog() {
        if (dialog == null)
            return;
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }


}
