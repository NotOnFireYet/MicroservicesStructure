package com.microservicetest.user;

import com.microservicetest.user.domain.User;
import com.microservicetest.user.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class UserApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserApplication.class, args);
	}

	@Bean
	CommandLineRunner run(UserService userService){
		return args -> {
			userService.saveUser(new User(null, "dude123", "1111"));
			userService.saveUser(new User(null, "john_doe", "2222"));
			userService.saveUser(new User(null, "jane_doe", "3333"));
			userService.saveUser(new User(null, "brad_from_finance", "4444"));
		};
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
