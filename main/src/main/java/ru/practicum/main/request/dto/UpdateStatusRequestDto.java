package ru.practicum.main.request.dto;

import lombok.*;
import ru.practicum.main.request.RequestStatus;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UpdateStatusRequestDto {
    private List<Long> requestIds;
    private RequestStatus status;
}
