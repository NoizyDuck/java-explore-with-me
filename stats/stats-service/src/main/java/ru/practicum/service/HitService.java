package ru.practicum.service;

import ru.practicum.model.EndpointHitDto;
import ru.practicum.model.ViewStats;

import java.util.List;


public interface HitService {

    void post(EndpointHitDto endpointHitDto);

    List<ViewStats> getStat(String start, String end, List<String> uri, Boolean unique);
}
