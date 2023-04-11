package ru.practicum.main.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.request.dto.RequestDto;
import ru.practicum.main.request.service.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @GetMapping("/users/{userId}/requests")
    public List<RequestDto> getRequestByUserId(@PathVariable Long userId) {
        return requestService.getRequestByUserId(userId);
    }

    @PostMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    RequestDto createRequestToEventByUser(@PathVariable Long userId,
                                          @RequestParam Long eventId) {
        return requestService.createRequestToEventByUser(userId, eventId);
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    RequestDto updateRequestStatusToCancel(@PathVariable Long userId,
                                           @PathVariable Long requestId) {
        return requestService.updateRequestStatusToCancel(userId, requestId);
    }
}
