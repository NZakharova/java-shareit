package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.utils.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class UserDto {
    @NotBlank(groups = Create.class)
    private final String name;

    @Email
    @NotBlank(groups = Create.class)
    private final String email;
}
