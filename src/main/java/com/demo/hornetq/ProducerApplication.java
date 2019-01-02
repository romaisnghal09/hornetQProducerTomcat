package com.demo.hornetq;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class ProducerApplication extends SpringBootServletInitializer{

	private static final Logger LOGGER = LogManager.getLogger(ProducerApplication.class);
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		
		LOGGER.debug("ProducerApplication SpringApplicationBuilder start");
		return application.sources(ProducerApplication.class);
	}
	
	public static void main(String[] args) {
		LOGGER.info("ProducerApplication context configuration start.......");
		SpringApplication.run(ProducerApplication.class, args);
		LOGGER.info("ProducerApplication  context configuration end.......");
	}

}

