package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.utils.Create;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @GetMapping("/items/{id}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int id) {
        log.info("Запрос предмета " + id + " пользователем " + userId);
        return service.get(id, userId);
    }

    @GetMapping("/items")
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Запрос предметов пользователем " + userId);
        return service.getAll(userId);
    }

    @PostMapping("/items")
    public ItemDto createItem(
            @RequestHeader("X-Sharer-User-Id")
                    int userId,
            @Validated(Create.class)
            @RequestBody
                    ItemDto item) {
        log.info("Создание предмета пользователем " + userId + ": " + item);
        var id = service.add(item.toBuilder().userId(userId).build());
        log.info("Создан предмет " + id);
        return service.get(id);
    }

    @PostMapping("/items/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId, @RequestBody CommentDto comment) {
        log.info("Добавление комментария пользователем " + userId + " для предмета " + itemId + ": " + comment);
        var id = service.addComment(userId, itemId, comment);
        log.info("Добавлен комментарий " + id);
        return service.findComment(id);
    }

    @PatchMapping("/items/{id}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int id, @RequestBody ItemDto item) {
        log.info("Обновление предмета пользователем " + userId + " для предмета " + id + ": " + item);
        service.update(item.toBuilder().userId(userId).id(id).build());
        return service.get(id);
    }

    @DeleteMapping("/items/{id}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int id) {
        log.info("Удаление предмета " + id + " пользователем " + userId);
        service.delete(id);
    }

    @GetMapping("/items/search")
    public List<ItemDto> search(@RequestParam String text) {
        log.info("Поиск предмета по тексту: " + text);
        return service.search(text);
    }
}
