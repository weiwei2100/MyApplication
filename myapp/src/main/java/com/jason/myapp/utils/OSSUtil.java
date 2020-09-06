package com.jason.myapp.utils;

import android.content.Context;
import android.util.Log;
import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.model.*;
import com.ips.system.message.DownloadProgress;
import com.meiguan.ipsplayer.base.common.db.dao.OSSConfigDao;
import com.meiguan.ipsplayer.base.common.db.model.OSSConfig;

import java.io.*;

/**
 * Created by qiuzi on 16/2/19.
 */
public class OSSUtil {

//    private static OSSUtil ossUtil;

    private static OSSCredentialProvider credentialProvider;

    private static String endpoint;

    private static String bucketName;

    private Context context;

    private boolean isStop = false;

    public OSSUtil(Context context) {
        this.context = context;
    }

    //    public static OSSUtil getOssUtil(Context context) {
//        if (ossUtil == null) {
//            ossUtil = new OSSUtil(context);
//        }
//        return ossUtil;
//    }
    public void Start() {
        this.isStop = false;
    }

    public void Stop() {
        Log.e("stop", "ossutil stop");
        this.isStop = true;
    }

    private ClientConfiguration getConfig() {
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
        conf.setMaxErrorRetry(10); // 失败后最大重试次数，默认2次
        return conf;
    }

    private void initOSS(Context context) {
        if (credentialProvider == null || endpoint == null || bucketName == null) {
            OSSConfigDao ossConfigDao = new OSSConfigDao(context);
            OSSConfig ossConfig = ossConfigDao.getOSSConfig();
            if (ossConfig == null) {
                throw new RuntimeException("缺少OSS配置信息");
            }
            bucketName = ossConfig.getBucketName();
            endpoint = "https://" + ossConfig.getEndpoint();
            String accessKeyId = ossConfig.getAccessKeyId();
            String accessKeySecret = ossConfig.getAccessKeySecret();
            credentialProvider = new OSSPlainTextAKSKCredentialProvider(accessKeyId, accessKeySecret);
        }
    }

    public long remoteCouponSize(String remote) throws Exception {
        initOSS(context);
        OSSClient client = new OSSClient(context, endpoint, credentialProvider, getConfig());
        // 获取远程文件大小
        long remoteSize = getRemoteFileSize(client, remote);
        return remoteSize;
    }

