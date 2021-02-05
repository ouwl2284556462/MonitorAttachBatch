package com.owl.monitorattachbatch.mapper.sqlserver;

import com.owl.monitorattachbatch.bean.MonitorAttachBean;
import com.owl.monitorattachbatch.bean.MonitorBean;

import java.util.List;

public interface MonitorAttachMapper {

    /**
     * 需要上传的文件
     * @return
     */
    List<MonitorBean> qryNeedUploadAttachList(boolean withVideo);


    void updateMonitorUploadState(Integer monitorId, int state);
}
