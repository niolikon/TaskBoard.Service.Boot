package com.niolikon.taskboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class TaskBoardServiceBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskBoardServiceBootApplication.class, args);
	}

}
