package ru.practicum.main.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.repository.CategoryRepository;
import ru.practicum.main.event.State;
import ru.practicum.main.event.dto.CreateEventDto;
import ru.practicum.main.event.dto.FullEventDto;
import ru.practicum.main.event.dto.ShortEventDto;
import ru.practicum.main.event.mapper.EventMapper;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exceptions.IncorrectRequestParameterException;
import ru.practicum.main.exceptions.NotFoundException;
import ru.practicum.main.exceptions.WrongEventDateException;
import ru.practicum.main.exceptions.WrongStateException;
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
import ru.practicum.model.EndpointHitDto;
import ru.practicum.model.ViewStats;
import ru.practicum.services.StatClient;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    private final StatClient statClient;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<ShortEventDto> getEventsByUserPrivate(Long userId, Integer from, Integer size) {
        PageRequest pageRequest = PageRequestMapper.pageRequestValidaCreate(from, size);
        List<ShortEventDto> shortEventDtoList = eventRepository.findAllByInitiatorId(userId, pageRequest).stream()
                .map(eventMapper::eventToShortDto).collect(Collectors.toList());
        if (shortEventDtoList.isEmpty()) {
            return Collections.emptyList();
        }
        return shortEventDtoList;
    }

    @Override
    public FullEventDto createEvent(Long userId, CreateEventDto createEventDto) {
        User initiator = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User id " + userId + " not found"));
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
        if (event.getPublishedOn() != null) {
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
        List<Request> requestList = requestRepository.findAllByEventIdAndRequesterId(userId, eventId);

        return requestList.stream()
                .map(requestMapper::requestToDto).collect(Collectors.toList());
    }

    @Override
    public UpdateStatusRequestResultDto updateRequestStatus(Long userId, Long eventId, UpdateStatusRequestDto updateStatusRequestDto) {
        Event event = getEvent(eventId);
        UpdateStatusRequestResultDto result = new UpdateStatusRequestResultDto();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            return result;
        }
        List<Request> requests = requestRepository.findAllByEventId(eventId);
        List<Request> requestsToUpdate = requests.stream().filter(x -> updateStatusRequestDto.getRequestIds().contains(x.getId())).collect(Collectors.toList());

        if (requestsToUpdate.stream().anyMatch(x -> x.getStatus().equals(RequestStatus.CONFIRMED) && updateStatusRequestDto.getStatus().equals(RequestStatus.REJECTED))) {
            throw new IncorrectRequestParameterException("request already confirmed");
        }

        if (event.getConfirmedRequests() + requestsToUpdate.size() > event.getParticipantLimit() && updateStatusRequestDto.getStatus().equals(RequestStatus.CONFIRMED)) {
            throw new IncorrectRequestParameterException("exceeding the limit of participants");
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
                if (event.getState() != State.PENDING) {
                    throw new WrongStateException("Only pending events can be changed");
                }
                event.setState(State.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            }
            if (updateEventDto.getStateAction().equals(State.REJECT_EVENT)) {
                if (event.getState() == State.PUBLISHED) {
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
                                                  String sort, Integer from, Integer size, HttpServletRequest httpServletRequest) {
        String ip = httpServletRequest.getRemoteAddr();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        Root<Event> root = criteriaQuery.from(Event.class);
        List<Predicate> predicates = new ArrayList<>();

        if (text != null && !text.isEmpty()) {
            Predicate annotation = criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")),
                    "%" + text.toLowerCase() + "%");
            Predicate description = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),
                    "%" + text.toLowerCase() + "%");
            Predicate containTextPredicate = criteriaBuilder.or(annotation, description);
            predicates.add(containTextPredicate);
        }

        if (categories != null && !categories.isEmpty()) {
            Join<Event, Category> categoryJoin = root.join("category");
            predicates.add(categoryJoin.get("id").in(categories));
        }

        if (paid) {
            predicates.add(criteriaBuilder.isTrue(root.get("paid")));
        } else {
            predicates.add(criteriaBuilder.isFalse(root.get("paid")));
        }

        if (rangeStart != null && !rangeStart.isEmpty() && rangeEnd != null && !rangeEnd.isEmpty()) {
            LocalDateTime startDateTime = LocalDateTime.parse(rangeStart, formatter);
            LocalDateTime endDateTime = LocalDateTime.parse(rangeEnd, formatter);
            predicates.add(criteriaBuilder.between(root.get("eventDate"), startDateTime, endDateTime));
        }

        if (onlyAvailable) {
            predicates.add(criteriaBuilder.greaterThan(root.get("capacity"), root.get("registered")));
        }

        criteriaQuery.where(predicates.toArray(new Predicate[0]));

        if (sort != null && !sort.isEmpty()) {
            if (sort.equalsIgnoreCase("name")) {
                criteriaQuery.orderBy(criteriaBuilder.asc(root.get("name")));
            } else if (sort.equalsIgnoreCase("date")) {
                criteriaQuery.orderBy(criteriaBuilder.asc(root.get("dateTime")));
            } else if (sort.equalsIgnoreCase("category")) {
                Join<Event, Category> categoryJoin = root.join("categories");
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
        List<Event> events = query.getResultList();
        setViewList(events);
        sendStatList(events, ip);

        return query.getResultList().stream().map(eventMapper::eventToFullDto).collect(Collectors.toList());
    }


    @Override
    public FullEventDto getEventById(Long id, HttpServletRequest httpServletRequest) {
        Event event = getEvent(id);
        String ip = httpServletRequest.getRemoteAddr();
        setView(event);
        sendStat(event, ip);
        return eventMapper.eventToFullDto(eventRepository.save(event));
    }


    @Override
    public List<FullEventDto> getEventsWithParamsAdmin(List<Long> users, State states, List<Integer> categories,
                                                       String rangeStart, String rangeEnd, Integer from,
                                                       Integer size) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = builder.createQuery(Event.class);
        Root<Event> root = criteriaQuery.from(Event.class);
        List<Predicate> predicates = new ArrayList<>();

//        if (users != null && users.size() > 0) { CriteriaBuilder.In<Long> inClause = builder
//                .in(root.get("initiator")); for (Long userId : users) { inClause.value(userId); } predicates.add(inClause); }

        if (users != null && users.size() > 0) {
            Predicate userPredicate = root.get("initiator").in(users);
            predicates.add(userPredicate);
        }
        if (states != null) {
            Predicate statesPredicate = root.get("state").in(states);
            predicates.add(statesPredicate);
        }
        if (categories != null && categories.size() > 0) {
            Predicate categoriesPredicate = root.get("category").in(categories);
            predicates.add(categoriesPredicate);
        }
        if (rangeStart != null && !rangeStart.isEmpty() && rangeEnd != null && !rangeEnd.isEmpty()) {
            LocalDateTime startDateTime = LocalDateTime.parse(rangeStart, formatter);
            LocalDateTime endDateTime = LocalDateTime.parse(rangeEnd, formatter);
            predicates.add(builder.between(root.get("eventDate"), startDateTime, endDateTime));
        }

        criteriaQuery.where(predicates.toArray(new Predicate[0]));


        TypedQuery<Event> query = entityManager.createQuery(criteriaQuery);
        if (from != null) {
            query.setFirstResult(from);
        }
        if (size != null) {
            query.setMaxResults(size);
        }

        return query.getResultList().stream().map(eventMapper::eventToFullDto).collect(Collectors.toList());

    }

    private Event getEventByIdAndInitiatorId(Long userId, Long eventId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                new NotFoundException("Event not found"));
    }

    private Event getEvent(Long id) {
        return eventRepository.findById(id).orElseThrow(() ->
                new NotFoundException("event not found"));
    }

    public void setView(Event event) {
        String startTime = event.getCreatedOn().format(formatter);
        String endTime = LocalDateTime.now().format(formatter);
        List<String> uris = List.of("/events/" + event.getId());

        List<ViewStats> stats = getStats(startTime, endTime, uris);
        if (stats.size() == 1) {
            event.setViews(stats.get(0).getHits());
        } else {
            event.setViews(0L);
        }
    }

    public void setViewList(List<Event> events) {
        LocalDateTime start = events.get(0).getCreatedOn();
        List<String> uris = new ArrayList<>();
        Map<String, Event> eventsUri = new HashMap<>();
        String uri = "";
        for (Event event : events) {
            if (start.isBefore(event.getCreatedOn())) {
                start = event.getCreatedOn();
            }
            uri = "/events/" + event.getId();
            uris.add(uri);
            eventsUri.put(uri, event);
            event.setViews(0L);
        }

        String startTime = start.format(formatter);
        String endTime = LocalDateTime.now().format(formatter);

        List<ViewStats> stats = getStats(startTime, endTime, uris);
        stats.forEach((stat) ->
                eventsUri.get(stat.getUri()).setViews(stat.getHits()));
    }

    private void sendStat(Event event, String ip) {
        String serviceName = "main-service";
        LocalDateTime localDateTime = LocalDateTime.now();
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app(serviceName)
                .uri("/events")
                .ip(ip)
                .timestamp(localDateTime.format(formatter))
                .build();
        statClient.addStats(endpointHitDto);
        sendStatForTheEvent(event.getId(), ip, localDateTime, serviceName);
    }

    public void sendStatList(List<Event> events, String ip) {
        LocalDateTime localDateTime = LocalDateTime.now();
        String nameService = "main-service";

        EndpointHitDto requestDto = new EndpointHitDto();
        requestDto.setApp(nameService);
        requestDto.setUri("/events");
        requestDto.setIp(ip);
        requestDto.setTimestamp(localDateTime.format(formatter));
        statClient.addStats(requestDto);
        sendStatForEveryEvent(events, ip, LocalDateTime.now(), nameService);
    }

    private void sendStatForTheEvent(Long eventId, String ip, LocalDateTime localDateTime,
                                     String serviceName) {
        EndpointHitDto endpointHitDto = new EndpointHitDto();
        endpointHitDto.setApp(serviceName);
        endpointHitDto.setUri("/events/" + eventId);
        endpointHitDto.setIp(ip);
        endpointHitDto.setTimestamp(localDateTime.format(formatter));
        statClient.addStats(endpointHitDto);
    }

    private void sendStatForEveryEvent(List<Event> events, String ip, LocalDateTime localDateTime,
                                       String serviceName) {
        for (Event event : events) {
            EndpointHitDto requestDto = new EndpointHitDto();
            requestDto.setApp(serviceName);
            requestDto.setUri("/events/" + event.getId());
            requestDto.setIp(ip);
            requestDto.setTimestamp(localDateTime.format(formatter));
            statClient.addStats(requestDto);
        }
    }

    private List<ViewStats> getStats(String startTime, String endTime, List<String> uris) {
        return statClient.getStats(startTime, endTime, uris, false);
    }
}
