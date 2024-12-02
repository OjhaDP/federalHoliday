package com.federal.holidays.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HolidayRequest {

    private String name;
    private LocalDate date;
    private String countryCode;
    private String status;

    public HolidayRequest(String diwali, LocalDate of, String in) {
    }
}
