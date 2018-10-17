package com.anshul.bot.calvinbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
public class CalvinbotApplication {

	public static void main(String[] args) {
		ApiContextInitializer.init();
		SpringApplication.run(CalvinbotApplication.class, args);
	}
}
