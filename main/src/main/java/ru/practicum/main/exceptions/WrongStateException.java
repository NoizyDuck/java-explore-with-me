package ru.practicum.main.exceptions;

public class WrongStateException extends RuntimeException {
    public WrongStateException(String message) {
        super(message);
    }
}
