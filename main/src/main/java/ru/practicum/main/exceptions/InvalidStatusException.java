package ru.practicum.main.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidStatusException extends RuntimeException {
    public InvalidStatusException() {
        super("Unknown state: UNSUPPORTED_STATUS");
    }
}