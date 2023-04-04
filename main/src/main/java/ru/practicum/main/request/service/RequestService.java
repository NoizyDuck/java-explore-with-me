package ru.practicum.main.request.service;

import ru.practicum.main.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    List<RequestDto> getRequestByUserId(Long userId);

    RequestDto createRequestToEventByUser(Long userId, Long eventId);

    RequestDto updateRequestStatusToCancel(Long userId, Long requestId);
}
