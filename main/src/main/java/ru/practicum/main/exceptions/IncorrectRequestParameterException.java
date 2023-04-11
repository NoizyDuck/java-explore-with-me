package ru.practicum.main.exceptions;

public class IncorrectRequestParameterException extends RuntimeException {
    public IncorrectRequestParameterException(String message) {
        super(message);
    }
}

