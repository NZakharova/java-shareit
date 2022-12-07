package ru.practicum.shareit.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class ItemRequestDto {
    private final Integer id;

    @NotNull
    @NotBlank
    private final String description;

    private final LocalDateTime created;

    private final Integer authorId;

    private final List<ItemDto> items;
}

