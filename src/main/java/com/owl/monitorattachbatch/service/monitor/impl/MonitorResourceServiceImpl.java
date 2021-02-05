package com.owl.monitorattachbatch.service.monitor.impl;

import com.github.pagehelper.PageHelper;
import com.owl.monitorattachbatch.bean.MonitorAttachBean;
import com.owl.monitorattachbatch.bean.MonitorBean;
import com.owl.monitorattachbatch.bean.SysAttachBean;
import com.owl.monitorattachbatch.bean.TransfiniteBean;
import com.owl.monitorattachbatch.mapper.mysql.TransfiniteMapper;
import com.owl.monitorattachbatch.mapper.sqlserver.MonitorAttachMapper;
import com.owl.monitorattachbatch.service.ftp.FtpService;
import com.owl.monitorattachbatch.service.monitor.MonitorResourceService;
import com.owl.monitorattachbatch.service.monitor.MonitorResourceUploadeService;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class MonitorResourceServiceImpl implements MonitorResourceService {

    private Logger logger = LoggerFactory.getLogger(MonitorResourceServiceImpl.class);

    @Autowired
    private MonitorAttachMapper monitorAttachMapper;

    @Autowired
    private MonitorResourceUploadeService monitorResourceUploadeService;


    @Autowired
    private FtpService ftpService;

    @Value("${attachUpload.ftp.ip}")
    private String ftpIp;
    @Value("${attachUpload.ftp.port}")
    private int ftpPort;
    @Value("${attachUpload.ftp.user}")
    private String ftpUser;
    @Value("${attachUpload.ftp.password}")
    private String ftpPassword;


    @Override
    public int resourceTransfer(int count) {
        int result = 0;
        //不带视频的
        result += qryAndUploadAttach(count, 3, false);

        //带视频的
        result += qryAndUploadAttach(count, 4, true);

        return result;
    }


    private int qryAndUploadAttach(int pageCount, int checkCount, boolean withVideo) {
        PageHelper.startPage(0, pageCount);
        List<MonitorBean> monitorAttachWithVideo = monitorAttachMapper.qryNeedUploadAttachList(withVideo);
        if(monitorAttachWithVideo.size() == 0){
            return 0;
        }

        int count = 0;
        FTPClient ftpClient = null;
        try{
            logger.info("connect ftp, ip:{}, port:{}, user:{}, password:{}", ftpIp, ftpPort, ftpUser, ftpPassword);
            ftpClient = ftpService.connectFtpServer(ftpIp, ftpPort, ftpUser, ftpPassword);
            for (MonitorBean monitorBean : monitorAttachWithVideo) {
                //防止数据刚好在分页处
                if(monitorBean.getMonitorAttachBeans().size() == checkCount){
                    monitorResourceUploadeService.uploadAttachAndUpdateState(ftpClient, monitorBean);
                    ++count;
                }
            }
            return count;
        }finally {
            if(null != ftpClient){
                try {
                    ftpClient.logout();
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error("ftpClient close fail", e);
                    throw new RuntimeException(e);
                }
            }
        }

    }




}
