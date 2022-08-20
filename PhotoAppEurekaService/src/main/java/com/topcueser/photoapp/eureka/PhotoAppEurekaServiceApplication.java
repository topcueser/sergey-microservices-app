package com.topcueser.photoapp.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class PhotoAppEurekaServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhotoAppEurekaServiceApplication.class, args);
	}

}
