package ru.practicum.main.event.model;

import lombok.*;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.event.ParamState;
import ru.practicum.main.event.State;
import ru.practicum.main.location.Location;
import ru.practicum.main.user.model.User;


import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@Entity
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private String annotation;
    @OneToOne
    @JoinColumn(name = "category_id")
    private Category category;
    private String description;
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "location_id")
    private Location location;
    private Boolean paid = Boolean.FALSE;
    private Integer participantLimit = 0;
    private Boolean requestModeration = Boolean.TRUE;
    private String title;
    private Integer confirmedRequests = 0;
    @Column(name = "created_On")
    private LocalDateTime createdOn;
    @OneToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;
    private LocalDateTime publishedOn;
    @Enumerated(EnumType.STRING)
    private State state;
    private Long views;
}
