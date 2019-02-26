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

import com.arialyy.aria.core.inf.AbsGroupTaskEntity;
import com.arialyy.aria.orm.ActionPolicy;
import com.arialyy.aria.orm.annotation.Foreign;
import com.arialyy.aria.orm.annotation.Ignore;
import com.arialyy.aria.orm.annotation.Primary;
import java.util.List;

/**
 * Created by AriaL on 2017/7/1.
 * 任务组的任务实体
 */
public class DownloadGroupTaskEntity extends AbsGroupTaskEntity<DownloadGroupEntity> {

  @Ignore private DownloadGroupEntity entity;

  @Ignore private List<DownloadTaskEntity> subTaskEntities;

  @Primary
  @Foreign(parent = DownloadGroupEntity.class, column = "groupName",
      onUpdate = ActionPolicy.CASCADE, onDelete = ActionPolicy.CASCADE)
  private String key;

  @Override public DownloadGroupEntity getEntity() {
    return entity;
  }

  public void setEntity(DownloadGroupEntity entity) {
    this.entity = entity;
  }

  public List<DownloadTaskEntity> getSubTaskEntities() {
    return subTaskEntities;
  }

  public void setSubTaskEntities(List<DownloadTaskEntity> subTaskEntities) {
    this.subTaskEntities = subTaskEntities;
  }

  @Override public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }
}
