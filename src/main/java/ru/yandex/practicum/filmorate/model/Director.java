package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Director {
    private Integer id;
    @NotNull(message = "director name should not null")
    @NotBlank(message = "director name should not be blank")
    private String name;
}
