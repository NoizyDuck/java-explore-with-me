package ru.practicum.main.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.event.State;
import ru.practicum.main.event.dto.CreateEventDto;
import ru.practicum.main.event.dto.FullEventDto;
import ru.practicum.main.event.dto.ShortEventDto;
import ru.practicum.main.event.service.EventService;
import ru.practicum.main.request.dto.RequestDto;
import ru.practicum.main.request.dto.UpdateEventDto;
import ru.practicum.main.request.dto.UpdateStatusRequestDto;
import ru.practicum.main.request.dto.UpdateStatusRequestResultDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventControllers {
    private final EventService eventService;

    @GetMapping("/users/{userId}/events")
    public List<ShortEventDto> getEventsByIdPrivate(@PathVariable Long userId,
                                                    @RequestParam(defaultValue = "0", required = false) Integer from,
                                                    @RequestParam(defaultValue = "10", required = false) Integer size) {
        return eventService.getEventsByUserPrivate(userId, from, size);
    }

    @PostMapping("/users/{userId}/events")
    @ResponseStatus(value = HttpStatus.CREATED)
    public FullEventDto createEventPrivate(@PathVariable Long userId,
                                           @Valid @RequestBody CreateEventDto createEventDto) {
        return eventService.createEvent(userId, createEventDto);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public FullEventDto getFullEventsInfoByUserPrivate(@PathVariable Long userId,
                                                       @PathVariable Long eventId) {
        return eventService.getFullEventsInfoByUser(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public FullEventDto updateEventByUserPrivate(@PathVariable Long userId,
                                                 @PathVariable Long eventId,
                                                 @RequestBody UpdateEventDto updateEventDto) {
        return eventService.updateEventByUserPrivate(userId, eventId, updateEventDto);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<RequestDto> getEventRequests(@PathVariable Long userId,
                                             @PathVariable Long eventId) {
        return eventService.getEventRequests(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    public UpdateStatusRequestResultDto updateRequestStatus(@PathVariable Long userId,
                                                            @PathVariable Long eventId,
                                                            @RequestBody UpdateStatusRequestDto updateStatusRequestDto) {
        return eventService.updateRequestStatus(userId, eventId, updateStatusRequestDto);
    }

    @GetMapping("/admin/events")
    public List<FullEventDto> getEventsWithParamsAdmin(@RequestParam(required = false) List<Long> users,
                                                       @RequestParam(required = false) State states,
                                                       @RequestParam(required = false) List<Integer> categories,
                                                       @RequestParam(required = false) String rangeStart,
                                                       @RequestParam(required = false) String rangeEnd,
                                                       @RequestParam(defaultValue = "0", required = false) Integer from,
                                                       @RequestParam(defaultValue = "10", required = false) Integer size) {
        return eventService.getEventsWithParamsAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/admin/events/{eventId}")
    public FullEventDto updateEventByAdmin(@PathVariable Long eventId,
                                           @Valid @RequestBody UpdateEventDto updateEventDto) {
        return eventService.updateEventByAdmin(eventId, updateEventDto);
    }


    @GetMapping("/events")
    public List<FullEventDto> getWithParams(@RequestParam(required = false) String text,
                                            @RequestParam(required = false) List<Integer> categories,
                                            @RequestParam(required = false) boolean paid,
                                            @RequestParam(required = false) String rangeStart,
                                            @RequestParam(required = false) String rangeEnd,
                                            @RequestParam(defaultValue = "false", required = false) boolean onlyAvailable,
                                            @RequestParam(required = false) String sort,
                                            @RequestParam(defaultValue = "0", required = false) Integer from,
                                            @RequestParam(defaultValue = "10", required = false) Integer size,
                                            HttpServletRequest httpServletRequest) {

        return eventService.getEventsWithParams(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, httpServletRequest);
    }

    @GetMapping("/events/{id}")
    public FullEventDto getEventById(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        return eventService.getEventById(id, httpServletRequest);
    }

}
