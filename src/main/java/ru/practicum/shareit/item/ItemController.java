package ru.practicum.shareit.item;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.utils.Create;

import java.util.List;

@RestController
public class ItemController {
    private final ItemService service;

    public ItemController(ItemService service) {
        this.service = service;
    }

    @GetMapping("/items/{id}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int id) {
        return service.find(id, userId);
    }

    @GetMapping("/items")
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") int userId) {
        return service.findAll(userId);
    }

    @PostMapping("/items")
    public ItemDto createItem(
            @RequestHeader("X-Sharer-User-Id")
                    int userId,
            @Validated(Create.class)
            @RequestBody
                    ItemDto item) {

        var id = service.add(item.toBuilder().userId(userId).build());
        return service.find(id);
    }

    @PatchMapping("/items/{id}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int id, @RequestBody ItemDto item) {
        service.update(item.toBuilder().userId(userId).id(id).build());
        return service.find(id);
    }

    @DeleteMapping("/items/{id}")
    public void updateItem(@PathVariable int id) {
        service.delete(id);
    }

    @GetMapping("/items/search")
    public List<ItemDto> search(@RequestParam String text) {
        return service.search(text);
    }
}
