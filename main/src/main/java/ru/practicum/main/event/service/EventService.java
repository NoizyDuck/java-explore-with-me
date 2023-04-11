package ru.practicum.main.event.service;

import org.springframework.stereotype.Service;
import ru.practicum.main.event.State;
import ru.practicum.main.event.dto.CreateEventDto;
import ru.practicum.main.event.dto.FullEventDto;
import ru.practicum.main.event.dto.ShortEventDto;
import ru.practicum.main.request.dto.RequestDto;
import ru.practicum.main.request.dto.UpdateEventDto;
import ru.practicum.main.request.dto.UpdateStatusRequestDto;
import ru.practicum.main.request.dto.UpdateStatusRequestResultDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public interface EventService {
    List<FullEventDto> getEventsWithParams(String text, List<Integer> categories, boolean paid, String rangeStart,
                                           String rangeEnd, boolean onlyAvailable, String sort, Integer from,
                                           Integer size, HttpServletRequest httpServletRequest);

    FullEventDto getEventById(Long id, HttpServletRequest httpServletRequest);

    List<FullEventDto> getEventsWithParamsAdmin(List<Long> users, State states, List<Integer> categories, String rangeStart, String rangeEnd, Integer from, Integer size);

    FullEventDto createEvent(Long userId, CreateEventDto createEventDto);

    List<ShortEventDto> getEventsByUserPrivate(Long userId, Integer from, Integer size);

    FullEventDto getFullEventsInfoByUser(Long userId, Long eventId);

    FullEventDto updateEventByUserPrivate(Long userId, Long eventId, UpdateEventDto updateEventDto);

    List<RequestDto> getEventRequests(Long userId, Long eventId);

    UpdateStatusRequestResultDto updateRequestStatus(Long userId, Long eventId, UpdateStatusRequestDto updateStatusRequestDto);

    FullEventDto updateEventByAdmin(Long eventId, UpdateEventDto updateEventDto);
}
