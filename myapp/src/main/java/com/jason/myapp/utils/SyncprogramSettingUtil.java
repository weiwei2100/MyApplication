package com.jason.myapp.utils;

import android.content.Context;
import com.meiguan.ipsplayer.base.common.db.dao.SyncProgramSettingDao;
import com.meiguan.ipsplayer.base.common.db.model.Config;
import com.meiguan.ipsplayer.base.common.db.model.SyncprogramSetting;

import java.util.ArrayList;

/**
 * Created by mrh on 2016/8/3.
 */
public class SyncprogramSettingUtil {
    private Context context;

    private SyncprogramSetting syncprogramSetting;
    private SyncProgramSettingDao dao;

    private Config config;

    private ConfigUtil configUtil;

    private String terminal_id;

    public SyncprogramSettingUtil(Context context) {
        this.context = context;
        this.dao = new SyncProgramSettingDao(context);
        this.configUtil = new ConfigUtil(context);
        this.config = this.configUtil.getConfig();
        if (config == null) {
            return;
        }
        this.terminal_id = this.config.getTerminalId();
    }

    public SyncprogramSetting getSyncProgramSetting(String syncGroupProgramId, String programId) {
        return dao.getSyncprogramSetting(syncGroupProgramId, terminal_id, programId);
    }

    public String getSyncGroupProgramId(String programid) {
        return dao.getSyncGroupProgramId(terminal_id, programid);
    }

    public void insert(ArrayList<SyncprogramSetting> syncprogramSettings, String syncgroupProgramid) {
        dao.insert(syncprogramSettings, syncgroupProgramid);
    }

    public void insterOnedate(SyncprogramSetting syncprogramSetting) {
        dao.insterOnedate(syncprogramSetting);
    }

    public void clearAll() {
        dao.removeAll();
    }

    public void clearonedate(SyncprogramSetting syncprogramSetting) {
        dao.clearOneDate(syncprogramSetting);
    }

    public String getProgramId(String syncGroupProgramId) {
        return dao.SyncProgramId(syncGroupProgramId, terminal_id);
    }
}
