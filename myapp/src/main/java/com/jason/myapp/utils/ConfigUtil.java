package com.jason.myapp.utils;

import android.content.Context;
import android.text.TextUtils;
import com.alibaba.fastjson.JSONObject;
import com.meiguan.ipsplayer.base.common.db.dao.ConfigDao;
import com.meiguan.ipsplayer.base.common.db.dao.OSSConfigDao;
import com.meiguan.ipsplayer.base.common.db.model.Config;
import com.meiguan.ipsplayer.base.common.db.model.OSSConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by qiuzi on 15/9/21.
 */
public class ConfigUtil {

    private Context context;

    private ConfigDao configDao;

    private OSSConfigDao ossConfigDao;

    public ConfigUtil(Context context) {
        this.context = context;
        this.configDao = new ConfigDao(context);
        this.ossConfigDao = new OSSConfigDao(context);
    }

    public Config getConfig() {
        return configDao.getConfig();
    }

    public boolean isRegistered() {
        boolean isRegistered = false;
        Config config = getConfig();
        if (config != null && !TextUtils.isEmpty(config.getTerminalId())) {
            isRegistered = true;
        }
        return isRegistered;
    }

    public boolean register(Config config) {
        boolean registerResult = false;
        String params = getRegisterParams();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("code", "micrown");
        paramsMap.put("terminal", params);
        String resultStr = HttpUtil.executePost(
                config.getServerIP(),
                Integer.parseInt(config.getServerPort()),
                "/" + config.getServerContext() + "/rest/terminal/access",
                paramsMap);
        JSONObject resultJson = JSONObject.parseObject(resultStr);
        if (resultJson != null && resultJson.getBoolean("isSuccess")) {
            config.setTerminalId(resultJson.getString("terminalId"));
            registerResult = true;

            String endpoint = resultJson.getString("endpoint");
            String bucketName = resultJson.getString("bucketName");
            String accessKeyId = resultJson.getString("accessKeyId");
            String accessKeySecret = resultJson.getString("accessKeySecret");

            OSSConfig ossConfig = new OSSConfig();
            ossConfig.setEndpoint(endpoint);
            ossConfig.setBucketName(bucketName);
            ossConfig.setAccessKeyId(accessKeyId);
            ossConfig.setAccessKeySecret(accessKeySecret);

            ossConfigDao.save(ossConfig);

        }
        configDao.save(config);
        return registerResult;
    }

    private String getRegisterParams() {
        SystemUtil util = new SystemUtil(context);
        JSONObject param = new JSONObject();
        param.put("tmlType", util.getTerminalType());
        param.put("tmlMicAddress", util.getMAC());
        param.put("tmlIpAddress", util.getIP());
        param.put("tmlSubnetMask", util.getNetmask());
        param.put("tmlGateway", util.getGateway());
        param.put("tmlCPU", util.getCPUInfo());
        param.put("tmlMemory", util.getMemTotal());
        param.put("tmlDisk", util.getSDCardTotal());
        param.put("tmlDisplayWidth", "" + util.getScreenWidth());
        param.put("tmlDisplayHeight", "" + util.getScreenHeight());
        param.put("tmlOperatingSystem", util.getOS());
        param.put("tmlSN", util.getSN());
        return param.toJSONString();
    }

    public void unRegister() {
        configDao.removeAll();
        ossConfigDao.removeAll();
    }
}
