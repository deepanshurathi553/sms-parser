package com.credochain.transactionanalysis.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.PROCESSING)
public class TooSoonException extends RuntimeException {

    private final String message;

    public TooSoonException(String message) {this.message = message;}

    @Override
    public String getMessage() {
        return this.message;
    }
}
