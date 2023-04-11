package ru.practicum.main.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.CreateCompilationDto;
import ru.practicum.main.compilation.mapper.CompilationMapper;
import ru.practicum.main.compilation.model.Compilation;
import ru.practicum.main.compilation.repository.CompilationRepository;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exceptions.NotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EntityManager entityManager;
    private final EventRepository eventRepository;

    @Override
    public List<CompilationDto> getEventsCompilation(Boolean pinned, Integer from, Integer size) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Compilation> query = builder.createQuery(Compilation.class);

        Root<Compilation> root = query.from(Compilation.class);
        Predicate predicate = builder.conjunction();

        if (pinned != null) {
            Predicate isPinned;
            if (pinned) {
                isPinned = builder.isTrue(root.get("pinned"));
            } else {
                isPinned = builder.isFalse(root.get("pinned"));
            }
            predicate = builder.and(predicate, isPinned);
        }

        query.select(root).where(predicate);
        List<Compilation> compilations = entityManager.createQuery(query)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();

        return compilations.stream().map(compilationMapper::compilationToDto).collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("compilation not found"));
        return compilationMapper.compilationToDto(compilation);
    }

    @Override
    public CompilationDto createCompilation(CreateCompilationDto createCompilationDto) {
        Compilation compilation = compilationMapper.createDtoToCompilation(createCompilationDto);
        List<Event> events = eventRepository.findAllByIdIn(createCompilationDto.getEvents());
        compilation.setEvents(events);

        return compilationMapper.compilationToDto(compilationRepository.save(compilation));
    }

    @Override
    public void deleteCompilationById(Long compId) {
        compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("compilation not found"));
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto updateCompilation(Long compId, CreateCompilationDto createCompilationDto) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("compilation not found"));
        List<Event> events = eventRepository.findAllByIdIn(createCompilationDto.getEvents());
        compilation.setEvents(events);
        compilation.setTitle(createCompilationDto.getTitle());
        return compilationMapper.compilationToDto(compilationRepository.save(compilation));
    }
}
