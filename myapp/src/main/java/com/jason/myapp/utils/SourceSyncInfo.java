package com.jason.myapp.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by qiuzi on 16/8/29.
 */
public class SourceSyncInfo implements Parcelable {

    public static int SYNC_ACTION_NEXT = 1;

    public static int SYNC_ACTION_PREVIUOS = 2;

    /**
     * 主机Id
     */
    private String controllerTerminalId;

    /**
     * 当前节目单ID
     */
    private String programId;

    /**
     * 计划Id
     */
    private String planId;

    /**
     * 下一个节目单ID
     */
    private String nextProgramId;

    /**
     * 下一个节目单开始时间
     */
    private long nextPrgramStartTime;

    /**
     * 播放区Id
     */
    private String areaId;

    /**
     * 当前播放素材Id
     */
    private String sourceId;

    /**
     * 当前素材已播放时长
     */
    private int sourcePlayedTime;

    /**
     * 下一个播放素材Id
     */
    private String nextSourceId;

    /**
     * 下一个素材播放开始时间
     */
    private long nextSourceStartTime;

    /**
     * 当前素材顺序
     */
    private int sourceSort;

    /**
     * 同步类型。同步节目单：2；同步素材：1；默认：0
     */
    private int type = 0;
    /**
     * 同步动作（向前、向后滑动）
     */
    private int syncAction;

    private String syncProgramGroupId;

    private String syncid;

    public String getControllerTerminalId() {
        return controllerTerminalId;
    }

    public void setControllerTerminalId(String controllerTerminalId) {
        this.controllerTerminalId = controllerTerminalId;
    }

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public String getNextProgramId() {
        return nextProgramId;
    }

    public void setNextProgramId(String nextProgramId) {
        this.nextProgramId = nextProgramId;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public int getSourcePlayedTime() {
        return sourcePlayedTime;
    }

    public void setSourcePlayedTime(int sourcePlayedTime) {
        this.sourcePlayedTime = sourcePlayedTime;
    }

    public String getNextSourceId() {
        return nextSourceId;
    }

    public void setNextSourceId(String nextSourceId) {
        this.nextSourceId = nextSourceId;
    }

    public long getNextPrgramStartTime() {
        return nextPrgramStartTime;
    }

    public void setNextPrgramStartTime(long nextPrgramStartTime) {
        this.nextPrgramStartTime = nextPrgramStartTime;
    }

    public long getNextSourceStartTime() {
        return nextSourceStartTime;
    }

    public void setNextSourceStartTime(long nextSourceStartTime) {
        this.nextSourceStartTime = nextSourceStartTime;
    }

    public int getSourceSort() {
        return sourceSort;
    }

    public void setSourceSort(int sourceSort) {
        this.sourceSort = sourceSort;
    }

    public int getSyncAction() {
        return syncAction;
    }

    public void setSyncAction(int syncAction) {
        this.syncAction = syncAction;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getSyncProgramGroupId() {
        return syncProgramGroupId;
    }

    public void setSyncProgramGroupId(String syncProgramGroupId) {
        this.syncProgramGroupId = syncProgramGroupId;
    }

    public String getSyncid() {
        return syncid;
    }

    public void setSyncid(String syncid) {
        this.syncid = syncid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(controllerTerminalId);
        parcel.writeString(programId);
        parcel.writeString(planId);
        parcel.writeString(nextProgramId);
        parcel.writeLong(nextPrgramStartTime);
        parcel.writeString(areaId);
        parcel.writeString(sourceId);
        parcel.writeInt(sourcePlayedTime);
        parcel.writeString(nextSourceId);
        parcel.writeLong(nextSourceStartTime);
        parcel.writeInt(sourceSort);
        parcel.writeInt(type);
        parcel.writeInt(syncAction);
        parcel.writeString(syncProgramGroupId);
        parcel.writeString(syncid);
    }

    public static final Creator<SourceSyncInfo> CREATOR = new Creator<SourceSyncInfo>() {
        @Override
        public SourceSyncInfo createFromParcel(Parcel parcel) {
            SourceSyncInfo syncInfo = new SourceSyncInfo();
            syncInfo.controllerTerminalId = parcel.readString();
            syncInfo.programId = parcel.readString();
            syncInfo.planId = parcel.readString();
            syncInfo.nextProgramId = parcel.readString();
            syncInfo.nextPrgramStartTime = parcel.readLong();
            syncInfo.areaId = parcel.readString();
            syncInfo.sourceId = parcel.readString();
            syncInfo.sourcePlayedTime = parcel.readInt();
            syncInfo.nextSourceId = parcel.readString();
            syncInfo.nextSourceStartTime = parcel.readLong();
            syncInfo.sourceSort = parcel.readInt();
            syncInfo.type = parcel.readInt();
            syncInfo.syncAction = parcel.readInt();
            syncInfo.syncProgramGroupId = parcel.readString();
            syncInfo.syncid = parcel.readString();
            return syncInfo;
        }

        @Override
        public SourceSyncInfo[] newArray(int i) {
            return new SourceSyncInfo[i];
        }
    };
}
