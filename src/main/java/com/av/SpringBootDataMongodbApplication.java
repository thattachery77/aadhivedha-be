package com.av;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import com.av.services.StorageProperties;
import com.av.services.StorageService;
 
 
 
@EnableConfigurationProperties(StorageProperties.class)
//@EnableWebFlux
public @SpringBootApplication class SpringBootDataMongodbApplication/* implements WebFluxConfigurer */{

	public static void main(String[] args) {
		SpringApplication.run(SpringBootDataMongodbApplication.class, args);
	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			//storageService.deleteAll();
			storageService.init();
		};
	}
	
	/*
	 * public @Override void addCorsMappings(CorsRegistry registry) {
	 * registry.addMapping("/**").allowedOrigins("http://localhost:8080"); }
	 */
	
}
