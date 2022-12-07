package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestService {
    private final UserService userService;
    private final ItemService itemService;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper mapper;

    public int add(int userId, ItemRequestDto request) {
        userService.get(userId);

        var model = mapper.toModel(request.getDescription(), userId, LocalDateTime.now());
        itemRequestRepository.save(model);
        return model.getId();
    }

    public ItemRequestDto get(int id) {
        return toDto(itemRequestRepository.findById(id).orElseThrow());
    }

    public ItemRequestDto get(int userId, int id) {
        // запросы доступны всем зарегистрированным пользователям
        userService.get(userId);
        return get(id);
    }

    public List<ItemRequestDto> getAllForUser(int userId) {
        userService.get(userId);
        return toDto(itemRequestRepository.findByAuthorIdOrderByCreatedDesc(userId));
    }

    public List<ItemRequestDto> getAll(int userId, int from, int size) {
        if (from < 0) {
            throw new IllegalArgumentException("Параметр 'from' должен быть больше либо равен нулю");
        }

        if (size <= 0) {
            throw new IllegalArgumentException("Параметр 'size' должен быть больше нуля");
        }

        var page = itemRequestRepository.findAll(PageRequest.of(from / size, size));
        return page.map(this::toDto).toList();
    }

    private List<ItemRequestDto> toDto(List<ItemRequest> requests) {
        return requests.stream().map(this::toDto).collect(Collectors.toList());
    }

    private ItemRequestDto toDto(ItemRequest request) {
        return mapper.toDto(request, itemService.getForRequest(request.getId()));
    }
}
