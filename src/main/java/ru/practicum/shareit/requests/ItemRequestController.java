package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping("/requests")
    public ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") int userId, @Validated @RequestBody ItemRequestDto request) {
        int id = itemRequestService.add(userId, request);
        return itemRequestService.get(id);
    }

    @GetMapping("/requests/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") int userId, @RequestParam Optional<Integer> from, @RequestParam Optional<Integer> size) {
        return itemRequestService.getAll(userId, from.orElse(0), size.orElse(Integer.MAX_VALUE));
    }

    @GetMapping("/requests/{id}")
    public ItemRequestDto getRequest(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int id) {
        return itemRequestService.get(userId, id);
    }

    @GetMapping("/requests")
    public List<ItemRequestDto> getRequests(@RequestHeader("X-Sharer-User-Id") int userId) {
        return itemRequestService.getAllForUser(userId);
    }
}
