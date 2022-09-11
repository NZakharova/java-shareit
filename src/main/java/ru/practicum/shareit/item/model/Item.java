package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder(toBuilder = true)
public class Item {
    private final int userId;
    private final int id;
    @NonNull
    private final String description;
    @NonNull
    private final String name;
    private final boolean available;
}
