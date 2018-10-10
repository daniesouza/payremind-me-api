package com.payremindme.api;

import com.payremindme.api.config.property.PayRemindMeApiProperty;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(PayRemindMeApiProperty.class)
public class PayremindMeApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PayremindMeApiApplication.class, args);
	}
}
