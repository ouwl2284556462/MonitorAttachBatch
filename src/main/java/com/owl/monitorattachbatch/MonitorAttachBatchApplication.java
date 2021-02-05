package com.owl.monitorattachbatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableScheduling
@SpringBootApplication
public class MonitorAttachBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(MonitorAttachBatchApplication.class, args);
	}

}
