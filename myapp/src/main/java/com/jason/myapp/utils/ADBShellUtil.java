package com.jason.myapp.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by qiuzi on 15/5/22.
 */
public class ADBShellUtil {

    public String exeShellCommand(String cmd) {
        String result = null;
        List<String> cmds = new ArrayList<String>();
        cmds.add("sh");
        cmds.add("-c");
        cmds.add(cmd);
        try {
            ProcessBuilder shellBuilder = new ProcessBuilder("adb", "shell");
            shellBuilder.redirectErrorStream(true);
            shellBuilder.start();
            shellBuilder.command(cmds);
            Process process = shellBuilder.start();
            result = streamToString(process.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String streamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "/n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
