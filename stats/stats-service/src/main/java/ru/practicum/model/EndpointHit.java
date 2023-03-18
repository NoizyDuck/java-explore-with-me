package ru.practicum.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Builder
@Entity
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "stats")
public class EndpointHit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String app;
    @Column(nullable = false)
    private String uri;
    @Column(nullable = false)
    private String ip;
    @Column(nullable = false)
    private String timestamp;
}
