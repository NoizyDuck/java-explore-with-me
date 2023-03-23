package ru.practicum.event;

import lombok.*;
import org.apache.catalina.User;
import ru.practicum.category.Category;
import ru.practicum.compilation.Compilation;
import ru.practicum.location.Location;
import ru.practicum.participation.Participation;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@ToString
@Builder
@Entity
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "event")
public class Event {
    @Id
    private Long eventId;
    private String annotation;
    private Category category;
    private String description;
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid = Boolean.FALSE;
    private Integer participantLimit = 0;
    private Boolean requestModeration = Boolean.TRUE;
    private String title;
    private Integer confirmedRequests = 0;
    private LocalDateTime createdOn;
    private User initiator;
    private LocalDateTime publishedOn;
    private State state = State.PENDING;
    private Long views = 0L;
    private Set<Participation> requests;
    private Set<Compilation> compilations;
}
