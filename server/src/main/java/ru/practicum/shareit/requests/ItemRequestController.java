package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.utils.PaginationUtils;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping("/requests")
    public ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") int userId, @RequestBody ItemRequestDto request) {
        log.info("Создание запроса пользователем " + userId + ": " + request);
        int id = itemRequestService.add(userId, request);
        log.info("Создан запрос " + id);
        return itemRequestService.get(id);
    }

    @GetMapping("/requests/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") int userId, @RequestParam Optional<Integer> from, @RequestParam Optional<Integer> size) {
        log.info("Получение всех запросов пользователем " + userId);
        return itemRequestService.getAll(userId, PaginationUtils.create(from, size));
    }

    @GetMapping("/requests/{id}")
    public ItemRequestDto getRequest(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int id) {
        log.info("Получение запроса " + id + " пользователем " + userId);
        return itemRequestService.get(userId, id);
    }

    @GetMapping("/requests")
    public List<ItemRequestDto> getRequests(@RequestHeader("X-Sharer-User-Id") int userId, @RequestParam Optional<Integer> from, @RequestParam Optional<Integer> size) {
        log.info("Получение собственных запросов пользователем " + userId);
        return itemRequestService.getAllForUser(userId, PaginationUtils.create(from, size));
    }
}
