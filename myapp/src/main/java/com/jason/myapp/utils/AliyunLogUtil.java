package com.jason.myapp.utils;

import android.content.Context;
import android.util.Log;
import com.aliyun.logsdk.LOGClient;
import com.aliyun.logsdk.LogException;
import com.aliyun.logsdk.LogGroup;
import com.meiguan.ipsplayer.base.common.db.dao.OSSConfigDao;
import com.meiguan.ipsplayer.base.common.db.model.OSSConfig;

/**
 * Created by qiuzi on 2017/5/15.
 */
public class AliyunLogUtil {

    private String endpoint = "cn-beijing.log.aliyuncs.com";

    private String projectName = "terminal-log";

    private String storeName = "terminal-log";

    private LOGClient logClient;

    private static final AliyunLogUtil instance = new AliyunLogUtil();

    private AliyunLogUtil() {

    }

    public static AliyunLogUtil getInstance() {
        return instance;
    }

    private boolean init(Context context) {
        try {
            OSSConfigDao dao = new OSSConfigDao(context);
            OSSConfig config = dao.getOSSConfig();
            if (config == null) {
//            throw new RuntimeException("终端未注册");
                return false;
            }
            String accessId = config.getAccessKeyId();
            String accessKey = config.getAccessKeySecret();
            logClient = new LOGClient(endpoint, accessId, accessKey, projectName);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 以LogGroup方式批量发送日志（需要在子线程中执行）
     *
     * @param context
     */
    public void postLogs(Context context, final LogGroup logGroup) throws LogException {
        if (logClient == null) {
            if (!init(context)) {
                Log.d("AliyunLog", "LogClient init failed!");
                return;
            }
        }

        logClient.PostLog(logGroup, storeName);

    }

}
