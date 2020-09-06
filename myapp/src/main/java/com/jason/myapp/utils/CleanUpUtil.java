package com.jason.myapp.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.ips.concert.model.PlaySource;
import com.ips.concert.utils.ConcertUtil;
import com.meiguan.ipsplayer.base.common.db.dao.*;
import com.meiguan.ipsplayer.base.common.db.dao.sync.TimClientSceneDAO;
import com.meiguan.ipsplayer.base.common.db.model.*;
import com.meiguan.ipsplayer.base.common.db.model.sync.TimClientScene;
import com.meiguan.ipsplayer.base.common.ticket.TicketCouponConfigHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by qiuzi on 15/11/25.
 */
public class CleanUpUtil {

    public static final File CONCERT_FOLDER_INTERNAL = new File(Environment.getExternalStorageDirectory() + "/concert");

    public static final File CONCERT_FOLDER_SDCARD = new File(Environment.getSDCardPath() + "/concert");

    private Context context;

    private TerminalPlanDao terminalPlanDao;

    private DownloadPlanDao downloadPlanDao;

    private DefaultPlanDao defaultPlanDao;

    private DownloadDefaultPlanDao downloadDefaultPlanDao;

    private TerminalTicketCouponConfigDao terminalTicketCouponConfigDao;

    private TimClientSceneDAO timClientSceneDAO;

    private AdSourceDao adSourceDao;

    private AdItemDAO adItemDAO;

    public CleanUpUtil(Context context) {
        this.context = context;
        this.terminalPlanDao = new TerminalPlanDao(context);
        this.downloadPlanDao = new DownloadPlanDao(context);
        this.defaultPlanDao = new DefaultPlanDao(context);
        this.downloadDefaultPlanDao = new DownloadDefaultPlanDao(context);
        this.terminalTicketCouponConfigDao = new TerminalTicketCouponConfigDao(context);
        this.timClientSceneDAO = new TimClientSceneDAO(context);
        this.adSourceDao = new AdSourceDao(context);
        this.adItemDAO = new AdItemDAO(context);

    }

    public void cleanUp() {
        clearExpiredPlaySource();
    }

    private void clearExpiredPlaySource() {
        List<PlaySource> exceptionSources = new ArrayList<PlaySource>();
        List<TerminalPlan> terminalPlans = terminalPlanDao.listForAll();
        List<DownloadPlan> downloadPlans = downloadPlanDao.listForAll();
        List<DefaultPlan> defaultPlans = defaultPlanDao.listForAll();
        List<DownloadDefaultPlan> downloadDefaultPlanDaos = downloadDefaultPlanDao.listForAll();
        List<TerminalTicketCouponConfig> terminalTicketCouponConfigs = terminalTicketCouponConfigDao.listForAll();
        List<TimClientScene> syncScenes = timClientSceneDAO.listForAll();
        List<AdSource> adSources = adSourceDao.listForAll();
        List<AdItem> adItems = adItemDAO.listForAll();

        for (TerminalPlan terminalPlan : terminalPlans) {
            List<PlaySource> sources = ConcertUtil.getPlaySources(terminalPlan.getPlayPlan());
            exceptionSources.addAll(sources);
        }
        for (DownloadPlan downloadPlan : downloadPlans) {
            List<PlaySource> sources = ConcertUtil.getPlaySources(downloadPlan.getPlayPlan());
            exceptionSources.addAll(sources);
        }

        for (DefaultPlan defaultPlan : defaultPlans) {
            List<PlaySource> sources = ConcertUtil.getPlaySources(defaultPlan.getPlayPlan());
            exceptionSources.addAll(sources);
        }

        for (DownloadDefaultPlan downloadDefaultPlanDao : downloadDefaultPlanDaos) {
            List<PlaySource> sources = ConcertUtil.getPlaySources(downloadDefaultPlanDao.getPlayPlan());
            exceptionSources.addAll(sources);
        }

        for (AdItem adItem : adItems) {
            exceptionSources.add(adItem.getAdContent().getAdSource());
        }

        List<String> fileNames = new ArrayList<String>();

        // 有效播放素材添加到例外文件列表
        for (PlaySource exceptionSource : exceptionSources) {
            fileNames.add(exceptionSource.getName());
        }

        // 广告素材
        for (AdSource adSource : adSources) {
            fileNames.add(adSource.getSourceName());
        }

        // 优惠券添加到例外文件列表
        for (TerminalTicketCouponConfig terminalTicketCouponConfig : terminalTicketCouponConfigs) {
            //TODO 获取有效期时间范围内的优惠券编号
            Date couponStartTime = terminalTicketCouponConfig.getStartTime();
            Date couponEndTime = terminalTicketCouponConfig.getEndTime();
            if (TicketCouponConfigHelper.belongCalendar(new Date(), couponStartTime, couponEndTime)) {
                String imgsSuffix = TicketCouponConfigHelper.transformSuffix(terminalTicketCouponConfig.getCouponImgUrl());
                fileNames.add(terminalTicketCouponConfig.getCouponNumber() + "." + imgsSuffix);
                Log.d("coupon", "添加到集合数据:" + JSON.toJSONString(fileNames));
            } else {
                Log.d("coupon", "不在当前日期范围内有效:" + terminalTicketCouponConfig.getCouponNumber());
            }
        }

        // 有效同步素材添加到例外文件列表
        for (TimClientScene syncScene : syncScenes) {
            if (!TextUtils.isEmpty(syncScene.getSourceName())) {
                fileNames.add(syncScene.getSourceName());
            }
        }

        clearSourceFiles(CONCERT_FOLDER_INTERNAL, fileNames);
        clearSourceFiles(CONCERT_FOLDER_SDCARD, fileNames);

    }

    /**
     * 清除除例外素材以外的素材文件
     *
     * @param file
     * @param exceptionFileNames
     */
    private void clearSourceFiles(File file, List<String> exceptionFileNames) {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] subFiles = file.listFiles();
            for (File subFile : subFiles) {
                clearSourceFiles(subFile, exceptionFileNames);
            }
        } else {
            if (exceptionFileNames == null || !exceptionFileNames.contains(file.getName())) {
                file.delete();
                Log.d("coupon", "删除的文件:" + file.getName());
            }
        }
    }

    private void deleteSourceFiles(File file, List<String> sourceNames) {

        if (!file.exists()) {
            throw new RuntimeException("concert 文件夹不存在");
        }
        if (file.isDirectory()) {
            File[] sourceFiles = file.listFiles();
            for (File sourceFile : sourceFiles) {
                Log.d("coupon", "文件夹:" + sourceFile);
                deleteSourceFiles(sourceFile, sourceNames);
            }
        } else {
            if (sourceNames.contains(file.getName())) {
                file.delete();
                Log.d("coupon", "删除的文件:" + file.getName());
            }
            Log.d("coupon", "集合没有:" + file.getName());
        }
    }

}
