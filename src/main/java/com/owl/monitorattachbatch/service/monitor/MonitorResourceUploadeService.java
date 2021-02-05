package com.owl.monitorattachbatch.service.monitor;

import com.owl.monitorattachbatch.bean.MonitorBean;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.stereotype.Service;

@Service
public interface MonitorResourceUploadeService {



    /**
     * 调用同一类中的方法，事务不会效果，因此把方法移动到这里
     * @param ftpClient
     * @param monitorBean
     */
    void uploadAttachAndUpdateState(FTPClient ftpClient, MonitorBean monitorBean);
}
