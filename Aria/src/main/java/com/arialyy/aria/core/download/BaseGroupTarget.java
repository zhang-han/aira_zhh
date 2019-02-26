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
package com.arialyy.aria.core.download;

import android.support.annotation.CheckResult;
import android.text.TextUtils;
import android.util.Log;
import com.arialyy.aria.core.manager.SubTaskManager;
import com.arialyy.aria.core.queue.DownloadGroupTaskQueue;
import com.arialyy.aria.util.ALog;
import java.io.File;
import java.util.List;

/**
 * Created by lyy on 2017/7/26.
 */
abstract class BaseGroupTarget<TARGET extends BaseGroupTarget>
    extends AbsDownloadTarget<TARGET, DownloadGroupEntity, DownloadGroupTaskEntity> {

  /**
   * 组任务名
   */
  String mGroupName;
  /**
   * 文件夹临时路径
   */
  String mDirPathTemp;
  /**
   * 是否需要修改路径
   */
  boolean needModifyPath = false;

  private SubTaskManager mSubTaskManager;

  /**
   * 获取子任务管理器
   *
   * @return 子任务管理器
   */
  @CheckResult
  public SubTaskManager getSubTaskManager() {
    if (mSubTaskManager == null) {
      mSubTaskManager = new SubTaskManager(mTargetName, mTaskEntity);
    }
    return mSubTaskManager;
  }

  /**
   * 设置任务组别名
   */
  @CheckResult
  public TARGET setGroupAlias(String alias) {
    if (TextUtils.isEmpty(alias)) return (TARGET) this;
    mEntity.setAlias(alias);
    return (TARGET) this;
  }

  @Override public boolean taskExists() {
    return DownloadGroupTaskQueue.getInstance().getTask(mEntity.getGroupName()) != null;
  }

  /**
   * 设置任务组的文件夹路径，该api后续会删除
   *
   * @param groupDirPath 任务组保存文件夹路径
   * @deprecated {@link #setDirPath(String)} 请使用这个api
   */
  @Deprecated
  @CheckResult
  public TARGET setDownloadDirPath(String groupDirPath) {
    return setDirPath(groupDirPath);
  }

  /**
   * 设置任务组的文件夹路径，在Aria中，任务组的所有子任务都会下载到以任务组组名的文件夹中。
   * 如：groupDirPath = "/mnt/sdcard/download/group_test"
   * <pre>
   *   {@code
   *      + mnt
   *        + sdcard
   *          + download
   *            + group_test
   *              - task1.apk
   *              - task2.apk
   *              - task3.apk
   *              ....
   *
   *   }
   * </pre>
   *
   * @param dirPath 任务组保存文件夹路径
   */
  @CheckResult
  public TARGET setDirPath(String dirPath) {
    mDirPathTemp = dirPath;
    return (TARGET) this;
  }

  @Override public boolean isRunning() {
    DownloadGroupTask task = DownloadGroupTaskQueue.getInstance().getTask(mEntity.getKey());
    return task != null && task.isRunning();
  }

  /**
   * 改变任务组文件夹路径，修改文件夹路径会将子任务所有路径更换
   *
   * @param newDirPath 新的文件夹路径
   */
  void reChangeDirPath(String newDirPath) {
    List<DownloadTaskEntity> subTasks = mTaskEntity.getSubTaskEntities();
    if (subTasks != null && !subTasks.isEmpty()) {
      for (DownloadTaskEntity dte : subTasks) {
        DownloadEntity de = dte.getEntity();
        String oldPath = de.getDownloadPath();
        String newPath = newDirPath + "/" + de.getFileName();
        File file = new File(oldPath);
        if (file.exists()) {
          file.renameTo(new File(newPath));
        }
        de.setDownloadPath(newPath);
        dte.setKey(newPath);
        de.save();
        dte.save();
      }
    }
  }

  /**
   * 检查并设置文件夹路径
   *
   * @return {@code true} 合法
   */
  boolean checkDirPath() {
    if (TextUtils.isEmpty(mDirPathTemp)) {
      ALog.e(TAG, "文件夹路径不能为null");
      return false;
    } else if (!mDirPathTemp.startsWith("/")) {
      ALog.e(TAG, "文件夹路径【" + mDirPathTemp + "】错误");
      return false;
    }
    File file = new File(mDirPathTemp);
    if (file.isFile()) {
      ALog.e(TAG, "路径【" + mDirPathTemp + "】是文件，请设置文件夹路径");
      return false;
    }

    if (TextUtils.isEmpty(mEntity.getDirPath()) || !mEntity.getDirPath().equals(mDirPathTemp)) {
      if (!file.exists()) {
        file.mkdirs();
      }
      needModifyPath = true;
      mEntity.setDirPath(mDirPathTemp);
    }

    return true;
  }
}
