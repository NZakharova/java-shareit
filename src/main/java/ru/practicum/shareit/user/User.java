package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
@Builder(toBuilder = true)
public class User {
    private final int id;

    @NotNull
    private final String name;

    @NotNull
    private final String email;
}
