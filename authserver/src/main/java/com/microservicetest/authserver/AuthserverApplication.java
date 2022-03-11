package com.microservicetest.authserver;

import com.microservicetest.authserver.domain.TokenEntity;
import com.microservicetest.authserver.domain.AppUser;
import com.microservicetest.authserver.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@SpringBootApplication
public class AuthserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthserverApplication.class, args);
	}

	@Bean
	CommandLineRunner run(UserService userService){
		return args -> {
			userService.saveUser(new AppUser(null, "dude123", "1111", "HR", "London", new TokenEntity()));
			userService.saveUser(new AppUser(null, "john_doe", "2222", "Finance", "Paris", new TokenEntity()));
			userService.saveUser(new AppUser(null, "jane_doe", "3333", "IT", "Mars", new TokenEntity()));
		};
	}

	@Bean
	BCryptPasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}
}
