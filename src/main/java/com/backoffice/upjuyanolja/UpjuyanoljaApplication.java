package com.backoffice.upjuyanolja;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UpjuyanoljaApplication {

	public static void main(String[] args) {
		SpringApplication.run(UpjuyanoljaApplication.class, args);
	}

	@PostConstruct
	public void setTimezone() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}
}
