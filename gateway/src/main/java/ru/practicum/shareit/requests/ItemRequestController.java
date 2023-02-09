package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.CreateItemRequestRequestDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ItemRequestController {
    private final RequestClient client;

    @PostMapping("/requests")
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") long userId, @Validated @RequestBody CreateItemRequestRequestDto request) {
        log.info("Создание запроса пользователем " + userId + ": " + request);
        return client.createRequest(userId, request);
    }

    @GetMapping("/requests/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Получение всех запросов пользователем " + userId);
        return client.getRequests(userId, from, size);
    }

    @GetMapping("/requests/{id}")
    public ResponseEntity<Object> getRequest(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable int id) {
        log.info("Получение запроса " + id + " пользователем " + userId);
        return client.getRequest(userId, id);
    }

    @GetMapping("/requests")
    public ResponseEntity<Object> getRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Получение собственных запросов пользователем " + userId);
        return client.getRequestsForUser(userId, from, size);
    }
}
