package com.jason.myapp.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class ServerConfigUtil {

    public static final String SERVER_CONFIG_FILE_PATH = "ips/config/server.json";

    private static final ServerConfigUtil serverConfigUtil = new ServerConfigUtil();

    private static JSONObject serverConfigJson = null;

    private ServerConfigUtil() {
    }

    public static ServerConfigUtil getInstance() {
        if (serverConfigJson == null) {
            reload();
        }
        return serverConfigUtil;
    }

    public static void reload() {
        if (FileUtil.getInstance().existsFile(SERVER_CONFIG_FILE_PATH)) {
            try {
                serverConfigJson = JSON.parseObject(FileUtil.getInstance().getFile(SERVER_CONFIG_FILE_PATH));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getServerIP() {
        String serverIP = "";
        if (serverConfigJson != null && serverConfigJson.containsKey("serverIP")) {
            serverIP = serverConfigJson.getString("serverIP");
        }
        return serverIP;
    }

    public String getServerPort() {
        String serverPort = "";
        if (serverConfigJson != null && serverConfigJson.containsKey("serverPort")) {
            serverPort = serverConfigJson.getString("serverPort");
        }
        return serverPort;
    }

    public String getTerminalId() {
        String terminalId = "";
        if (serverConfigJson != null && serverConfigJson.containsKey("terminalId")) {
            terminalId = serverConfigJson.getString("terminalId");
        }
        return terminalId;
    }

    public String getFtpIP() {
        String ftpIP = "";
        if (serverConfigJson != null && serverConfigJson.containsKey("ftpIP")) {
            ftpIP = serverConfigJson.getString("ftpIP");
        }
        return ftpIP;
    }

    public int getFtpPort() {
        int ftpPort = 21;
        if (serverConfigJson != null && serverConfigJson.containsKey("ftpPort")) {
            ftpPort = serverConfigJson.getIntValue("ftpPort");
        }
        return ftpPort;
    }

    public String getMqIP() {
        String mqIP = "";
        if (serverConfigJson != null && serverConfigJson.containsKey("mqIP")) {
            mqIP = serverConfigJson.getString("mqIP");
        }
        return mqIP;
    }

    public String getMqPort() {
        String mqPort = "";
        if (serverConfigJson != null && serverConfigJson.containsKey("mqPort")) {
            mqPort = serverConfigJson.getString("mqPort");
        }
        return mqPort;
    }

    public String getFtpUser() {
        String ftpUser = "";
        if (serverConfigJson != null && serverConfigJson.containsKey("ftpUser")) {
            ftpUser = serverConfigJson.getString("ftpUser");
        }
        return ftpUser;
    }

    public String getFtpPassword() {
        String ftpPassword = "";
        if (serverConfigJson != null && serverConfigJson.containsKey("ftpPassword")) {
            ftpPassword = serverConfigJson.getString("ftpPassword");
        }
        return ftpPassword;
    }

    public String getTerminalName() {
        String terminalName = "";
        if (serverConfigJson != null && serverConfigJson.containsKey("terminalName")) {
            terminalName = serverConfigJson.getString("terminalName");
        }
        return terminalName;
    }

    public JSONObject getServerConfigJson() {
        return serverConfigJson;
    }

}
