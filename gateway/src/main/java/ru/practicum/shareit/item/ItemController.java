package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateCommentRequest;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient client;

    @GetMapping("/items/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("Запрос предмета " + itemId + " пользователем " + userId);
        return client.getItem(userId, itemId);
    }

    @GetMapping("/items")
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                           @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Запрос предметов пользователем " + userId);
        return client.getItems(userId, from, size);
    }

    @PostMapping("/items")
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long userId, @Validated @RequestBody CreateItemRequest item) {
        log.info("Создание предмета пользователем " + userId + ": " + item);
        return client.createItem(userId, item);
    }

    @PostMapping("/items/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId, @Validated @RequestBody CreateCommentRequest comment) {
        log.info("Добавление комментария пользователем " + userId + " для предмета " + itemId + ": " + comment);
        return client.createComment(userId, itemId, comment);
    }

    @PatchMapping("/items/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId, @Validated @RequestBody UpdateItemRequest request) {
        log.info("Обновление предмета пользователем " + userId + " для предмета " + itemId + ": " + request);
        return client.updateItem(userId, itemId, request);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Object> deleteItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("Удаление предмета " + itemId + " пользователем " + userId);
        return client.deleteItem(userId, itemId);
    }

    @GetMapping("/items/search")
    public ResponseEntity<Object> search(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam String text,
                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                         @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Поиск предмета по тексту: " + text);
        return client.search(userId, text, from, size);
    }
}
