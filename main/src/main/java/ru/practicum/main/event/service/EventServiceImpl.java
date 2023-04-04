package ru.practicum.main.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.repository.CategoryRepository;
import ru.practicum.main.event.State;
import ru.practicum.main.event.dto.CreateEventDto;
import ru.practicum.main.event.dto.FullEventDto;
import ru.practicum.main.event.dto.ShortEventDto;
import ru.practicum.main.event.mapper.EventMapper;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exceptions.*;
import ru.practicum.main.pageRequest.PageRequestMapper;
import ru.practicum.main.request.RequestStatus;
import ru.practicum.main.request.dto.RequestDto;
import ru.practicum.main.request.dto.UpdateEventDto;
import ru.practicum.main.request.dto.UpdateStatusRequestDto;
import ru.practicum.main.request.dto.UpdateStatusRequestResultDto;
import ru.practicum.main.request.mapper.RequestMapper;
import ru.practicum.main.request.model.Request;
import ru.practicum.main.request.repository.RequestRepository;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final EntityManager entityManager;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<ShortEventDto> getEventsByUserPrivate(Long userId, Integer from, Integer size) {
        PageRequest pageRequest = PageRequestMapper.pageRequestValidaCreate(from, size);
        List<ShortEventDto> shortEventDtoList = eventRepository.findAllByInitiatorId(userId, pageRequest).stream().
                map(eventMapper::eventToShortDto).collect(Collectors.toList());
        if (shortEventDtoList.isEmpty()) {
            return Collections.emptyList();
        }
        return shortEventDtoList;
    }

    @Override
    public FullEventDto createEvent(Long userId, CreateEventDto createEventDto) {
        User initiator = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User id " + userId + " not found"));
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LocalDateTime eventDate = LocalDateTime.parse(createEventDto.getEventDate(), formatter);
        LocalDateTime eventDate = createEventDto.getEventDate();
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new WrongEventDateException("Event date cant be less then 2 hours from event creation time");
        }
        Category category = categoryRepository.findById(createEventDto.getCategory()).orElseThrow(() ->
                new NotFoundException("Category not found"));
        Event event = eventMapper.createEventDtoToEvent(createEventDto);
        event.setCategory(category);
        event.setInitiator(initiator);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(State.PENDING);
        event.setViews(0L);
        event.setConfirmedRequests(0);

        return eventMapper.eventToFullDto(eventRepository.save(event));
    }

    @Override
    public FullEventDto getFullEventsInfoByUser(Long userId, Long eventId) {
        return eventMapper.eventToFullDto(getEventByIdAndInitiatorId(userId, eventId));
    }

    @Override
    public FullEventDto updateEventByUserPrivate(Long userId, Long eventId, UpdateEventDto updateEventDto) {
        Event event = getEventByIdAndInitiatorId(userId, eventId);
        LocalDateTime eventDate = event.getEventDate();
        if(event.getPublishedOn() != null){
            throw new IncorrectRequestParameterException("event already published");
        }

        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new WrongEventDateException("Event date cant be less then 2 hours from now");
        }
        if (updateEventDto.getAnnotation() != null) {
            event.setAnnotation(updateEventDto.getAnnotation());
        }
        if (updateEventDto.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventDto.getCategory()).orElseThrow(() ->
                    new NotFoundException("no such category"));
            event.setCategory(category);
        }
        if (updateEventDto.getDescription() != null) {
            event.setDescription(updateEventDto.getDescription());
        }
        if (updateEventDto.getEventDate() != null) {
            LocalDateTime updateEventTime = updateEventDto.getEventDate();
            if (updateEventTime.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new WrongEventDateException("Event date cant be less then 2 hours from now");
            }
            event.setEventDate(updateEventDto.getEventDate());
        }
        if (updateEventDto.getLocation() != null) {
            event.setLocation(updateEventDto.getLocation());
        }
        if (updateEventDto.getPaid() != null) {
            event.setPaid(updateEventDto.getPaid());
        }
        if (updateEventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventDto.getParticipantLimit());
        }
        if (updateEventDto.getRequestModeration() != null) {
            event.setRequestModeration(updateEventDto.getRequestModeration());
        }
        if (updateEventDto.getStateAction() != null) {
            if (updateEventDto.getStateAction().equals(State.SEND_TO_REVIEW)) {
                event.setState(State.PENDING);
            } else {
                event.setState(State.CANCELED);
            }
        }
        if (updateEventDto.getTitle() != null) {
            event.setTitle(updateEventDto.getTitle());
        }

        return eventMapper.eventToFullDto(eventRepository.save(event));
    }

    @Override
    public List<RequestDto> getEventRequests(Long userId, Long eventId) {
        getEventByIdAndInitiatorId(userId, eventId);
        List<Request> requestList = requestRepository.findAllByEventIdAndRequesterId(eventId, userId);
        return requestList.stream().
                map(requestMapper::requestToDto).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UpdateStatusRequestResultDto updateRequestStatus(Long userId, Long eventId, UpdateStatusRequestDto updateStatusRequestDto) {
        Event event = getEvent(eventId);
        UpdateStatusRequestResultDto result = new UpdateStatusRequestResultDto();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            return result;
        }

        List<Request> requests = requestRepository.findAllByEventIdAndRequesterId(eventId, userId);
        List<Request> requestsToUpdate = requests.stream().filter(x -> updateStatusRequestDto.getRequestIds().contains(x.getId())).collect(Collectors.toList());

        if (requestsToUpdate.stream().anyMatch(x -> x.getStatus().equals(RequestStatus.CONFIRMED) && updateStatusRequestDto.getStatus().equals(RequestStatus.REJECTED))) {
            throw new IncorrectRequestParameterException("request already confirmed");
        }

        if (event.getConfirmedRequests() + requestsToUpdate.size() > event.getParticipantLimit() && updateStatusRequestDto.getStatus().equals(RequestStatus.CONFIRMED)) {
            throw new IncorrectParameterException("exceeding the limit of participants");
        }

        for (Request x : requestsToUpdate) {
            x.setStatus(RequestStatus.valueOf(updateStatusRequestDto.getStatus().toString()));
        }

        requestRepository.saveAll(requestsToUpdate);

        if (updateStatusRequestDto.getStatus().equals(RequestStatus.CONFIRMED)) {
            event.setConfirmedRequests(event.getConfirmedRequests() + requestsToUpdate.size());
        }

        eventRepository.save(event);

        if (updateStatusRequestDto.getStatus().equals(RequestStatus.CONFIRMED)) {
            result.setConfirmedRequests(requestsToUpdate.stream().map(requestMapper::requestToDto).collect(Collectors.toList()));
        }

        if (updateStatusRequestDto.getStatus().equals(RequestStatus.REJECTED)) {
            result.setRejectedRequests(requestsToUpdate.stream().map(requestMapper::requestToDto).collect(Collectors.toList()));
        }

        return result;
    }

    //
