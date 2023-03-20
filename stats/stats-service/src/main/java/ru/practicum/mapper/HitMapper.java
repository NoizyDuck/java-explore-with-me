package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.EndpointHitDto;

@Component
public class HitMapper {
    public EndpointHit endpointHitToHit(EndpointHitDto endpointHitDto) {
        return EndpointHit.builder()
                .app(endpointHitDto.getApp())
                .uri(endpointHitDto.getUri())
                .ip(endpointHitDto.getIp())
                .timestamp(endpointHitDto.getTimestamp())
                .build();
    }
}
