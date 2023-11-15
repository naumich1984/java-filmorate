package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder
public class User {
    private Long id;
    @NotBlank(message = "email should not be blank")
    @Email(message = "email should exists @ symbol")
    private String email;
    @NotNull(message = "login should not null")
    @NotBlank(message = "login should not be blank")
    @Pattern(regexp = "\\S+", message = "login should not exists space")
    private String login;
    private String name;
    @Past(message = "birthday should be in past")
    private LocalDate birthday;
}
