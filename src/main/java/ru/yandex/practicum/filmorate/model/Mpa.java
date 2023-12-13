package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@Builder
public class Mpa {
    private Integer id;
    @NotNull(message = "mpa name should not null")
    @NotBlank(message = "mpa name should not be blank")
    private String name;
}
