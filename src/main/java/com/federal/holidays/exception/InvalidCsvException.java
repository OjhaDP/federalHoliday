package com.federal.holidays.exception;

import lombok.Getter;

@Getter
public class InvalidCsvException extends RuntimeException{

    private String fileName;

    public InvalidCsvException(String message, String fileName) {
        super(message);
        this.fileName = fileName;
    }
}
