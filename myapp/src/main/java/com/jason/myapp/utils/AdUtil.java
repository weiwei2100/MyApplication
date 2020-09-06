package com.jason.myapp.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.ips.concert.model.advertisement.AdContent;
import com.ips.concert.model.advertisement.AdList;
import com.ips.concert.model.cinema.buy.ResultResp;
import com.ips.concert.utils.MD5Util;
import com.meiguan.ipsplayer.base.BuildConfig;
import com.meiguan.ipsplayer.base.common.db.dao.AdCycleDAO;
import com.meiguan.ipsplayer.base.common.db.dao.AdItemDAO;
import com.meiguan.ipsplayer.base.common.db.dao.AdTerminalSettingDAO;
import com.meiguan.ipsplayer.base.common.db.dao.sync.SyncServerAdDAO;
import com.meiguan.ipsplayer.base.common.db.model.AdCycle;
import com.meiguan.ipsplayer.base.common.db.model.AdItem;
import com.meiguan.ipsplayer.base.common.db.model.AdTerminalSetting;
import com.meiguan.ipsplayer.base.common.db.model.Config;
import com.meiguan.ipsplayer.base.common.db.model.sync.SyncServerAdConfig;

import java.util.*;

/**
 * Created by sunjianchao on 18/7/5.
 */
public class AdUtil {

    private String TAG = "AdUtil";
    private List<List<AdContent>> adContentList = new ArrayList<List<AdContent>>();
    //下载标记位 true:正在下载 false:没有下载
    private boolean downloadADFlag = false;
    //是否正在从缓存取广告 true:正在取广告 false:没有取广告
    private boolean pollAdFlag = false;

    private static AdUtil instance = new AdUtil();

    public static AdUtil getInstance() {
        return instance;
    }

    public static final String SET_URI = "rest/terminal/getAdvertSyncPlans";


    public synchronized void getAdContent(Context context) throws Exception {
        Log.d(TAG, "当前是否有广告正在下载:" + downloadADFlag + ",是否正在从缓存取广告:" + pollAdFlag);
        if (pollAdFlag) {
            throw new RuntimeException("download ad data error");
        }

        AdList adList = requestAdList(context);
        downloadADFlag = false;
        if (adList == null) {
            return;
        }

        Log.d(TAG, "adList:" + JSON.toJSONString(adList));

        saveCycle(adList.getCycle(), context);
//        saveAdContents(adList.getAdContents(), context);

        addAdData(adList.getAdContents());

    }


