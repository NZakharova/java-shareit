package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemMapper {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public ItemDto toDto(Item item) {
        var comments = commentRepository
                .findByItemId(item.getId())
                .stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());

        var builder = ItemDto.builder()
                .id(item.getId())
                .userId(item.getUserId())
                .name(item.getName())
                .description(item.getDescription())
                .comments(comments)
                .requestId(item.getRequestId())
                .available(item.isAvailable());

        return builder.build();
    }

    public Item toModel(ItemDto item) {
        return new Item(
                Optional.ofNullable(item.getId()).orElse(0),
                item.getUserId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId()
        );
    }
}

