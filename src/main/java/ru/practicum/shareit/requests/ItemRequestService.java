package ru.practicum.shareit.requests;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    int add(int userId, ItemRequestDto request);

    ItemRequestDto get(int id);

    ItemRequestDto get(int userId, int id);

    List<ItemRequestDto> getAllForUser(int userId, Pageable pageable);

    List<ItemRequestDto> getAll(int userId, Pageable pageable);
}
