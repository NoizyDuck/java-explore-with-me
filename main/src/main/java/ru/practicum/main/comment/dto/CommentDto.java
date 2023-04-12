package ru.practicum.main.comment.dto;

import lombok.*;
import ru.practicum.main.user.model.User;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CommentDto {
    private Long id;
    private String commentText;
    private User author;
    private LocalDateTime createdTime;
    private LocalDateTime moderationTime;
}
