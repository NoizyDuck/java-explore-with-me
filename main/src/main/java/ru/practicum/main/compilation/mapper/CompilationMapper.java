package ru.practicum.main.compilation.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.CreateCompilationDto;
import ru.practicum.main.compilation.model.Compilation;
import ru.practicum.main.event.mapper.EventMapper;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CompilationMapper {
    private final EventMapper eventMapper;


    public CompilationDto compilationToDto(Compilation compilation) {
        return CompilationDto.builder().
                id(compilation.getId()).
                pinned(compilation.getPinned()).
                title(compilation.getTitle()).
                events(compilation.getEvents().stream().map(eventMapper::eventToShortDto).collect(Collectors.toList())).
                build();
    }

    public Compilation createDtoToCompilation(CreateCompilationDto createCompilationDto) {
        return Compilation.builder().
                pinned(createCompilationDto.getPinned()).
                title(createCompilationDto.getTitle()).
                build();
    }
}
