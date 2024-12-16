package com.federal.holidays.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HolidayRequest {

    @NotBlank(message = "Name is required and cannot be blank.")
    @Pattern(regexp = "[a-zA-Z ]+", message = "Name must only contain alphabetic characters and spaces.")
    private String name;

    @NotNull(message = "Date is required.")
    private LocalDate date;

    @NotBlank(message = "Country code is required and cannot be blank.")
    @Pattern(regexp = "^[A-Z]{2,3}$", message = "Country code must be 2 or 3 uppercase alphabetic characters.")
    private String countryCode;

    @NotBlank(message = "Status is required and cannot be blank.")
    @Pattern(regexp = "^(Active|Inactive)$", message = "Status must be either 'ACTIVE' or 'INACTIVE'.")
    private String status;

}
