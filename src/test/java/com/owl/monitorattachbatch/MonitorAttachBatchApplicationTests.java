package com.owl.monitorattachbatch;

import com.owl.monitorattachbatch.service.ftp.FtpService;
import com.owl.monitorattachbatch.service.monitor.MonitorResourceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@SpringBootTest
class MonitorAttachBatchApplicationTests {

	@Autowired
	FtpService ftpService;

	@Autowired
	MonitorResourceService monitorResourceService;

	//@Test
	void testFtp() throws FileNotFoundException {
		File file = new File("C:\\Users\\Qin\\Desktop\\q5.bmp");
		FileInputStream fileInputStream = new FileInputStream(file);
		ftpService.uploadFile("192.168.1.100", 21, "k", "1", "111", fileInputStream, file.getName());
	}


	@Test
	void MonitorResourceServiceImpl(){
		monitorResourceService.resourceTransfer(10);
	}

	//@Test
	void testDate(){
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMM");
		System.out.println(dtf.format(LocalDate.now()));
		//transfinite_202102
	}

}
