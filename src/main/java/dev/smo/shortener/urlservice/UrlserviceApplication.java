package dev.smo.shortener.urlservice;

import io.mongock.runner.springboot.EnableMongock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableMongock
@SpringBootApplication
public class UrlserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UrlserviceApplication.class, args);
	}

}
