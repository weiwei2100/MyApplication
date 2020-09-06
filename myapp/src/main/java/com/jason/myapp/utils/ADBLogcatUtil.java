package com.jason.myapp.utils;

import android.text.TextUtils;
import com.ips.system.logcat.LogcatParams;

import java.io.File;

/**
 * Created by qiuzi on 15/7/16.
 */
public class ADBLogcatUtil {

    public String logcat() {
        ADBShellUtil util = new ADBShellUtil();
        String result = util.exeShellCommand("logcat -v time -t 1000");
        if (result != null) {
            util.exeShellCommand("logcat -c");
        }
        return result;
    }

    public String writeLogcatToFile(LogcatParams logcatParams, String localFile) {
        if (logcatParams == null) {
            return "";
        }
        File file = new File(localFile);
        if (file.exists()) {
            file.delete();
        }
        String cmd = "logcat -v time";
        if (logcatParams.getLines() > 0) {
            cmd += " -t " + logcatParams.getLines();
        }
        cmd += " -f " + localFile;
        if (!TextUtils.isEmpty(logcatParams.getLevel())) {
            cmd += " *:" + logcatParams.getLevel();
        }
        cmd += " -d";
        if (!TextUtils.isEmpty(logcatParams.getGrep())) {
            cmd += " |grep " + logcatParams.getGrep();
        }
        ADBShellUtil util = new ADBShellUtil();
        String result = util.exeShellCommand(cmd);
        if (result != null) {
            util.exeShellCommand("logcat -c");
        }
        return result;
    }

}
