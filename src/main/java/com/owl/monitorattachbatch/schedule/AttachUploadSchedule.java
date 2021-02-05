package com.owl.monitorattachbatch.schedule;

import com.owl.monitorattachbatch.service.monitor.MonitorResourceService;
import com.owl.monitorattachbatch.service.monitor.impl.MonitorResourceServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

/**
 * 文件上传定时任务
 */
@Configuration
public class AttachUploadSchedule {

    private Logger logger = LoggerFactory.getLogger(AttachUploadSchedule.class);

    @Autowired
    MonitorResourceService monitorResourceService;

    @Value("${attachUpload.schedule.eachCount}")
    private int dealEachCount;

    @Scheduled(cron = "${attachUpload.schedule.cron}")
    private void attachUploadTask() {
        logger.info("start resourceTransfer, time:{}", LocalDateTime.now());
        int count = monitorResourceService.resourceTransfer(dealEachCount);
        logger.info("finish resourceTransfer, time:{}, deal monitor count:{}", LocalDateTime.now(), count);
    }
}
