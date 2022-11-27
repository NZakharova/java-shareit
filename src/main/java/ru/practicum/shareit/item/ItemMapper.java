package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import java.util.Optional;

@Service
public class ItemMapper {
    public ItemDto toDto(Item item) {
        var builder = ItemDto.builder()
                .id(item.getId())
                .userId(item.getUserId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable());

        return builder.build();
    }

    public Item toModel(ItemDto item) {
        return new Item(
                Optional.ofNullable(item.getId()).orElse(0),
                item.getUserId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }
}