    public boolean downloadSync(String remote, String local)
            throws Exception {

        initOSS(context);

        OSSClient client = new OSSClient(context, endpoint, credentialProvider, getConfig());
        InputStream inputStream = null;
        OutputStream outputStream = null;
        boolean result = false;

        try {
            // 创建上层文件夹
            File localFile = new File(local);
            File folder = localFile.getParentFile();
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // 获取远程文件大小
            long remoteSize = getRemoteFileSize(client, remote);

            // 远程文件大小为0则退出下载
            if (remoteSize <= 0) {
                return false;
            }

            // 本地文件与OSS文件大小一致则不执行下载
            if (localFile.exists() && localFile.length() == remoteSize) {
                return true;
            }

            // 本地文件大小超出OSS文件大小则重新下载
            if (localFile.exists() && localFile.length() > remoteSize) {
                localFile.delete();
            }
            if (isStop) {
                return false;
            }

            // 判断是否需要断点续传
            long startIndex = 0;
            if (localFile.exists() && localFile.length() > 0) {
                startIndex = localFile.length();
            }

            Log.d("OSSDownload", "local file size:" + localFile.length());

            // 创建下载请求
            GetObjectRequest request = new GetObjectRequest(bucketName, remote);

            // 设置下载范围
            Range range = new Range(startIndex, remoteSize - 1);
            request.setRange(range);
            Log.d("OSSDownload", "range:(" + range.getBegin() + ", " + range.getEnd() + ")");

            // 同步下载
            GetObjectResult getObjectResult = client.getObject(request);
            Log.d("OSSDownload", "request file length:" + getObjectResult.getContentLength());
            inputStream = getObjectResult.getObjectContent();
            outputStream = new FileOutputStream(localFile, true);
            byte[] buffer = new byte[4 * 1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
                if (isStop) {
                    Log.e("download", "有下载级别高的任务进入下载队列，Ossutil停止下载");
                    getObjectResult = null;
                    buffer = null;
                    client = null;
                    inputStream.close();
                    outputStream.close();
                    localFile.delete();
                    return false;
                }
            }

            // 下载结束后判断本地文件是否完整
            if (localFile.length() == remoteSize) {
                result = true;
            }
            Log.d("OSSDownload", "download result:" + localFile.length() + "/" + remoteSize);
        } catch (ServiceException e) {

            throw new OSSFileDownloadException(e.getErrorCode() + "服务器有错误信息:" + e.getStatusCode() + e.getMessage());

        } catch (Exception e) {

            throw e;

        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;

    }

    public boolean downloadSync(String remote, String local, OSSCallback callback)
            throws Exception {

        if (callback == null) {
            throw new RuntimeException("callback is null.");
        }

        initOSS(context);

        OSSClient client = new OSSClient(context, endpoint, credentialProvider, getConfig());
        InputStream inputStream = null;
        OutputStream outputStream = null;
        boolean result = false;

        // 远程文件大小
        long remoteSize = 0;

        // 整个文件完成的大小
        long completeSize = 0;

        // 进度大小，达到bufferSize后发送，发送进度后清零
        long progressSize = 0;

        // 判断是否发送进度的缓冲区总大小
        long bufferSize = 1 * 1024 * 1024;

        try {
            // 创建上层文件夹
            File localFile = new File(local);
            File folder = localFile.getParentFile();
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // 获取远程文件大小
            remoteSize = getRemoteFileSize(client, remote);

            // 远程文件大小为0则退出下载
            if (remoteSize <= 0) {
                callback.sendDownloadProgress(remoteSize, 0, DownloadProgress.STATE_FAILED);
                return false;
            }

            // 本地文件与OSS文件大小一致则不执行下载
            if (localFile.exists() && localFile.length() == remoteSize) {
                callback.sendDownloadProgress(remoteSize, remoteSize, DownloadProgress.STATE_SUCCESS);
                return true;
            }

            // 本地文件大小超出OSS文件大小则重新下载
            if (localFile.exists() && localFile.length() > remoteSize) {
                localFile.delete();
            }
            if (isStop) {
                callback.sendDownloadProgress(remoteSize, 0, DownloadProgress.STATE_FAILED);
                return false;
            }

            // 判断是否需要断点续传
            long startIndex = 0;
            if (localFile.exists() && localFile.length() > 0) {
                startIndex = localFile.length();
                completeSize = localFile.length();
            }

            Log.d("OSSDownload", "local file size:" + localFile.length());

            // 创建下载请求
            GetObjectRequest request = new GetObjectRequest(bucketName, remote);

            // 设置下载范围
            Range range = new Range(startIndex, remoteSize - 1);
            request.setRange(range);
            Log.d("OSSDownload", "range:(" + range.getBegin() + ", " + range.getEnd() + ")");

            // 同步下载
            GetObjectResult getObjectResult = client.getObject(request);
            Log.d("OSSDownload", "request file length:" + getObjectResult.getContentLength());
            inputStream = getObjectResult.getObjectContent();
            outputStream = new FileOutputStream(localFile, true);
            byte[] buffer = new byte[4 * 1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1 && !isStop) {
                outputStream.write(buffer, 0, len);

                // 记录到进度缓冲区
                progressSize += len;
                completeSize += len;

                // 判断进度是否可以发送
                if (progressSize >= bufferSize) {
                    callback.sendDownloadProgress(remoteSize, completeSize, DownloadProgress.STATE_DOWNLOADING);
                    progressSize = 0;
                }
            }

            // 下载结束后判断本地文件是否完整
            if (localFile.length() == remoteSize) {
                result = true;
                callback.sendDownloadProgress(remoteSize, completeSize, DownloadProgress.STATE_SUCCESS);
            }
            Log.d("OSSDownload", "download result:" + localFile.length() + "/" + remoteSize);
        } catch (ServiceException e) {
            callback.sendDownloadProgress(remoteSize, completeSize, DownloadProgress.STATE_FAILED);
            throw new OSSFileDownloadException(e.getErrorCode() + "服务器有错误信息:" + e.getStatusCode() + e.getMessage());

        } catch (Exception e) {
            callback.sendDownloadProgress(remoteSize, completeSize, DownloadProgress.STATE_FAILED);
            throw e;

        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;

    }

    public boolean downloadAdSource(String remote, String local, AdSourceDownloadCallback callback)
            throws Exception {

        if (callback == null) {
            throw new RuntimeException("callback is null.");
        }

        initOSS(context);

        OSSClient client = new OSSClient(context, endpoint, credentialProvider, getConfig());
        InputStream inputStream = null;
        OutputStream outputStream = null;
        boolean result = false;

        // 远程文件大小
        long remoteSize = 0;

        // 整个文件完成的大小
        long completeSize = 0;

        // 进度大小，达到bufferSize后发送，发送进度后清零
        long progressSize = 0;

        // 判断是否发送进度的缓冲区总大小
        long bufferSize = 1 * 1024 * 1024;

        try {
            // 创建上层文件夹
            File localFile = new File(local);
            File folder = localFile.getParentFile();
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // 获取远程文件大小
            remoteSize = getRemoteFileSize(client, remote);

            // 远程文件大小为0则退出下载
            if (remoteSize <= 0) {
                callback.sendDownloadProgress(remoteSize, 0, DownloadProgress.STATE_FAILED);
                return false;
            }

            // 本地文件与OSS文件大小一致则不执行下载
            if (localFile.exists() && localFile.length() == remoteSize) {
                callback.sendDownloadProgress(remoteSize, remoteSize, DownloadProgress.STATE_SUCCESS);
                return true;
            }

            // 本地文件大小超出OSS文件大小则重新下载
            if (localFile.exists() && localFile.length() > remoteSize) {
                localFile.delete();
            }
            if (isStop) {
                callback.sendDownloadProgress(remoteSize, 0, DownloadProgress.STATE_FAILED);
                return false;
            }

            // 判断是否需要断点续传
            long startIndex = 0;
            if (localFile.exists() && localFile.length() > 0) {
                startIndex = localFile.length();
                completeSize = localFile.length();
            }

            Log.d("OSSDownload", "local file size:" + localFile.length());

            // 创建下载请求
            GetObjectRequest request = new GetObjectRequest(bucketName, remote);

            // 设置下载范围
            Range range = new Range(startIndex, remoteSize - 1);
            request.setRange(range);
            Log.d("OSSDownload", "range:(" + range.getBegin() + ", " + range.getEnd() + ")");

            // 同步下载
            GetObjectResult getObjectResult = client.getObject(request);
            Log.d("OSSDownload", "request file length:" + getObjectResult.getContentLength());
            inputStream = getObjectResult.getObjectContent();
            outputStream = new FileOutputStream(localFile, true);
            byte[] buffer = new byte[4 * 1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1 && !isStop) {
                outputStream.write(buffer, 0, len);

                // 记录到进度缓冲区
                progressSize += len;
                completeSize += len;

                // 判断进度是否可以发送
                if (progressSize >= bufferSize) {
                    callback.sendDownloadProgress(remoteSize, completeSize, DownloadProgress.STATE_DOWNLOADING);
                    progressSize = 0;
                }
            }

            // 下载结束后判断本地文件是否完整
            if (localFile.length() == remoteSize) {
                result = true;
                callback.sendDownloadProgress(remoteSize, completeSize, DownloadProgress.STATE_SUCCESS);
            }
            Log.d("OSSDownload", "download result:" + localFile.length() + "/" + remoteSize);
        } catch (ServiceException e) {
            callback.sendDownloadProgress(remoteSize, completeSize, DownloadProgress.STATE_FAILED);
            throw new OSSFileDownloadException(e.getErrorCode() + "服务器有错误信息:" + e.getStatusCode() + e.getMessage());

        } catch (Exception e) {
            callback.sendDownloadProgress(remoteSize, completeSize, DownloadProgress.STATE_FAILED);
            throw e;

        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;

    }

    private long getRemoteFileSize(OSSClient client, String remote) throws ClientException, ServiceException {
        long fileSize = 0;
        // 创建同步获取文件元信息请求
        HeadObjectRequest head = new HeadObjectRequest(bucketName, remote);
        try {
            HeadObjectResult headObject = client.headObject(head);
            fileSize = headObject.getMetadata().getContentLength();
        } catch (ClientException e) {
            throw e;
        } catch (ServiceException e) {
            throw e;
        }
        return fileSize;
    }

    public void uploadAsync(Context context, String local, String remote, final OSSCallback callback) {

        initOSS(context);

        OSSClient client = new OSSClient(context, endpoint, credentialProvider, getConfig());

        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(bucketName, remote, local);

        // 异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });

        client.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                callback.sendUploadResult(true, request.getObjectKey());
                Log.d("PutObject", "UploadSuccess");
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                callback.sendUploadResult(false, null);
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });

    }
}
