package com.jason.myapp.utils;

import android.content.Context;
import com.meiguan.ipsplayer.base.common.db.dao.SyncSettingDao;
import com.meiguan.ipsplayer.base.common.db.model.Config;
import com.meiguan.ipsplayer.base.common.db.model.SyncSetting;

import java.util.ArrayList;

/**
 * Created by mrh on 2016/8/3.
 */
public class SyncSettingUtil {
    private Context context;

    private SyncSettingDao syncSettingDao;

    private Config config;

    private ConfigUtil configUtil;

    public SyncSettingUtil(Context context) {
        this.context = context;
        this.syncSettingDao = new SyncSettingDao(context);
        this.configUtil = new ConfigUtil(context);
    }

    public SyncSetting getSyncSetting(String terminalId) {
        return syncSettingDao.getSyncSetting(terminalId);
    }

    public String getsyncIp(String syncId, String terminalId) {
        return syncSettingDao.getSyncIp(syncId, terminalId);
    }

    public int getsyncPost(String syncId, String terminalId) {
        return syncSettingDao.getSyncPost(syncId, terminalId);
    }

    public void deleteSyncidsetting(String syncid) {
        syncSettingDao.delete(syncid);
    }

    public boolean isHost(String syncId, String terminalId) {
        return syncSettingDao.selectHost(syncId, terminalId);
    }

    //判断是否同步
    public boolean isSync(String terminalId, String syncId) {
        if (!syncSettingDao.getSyncId(terminalId).equals("")) {
            if (!getsyncIp(syncId, terminalId).equals("")) {
                if (getsyncPost(syncId, terminalId) != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getSyncId(String terminalId) {
        return syncSettingDao.getSyncId(terminalId);
    }

    public void insert(ArrayList<SyncSetting> syncSettings, String syncID) {
        syncSettingDao.insert(syncSettings, syncID);
    }

    public void insertOnedata(SyncSetting syncSetting) {
        syncSettingDao.insertOneData(syncSetting);
    }

    public void delSyncsetting() {
        syncSettingDao.removeAll();
//        syncconfigDao.removeAll();
    }

    public ArrayList<String> getAllTermiderId(String syncId) {
        return syncSettingDao.getAllTerminalId(syncId);
    }
}
