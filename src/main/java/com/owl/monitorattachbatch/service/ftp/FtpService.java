package com.owl.monitorattachbatch.service.ftp;

import org.apache.commons.net.ftp.FTPClient;

import java.io.InputStream;

public interface FtpService {

    public FTPClient connectFtpServer(String ip, int port, String userName, String password);

    public boolean uploadFile(String ip, int port, String userName, String password, String remoteDir, InputStream inputStream, String fileName);

    public boolean uploadFile(FTPClient ftpClient, String remoteDir, InputStream inputStream, String fileName);

    public boolean uploadFile(FTPClient ftpClient, String remoteDir, String filePath, String fileName);
}
