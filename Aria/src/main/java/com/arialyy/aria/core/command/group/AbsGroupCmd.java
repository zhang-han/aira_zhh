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
package com.arialyy.aria.core.command.group;

import com.arialyy.aria.core.command.AbsCmd;
import com.arialyy.aria.core.download.DownloadGroupTaskEntity;
import com.arialyy.aria.core.inf.AbsGroupTask;
import com.arialyy.aria.core.inf.AbsGroupTaskEntity;
import com.arialyy.aria.core.inf.AbsTask;
import com.arialyy.aria.core.queue.DownloadGroupTaskQueue;
import com.arialyy.aria.util.ALog;
import com.arialyy.aria.util.CommonUtil;

/**
 * Created by AriaL on 2017/6/29.
 * 任务组命令
 */
public abstract class AbsGroupCmd<T extends AbsGroupTaskEntity> extends AbsCmd<T> {
  /**
   * 需要控制的子任务url
   */
  String childUrl;

  AbsGroupTask tempTask;

  AbsGroupCmd(T entity) {
    mTaskEntity = entity;
    TAG = CommonUtil.getClassName(this);
    if (entity instanceof DownloadGroupTaskEntity) {
      mQueue = DownloadGroupTaskQueue.getInstance();
      isDownloadCmd = true;
    }
  }

  /**
   * 创建任务
   *
   * @return 创建的任务
   */
  AbsTask createTask() {
    tempTask = (AbsGroupTask) mQueue.createTask(mTaskEntity);
    return tempTask;
  }

  boolean checkTask() {
    tempTask = (AbsGroupTask) mQueue.getTask(mTaskEntity.getEntity().getKey());
    if (tempTask == null) {
      createTask();
      if (tempTask.isComplete()) {
        ALog.i(TAG, "任务已完成");
        return false;
      }
    }
    return true;
  }
}
