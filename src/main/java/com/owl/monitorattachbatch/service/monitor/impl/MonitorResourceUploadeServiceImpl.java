package com.owl.monitorattachbatch.service.monitor.impl;

import com.owl.monitorattachbatch.bean.MonitorAttachBean;
import com.owl.monitorattachbatch.bean.MonitorBean;
import com.owl.monitorattachbatch.bean.SysAttachBean;
import com.owl.monitorattachbatch.bean.TransfiniteBean;
import com.owl.monitorattachbatch.mapper.mysql.TransfiniteMapper;
import com.owl.monitorattachbatch.mapper.sqlserver.MonitorAttachMapper;
import com.owl.monitorattachbatch.service.ftp.FtpService;
import com.owl.monitorattachbatch.service.monitor.MonitorResourceUploadeService;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class MonitorResourceUploadeServiceImpl implements MonitorResourceUploadeService {

    private static final int ATTACH_TYPE_VIDEO = 3;
    private static final int[] ATTACH_TYPE_PHOTOES = {1, 2, 6};

    @Value("${attachUpload.ftp.ip}")
    private String ftpIp;

    private Logger logger = LoggerFactory.getLogger(MonitorResourceUploadeServiceImpl.class);

    @Value("${attachUpload.localFileBasePath}")
    private String localFileBasePath;

    @Value("${attachUpload.ftp.ftpRemotePath}")
    private String ftpRemotePath;

    @Autowired
    private MonitorAttachMapper monitorAttachMapper;

    @Autowired
    private TransfiniteMapper transfiniteMapper;

    @Autowired
    private FtpService ftpService;

    @Transactional
    public void uploadAttachAndUpdateState(FTPClient ftpClient, MonitorBean monitorBean) {
        List<MonitorAttachBean> monitorAttachBeans = monitorBean.getMonitorAttachBeans();

        List<SysAttachBean> photoes = new ArrayList<>(3);
        SysAttachBean video = null;
        for (MonitorAttachBean monitorAttachBean : monitorAttachBeans) {
            SysAttachBean sysAttachBean = monitorAttachBean.getSysAttachBean();
            String fileName = sysAttachBean.getName() + sysAttachBean.getExt();
            String path = localFileBasePath + sysAttachBean.getPath();
            logger.info("upload file, monitorId:{}, attachId:{}, fileName:{}, path:{}, ftpRemotePath:{}", monitorBean.getId(), sysAttachBean.getId(), fileName, path, ftpRemotePath);
            ftpService.uploadFile(ftpClient, ftpRemotePath, path, fileName);

            if(isVideo(monitorAttachBean)){
                video = sysAttachBean;
            }else{
                photoes.add(sysAttachBean);
            }
        }


        TransfiniteBean transfiniteBean = new TransfiniteBean();

        transfiniteBean.setTransfinteTableName(getTransfinteTableName());
        transfiniteBean.setCarNumber(monitorBean.getCarNumber());
        transfiniteBean.setOverWeight(getDoubleFromInteger(monitorBean.getOverWeight()));
        transfiniteBean.setSpeed(monitorBean.getSpeed());
        transfiniteBean.setWheelBase(getStringFromObject(monitorBean.getWheelBase()));
        transfiniteBean.setCarNumColor(getStringFromObject(monitorBean.getCarNumberColor()));
        transfiniteBean.setIsOverweight(monitorBean.getIsOverWeight());
        transfiniteBean.setVideo(getFileFullName(video));
        transfiniteBean.setPhoto(getFileFullName(photoes.get(0)));
        transfiniteBean.setPhoto1(getFileFullName(photoes.get(1)));
        transfiniteBean.setPhoto2(getFileFullName(photoes.get(2)));
        transfiniteBean.setDetectionTime(monitorBean.getCreateTime());


        //待确定，必填
        if(transfiniteBean.getVideo() == null){
            transfiniteBean.setVideo("");
        }

        transfiniteBean.setDetectionNum("");
        transfiniteBean.setStationCode("");
        transfiniteBean.setStationName("");
        transfiniteBean.setDrivingDirection(0);
        transfiniteBean.setAxlesNumber(0);
        transfiniteBean.setTotalWeight(0.0);
        transfiniteBean.setOverRate(0.0);
        transfiniteBean.setUnitCode(0);
        transfiniteBean.setVehicleType(0);

        //-------
        //插入数据到transfinite表
        transfiniteMapper.insertTransfiniteBean(transfiniteBean);
        //更新monitor状态
        monitorAttachMapper.updateMonitorUploadState(monitorBean.getId(), 2);
    }


    private String getFileFullName(SysAttachBean bean){
        if(bean == null){
            return null;
        }

        return String.format("FTP://%s/%s/%s%s", ftpIp, ftpRemotePath, bean.getName(), bean.getExt());
    }

    private boolean isPhoto(MonitorAttachBean bean){
        int type = bean.getType();
        for (int attachTypePhotoe : ATTACH_TYPE_PHOTOES) {
            if(attachTypePhotoe == type){
                return true;
            }
        }

        return false;
    }

    private boolean isVideo(MonitorAttachBean bean){
        return bean.getType() == ATTACH_TYPE_VIDEO;
    }

    private String getStringFromObject(Object obj){
        if(obj == null){
            return null;
        }

        return String.valueOf(obj);
    }


    private Double getDoubleFromInteger(Integer num) {
        if(num == null){
            return null;
        }

        return Double.valueOf(num.doubleValue());
    }

    private String getTransfinteTableName(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMM");
        //transfinite_202102
        return "transfinite_" + dtf.format(LocalDate.now());
    }
}
