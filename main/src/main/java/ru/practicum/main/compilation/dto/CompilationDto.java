package ru.practicum.main.compilation.dto;

import lombok.*;
import ru.practicum.main.event.dto.ShortEventDto;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CompilationDto {
    private Long id;
    private List<ShortEventDto> events;
    private Boolean pinned;
    private String title;
}
