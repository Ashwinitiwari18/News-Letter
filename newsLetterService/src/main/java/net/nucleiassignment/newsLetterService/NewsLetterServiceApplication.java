package net.nucleiassignment.newsLetterService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "net.nucleiassignment.newsLetterService.service")
public class NewsLetterServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewsLetterServiceApplication.class, args);
	}
}
