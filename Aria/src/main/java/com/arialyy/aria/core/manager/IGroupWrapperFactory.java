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
package com.arialyy.aria.core.manager;

import com.arialyy.aria.core.common.AbsEntity;
import com.arialyy.aria.core.wrapper.AbsTaskWrapper;

/**
 * 任务组通过组创建任务
 * Created by Aria.Lao on 2017/11/1.
 */
interface IGroupWrapperFactory<ENTITY extends AbsEntity, TASK_ENTITY extends AbsTaskWrapper<ENTITY>> {

  /**
   * 获取任务组的任务实体，
   * 1、创建实体和任务实体之间的关联
   * 2、如果在数据库中查找不到对应的数据，则新创建任务实体
   *
   * @param taskId 组合任务任务Id
   */
  TASK_ENTITY getGroupWrapper(long taskId);
}
