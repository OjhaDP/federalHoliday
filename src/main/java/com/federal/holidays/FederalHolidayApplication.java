package com.federal.holidays;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Federal Holiday API", version = "1.0", description = "API Documentation"))
public class FederalHolidayApplication {

	public static void main(String[] args) {
		SpringApplication.run(FederalHolidayApplication.class, args);
	}

}
