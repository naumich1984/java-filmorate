package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class Director {
    private Integer id;
    @NotBlank(message = "director name should not be blank")
    private String name;
}
