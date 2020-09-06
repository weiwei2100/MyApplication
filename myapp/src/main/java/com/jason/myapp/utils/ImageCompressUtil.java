package com.jason.myapp.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by qiuzi on 15/11/21.
 */
public class ImageCompressUtil {

    /**
     * 按指定大小压缩图片，并保存结果到指定文件
     *
     * @param src    源文件路径
     * @param dest   结果文件路径
     * @param width  指定宽度
     * @param height 指定高度
     */
    public static void compress(String src, String dest, int width, int height) throws Exception {

        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = null;

        try {
            BitmapFactory.decodeFile(src, newOpts);
            newOpts.inJustDecodeBounds = false;
            int realWidth = newOpts.outWidth;
            int realHeight = newOpts.outHeight;
            int rate = 1;
            if (realWidth > realHeight && realWidth > width) {
                rate = (int) Math.rint((float) newOpts.outWidth / width);
            } else if (realWidth < realHeight && realHeight > height) {
                rate = (int) Math.rint((float) newOpts.outHeight / height);
            }
            if (rate <= 0) {
                rate = 1;
            }
            newOpts.inSampleSize = rate;

            newOpts.inPurgeable = true;
            newOpts.inInputShareable = true;

            bitmap = BitmapFactory.decodeFile(src, newOpts);
            saveBitmapToFile(bitmap, dest);
        } catch (Exception e) {
            throw e;
        } finally {
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
    }

    private static void saveBitmapToFile(Bitmap bitmap, String path) throws Exception {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            throw e;
        }
    }
}
