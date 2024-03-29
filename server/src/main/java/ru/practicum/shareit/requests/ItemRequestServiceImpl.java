package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.utils.DateUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserService userService;
    private final ItemService itemService;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper mapper;

    @Override
    @Transactional
    public int add(int userId, ItemRequestDto request) {
        userService.get(userId);

        var model = mapper.toModel(request.getDescription(), userId, DateUtils.now());
        itemRequestRepository.save(model);
        return model.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto get(int id) {
        return toDto(itemRequestRepository.findById(id).orElseThrow());
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto get(int userId, int id) {
        // запросы доступны всем зарегистрированным пользователям
        userService.get(userId);
        return get(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAllForUser(int userId, Pageable pageable) {
        userService.get(userId);
        return toDto(itemRequestRepository.findByAuthorIdOrderByCreatedDesc(userId, pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAll(int userId, Pageable pageable) {
        var page = itemRequestRepository.findByAuthorIdNotOrderByCreatedDesc(userId, pageable);
        return page.map(this::toDto).toList();
    }

    private List<ItemRequestDto> toDto(Page<ItemRequest> requests) {
        return requests.map(this::toDto).stream().collect(Collectors.toList());
    }

    private ItemRequestDto toDto(ItemRequest request) {
        return mapper.toDto(request, itemService.getForRequest(request.getId()));
    }
}
