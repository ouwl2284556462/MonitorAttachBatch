package com.owl.monitorattachbatch.service.monitor;

public interface MonitorResourceService {

    /**
     * 处理的数目是 2 * count
     * @param count
     */
    int resourceTransfer(int count);
}
