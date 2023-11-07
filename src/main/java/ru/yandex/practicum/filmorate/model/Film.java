package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Builder
public class Film {
    private Integer id;
    @NotNull(message = "name should not null")
    @NotBlank(message = "name should not be blank")
    private String name;
    @Size(max = 200, message = "description length should be less 200 symbols")
    private String description;
    private LocalDate releaseDate;
    @Positive(message = "duration should be positive")
    private Integer duration;
}
