package ru.practicum.main.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.main.request.dto.RequestDto;
import ru.practicum.main.request.model.Request;

@Component
public class RequestMapper {

    public RequestDto requestToDto(Request request){
        return RequestDto.builder().
                id(request.getId()).
                created(request.getCreated()).
                event(request.getEventId()).
                requester(request.getRequesterId()).
                status(request.getStatus()).
                build();
    }
}
