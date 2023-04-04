package ru.practicum.main.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.main.request.model.Request;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByEventIdAndRequesterId(Long eventId, Long requesterId);
    List<Request> findAllByRequesterId(Long requesterId);
    List<Request> findAllByEventId(Long eventId);
    Request findByIdAndEventId(Long requestId, Long eventId);
    Optional<Request> findByIdAndRequesterId(Long requestId, Long requesterId);
    boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);

    @Query("SELECT r FROM Request r " +
            "WHERE r.eventId = :eventId AND " +
            "r.requesterId = :initiatorId AND " +
            "r.id in :requestIds " +
            "ORDER BY r.created ASC")
    List<Request> findRequestsForUpdating(
            @Param("eventId") long eventId,
            @Param("initiatorId") long initiatorId,
            @Param("requestIds") List<Long> requestIds
    );
}