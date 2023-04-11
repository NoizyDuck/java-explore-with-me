package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.mapper.HitMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.EndpointHitDto;
import ru.practicum.model.ViewStats;
import ru.practicum.repository.HitRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HitServiceImpl implements HitService {
    private final HitRepository hitRepository;
    private final HitMapper hitMapper;

    @Override
    public void post(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = hitMapper.endpointHitToHit(endpointHitDto);
        hitRepository.save(endpointHit);
    }

    @Override
    public List<ViewStats> getStat(String start, String end, List<String> uris, Boolean unique) {
        if (uris != null && !uris.isEmpty()) {
            if (unique) {
                return hitRepository.getUniqueStatisticByUris(start, end, uris);
            }
            return hitRepository.getStatisticByUris(start, end, uris);
        } else {
            if (unique) {
                return hitRepository.getUniqueStatisticByUrisNull(start, end);
            }
            return hitRepository.getStatisticByUrisNull(start, end);
        }
    }
}
