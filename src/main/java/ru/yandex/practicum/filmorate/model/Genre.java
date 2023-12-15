package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@Builder
public class Genre {
    private Integer id;
    @NotNull(message = "genre name should not null")
    @NotBlank(message = "genre name should not be blank")
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Genre genre = (Genre) o;

        return id.equals(genre.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
