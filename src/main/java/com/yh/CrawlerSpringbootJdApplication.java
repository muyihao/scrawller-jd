package com.yh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.yh.mapper")
@EnableScheduling
public class CrawlerSpringbootJdApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrawlerSpringbootJdApplication.class, args);
	}

}
