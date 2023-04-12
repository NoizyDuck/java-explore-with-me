package ru.practicum.main.comment.service;

import org.springframework.stereotype.Service;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.dto.CreateCommentDto;

import java.util.List;

@Service
public interface CommentService {
    CommentDto createComment(Long userId, Long eventId, CreateCommentDto createCommentDto);

    CommentDto updateComment(Long userId, Long eventId, Long commentId, CreateCommentDto createCommentDto);

    void deleteComment(Long userId, Long eventId, Long commentId);

    void deleteCommentAdmin(Long commentId);

    CommentDto getComment(Long commentId);

    List<CommentDto> getAllComments(Long eventId, Integer from, Integer size);
}
