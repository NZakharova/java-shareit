package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class Item {
    private int id;
    private final int userId;

    @NotNull
    private String name;

    @NotNull
    private String description;

    private boolean available;
}
