/*
 * Copyright (C) 2016 AriaLyy(https://github.com/AriaLyy/Aria)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.arialyy.aria.core.download.downloader;

import com.arialyy.aria.core.common.CompleteInfo;
import com.arialyy.aria.core.common.IUtil;
import com.arialyy.aria.core.common.OnFileInfoCallback;
import com.arialyy.aria.core.download.DownloadTaskEntity;
import com.arialyy.aria.core.inf.AbsTaskEntity;
import com.arialyy.aria.core.inf.IDownloadListener;
import com.arialyy.aria.core.inf.IEntity;
import com.arialyy.aria.exception.BaseException;
import com.arialyy.aria.exception.TaskException;

/**
 * Created by lyy on 2015/8/25.
 * D_HTTP\FTP单任务下载工具
 */
public class SimpleDownloadUtil implements IUtil, Runnable {
  private String TAG = "SimpleDownloadUtil";
  private IDownloadListener mListener;
  private Downloader mDownloader;
  private DownloadTaskEntity mTaskEntity;
  private boolean isStop = false, isCancel = false;

  public SimpleDownloadUtil(DownloadTaskEntity entity, IDownloadListener downloadListener) {
    mTaskEntity = entity;
    mListener = downloadListener;
    mDownloader = new Downloader(downloadListener, entity);
  }

  @Override public long getFileSize() {
    return mDownloader.getFileSize();
  }

  /**
   * 获取当前下载位置
   */
  @Override public long getCurrentLocation() {
    return mDownloader.getCurrentLocation();
  }

  @Override public boolean isRunning() {
    return mDownloader.isRunning();
  }

  /**
   * 取消下载
   */
  @Override public void cancel() {
    isCancel = true;
    mDownloader.cancel();
  }

  /**
   * 停止下载
   */
  @Override public void stop() {
    isStop = true;
    mDownloader.stop();
  }

  /**
   * 多线程断点续传下载文件，开始下载
   */
  @Override public void start() {
    if (isStop || isCancel) {
      return;
    }
    new Thread(this).start();
  }

  @Override public void resume() {
    start();
  }

  @Override public void setMaxSpeed(int speed) {
    mDownloader.setMaxSpeed(speed);
  }

  private void failDownload(BaseException e, boolean needRetry) {
    if (isStop || isCancel) {
      return;
    }
    mListener.onFail(needRetry, e);
  }

  @Override public void run() {
    mListener.onPre();
    if (isStop || isCancel) {
      return;
    }
    if (mTaskEntity.getEntity().getFileSize() <= 1
        || mTaskEntity.isRefreshInfo()
        || mTaskEntity.getRequestType() == AbsTaskEntity.D_FTP
        || mTaskEntity.getState() == IEntity.STATE_FAIL) {
      new Thread(createInfoThread()).start();
    } else {
      mDownloader.start();
    }
  }

  /**
   * 通过链接类型创建不同的获取文件信息的线程
   */
  private Runnable createInfoThread() {
    switch (mTaskEntity.getRequestType()) {
      case AbsTaskEntity.D_FTP:
        return new FtpFileInfoThread(mTaskEntity, new OnFileInfoCallback() {
          @Override public void onComplete(String url, CompleteInfo info) {
            mDownloader.start();
          }

          @Override public void onFail(String url, BaseException e, boolean needRetry) {
            failDownload(e, needRetry);
            mDownloader.closeTimer();
          }
        });
      case AbsTaskEntity.D_HTTP:
        return new HttpFileInfoThread(mTaskEntity, new OnFileInfoCallback() {
          @Override public void onComplete(String url, CompleteInfo info) {
            mDownloader.start();
          }

          @Override public void onFail(String url, BaseException e, boolean needRetry) {
            failDownload(e, needRetry);
            mDownloader.closeTimer();
          }
        });
    }
    return null;
  }
}