package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;

import java.util.List;


public interface HitRepository extends JpaRepository<EndpointHit, Integer> {

    @Query("SELECT new ru.practicum.model.ViewStats(e.app, e.uri, count(e.ip)) " +
            "FROM EndpointHit e " +
            "WHERE (e.timestamp BETWEEN :start AND :end) AND " +
            "(e.uri IN :uris) " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY count(e.ip) DESC")
    List<ViewStats> getStatisticByUris(
            @Param("start") String start,
            @Param("end") String end,
            @Param("uris") List<String> uris
    );

    @Query("SELECT new ru.practicum.model.ViewStats(h.app, h.uri, count(DISTINCT h.ip)) " +
            "FROM EndpointHit h " +
            "WHERE (h.timestamp BETWEEN :start AND :end) AND " +
            "(h.uri IN :uris) " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY count(DISTINCT h.ip) DESC")
    List<ViewStats> getUniqueStatisticByUris(
            @Param("start") String start,
            @Param("end") String end,
            @Param("uris") List<String> uris
    );

    @Query("SELECT new ru.practicum.model.ViewStats(h.app, h.uri, count(h.ip)) " +
            "FROM EndpointHit h " +
            "WHERE (h.timestamp BETWEEN ?1 AND ?2) " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY count(h.ip) DESC")
    List<ViewStats> getUniqueStatisticByUrisNull(String start, String end);

    @Query("SELECT new ru.practicum.model.ViewStats(h.app, h.uri, count(DISTINCT h.ip)) " +
            "FROM EndpointHit h " +
            "WHERE (h.timestamp BETWEEN ?1 AND ?2) " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY count(DISTINCT h.ip) DESC")
    List<ViewStats> getStatisticByUrisNull(String start, String end);
}

