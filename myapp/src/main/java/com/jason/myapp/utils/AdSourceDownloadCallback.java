package com.jason.myapp.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.ips.system.message.DownloadProgress;

/**
 * Created by qiuzi on 2016/12/1.
 */
public class AdSourceDownloadCallback {

    private Context context;

    private String sourceId;

    public AdSourceDownloadCallback(Context context, String sourceId) {
        this.context = context;
        this.sourceId = sourceId;
    }

    public void sendDownloadProgress(long sourceSize, long completeSize, int state) {

        DownloadProgress downloadProgress = new DownloadProgress();
//        downloadProgress.setPlanId(planId);
        downloadProgress.setSourceId(sourceId);
        downloadProgress.setSourceSize(sourceSize);
        downloadProgress.setCompleteSize(completeSize);
        downloadProgress.setState(state);

        sendDownloadProgressToBus(downloadProgress);

    }

    private void sendDownloadProgressToBus(DownloadProgress downloadProgress) {
        Intent intent = new Intent(IntentActionUtil.ACTION_AD_DOWNLOAD_PROGRESS);
        Bundle bundle = new Bundle();
        bundle.putSerializable("downloadProgress", downloadProgress);
        intent.putExtras(bundle);
        context.sendBroadcast(intent);
    }
}
