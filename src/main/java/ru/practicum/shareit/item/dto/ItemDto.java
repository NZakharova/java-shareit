package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ItemDto {
    private final Integer userId;
    private final Integer id;
    private final String name;
    private final String description;
    private final Boolean available;
}

