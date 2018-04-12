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
package com.arialyy.aria.core.inf;

import android.text.TextUtils;
import com.arialyy.aria.core.AriaManager;
import com.arialyy.aria.core.command.ICmd;
import com.arialyy.aria.core.command.normal.CancelCmd;
import com.arialyy.aria.core.command.normal.NormalCmdFactory;
import com.arialyy.aria.core.download.DownloadGroupTaskEntity;
import com.arialyy.aria.core.download.DownloadTaskEntity;
import com.arialyy.aria.core.manager.TEManager;
import com.arialyy.aria.core.upload.UploadTaskEntity;
import com.arialyy.aria.util.ALog;
import com.arialyy.aria.util.CommonUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AriaL on 2017/7/3.
 */
public abstract class AbsTarget<TARGET extends AbsTarget, ENTITY extends AbsEntity, TASK_ENTITY extends AbsTaskEntity>
    implements ITarget<TARGET> {
  protected String TAG = "";
  protected ENTITY mEntity;
  protected TASK_ENTITY mTaskEntity;
  protected String mTargetName;

  protected AbsTarget() {
    TAG = CommonUtil.getClassName(this);
  }

  /**
   * 重置状态，将任务状态设置为未开始状态
   */
  public TARGET resetState() {
    if (checkEntity()) {
      mTaskEntity.getEntity().setState(IEntity.STATE_WAIT);
      mTaskEntity.refreshInfo = true;
    } else {
      ALog.e(TAG, "重置状态失败");
    }
    return (TARGET) this;
  }

  /**
   * 删除记录，如果任务正在执行，则会删除正在下载的任务
   */
  public void removeRecord() {
    if (isRunning()) {
      ALog.d("AbsTarget", "任务正在下载，即将删除任务");
      cancel();
    } else {
      mEntity.deleteData();
      TEManager.getInstance().removeTEntity(mEntity.getKey());
    }
  }

  /**
   * 获取任务进度，如果任务存在，则返回当前进度
   *
   * @return 该任务进度
   */
  @Override
  public long getCurrentProgress() {
    return mEntity == null ? -1 : mEntity.getCurrentProgress();
  }

  /**
   * 获取任务文件大小
   *
   * @return 文件大小
   */
  @Override public long getSize() {
    return mEntity == null ? 0 : mEntity.getFileSize();
  }

  /**
   * 获取单位转换后的文件大小
   *
   * @return 文件大小{@code xxx mb}
   */
  @Override public String getConvertSize() {
    return mEntity == null ? "0b" : CommonUtil.formatFileSize(mEntity.getFileSize());
  }

  /**
   * 设置扩展字段，用来保存你的其它数据，如果你的数据比较多，你可以把你的数据转换为JSON字符串，然后再存到Aria中
   *
   * @param str 扩展数据
   */
  public TARGET setExtendField(String str) {
    if (TextUtils.isEmpty(str)) return (TARGET) this;
    if (checkEntity()) {
      if (TextUtils.isEmpty(mEntity.getStr()) || !mEntity.getStr().equals(str)) {
        mEntity.setStr(str);
      } else {
        ALog.e(TAG, "设置扩展字段失败，扩展字段为null");
      }
    } else {
      ALog.e(TAG, "设置扩展字段失败");
    }

    return (TARGET) this;
  }

  /**
   * 获取存放的扩展字段
   * 设置扩展字段{@link #setExtendField(String)}
   */
  public String getExtendField() {
    return mEntity.getStr();
  }

  /**
   * 获取任务状态
   *
   * @return {@link IEntity}
   */
  @Override
  public int getTaskState() {
    return mEntity.getState();
  }

  /**
   * 获取任务进度百分比
   *
   * @return 返回任务进度
   */
  @Override public int getPercent() {
    if (mEntity == null) {
      ALog.e("AbsTarget", "下载管理器中没有该任务");
      return 0;
    }
    if (mEntity.getFileSize() != 0) {
      return (int) (mEntity.getCurrentProgress() * 100 / mEntity.getFileSize());
    }
    return 0;
  }

  /**
   * 检查实体是否合法，如果实体合法，将保存实体到数据库，或更新数据库中的实体对象
   *
   * @return {@code true} 合法
   */
  protected abstract boolean checkEntity();

  protected int checkTaskType() {
    int taskType = 0;
    if (mTaskEntity instanceof DownloadTaskEntity) {
      taskType = ICmd.TASK_TYPE_DOWNLOAD;
    } else if (mTaskEntity instanceof DownloadGroupTaskEntity) {
      taskType = ICmd.TASK_TYPE_DOWNLOAD_GROUP;
    } else if (mTaskEntity instanceof UploadTaskEntity) {
      taskType = ICmd.TASK_TYPE_UPLOAD;
    }
    return taskType;
  }

  /**
   * 开始任务
   */
  @Override public void start() {
    if (checkEntity()) {
      AriaManager.getInstance(AriaManager.APP)
          .setCmd(CommonUtil.createNormalCmd(mTargetName, mTaskEntity, NormalCmdFactory.TASK_START,
              checkTaskType()))
          .exe();
    }
  }

  /**
   * 停止任务
   *
   * @see #stop()
   */
  @Deprecated public void pause() {
    if (checkEntity()) {
      stop();
    }
  }

  @Override public void stop() {
    if (checkEntity()) {
      AriaManager.getInstance(AriaManager.APP)
          .setCmd(CommonUtil.createNormalCmd(mTargetName, mTaskEntity, NormalCmdFactory.TASK_STOP,
              checkTaskType()))
          .exe();
    }
  }

  /**
   * 恢复任务
   */
  @Override public void resume() {
    if (checkEntity()) {
      AriaManager.getInstance(AriaManager.APP)
          .setCmd(CommonUtil.createNormalCmd(mTargetName, mTaskEntity, NormalCmdFactory.TASK_START,
              checkTaskType()))
          .exe();
    }
  }

  /**
   * 删除任务
   */
  @Override public void cancel() {
    if (checkEntity()) {
      AriaManager.getInstance(AriaManager.APP)
          .setCmd(CommonUtil.createNormalCmd(mTargetName, mTaskEntity, NormalCmdFactory.TASK_CANCEL,
              checkTaskType()))
          .exe();
    }
  }

  /**
   * 任务重试
   */
  public void reTry() {
    if (checkEntity()) {
      List<ICmd> cmds = new ArrayList<>();
      int taskType = checkTaskType();
      cmds.add(
          CommonUtil.createNormalCmd(mTargetName, mTaskEntity, NormalCmdFactory.TASK_STOP,
              taskType));
      cmds.add(CommonUtil.createNormalCmd(mTargetName, mTaskEntity, NormalCmdFactory.TASK_START,
          taskType));
      AriaManager.getInstance(AriaManager.APP).setCmds(cmds).exe();
    }
  }

  /**
   * 删除任务
   *
   * @param removeFile {@code true} 不仅删除任务数据库记录，还会删除已经删除完成的文件
   * {@code false}如果任务已经完成，只删除任务数据库记录，
   */
  public void cancel(boolean removeFile) {
    if (checkEntity()) {
      CancelCmd cancelCmd = (CancelCmd) CommonUtil.createNormalCmd(mTargetName, mTaskEntity,
          NormalCmdFactory.TASK_CANCEL, checkTaskType());
      cancelCmd.removeFile = removeFile;
      AriaManager.getInstance(AriaManager.APP).setCmd(cancelCmd).exe();
    }
  }

  /**
   * 重新下载
   */
  public void reStart() {
    if (checkEntity()) {
      cancel();
      start();
    }
  }
}
