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

package com.arialyy.aria.orm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import java.util.List;

/**
 * Created by lyy on 2015/2/11.
 * 数据库操作工具
 */
public class DelegateWrapper {
  private static final String TAG = "DelegateWrapper";
  private volatile static DelegateWrapper INSTANCE = null;

  private SQLiteDatabase mDb;
  private DelegateManager mDManager;

  private DelegateWrapper() {

  }

  private DelegateWrapper(Context context) {
    SqlHelper helper = SqlHelper.init(context.getApplicationContext());
    mDb = helper.getWritableDatabase();
    mDManager = DelegateManager.getInstance();
  }

  public static void init(Context context) {
    synchronized (DelegateWrapper.class) {
      if (INSTANCE == null) {
        INSTANCE = new DelegateWrapper(context);
      }
    }
  }

  static DelegateWrapper getInstance() {
    if (INSTANCE == null) {
      throw new NullPointerException("请在Application中调用init进行数据库工具注册注册");
    }
    return INSTANCE;
  }

  /**
   * 查询关联表数据
   *
   * @param expression 查询条件
   */
  <T extends AbsWrapper> List<T> findRelationData(Class<T> clazz, String... expression) {
    return mDManager.getDelegate(DelegateFind.class).findRelationData(mDb, clazz, expression);
  }

  /**
   * 检查某个字段的值是否存在
   *
   * @param expression 字段和值"url=xxx"
   * @return {@code true}该字段的对应的value已存在
   */
  boolean checkDataExist(Class clazz, String... expression) {
    return mDManager.getDelegate(DelegateCommon.class)
        .checkDataExist(mDb, clazz, expression);
  }

  /**
   * 清空表数据
   */
  <T extends DbEntity> void clean(Class<T> clazz) {
    mDManager.getDelegate(DelegateCommon.class).clean(mDb, clazz);
  }

  /**
   * 执行sql语句
   */
  void exeSql(String sql) {
    mDb.execSQL(sql);
  }

  /**
   * 删除某条数据
   */
  <T extends DbEntity> void delData(Class<T> clazz, String... expression) {
    mDManager.getDelegate(DelegateUpdate.class).delData(mDb, clazz, expression);
  }

  /**
   * 修改某行数据
   */
  void modifyData(DbEntity dbEntity) {
    mDManager.getDelegate(DelegateUpdate.class).modifyData(mDb, dbEntity);
  }

  /**
   * 遍历所有数据
   */
  <T extends DbEntity> List<T> findAllData(Class<T> clazz) {
    return mDManager.getDelegate(DelegateFind.class).findAllData(mDb, clazz);
  }

  /**
   * 条件查寻数据
   */
  <T extends DbEntity> List<T> findData(Class<T> clazz, String... expression) {
    return mDManager.getDelegate(DelegateFind.class).findData(mDb, clazz, expression);
  }

  /**
   * 模糊查寻数据
   */
  <T extends DbEntity> List<T> findDataByFuzzy(Class<T> clazz, String conditions) {
    return mDManager.getDelegate(DelegateFind.class).findDataByFuzzy(mDb, clazz, conditions);
  }

  /**
   * 通过rowId判断数据是否存在
   */
  <T extends DbEntity> boolean isExist(Class<T> clazz, long rowId) {
    return mDManager.getDelegate(DelegateFind.class).itemExist(mDb, clazz, rowId);
  }

  /**
   * 通过rowId判断数据是否存在
   */
  boolean isExist(String tableName, long rowId) {
    return mDManager.getDelegate(DelegateFind.class).itemExist(mDb, tableName, rowId);
  }

  /**
   * 插入数据
   */
  void insertData(DbEntity dbEntity) {
    mDManager.getDelegate(DelegateUpdate.class).insertData(mDb, dbEntity);
  }

  /**
   * 查找某张表是否存在
   */
  boolean tableExists(Class clazz) {
    return mDManager.getDelegate(DelegateCommon.class).tableExists(mDb, clazz);
  }

  /**
   * 获取所在行Id
   */
  int[] getRowId(Class clazz) {
    return mDManager.getDelegate(DelegateFind.class).getRowId(mDb, clazz);
  }

  /**
   * 获取行Id
   */
  int getRowId(Class clazz, Object[] wheres, Object[] values) {
    return mDManager.getDelegate(DelegateFind.class).getRowId(mDb, clazz, wheres, values);
  }
}