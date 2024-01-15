package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class Friendship {
    private Long id;
    @NotBlank(message = "friendship type should not be blank")
    private String friendshipType;
}
