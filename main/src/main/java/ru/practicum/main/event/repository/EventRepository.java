package ru.practicum.main.event.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main.event.model.Event;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiatorId(Long initiatorId, PageRequest pageRequest);

    Optional<Event> findByIdAndInitiatorId(Long id, Long initiatorId);

    List<Event> findAllByIdIn(List<Long> eventIds);

    boolean existsByCategoryId(Long categoryId);

}
