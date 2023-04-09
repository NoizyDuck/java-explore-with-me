package ru.practicum.main.request.service;

import org.springframework.stereotype.Service;
import ru.practicum.main.request.dto.RequestDto;

import java.util.List;

@Service
public interface RequestService {
    List<RequestDto> getRequestByUserId(Long userId);

    RequestDto createRequestToEventByUser(Long userId, Long eventId);

    RequestDto updateRequestStatusToCancel(Long userId, Long requestId);
}
