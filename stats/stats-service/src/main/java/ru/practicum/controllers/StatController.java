package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.EndpointHitDto;
import ru.practicum.model.ViewStats;
import ru.practicum.service.HitService;

import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class StatController {
    private final HitService hitService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void postHit(@RequestBody EndpointHitDto endpointHitDto) {
        hitService.post(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStats> getHit(@RequestParam String start,
                                  @RequestParam String end,
                                  @RequestParam List<String> uris,
                                  @RequestParam(defaultValue = "false") boolean unique) {
        if (uris.isEmpty()) {
            return Collections.emptyList();
        }
        return hitService.getStat(start, end, uris, unique);
    }
}


