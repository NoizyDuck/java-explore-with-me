package ru.practicum.event;

import javax.xml.stream.Location;

public class CreateEventDto {
    private long id;
    private String annotation;
    private String description;
    private String eventDate;
    private Location location;
    private boolean paid;
    private int participantLimit;
    private boolean requestModeration;
    private String title;
}
