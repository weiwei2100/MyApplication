package com.jason.myapp.utils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * 读写并发文件读写工具类
 * <p>
 * Created by qiuzi on 2018/5/2 下午2:21.
 */
public class ConcurrentFileUtil {

    /**
     * 读取文件内容
     *
     * @param path    文件绝对路径
     * @param isClear 读取后是否清空内容
     * @return
     */
    public static String read(String path, boolean isClear) {
        RandomAccessFile raf = null;
        FileChannel fc = null;
        FileLock fl = null;
        File file = new File(path);

        String result = "";
        BufferedReader br = null;
        FileInputStream fileInputStream = null;
        InputStreamReader isr = null;

        try {
            raf = new RandomAccessFile(file, "rw");
            fc = raf.getChannel();
            int i = 0;
            while (i < 2000) {
                try {
                    fl = fc.tryLock();
                    if (fl != null) {
                        System.out.println(Thread.currentThread().getName() + ":read thread get the lock");
                        break;
                    }
                } catch (Exception e) {
                    System.out.println(Thread.currentThread().getName() + ":read thread is block");
                }

                Thread.sleep(30);
                i += 30;

            }

            if (fl == null) {
                return "";
            }

            fileInputStream = new FileInputStream(file);
            isr = new InputStreamReader(fileInputStream);
            br = new BufferedReader(isr);
            String str = null;

            while ((str = br.readLine()) != null) {
                result += str;
            }

            if (isClear) {
                fc.truncate(0);
            }

            fl.release();
            fc.close();
            raf.close();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (fl != null && fl.isValid()) {
                try {
                    fl.release();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (br != null) {
                try {
                    br.close();
                    new FileInputStream(file).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;

    }

    /**
     * 写入文件内容
     *
     * @param path     文件绝对路径
     * @param content  文件内容
     * @param isAppend 是否追加
     */
    public static void write(String path, String content, boolean isAppend) {
        RandomAccessFile raf = null;
        FileChannel fc = null;
        FileLock fl = null;
        FileWriter fw = null;
        File file = new File(path);

        try {
            File folder = new File(file.getParent());
            if (!folder.exists()) {
                folder.mkdirs();
            }

            raf = new RandomAccessFile(file, "rw");
            fc = raf.getChannel();
            int i = 0;
            while (i < 2000) {
                try {
                    fl = fc.tryLock();
                    if (fl != null) {
                        System.out.println(Thread.currentThread().getName() + ":write thread get the lock");
                        break;
                    }
                } catch (Exception e) {
                    System.out.println(Thread.currentThread().getName() + ":write thread is block");
                }

                Thread.sleep(30);
                i += 30;

            }

            if (fl == null) {
                return;
            }

            fw = new FileWriter(file, isAppend);
            fw.write(content);

            fl.release();
            fc.close();
            raf.close();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (fl != null && fl.isValid()) {
                try {
                    fl.release();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
