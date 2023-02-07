package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestMapper {
    public ItemRequestDto toDto(ItemRequest model, List<ItemDto> items) {
        return new ItemRequestDto(model.getId(), model.getDescription(), model.getCreated(), model.getAuthorId(), items);
    }

    public ItemRequest toModel(String description, int authorId, LocalDateTime created) {
        return new ItemRequest(null, description, authorId, created);
    }
}
