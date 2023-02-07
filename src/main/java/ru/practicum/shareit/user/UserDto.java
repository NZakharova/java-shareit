package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.utils.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Integer id;

    @NotBlank(groups = Create.class)
    private String name;

    @Email
    @NotBlank(groups = Create.class)
    private String email;
}
