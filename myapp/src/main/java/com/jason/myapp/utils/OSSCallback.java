package com.jason.myapp.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import com.ips.system.message.DownloadProgress;

/**
 * Created by qiuzi on 2016/12/1.
 */
public class OSSCallback {

    private Context context;

    private String planId;

    private String sourceId;

    public OSSCallback(Context context, String planId, String sourceId) {
        this.context = context;
        this.planId = planId;
        this.sourceId = sourceId;
    }

    public void sendDownloadProgress(long sourceSize, long completeSize, int state) {

        if (TextUtils.isEmpty(planId) || TextUtils.isEmpty(sourceId)) {
            return;
        }

        DownloadProgress downloadProgress = new DownloadProgress();
        downloadProgress.setPlanId(planId);
        downloadProgress.setSourceId(sourceId);
        downloadProgress.setSourceSize(sourceSize);
        downloadProgress.setCompleteSize(completeSize);
        downloadProgress.setState(state);

        sendDownloadProgressToBus(downloadProgress);

    }

    public void sendUploadResult(boolean result, String filePath) {
        Intent intent = new Intent(IntentActionUtil.ACTION_SCREENSHOT_UPLOAD_RESULT);
        Bundle bundle = new Bundle();
        if (!result) {
            filePath = "data/ftp/static/terminal_img/error.png";
        } else {
            // 设置图片预览的旋转角度
            SystemUtil systemUtil = new SystemUtil(context);
            String rotation = systemUtil.getRotation();
            if ("90".equals(rotation)) {
                rotation = "270";
            } else if ("270".equals(rotation)) {
                rotation = "90";
            }
            String params = "?x-oss-process=image/rotate," + rotation;
            filePath = filePath + params;
        }
        bundle.putString("filePath", filePath);
        bundle.putBoolean("result", result);
        intent.putExtras(bundle);
        context.sendBroadcast(intent);
    }

    private void sendDownloadProgressToBus(DownloadProgress downloadProgress) {
        Intent intent = new Intent(IntentActionUtil.ACTION_DOWNLOAD_PROGRESS);
        Bundle bundle = new Bundle();
        bundle.putSerializable("downloadProgress", downloadProgress);
        intent.putExtras(bundle);
        context.sendBroadcast(intent);
    }
}
