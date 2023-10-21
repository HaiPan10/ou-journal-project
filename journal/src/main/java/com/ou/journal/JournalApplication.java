package com.ou.journal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JournalApplication {

	public static void main(String[] args) {
		// ConfigurableApplicationContext context = 
		SpringApplication.run(JournalApplication.class, args);
		// PasswordEncoder passwordEncoder = context.getBean(PasswordEncoder.class);
		// System.out.println("[DEBUG] - " + passwordEncoder.encode("123456"));
	}
}
