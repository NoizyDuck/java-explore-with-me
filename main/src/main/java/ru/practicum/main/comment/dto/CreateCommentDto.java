package ru.practicum.main.comment.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CreateCommentDto {
    @NotBlank
    @Size(max = 1000)
    private String commentText;
}
