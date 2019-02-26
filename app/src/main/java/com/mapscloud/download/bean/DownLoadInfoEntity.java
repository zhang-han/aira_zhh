package com.mapscloud.download.bean;

import android.annotation.SuppressLint;

import com.arialyy.aria.core.inf.AbsEntity;
import com.arialyy.aria.orm.annotation.Ignore;
import com.arialyy.aria.orm.annotation.Unique;


@SuppressLint("ParcelCreator")
public class DownLoadInfoEntity extends AbsEntity {
    private long currprogress = 0;
    private int statu = -1; //下载状态 默认-1 完成1 停止2 等待3 正在执行4 预处理5 预处理完成6  删除7

    private String tileName;
    @Unique
    private String downLoadUrl;
    private String tileSize;
    private int version;
    private String releaseDate;
    private String soureceName;
    private int downloadStatus;
    private String md5;
    private int x;
    private int y;
    private int z;


    public long getCurrprogress() {
        return currprogress;
    }

    public void setCurrprogress(long currprogress) {
        this.currprogress = currprogress;
    }

    public int getStatu() {
        return statu;
    }

    public void setStatu(int statu) {
        this.statu = statu;
    }

    public String getTileName() {
        return tileName;
    }

    public void setTileName(String tileName) {
        this.tileName = tileName;
    }

    public String getDownLoadUrl() {
        return downLoadUrl;
    }

    public void setDownLoadUrl(String downLoadUrl) {
        this.downLoadUrl = downLoadUrl;
    }

    public String getTileSize() {
        return tileSize;
    }

    public void setTileSize(String tileSize) {
        this.tileSize = tileSize;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getSoureceName() {
        return soureceName;
    }

    public void setSoureceName(String soureceName) {
        this.soureceName = soureceName;
    }

    public int getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    /**
     * 其它状态
     */
    @Ignore
    int STATE_OTHER = -1;                        //默认状态
    /**
     * 失败状态
     */
    @Ignore int STATE_FAIL = 0;             //下载失败
    /**
     * 完成状态
     */
    @Ignore int STATE_COMPLETE = 1;           //下载完成
    /**
     * 停止状态
     */
    @Ignore int STATE_STOP = 2;                 //暂停下载
    /**
     * 等待状态
     */
    @Ignore int STATE_WAIT = 3;
    /**
     * 正在执行
     */
    @Ignore int STATE_RUNNING = 4;              //点击下载
    /**
     * 预处理
     */
    @Ignore int STATE_PRE = 5;
    /**
     * 预处理完成
     */
    @Ignore int STATE_POST_PRE = 6;
    /**
     * 删除任务
     */
    @Ignore int STATE_CANCEL = 7;

    @Override
    public String getKey() {
        return downLoadUrl;
    }

    @Override
    public int getTaskType() {
        return 0;
    }
}
