package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto get(int id);

    ItemDto get(int id, int userId);

    List<ItemDto> getAll(int userId, Pageable pageable);

    List<ItemDto> getForRequest(int requestId);

    int add(ItemDto item);

    void update(ItemDto item);

    void delete(int id);

    List<ItemDto> search(String text, Pageable pageable);

    int addComment(int userId, int itemId, CommentDto comment);

    CommentDto findComment(int id);
}
