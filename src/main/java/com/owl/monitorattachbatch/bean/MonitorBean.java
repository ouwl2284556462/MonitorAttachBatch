package com.owl.monitorattachbatch.bean;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class MonitorBean {
    private Integer id;
    private Integer uploadState;
    private Integer isOverWeight;
    private Integer monitorPointDirection;
    private String carNumber;
    private Integer carNumberColor;
    private Integer carType;
    private Integer lane;
    private Integer axisCount;
    private Integer speed;
    private Integer direction;
    private Integer weight;
    private Integer wheelBase;
    private Integer axis1;
    private Integer axis2;
    private Integer axis3;
    private Integer axis4;
    private Integer axis5;
    private Integer axis6;
    private Integer axis7;
    private Integer axis8;
    private Integer weightStandard;
    private Integer overWeight;
    private Integer deviceWeighId;
    private Integer deviceWeighCalibrationId;
    private Integer state;
    private Integer mediaState;
    private String serialNumber;
    private Integer uploadState2;
    private Timestamp createTime;

    private List<MonitorAttachBean> monitorAttachBeans;

}
