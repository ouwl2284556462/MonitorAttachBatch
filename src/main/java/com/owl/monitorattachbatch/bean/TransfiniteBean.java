package com.owl.monitorattachbatch.bean;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class TransfiniteBean {
    private String transfinteTableName;

    private Long id;
    private String detectionNum;
    private String stationCode;
    private String stationName;
    private String laneDetection;
    private Integer drivingDirection;
    private String carNumber;
    private String carNumColor;
    private Timestamp detectionTime;
    private Integer speed;
    private Integer axlesNumber;
    private String wheelBase;
    private String axleLoadStandard;
    private Double totalWeight;
    private Integer isOverweight;
    private Double overWeight;
    private Double overRate;
    private String carColor;
    private Integer vehicleType;
    private String photo;
    private String photo1;
    private String photo2;
    private String photo3;
    private String video;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Integer unitCode;
    private String quartzflag;
}
