package com.topcueser.photoapp.api.users;

import com.topcueser.photoapp.api.users.entities.Authority;
import com.topcueser.photoapp.api.users.repositories.AuthorityRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableEurekaClient
public class PhotoAppApiUsersApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhotoAppApiUsersApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(AuthorityRepository authorityRepository) {
		return args -> {

			Authority authorityAdmin = new Authority();
			authorityAdmin.setName("ROLE_ADMIN");
			authorityRepository.save(authorityAdmin);

			Authority authorityUser = new Authority();
			authorityUser.setName("ROLE_USER");
			authorityRepository.save(authorityUser);

		};
	}
}