    private AdList requestAdList(Context context) {
        AdList adList = null;

        try {
            downloadADFlag = true;
            ConfigUtil util = new ConfigUtil(context);
            Config config = util.getConfig();
            if (config == null || TextUtils.isEmpty(config.getTerminalId())) {
                return null;
            }
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("terminalId", config.getTerminalId());
            String resultStr = HttpUtil.executePost(
                    config.getServerIP(),
                    Integer.parseInt(config.getServerPort()),
                    "/" + config.getServerContext() + "/rest/terminal/getAdvertSource",
                    paramMap);

            Log.d(TAG, "requestAdList: " + resultStr);
            if (TextUtils.isEmpty(resultStr)) {
                return null;
            }
//            JSONArray array = JSON.parseArray(resultStr);
//            adList = array.getObject(0, AdList.class);

            adList = JSON.parseObject(resultStr, AdList.class);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return adList;
    }

    private void saveCycle(int cycle, Context context) {
        AdCycleDAO adCycleDAO = new AdCycleDAO(context);

        AdCycle adCycle = new AdCycle();
        adCycle.setCycle(cycle);

        adCycleDAO.save(adCycle);

    }

    private void saveAdContents(List<AdContent> adContents, Context context) {
        try {

            AdItemDAO adItemDAO = new AdItemDAO(context);
            List<AdItem> saveAdItems = adItemDAO.listForAll();

            if (adContents == null || adContents.size() <= 0) {
                Log.d(TAG, "数据库中存在的广告：" + JSON.toJSONString(saveAdItems));
                if (saveAdItems != null && saveAdItems.size() > 0) {
                    Log.d(TAG, "删除数据库广告");
                    adItemDAO.removeAll();
                }
                Log.d(TAG, "广告数据为空");
                return;
            }

            //数据库存储的广告为空,直接存储
//            if (saveAdItems == null || saveAdItems.size() == 0) {
//                for (AdContent adContent : adContents) {
//                    AdItem adItem = getAdItem(adContent);
//                    adItemDAO.save(adItem);
//                }
//                Log.d(TAG, "数据库广告表为空,直接存储完毕");
//                return;
//            }

            Map<String, AdItem> oldMap = new HashMap<String, AdItem>();
            Map<String, AdItem> newMap = new HashMap<String, AdItem>();

            for (AdItem oldAdItem : saveAdItems) {
                oldMap.put(oldAdItem.getOrderId(), oldAdItem);
            }

            for (AdContent adContent : adContents) {
                AdItem adItem = getAdItem(adContent);
                newMap.put(adItem.getOrderId(), adItem);
            }

            ///删除
            for (Map.Entry<String, AdItem> entry : oldMap.entrySet()) {
                boolean contains = newMap.containsKey(entry.getKey());
                if (!contains) {
                    adItemDAO.removeAdContentByOrderId(entry.getKey());
                    Log.d(TAG, "删除oriderId:" + entry.getKey());
                }
            }

            //更新 添加
            for (Map.Entry<String, AdItem> entry : newMap.entrySet()) {
                boolean contains = oldMap.containsKey(entry.getKey());
                if (contains) {
                    Log.d(TAG, "oriderId相同 new SourceId():" + entry.getValue().getSourceId() + ",old SourceId():" + oldMap.get(entry.getKey()).getSourceId());
                    if (!entry.getValue().getSourceId().equals(oldMap.get(entry.getKey()).getSourceId())) {

                        oldMap.get(entry.getKey()).setSourceId(entry.getValue().getSourceId());
                        oldMap.get(entry.getKey()).setReady(false);
                        AdItem adItem = entry.getValue();
                        adItem.setId(oldMap.get(entry.getKey()).getId());
                        adItemDAO.update(adItem);
                        Log.d(TAG, "oriderId相同,sourceId不同,更新记录:" + JSON.toJSONString(adItem));
                    }
                } else {
                    adItemDAO.save(entry.getValue());
                    Log.d(TAG, "oriderId不同,直接存入到数据库:" + JSON.toJSONString(entry.getValue()));
                }
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private AdItem getAdItem(AdContent adContent) {

        AdItem adItem = new AdItem();
        adItem.setOrderId(adContent.getOrderId());
        adItem.setTimeInCycle(adContent.getTimeInCycle());
        adItem.setAdContent(adContent);
        adItem.setReady(false);
        adItem.setSourceId(adContent.getAdSource().getId());

        Date effectiveDate = adContent.getEffectiveDate();
        Date expiryDate = adContent.getExpiryDate();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(effectiveDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        adItem.setEffectiveDate(calendar.getTime());

        calendar.setTime(expiryDate);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        adItem.setExpiryDate(calendar.getTime());

        return adItem;
    }


    /**
     * 判断是否是广告机 true:是 false:不是
     *
     * @param context
     * @return
     */
    public boolean isAdTerminal(Context context) {
        AdTerminalSettingDAO adTerminalSettingDAO = new AdTerminalSettingDAO(context);
        AdTerminalSetting adTerminalSetting = adTerminalSettingDAO.getAdTerminalSetting();
        Log.d(TAG, "adTerminalSetting:" + adTerminalSetting);
        if (adTerminalSetting == null) {
            return false;
        }
        boolean isAdTerminal = adTerminalSetting.isAdTerminal();
        Log.d(TAG, "isAdTerminal: :" + isAdTerminal);
        return isAdTerminal;
    }

    public void addAdData(List<AdContent> adContents) {
        if (adContentList.size() > 100) {
            return;
        }
        adContentList.add(adContents);
        Log.d(TAG, "广告添加到缓存:" + JSON.toJSONString(adContentList));
    }

    /**
     * 轮询缓存处理数据
     *
     * @param context
     */
    public void start(final Context context) {
        ThreadPoolUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Log.d(TAG, "轮询下载广告标记位:" + downloadADFlag);
                        if (adContentList != null && adContentList.size() > 0 && !downloadADFlag) {
                            pollAdFlag = true;
                            Log.d(TAG, "正在从缓存取广告:" + JSON.toJSONString(adContentList));
                            for (List<AdContent> adContentList : adContentList) {
                                saveAdContents(adContentList, context);
                            }
                            adContentList.clear();
                            pollAdFlag = false;
                            Log.d(TAG, "缓存取广告结束,清空缓存,标志：" + pollAdFlag);

                        }
                        Thread.sleep(30000L);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    /**
     * 轮询缓存处理数据 30分钟一次
     *
     * @param context
     */
    public void startSyncServerAd(final Context context) {
        ThreadPoolUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        requestSyncPLayProgram(SET_URI, context);
                        Thread.sleep(1800000L);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    /**
     * 同步广告主机下发数据
     *
     * @param uri
     * @param context
     * @throws Exception
     */
    public synchronized void requestSyncPLayProgram(String uri, Context context) throws Exception {
        ConfigUtil configUtil = new ConfigUtil(context);
        Config config = configUtil.getConfig();

        if (config == null) {
            return;
        }

        String terminalId = config.getTerminalId();

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("terminalId", terminalId);

        String result = HttpUtil.executePost(
                config.getServerIP(),
                Integer.parseInt(config.getServerPort()),
                "/" + config.getServerContext() +
                        "/" + uri,
                paramMap);

        Log.d("sync", "SyncServerAdHandler:" + JSON.toJSONString(result));

        SyncServerAdDAO syncServerAdDAO = new SyncServerAdDAO(context);
        syncServerAdDAO.removeAll();

        if (TextUtils.isEmpty(result)) {
            return;
        }

        List<SyncServerAdConfig> list = JSON.parseArray(result, SyncServerAdConfig.class);
        if (list == null || list.size() <= 0) {
            return;
        }

        for (SyncServerAdConfig syncServerAdConfig : list) {
            changeTime(syncServerAdConfig);
            syncServerAdDAO.saveAds(syncServerAdConfig);
        }

    }

    private void changeTime(SyncServerAdConfig syncServerAdConfig) {
        Date effectiveDate = syncServerAdConfig.getEffectiveDate();
        Date expiryDate = syncServerAdConfig.getExpiryDate();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(effectiveDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        syncServerAdConfig.setEffectiveDate(calendar.getTime());

        calendar.setTime(expiryDate);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        syncServerAdConfig.setExpiryDate(calendar.getTime());
    }

    public void selectAdData(Context context) {
        try {
            AdItemDAO adItemDAO = new AdItemDAO(context);
            List<AdItem> adItems = adItemDAO.listForAll();
            if (adItems == null || adItems.size() == 0) {
                return;
            }

            for (AdItem adItem : adItems) {
                String sourceId = adItem.getSourceId();
                String orderId = adItem.getOrderId();
                boolean ready = adItem.isReady();
                int status = (ready ? 1 : 0);
                int reportStatus = adItem.getAdContent().getReportStatus();
                Log.d("AdStatus", "orderId:" + orderId + ",下载状态:" + status + ",是否上报状态:" + reportStatus);
                //0：上报服务器 1：不需上报
                if (reportStatus == 0) {
                    reportDownloadAdStatus(adItem, context, orderId, sourceId, status);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reportDownloadAdStatus(AdItem adItem, Context context, String orderId, String sourceId, int status) {

        AdItemDAO adItemDAO = new AdItemDAO(context);

        try {
            ConfigUtil util = new ConfigUtil(context);
            Config config = util.getConfig();
            if (config == null || TextUtils.isEmpty(config.getTerminalId())) {
                return;
            }

            Map<String, Object> paramMapJson = new HashMap<String, Object>();

            paramMapJson.put("terminalId", config.getTerminalId());
            paramMapJson.put("orderId", orderId);
            paramMapJson.put("sourceId", sourceId);
            paramMapJson.put("downloadStatus", status);

            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("log", JSON.toJSONString(paramMapJson));

            Log.d("AdStatus", "confirmDownloadStatus parameter:" + paramMap.toString());


            String resultStr = HttpUtil.executePost(
                    BuildConfig.LOG_SERVER_IP,
                    BuildConfig.LOG_SERVER_PORT,
                    "/" + BuildConfig.LOG_SERVER_CONTEXT + "/rest/terminalScreenSettingAction/confirmDownloadStatus",
                    paramMap);

            Log.d("AdStatus", "confirmDownloadStatus resultStr:" + resultStr);

            ResultResp resultResp = JSON.parseObject(resultStr, ResultResp.class);
            boolean reportStatus = resultResp.isSuccess();
            if (reportStatus) {
                adItem.getAdContent().setReportStatus(1);
                adItemDAO.update(adItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
            adItem.getAdContent().setReportStatus(0);
            adItemDAO.update(adItem);
        }

    }

    public String getDspOrderId(AdContent adContent) {
        String orderId = null;
        try {
            orderId = MD5Util.getMD5(adContent.getAdSource().getRemote());
        } catch (Exception e) {
            orderId = null;
            e.printStackTrace();
        }
        return orderId;
    }

}
