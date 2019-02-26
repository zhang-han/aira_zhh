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

import com.arialyy.aria.core.AriaManager;
import com.arialyy.aria.core.command.normal.NormalCmdFactory;
import com.arialyy.aria.core.inf.AbsEntity;
import com.arialyy.aria.core.inf.AbsTarget;
import com.arialyy.aria.core.inf.AbsTaskEntity;
import com.arialyy.aria.util.CommonUtil;

/**
 * Created by lyy on 2017/2/28.
 */
abstract class AbsDownloadTarget<TARGET extends AbsTarget, ENTITY extends AbsEntity, TASK_ENTITY extends AbsTaskEntity>
    extends AbsTarget<TARGET, ENTITY, TASK_ENTITY> {

  static final int HTTP = 1;
  static final int FTP = 2;
  //HTTP任务组
  static final int GROUP_HTTP = 3;
  //FTP文件夹
  static final int GROUP_FTP_DIR = 4;

  /**
   * 设置的文件保存路径的临时变量
   */
  String mTempFilePath;

  /**
   * {@code true}强制下载，不考虑文件路径是否被占用
   */
  boolean forceDownload = false;

  /**
   * 将任务设置为最高优先级任务，最高优先级任务有以下特点：
   * 1、在下载队列中，有且只有一个最高优先级任务
   * 2、最高优先级任务会一直存在，直到用户手动暂停或任务完成
   * 3、任务调度器不会暂停最高优先级任务
   * 4、用户手动暂停或任务完成后，第二次重新执行该任务，该命令将失效
   * 5、如果下载队列中已经满了，则会停止队尾的任务，当高优先级任务完成后，该队尾任务将自动执行
   * 6、把任务设置为最高优先级任务后，将自动执行任务，不需要重新调用start()启动任务
   */
  protected void setHighestPriority() {
    if (checkEntity()) {
      AriaManager.getInstance(AriaManager.APP)
          .setCmd(CommonUtil.createNormalCmd(mTaskEntity, NormalCmdFactory.TASK_HIGHEST_PRIORITY,
              checkTaskType()))
          .exe();
    }
  }

  /**
   * 添加任务
   */
  public void add() {
    if (checkEntity()) {
      AriaManager.getInstance(AriaManager.APP)
          .setCmd(CommonUtil.createNormalCmd(mTaskEntity, NormalCmdFactory.TASK_CREATE,
              checkTaskType()))
          .exe();
    }
  }

  /**
   * 获取任务文件大小
   *
   * @return 文件大小
   */
  public long getFileSize() {
    return getSize();
  }

  /**
   * 获取单位转换后的文件大小
   *
   * @return 文件大小{@code xxx mb}
   */
  public String getConvertFileSize() {
    return getConvertSize();
  }

  /**
   * 设置target类型
   *
   * @return {@link #HTTP}、{@link #FTP}、{@link #GROUP_HTTP}、{@link #GROUP_FTP_DIR}
   */
  protected abstract int getTargetType();
}
