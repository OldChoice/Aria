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

import com.arialyy.aria.core.inf.AbsNormalTaskEntity;
import com.arialyy.aria.orm.ActionPolicy;
import com.arialyy.aria.orm.annotation.Foreign;
import com.arialyy.aria.orm.annotation.Ignore;
import com.arialyy.aria.orm.annotation.NoNull;
import com.arialyy.aria.orm.annotation.Primary;

/**
 * Created by lyy on 2017/1/23.
 * 下载任务实体和下载实体为一对一关系，下载实体删除，任务实体自动删除
 */
public class DownloadTaskEntity extends AbsNormalTaskEntity<DownloadEntity> {

  @Ignore public DownloadEntity entity;

  /**
   * 任务的url
   */
  @NoNull public String url = "";

  /**
   * 所属的任务组组名，如果不属于任务组，则为null
   */
  @Foreign(parent = DownloadGroupTaskEntity.class, column = "key",
      onUpdate = ActionPolicy.CASCADE, onDelete = ActionPolicy.CASCADE)
  public String groupName;

  /**
   * 是否是chunk模式
   */
  public boolean isChunked = false;

  /**
   * 该任务是否属于任务组
   */
  public boolean isGroupTask = false;

  /**
   * Task实体对应的key
   */
  @Primary
  @Foreign(parent = DownloadEntity.class, column = "downloadPath",
      onUpdate = ActionPolicy.CASCADE, onDelete = ActionPolicy.CASCADE)
  public String key;

  public DownloadTaskEntity() {
  }

  @Override public DownloadEntity getEntity() {
    return entity;
  }

  @Override public String getKey() {
    return key;
  }
}
