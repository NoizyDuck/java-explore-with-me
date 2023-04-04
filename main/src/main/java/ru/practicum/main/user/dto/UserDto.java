package ru.practicum.main.user.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserDto {
    private String email;
    private Long id;
    private String name;
}