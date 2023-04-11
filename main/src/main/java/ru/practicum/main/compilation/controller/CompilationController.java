package ru.practicum.main.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.CreateCompilationDto;
import ru.practicum.main.compilation.service.CompilationService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CompilationController {
    private final CompilationService compilationService;

    @GetMapping("/compilations")
    public List<CompilationDto> getEventsCompilation(@RequestParam(required = false) Boolean pinned,
                                                     @RequestParam(defaultValue = "0", required = false) Integer from,
                                                     @RequestParam(defaultValue = "10", required = false) Integer size) {
        return compilationService.getEventsCompilation(pinned, from, size);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getCompilationById(@PathVariable Long compId) {
        return compilationService.getCompilationById(compId);
    }

    @PostMapping("/admin/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    CompilationDto createCompilation(@Valid @RequestBody CreateCompilationDto createCompilationDto) {
        return compilationService.createCompilation(createCompilationDto);
    }

    @DeleteMapping("/admin/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilationById(@PathVariable Long compId) {
        compilationService.deleteCompilationById(compId);
    }

    @PatchMapping("/admin/compilations/{compId}")
    CompilationDto updateCompilation(@PathVariable Long compId,
                                     @RequestBody CreateCompilationDto createCompilationDto) {
        return compilationService.updateCompilation(compId, createCompilationDto);
    }
}
