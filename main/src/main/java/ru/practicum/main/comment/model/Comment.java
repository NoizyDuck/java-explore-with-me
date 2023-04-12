package ru.practicum.main.comment.model;

import lombok.*;
import ru.practicum.main.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@ToString
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "comment_text")
    private String commentText;
    @Column(name = "event_id")
    private Long eventId;
    @ManyToOne
    @JoinColumn(name = "author")
    private User author;
    @Column(name = "created_time")
    private LocalDateTime createdTime;
    @Column(name = "moderation_time")
    private LocalDateTime moderationTime;
}
