package ru.practicum.participation;

import ru.practicum.event.Event;
import ru.practicum.user.User;

import java.time.LocalDateTime;

public class Participation {
    private Long id;
    private LocalDateTime created;
    private Event event;
    private User requester;
    private String status;

}
