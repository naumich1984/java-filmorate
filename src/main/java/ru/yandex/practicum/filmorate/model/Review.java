package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@Builder
public class Review {
    private Long reviewId;
    @NotBlank(message = "review should not be blank")
    @Size(max = 32768, message = "description length should be less 32768 symbols")
    private String content;
    private Boolean isPositive;
    private Long filmId;
    private Long userId;
    private Long useful;
}
