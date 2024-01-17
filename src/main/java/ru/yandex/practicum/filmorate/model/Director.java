package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@Builder
public class Director {
    private Integer id;
    @NotBlank(message = "director name should not be blank")
    @Size(max = 256, message = "director name length should be less 256 symbols")
    private String name;
}
