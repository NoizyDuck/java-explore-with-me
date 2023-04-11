package ru.practicum.main.compilation.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CreateCompilationDto {
    @NotNull
    private List<Long> events;
    private Boolean pinned;
    @NotBlank
    @NotNull
    private String title;
}
