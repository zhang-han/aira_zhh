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
package com.arialyy.aria.core.upload;

import com.arialyy.aria.core.inf.AbsNormalTaskEntity;
import com.arialyy.aria.orm.ActionPolicy;
import com.arialyy.aria.orm.annotation.Foreign;
import com.arialyy.aria.orm.annotation.Ignore;
import com.arialyy.aria.orm.annotation.Primary;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lyy on 2017/2/9.
 * 上传任务实体
 */
public class UploadTaskEntity extends AbsNormalTaskEntity<UploadEntity> {
  private String attachment;  //文件上传需要的key
  private String contentType = "multipart/form-data"; //上传的文件类型
  private String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)";

  @Ignore private UploadEntity entity;

  private String filePath;

  @Primary
  @Foreign(parent = UploadEntity.class, column = "filePath",
      onUpdate = ActionPolicy.CASCADE, onDelete = ActionPolicy.CASCADE)
  private String key;

  /**
   * 文件上传表单
   */
  private Map<String, String> formFields = new HashMap<>();

  public UploadTaskEntity() {
  }

  @Override public UploadEntity getEntity() {
    return entity;
  }

  @Override public String getKey() {
    return key;
  }

  public void setEntity(UploadEntity entity) {
    this.entity = entity;
  }

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public Map<String, String> getFormFields() {
    return formFields;
  }

  public void setFormFields(Map<String, String> formFields) {
    this.formFields = formFields;
  }

  public String getAttachment() {
    return attachment;
  }

  public void setAttachment(String attachment) {
    this.attachment = attachment;
  }

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public String getUserAgent() {
    return userAgent;
  }

  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }
}
