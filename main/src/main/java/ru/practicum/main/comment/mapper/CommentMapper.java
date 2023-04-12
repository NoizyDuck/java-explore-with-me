package ru.practicum.main.comment.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.model.Comment;

@Component
@RequiredArgsConstructor
public class CommentMapper {
    public CommentDto commentToDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .commentText(comment.getCommentText())
                .author(comment.getAuthor())
                .createdTime(comment.getCreatedTime())
                .moderationTime(comment.getModerationTime())
                .build();
    }
}
