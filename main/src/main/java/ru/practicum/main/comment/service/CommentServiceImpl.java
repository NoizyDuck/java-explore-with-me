package ru.practicum.main.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.dto.CreateCommentDto;
import ru.practicum.main.comment.mapper.CommentMapper;
import ru.practicum.main.comment.model.Comment;
import ru.practicum.main.comment.repository.CommentRepository;
import ru.practicum.main.event.State;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exceptions.NotFoundException;
import ru.practicum.main.exceptions.WrongStateException;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public CommentDto createComment(Long userId, Long eventId, CreateCommentDto createCommentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found."));
        Event event = getEvent(eventId);
        if (!event.getState().equals(State.PUBLISHED) && !userId.equals(event.getInitiator().getId())) {
            throw new WrongStateException("Event not published yet.");
        }
        Comment comment = new Comment();
        comment.setAuthor(user);
        comment.setEventId(eventId);
        comment.setCommentText(createCommentDto.getCommentText());
        comment.setCreatedTime(LocalDateTime.now());
        return commentMapper.commentToDto(commentRepository.save(comment));
    }


    @Override
    @Transactional
    public CommentDto updateComment(Long userId, Long eventId, Long commentId, CreateCommentDto createCommentDto) {
        userExistCheck(userId);
        eventExistCheck(eventId);
        Comment comment = getOrElseThrow(commentId);
        if (!userId.equals(comment.getAuthor().getId())) {
            throw new NotFoundException("Comment not found.");
        }
        comment.setCommentText(createCommentDto.getCommentText());
        comment.setModerationTime(LocalDateTime.now());
        return commentMapper.commentToDto(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long eventId, Long commentId) {
        userExistCheck(userId);
        eventExistCheck(eventId);
        Comment comment = getOrElseThrow(commentId);
        if (!userId.equals(comment.getAuthor().getId())) {
            throw new NotFoundException("Comment not found.");
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    public void deleteCommentAdmin(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new NotFoundException("Comment was not found.");
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    public CommentDto getComment(Long commentId) {
        Comment comment = getOrElseThrow(commentId);
        return commentMapper.commentToDto(comment);
    }

    @Override
    public List<CommentDto> getAllComments(Long eventId, Integer from, Integer size) {
        Event event = getEvent(eventId);
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new WrongStateException("Event not published yet.");
        }
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(ASC, "id"));
        List<Comment> comments = commentRepository.findByEventId(eventId, pageable);
        return comments.stream().map(commentMapper::commentToDto).collect(Collectors.toList());
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
    }

    private Comment getOrElseThrow(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found."));
    }

    private void userExistCheck(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User was not found.");
        }
    }

    private void eventExistCheck(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Event not found");
        }
    }
}
