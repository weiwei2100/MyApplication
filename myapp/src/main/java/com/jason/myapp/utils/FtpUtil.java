package com.jason.myapp.utils;

import android.util.Log;
import com.ips.system.message.DownloadProgress;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.*;

/**
 * Created by lizhiqi on 15/4/19.
 */
public class FtpUtil {

    private String ip;

    private int port;

    private String user;

    private String password;

    private boolean isStop = false;

    public FtpUtil(String ip, int port, String user, String password) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    /**
     * 创建FTP连接
     *
     * @return
     * @throws IOException
     */
    private FTPClient connect() throws IOException {

        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(ip, port);
        ftpClient.login(user, password);
        ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
        ftpClient.enterLocalPassiveMode();

        return ftpClient;
    }

    /**
     * 断开FTP连接
     *
     * @param ftpClient
     * @throws IOException
     */
    private void disconnect(FTPClient ftpClient) throws IOException {

        if (ftpClient == null) {
            return;
        }

        ftpClient.disconnect();
    }

    public void stop() {
        this.isStop = true;
    }

    /**
     * 下载文件
     *
     * @param remote
     * @param local
     * @return
     */
    public boolean download(String remote, String local) {
        FTPClient ftpClient = null;

        InputStream in = null;

        OutputStream os = null;

        boolean result = false;

        // 远程文件大小
        long remoteSize = 0;

        // 本地文件大小
        long localSize = 0;

        try {
            ftpClient = this.connect();

            String dir = ftpClient.printWorkingDirectory();
            Log.d("FTPDownload", dir);

            // 判断远程文件是否存在
            FTPFile[] files = ftpClient.listFiles(remote);
            if (files.length != 1) {
                return false;
            }

            // 判断远程文件大小
            remoteSize = files[0].getSize();
            if (remoteSize <= 0) {
                return false;
            }

            // 创建上层文件夹
            File localFile = new File(local);
            File folder = localFile.getParentFile();
            if (!folder.exists()) {
                folder.mkdirs();
            }

            if (!localFile.exists()) {
                localFile.createNewFile();
            }

            localSize = localFile.length();

            // 本地文件与FTP文件大小一致则不执行下载
            if (localSize == remoteSize) {
                return true;
            }

            // 本地文件大小超出FTP文件大小则重新下载
            if (localSize > remoteSize) {
                localFile.delete();
            }

            // 判断是否需要断点续传
            long startIndex = 0;
            if (localSize > 0) {
                startIndex = localSize;
            }
//            ftpClient.changeToParentDirectory();

            ftpClient.setRestartOffset(startIndex);

            in = ftpClient.retrieveFileStream(remote);
            if (in == null) {
                return false;
            }

            os = new FileOutputStream(localFile);

            int len;

            byte[] buffer = new byte[4 * 1024];

            while ((len = in.read(buffer)) != -1 && !isStop) {
                os.write(buffer, 0, len);
            }

            // 下载结束后判断本地文件是否完整
            if (localFile.length() == remoteSize) {
                result = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {

            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                this.disconnect(ftpClient);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * 下载文件（回调上报进度）
     *
     * @param remote
     * @param local
     * @param callback
     */
    public boolean download(String remote, String local, FTPCallback callback) {

        FTPClient ftpClient = null;

        InputStream in = null;

        OutputStream os = null;

        boolean result = false;

        // 远程文件大小
        long remoteSize = 0;

        // 本地文件大小
        long localSize = 0;

        // 整个文件完成的大小
        long completeSize = 0;

        // 进度大小，达到bufferSize后发送，发送进度后清零
        long progressSize = 0;

        // 判断是否发送进度的缓冲区总大小
        long bufferSize = 1 * 1024 * 1024;

        try {
            ftpClient = this.connect();

            String dir = ftpClient.printWorkingDirectory();
            Log.d("FTPDownload", dir);

            // 判断远程文件是否存在
            FTPFile[] files = ftpClient.listFiles(remote);
            if (files.length != 1) {
                callback.sendDownloadProgress(0, 0, DownloadProgress.STATE_FAILED);
                return false;
            }

            // 判断远程文件大小
            remoteSize = files[0].getSize();
            if (remoteSize <= 0) {
                callback.sendDownloadProgress(remoteSize, 0, DownloadProgress.STATE_FAILED);
                return false;
            }

            // 创建上层文件夹
            File localFile = new File(local);
            File folder = localFile.getParentFile();
            if (!folder.exists()) {
                folder.mkdirs();
            }

            if (!localFile.exists()) {
                localFile.createNewFile();
            }

            localSize = localFile.length();

            // 本地文件与FTP文件大小一致则不执行下载
            if (localSize == remoteSize) {
                callback.sendDownloadProgress(remoteSize, remoteSize, DownloadProgress.STATE_SUCCESS);
                return true;
            }

            // 本地文件大小超出FTP文件大小则重新下载
            if (localSize > remoteSize) {
                localFile.delete();
            }

            // 判断是否需要断点续传
            long startIndex = 0;
            if (localSize > 0) {
                startIndex = localSize;
                completeSize = localSize;
            }
//            ftpClient.changeToParentDirectory();

            ftpClient.setRestartOffset(startIndex);

            in = ftpClient.retrieveFileStream(remote);
            if (in == null) {
                callback.sendDownloadProgress(remoteSize, localSize, DownloadProgress.STATE_FAILED);
                return false;
            }

            os = new FileOutputStream(localFile);

            int len;

            byte[] buffer = new byte[4 * 1024];

            while ((len = in.read(buffer)) != -1 && !isStop) {
                os.write(buffer, 0, len);

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

        } catch (Exception e) {
            e.printStackTrace();
            callback.sendDownloadProgress(remoteSize, completeSize, DownloadProgress.STATE_FAILED);
            return false;
        } finally {

            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                this.disconnect(ftpClient);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;

    }

    /**
     * 上传文件流
     *
     * @param remoteFolder   ftp目标目录
     * @param remoteFileName ftp目标文件名
     * @param local          本地文件路径
     * @return
     */
    public boolean upload(String remoteFolder, String remoteFileName, String local) {
        FTPClient ftpClient = null;
        InputStream in = null;
        try {
            in = new FileInputStream(local);
            ftpClient = this.connect();
//            ftpClient.changeToParentDirectory();

            String[] folders = remoteFolder.split("/");
            for (int i = 0; i < folders.length; i++) {
                String folder = folders[i];
                ftpClient.makeDirectory(folder);
                ftpClient.changeWorkingDirectory(folder);
            }
            boolean result = ftpClient.storeFile(remoteFileName, in);
            return result;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                this.disconnect(ftpClient);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
