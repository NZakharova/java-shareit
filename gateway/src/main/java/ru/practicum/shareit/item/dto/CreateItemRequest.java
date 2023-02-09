package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class CreateItemRequest {
    @NotNull
    @NotBlank
    private final String name;

    @NotNull
    @NotBlank
    private final String description;

    @NotNull
    private final Boolean available;
}
