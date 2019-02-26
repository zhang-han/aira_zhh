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
package com.arialyy.aria.util;

import com.arialyy.aria.core.common.RecordWrapper;
import com.arialyy.aria.core.common.TaskRecord;
import com.arialyy.aria.orm.DbEntity;
import java.util.List;

/**
 * 数据库帮助类
 */
public class DbHelper {

  /**
   * 获取任务记录
   *
   * @param filePath 文件地址
   * @return 没有记录返回null，有记录则返回任务记录
   */
  public static TaskRecord getTaskRecord(String filePath) {
    List<RecordWrapper> records =
        DbEntity.findRelationData(RecordWrapper.class, "TaskRecord.filePath=?", filePath);
    if (records == null || records.size() == 0) {
      return null;
    }
    return records.get(0).taskRecord;
  }
}
