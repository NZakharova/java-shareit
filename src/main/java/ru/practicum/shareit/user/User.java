package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class User {
    private int id;

    @NotBlank
    private String name;

    @NotBlank
    private String email;
}
