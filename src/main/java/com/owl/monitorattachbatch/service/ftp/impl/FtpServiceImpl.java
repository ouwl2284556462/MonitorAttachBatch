package com.owl.monitorattachbatch.service.ftp.impl;

import com.owl.monitorattachbatch.config.SqlServerSqlSessionTplConfig;
import com.owl.monitorattachbatch.service.ftp.FtpService;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class FtpServiceImpl implements FtpService {

    private Logger logger = LoggerFactory.getLogger(FtpServiceImpl.class);


    @Override
    public FTPClient connectFtpServer(String ip, int port, String userName, String password) {
        FTPClient ftpClient = new FTPClient();
        //设置连接超时时间
        ftpClient.setConnectTimeout(1000 * 30);
        //设置ftp字符集
        ftpClient.setControlEncoding("utf-8");
        //设置被动模式，文件传输端口设置
        ftpClient.enterLocalPassiveMode();
        try {
            ftpClient.connect(ip, port);
            //设置文件传输模式为二进制，可以保证传输的内容不会被改变
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.login(userName, password);
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                ftpClient.disconnect();
                throw new RuntimeException("content ftp fail reply code: " + replyCode);
            }
            logger.info("ftp replyCode {}", replyCode);
        } catch (Exception e) {
            logger.error("ftp connect fail {}", e.getMessage());
            throw new RuntimeException(e);
        }


        return ftpClient;
    }


    /**
     * @param inputStream 待上传文件的输入流
     * @param fileName  文件保存时的名字
     */
    @Override
    public boolean uploadFile(String ip, int port, String userName, String password, String remoteDir, InputStream inputStream, String fileName) {
        FTPClient ftpClient = connectFtpServer(ip, port, userName, password);
        try {
            return uploadFile(ftpClient, remoteDir, inputStream, fileName);
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.logout();
                    ftpClient.disconnect();
                } catch (IOException e) {
                    throw new RuntimeException("disconnect fail: " + e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public boolean uploadFile(FTPClient ftpClient, String remoteDir, InputStream inputStream, String fileName) {
        try {
            //进入到文件保存的目录
            ftpClient.changeWorkingDirectory(remoteDir);
            //保存文件
            logger.info("start upload, fileName:{}", fileName);
            boolean isSuccess = ftpClient.storeFile(fileName, inputStream);
            if (!isSuccess) {
                logger.info("upload fail, fileName:{}", fileName);
                return false;
            }
            logger.info("upload success, fileName:{}", fileName);
            return true;
        } catch (IOException e) {
            logger.error("upload fail: {}", e.getMessage());
            throw new RuntimeException("upload fail: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean uploadFile(FTPClient ftpClient, String remoteDir, String filePath, String fileName) {
        File targetFile = new File(filePath + File.separator + fileName);
        try (FileInputStream fileInputStream = new FileInputStream(targetFile)){
            return uploadFile(ftpClient, remoteDir, fileInputStream, fileName);
        } catch (Exception e) {
            logger.error("upload fail: {}", e.getMessage());
            throw new RuntimeException("upload fail: " + e.getMessage(), e);
        }
    }

}
