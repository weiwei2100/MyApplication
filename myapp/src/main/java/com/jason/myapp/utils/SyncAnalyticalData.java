package com.jason.myapp.utils;

import android.content.Context;
import android.os.Bundle;
import com.meiguan.ipsplayer.base.common.db.model.Config;
import com.meiguan.ipsplayer.base.common.db.model.SyncSetting;
import com.meiguan.ipsplayer.base.common.db.model.SyncprogramSetting;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mrh on 2016/8/5.
 */
public class SyncAnalyticalData {

    public void AnalyticalSyncJson(Context context, String syncJson) throws Exception {
        SyncSettingUtil syncSettingUtil = new SyncSettingUtil(context);
        JSONArray jsonArray = new JSONArray(syncJson);
        ArrayList<SyncSetting> syncSettingList = new ArrayList<SyncSetting>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String cmd = jsonObject.getString("cmd");
            if (cmd.equals("delSyncDevice")) {
//                    syncSettingUtil.deleteSyncidsetting(jsonObject1.getString("groupId"));
                syncSettingUtil.delSyncsetting();
            } else if (cmd.equals("addSyncDevice")) {
                JSONObject jsonObject1 = jsonObject.getJSONObject("cmdData");
                JSONArray jsonArray1 = jsonObject1.getJSONArray("syncTerminals");
                for (int j = 0; j < jsonArray1.length(); j++) {
                    JSONObject jsonObject2 = jsonArray1.getJSONObject(j);
                    SyncSetting syncSetting1 = new SyncSetting();
                    syncSetting1.setSyncIP(jsonObject1.getString("groupIP"));
                    syncSetting1.setSyncPort(jsonObject1.getInt("groupPort"));
                    syncSetting1.setSyncID(jsonObject1.getString("groupId"));
                    syncSetting1.setSyncdata(jsonObject1.getString("date"));
                    syncSetting1.setDeviceType(jsonObject2.getString("deviceType"));
                    syncSetting1.setTerminalId(jsonObject2.getString("terminalId"));
                    syncSettingList.add(syncSetting1);
                }
                syncSettingUtil.insert(syncSettingList, jsonObject1.getString("groupId"));
                syncSettingList.clear();
            }
//                ToastUtil.showToast(context, "同步设置成功！", 1);
        }
    }

    public Bundle getbundle(Context context, String syncJson) throws Exception {
        JSONArray jsonArray = new JSONArray(syncJson);
        Bundle bundle = new Bundle();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String cmd = jsonObject.getString("cmd");
            if (cmd.equals("delSyncDevice")) {
                bundle.putBoolean("isSync", false);
            } else if (cmd.equals("addSyncDevice")) {
                ConfigUtil util = new ConfigUtil(context);
                Config config = util.getConfig();
                JSONObject jsonObject1 = jsonObject.getJSONObject("cmdData");
                JSONArray jsonArray1 = jsonObject1.getJSONArray("syncTerminals");
                for (int j = 0; j < jsonArray1.length(); j++) {
                    JSONObject jsonObject2 = jsonArray1.getJSONObject(j);
                    if (jsonObject2.getString("terminalId").equals(config.getTerminalId())) {
                        bundle.putBoolean("isSync", true);
                        bundle.putString("devicetype", jsonObject2.getString("deviceType"));
                        bundle.putString("syncIP", jsonObject1.getString("groupIP"));
                        bundle.putString("syncID", jsonObject1.getString("groupId"));
                        bundle.putInt("syncPort", jsonObject1.getInt("groupPort"));
                        break;
                    }
                }
            }
        }
        return bundle;
    }

    public void AnalyticalSyncprogramJson(Context context, String syncprogramJson) {
        try {
            JSONArray jsonArray = new JSONArray(syncprogramJson);
            SyncprogramSettingUtil syncprogramSettingUtil = new SyncprogramSettingUtil(context);
            ArrayList<SyncprogramSetting> syncprogramSettingArrayList = new ArrayList<SyncprogramSetting>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String cmd = jsonObject.getString("cmd");
                JSONObject jsonObject1 = jsonObject.getJSONObject("cmdData");
                JSONObject jsonObject2 = jsonObject1.getJSONObject("syncProgram");
                JSONArray jsonArray1 = jsonObject2.getJSONArray("syncProgramToTerminal");
                for (int j = 0; j < jsonArray1.length(); j++) {
                    JSONObject jsonObject3 = jsonArray1.getJSONObject(j);
                    SyncprogramSetting syncprogramSetting = new SyncprogramSetting();
                    syncprogramSetting.setSyncId(jsonObject3.getString("groupId"));
                    syncprogramSetting.setSyncprogramId(jsonObject3.getString("programId"));
                    syncprogramSetting.setTerminalId(jsonObject3.getString("terminalId"));
                    syncprogramSetting.setData(jsonObject1.getString("date"));
                    syncprogramSetting.setType(jsonObject2.getString("type"));
                    syncprogramSetting.setSyncListenerType(jsonObject2.getString("syncListenerType"));
                    syncprogramSetting.setSyncStartTime(jsonObject2.getString("syncStartTime"));
                    syncprogramSetting.setSyncEndtime(jsonObject2.getString("syncEndTime"));
                    syncprogramSetting.setSyncGroupProgramId(jsonObject2.getString("syncProgramGroupId"));
                    syncprogramSettingArrayList.add(syncprogramSetting);
                }
                syncprogramSettingUtil.insert(syncprogramSettingArrayList, jsonObject2.getString("syncProgramGroupId"));
                syncprogramSettingArrayList.clear();
            }
            ToastUtil.showToast(context, "同步节目单设置成功！", 1);
        } catch (JSONException e) {
            e.printStackTrace();
            ToastUtil.showToast(context, "同步节目单设置失败！", 1);
        }

    }
}
