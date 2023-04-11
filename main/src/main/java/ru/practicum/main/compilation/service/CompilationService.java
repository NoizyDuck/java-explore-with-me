package ru.practicum.main.compilation.service;

import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.CreateCompilationDto;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getEventsCompilation(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationById(Long compId);

    CompilationDto createCompilation(CreateCompilationDto createCompilationDto);

    void deleteCompilationById(Long compId);

    CompilationDto updateCompilation(Long compId, CreateCompilationDto createCompilationDto);
}
