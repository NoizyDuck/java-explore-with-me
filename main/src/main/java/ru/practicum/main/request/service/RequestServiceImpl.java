package ru.practicum.main.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exceptions.IncorrectRequestParameterException;
import ru.practicum.main.exceptions.NotFoundException;
import ru.practicum.main.request.RequestStatus;
import ru.practicum.main.request.dto.RequestDto;
import ru.practicum.main.request.mapper.RequestMapper;
import ru.practicum.main.request.model.Request;
import ru.practicum.main.request.repository.RequestRepository;
import ru.practicum.main.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;
    private final UserRepository userRepository;

    @Override
    public List<RequestDto> getRequestByUserId(Long userId) {
        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        if (requests.isEmpty()) {
            return Collections.emptyList();
        }
        return requests.stream().map(requestMapper::requestToDto).collect(Collectors.toList());
    }

    @Override
    public RequestDto createRequestToEventByUser(Long userId, Long eventId) {
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new IncorrectRequestParameterException("request already exist");
        }
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event not found"));
        List<Request> requests = requestRepository.findAllByEventId(eventId);

        if (event.getInitiator().getId().equals(userId)) {
            throw new IncorrectRequestParameterException("Initiator of event cant make request to his event");
        }
        if (event.getPublishedOn() == null) {
            throw new IncorrectRequestParameterException("Event is not published");
        }
        if (!event.getRequestModeration() && requests.size() >= event.getParticipantLimit()) {
            throw new IncorrectRequestParameterException("Out of limit");
        }
        Request request = Request.builder().
                created(LocalDateTime.now()).
                eventId(eventId).
                requesterId(userId).
                status(RequestStatus.PENDING).
                build();


        return requestMapper.requestToDto(requestRepository.save(request));
    }

    @Override
    public RequestDto updateRequestStatusToCancel(Long userId, Long requestId) {
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId).orElseThrow(() ->
                new NotFoundException("Request not found"));
        request.setStatus(RequestStatus.CANCELED);
        return requestMapper.requestToDto(requestRepository.save(request));
    }

}
