package ru.practicum.main.event.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.main.category.categoryMapper.CategoryMapper;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.event.dto.CreateEventDto;
import ru.practicum.main.event.dto.FullEventDto;
import ru.practicum.main.event.dto.ShortEventDto;
import ru.practicum.main.user.userMapper.UserMapper;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class EventMapper {
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;
    private final EventRepository eventRepository;


    public ShortEventDto eventToShortDto(Event event) {
        return ShortEventDto.builder().
                annotation(event.getAnnotation()).
                category(categoryMapper.categoryToDto(event.getCategory())). //TODO: переделать мапер в мапере - херня
                confirmedRequests(event.getConfirmedRequests()).
                eventDate(event.getEventDate()).
                initiator(userMapper.userToShortDto(event.getInitiator())). //TODO: переделать мапер в мапере - херня
                paid(event.getPaid()).
                title(event.getTitle()).
                views(event.getViews()).
                build();
    }

    public Event createEventDtoToEvent(CreateEventDto createEventDto) {
        return Event.builder().
                annotation(createEventDto.getAnnotation()).
                description(createEventDto.getDescription()).
                eventDate(createEventDto.getEventDate()).
                location(createEventDto.getLocation()).
                participantLimit(createEventDto.getParticipantLimit()).
                paid(createEventDto.getPaid()).
                requestModeration(createEventDto.getRequestModeration()).
                title(createEventDto.getTitle())
                .build();
    }

    public FullEventDto eventToFullDto(Event event){
        return FullEventDto.builder().
                annotation(event.getAnnotation()).
                confirmedRequests(event.getConfirmedRequests()).
                createdOn(event.getCreatedOn()).
//                createdOn(LocalDateTime.now()).
                category(categoryMapper.categoryToDto(event.getCategory())).
                description(event.getDescription()).
                eventDate(event.getEventDate()).
                id(event.getId()).
                location(event.getLocation()).
                initiator(userMapper.userToShortDto(event.getInitiator())).
                paid(event.getPaid()).
                participantLimit(event.getParticipantLimit()).
                publishedOn(event.getPublishedOn()).
                requestModeration(event.getRequestModeration()).
                state(event.getState()).
                title(event.getTitle()).
                views(event.getViews()).
                build();
    }
}
