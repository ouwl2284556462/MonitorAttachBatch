package com.owl.monitorattachbatch.bean;

import lombok.Data;

@Data
public class MonitorAttachBean {
    private Integer monitorId;
    private String attachId;
    private Integer type;

    private SysAttachBean sysAttachBean;
}
