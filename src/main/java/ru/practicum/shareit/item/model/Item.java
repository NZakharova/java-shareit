package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder(toBuilder = true)
public class Item {
    private final int userId;
    private final int id;
    @NotNull
    private final String description;
    @NotNull
    private final String name;
    private final boolean available;
}
