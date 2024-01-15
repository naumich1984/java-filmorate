package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    @NotBlank(message = "email should not be blank")
    @Email(message = "email should exists @ symbol")
    private String email;
    @NotBlank(message = "login should not be blank")
    @Pattern(regexp = "\\S+", message = "login should not exists space")
    private String login;
    private String name;
    @Past(message = "birthday should be in past")
    private LocalDate birthday;
}