//        if (event.getParticipantLimit() != 0 && Objects.equals(event.getConfirmedRequests(), event.getParticipantLimit())) {
//            throw new IncorrectRequestParameterException("event out of request limit");
//        }
//        List<Long> requestIds = updateStatusRequestDto.getRequestIds();
//        List<Request> requests = requestRepository.findRequestsForUpdating(eventId, userId, requestIds);
//        if (updateStatusRequestDto.getStatus() == RequestStatus.REJECTED) {
//            for (Request request :
//                    requests) {
//                request.setStatus(RequestStatus.REJECTED);
//                requestRepository.saveAll(requests);
//                List<RequestDto> requestDtos = requests.stream().map(requestMapper::requestToDto).collect(Collectors.toList());
//                return UpdateStatusRequestResultDto.builder().
//                        confirmedRequests(Collections.emptyList()).
//                        rejectedRequests(requestDtos).
//                        build();
//            }
//        }
//            UpdateStatusRequestResultDto response = new UpdateStatusRequestResultDto(Collections.emptyList(), Collections.emptyList());
//
//            requests.forEach(request -> {
//                if (event.getConfirmedRequests() < event.getParticipantLimit()) {
//                    request.setStatus(RequestStatus.CONFIRMED);
//                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);
//
//                    List<RequestDto> newRequests = new ArrayList<>(response.getConfirmedRequests());
//                    newRequests.add(requestMapper.requestToDto(request));
//                    response.setConfirmedRequests(newRequests);
//                } else {
//                    request.setStatus(RequestStatus.REJECTED);
//
//                    List<RequestDto> newRequests = new ArrayList<>(response.getRejectedRequests());
//                    newRequests.add(requestMapper.requestToDto(request));
//                    response.setRejectedRequests(newRequests);
//                }
//            });
//
//
//            requestRepository.saveAll(requests);
//            eventRepository.save(event);
//
//            return response;
//        }

    @Override
    public FullEventDto updateEventByAdmin(Long eventId, UpdateEventDto updateEventDto) {
        Event event = getEvent(eventId);

        if (updateEventDto.getAnnotation() != null) {
            event.setAnnotation(updateEventDto.getAnnotation());
        }
        if (updateEventDto.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventDto.getCategory()).orElseThrow(() ->
                    new NotFoundException("category not found"));
            event.setCategory(category);
        }
        if (updateEventDto.getDescription() != null) {
            event.setDescription(updateEventDto.getDescription());
        }
        if (updateEventDto.getLocation() != null) {
            event.setLocation(updateEventDto.getLocation());
        }
        if (updateEventDto.getPaid() != null) {
            event.setPaid(updateEventDto.getPaid());
        }
        if (updateEventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventDto.getParticipantLimit());
        }
        if (updateEventDto.getRequestModeration() != null) {
            event.setRequestModeration(updateEventDto.getRequestModeration());
        }
        if (updateEventDto.getTitle() != null) {
            event.setTitle(updateEventDto.getTitle());
        }
        if (updateEventDto.getStateAction() != null) {
            if (updateEventDto.getStateAction().equals(State.PUBLISH_EVENT)) {
                if (event.getPublishedOn() != null) {
                    throw new WrongStateException("event already published");
                }
                if (event.getState().equals(State.CANCELED)) {
                    throw new WrongStateException("event already canceled");
                }
                event.setState(State.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (updateEventDto.getStateAction().equals(State.REJECT_EVENT)) {
                if (event.getPublishedOn() != null) {
                    throw new WrongStateException("event already published");
                }
                event.setState(State.CANCELED);
            }
        }
        if (updateEventDto.getEventDate() != null) {
            LocalDateTime eventDateTime = updateEventDto.getEventDate();
            if (eventDateTime.isBefore(LocalDateTime.now())
                    || eventDateTime.isBefore(event.getPublishedOn().plusHours(1))) {
                throw new WrongEventDateException("event date must be not less then 1 hour before publication date");
            }
        }

        return eventMapper.eventToFullDto(eventRepository.save(event));

    }


    @Override
    public List<FullEventDto> getEventsWithParams(String text, List<Integer> categories, boolean paid,
                                                  String rangeStart, String rangeEnd, boolean onlyAvailable,
                                                  String sort, Integer from, Integer size) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        Root<Event> event = criteriaQuery.from(Event.class);
        List<Predicate> predicates = new ArrayList<>();

        if (text != null && !text.isEmpty()) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(event.get("name")),
                    "%" + text.toLowerCase() + "%"));
        }

        if (categories != null && !categories.isEmpty()) {
            Join<Event, Category> categoryJoin = event.join("categories");
            predicates.add(categoryJoin.get("id").in(categories));
        }

        if (paid) {
            predicates.add(criteriaBuilder.isTrue(event.get("paid")));
        } else {
            predicates.add(criteriaBuilder.isFalse(event.get("paid")));
        }

        if (rangeStart != null && !rangeStart.isEmpty() && rangeEnd != null && !rangeEnd.isEmpty()) {
            LocalDateTime startDateTime = LocalDateTime.parse(rangeStart, formatter);
            LocalDateTime endDateTime = LocalDateTime.parse(rangeEnd, formatter);
            predicates.add(criteriaBuilder.between(event.get("dateTime"), startDateTime, endDateTime));
        }

        if (onlyAvailable) {
            predicates.add(criteriaBuilder.greaterThan(event.get("capacity"), event.get("registered")));
        }

        criteriaQuery.where(predicates.toArray(new Predicate[0]));

        if (sort != null && !sort.isEmpty()) {
            if (sort.equalsIgnoreCase("name")) {
                criteriaQuery.orderBy(criteriaBuilder.asc(event.get("name")));
            } else if (sort.equalsIgnoreCase("date")) {
                criteriaQuery.orderBy(criteriaBuilder.asc(event.get("dateTime")));
            } else if (sort.equalsIgnoreCase("category")) {
                Join<Event, Category> categoryJoin = event.join("categories");
                criteriaQuery.orderBy(criteriaBuilder.asc(categoryJoin.get("name")));
            }
        }

        TypedQuery<Event> query = entityManager.createQuery(criteriaQuery);
        if (from != null) {
            query.setFirstResult(from);
        }
        if (size != null) {
            query.setMaxResults(size);
        }

        return query.getResultList().stream().map(eventMapper::eventToFullDto).collect(Collectors.toList());
    }


    @Override
    public FullEventDto getEventById(Long id) {
        Event event = getEvent(id);
        return eventMapper.eventToFullDto(eventRepository.save(event));
    }

    @Override
    public List<FullEventDto> getEventsWithParamsAdmin(List<Long> users, List<State> states, List<Integer> categories,
                                                       String rangeStart, String rangeEnd, Integer from,
                                                       Integer size) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = builder.createQuery(Event.class);
        Root<Event> root = criteriaQuery.from(Event.class);
        Predicate predicate = builder.conjunction();

        if (users != null && !users.isEmpty()) {
            Predicate userPredicate = root.get("initiator").in(users);
            predicate = builder.and(predicate, userPredicate);
        }
        if (states != null && !states.isEmpty()) {
            Predicate statesPredicate = root.get("state").in(states);
            predicate = builder.and(predicate, statesPredicate);
        }
        if (categories != null && !categories.isEmpty()) {
            Predicate categoriesPredicate = root.get("category").in(categories);
            predicate = builder.and(predicate, categoriesPredicate);
        }
        if (rangeStart != null) {
            LocalDateTime startDateTime = LocalDateTime.parse(rangeStart, formatter);
            Predicate rangeStartPredicate = root.get("eventDate").in(startDateTime);
            predicate = builder.and(predicate, rangeStartPredicate);
        }
        if (rangeEnd != null) {
            LocalDateTime endDateTime = LocalDateTime.parse(rangeEnd, formatter);
            Predicate rangeEndPredicate = root.get("eventDate").in(endDateTime);
            predicate = builder.and(predicate, rangeEndPredicate);
        }
        criteriaQuery.select(root).where(predicate).orderBy(builder.asc(root.get("eventDate")));
        List<Event> events = entityManager.createQuery(criteriaQuery).setFirstResult(from).
                setMaxResults(size).getResultList();
        if (events.isEmpty()) {
            return Collections.emptyList();
        }

        //TODO: с просмотрами пока непонятно ><

        return events.stream().map(eventMapper::eventToFullDto).collect(Collectors.toList());
    }


    private Event getEventByIdAndInitiatorId(Long userId, Long eventId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                new NotFoundException("Event not found"));
    }

    private Event getEvent(Long id) {
        return eventRepository.findById(id).orElseThrow(() ->
                new NotFoundException("event not found"));
    }
}
