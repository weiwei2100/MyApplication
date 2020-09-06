package com.jason.myapp.utils;

import android.content.Context;
import com.meiguan.ipsplayer.base.common.board.BoardUtilFactory;

import java.io.File;

/**
 * Created by qiuzi on 16/1/14.
 */
public class ScreenshotUtil {

    public static void screenshot(String storePath, int compressWidth, int compressHeight, Context context) {
        File storeFile = new File(storePath);
//        if (storeFile.isDirectory()) {
//            return;
//        }
        File storeFolder = storeFile.getParentFile();
        if (!storeFolder.exists()) {
            storeFolder.mkdirs();
        }

        BoardUtilFactory.getInstance().getBoardUtil().screenshot(storePath, "screenshot.jpg", context, compressWidth, compressHeight);

//        ShellUtils.CommandResult commandResult = ShellUtils.execCommand("screencap -p " + storePath, true);
//        int result = commandResult.result;
//        if (result == 0 && TextUtils.isEmpty(commandResult.errorMsg)) {
//            try {
//                ImageCompressUtil.compress(storePath, storePath, compressWidth, compressHeight);
//            } catch (Exception e) {
//                e.printStackTrace();
//                throw new RuntimeException("图片压缩失败.");
//            }
//        } else {
//            throw new RuntimeException("截屏命令执行失败.");
//        }
    }
}
