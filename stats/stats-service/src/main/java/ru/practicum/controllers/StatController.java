package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.EndpointHitDto;
import ru.practicum.model.ViewStats;
import ru.practicum.service.HitService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class StatController {
    private final HitService hitService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void postHit(@Valid @RequestBody EndpointHitDto endpointHitDto) {
        hitService.post(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStats> getHit(@RequestParam String start,
                                  @RequestParam String end,
                                  @RequestParam(required = false) List<String> uris,
                                  @RequestParam(defaultValue = "false") Boolean unique) {
        return hitService.getStat(start, end, uris, unique);
    }
}


