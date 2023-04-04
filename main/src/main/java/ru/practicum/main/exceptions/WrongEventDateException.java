package ru.practicum.main.exceptions;

public class WrongEventDateException extends RuntimeException{
    public WrongEventDateException(String message) {
        super(message);
    }
}
