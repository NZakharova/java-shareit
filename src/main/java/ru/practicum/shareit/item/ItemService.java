package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.utils.InvalidObjectException;
import ru.practicum.shareit.utils.ObjectNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ItemService {
    private final ItemValidator itemValidator;
    private final ItemDtoValidator itemDtoValidator;
    private final ItemRepository itemStorage;
    private final UserRepository userRepository;

    public ItemService(ItemValidator itemValidator, ItemDtoValidator itemDtoValidator, ItemRepository storage, UserRepository userRepository) {
        this.itemValidator = itemValidator;
        this.itemDtoValidator = itemDtoValidator;
        this.itemStorage = storage;
        this.userRepository = userRepository;
    }

    ItemDto find(int id) {
        return ItemMapper.toDto(itemStorage.findById(id).orElseThrow(() -> { throw new ObjectNotFoundException(id); }));
    }

    List<ItemDto> findAll() {
        return itemStorage.findAll().stream().map(ItemMapper::toDto).collect(Collectors.toList());
    }

    List<ItemDto> findAll(int userId) {
        return itemStorage.findByUserId(userId).stream().map(ItemMapper::toDto).collect(Collectors.toList());
    }

    int add(ItemDto item) {
        if (!userRepository.existsById(item.getUserId())) {
            throw new ObjectNotFoundException(item.getUserId());
        }

        Item i = ItemMapper.toModel(item);
        itemValidator.validate(i);
        return itemStorage.save(i).getId();
    }

    void update(ItemDto item) {
        itemDtoValidator.validateForUpdate(item);

        var existing = itemStorage
                .findById(item.getId())
                .orElseThrow(() -> new ObjectNotFoundException(item.getId()));

        if (item.getUserId() != null && existing.getUserId() != item.getUserId()) {
            throw new InvalidObjectException("Данный предмет принадлежит другому пользователю");
        }

        if (item.getName() != null) {
            existing.setName(item.getName());
        }

        if (item.getDescription() != null) {
            existing.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            existing.setAvailable(item.getAvailable());
        }

        itemStorage.save(existing);
    }

    void delete(int id) {
        itemStorage.deleteById(id);
    }

    public List<ItemDto> search(String text) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        } else {
            var list1 = itemStorage.findByNameContainingIgnoreCaseAndAvailable(text, true).stream();
            var list2 = itemStorage.findByDescriptionContainingIgnoreCaseAndAvailable(text, true).stream();
            return Stream.concat(list1, list2).distinct().map(ItemMapper::toDto).collect(Collectors.toList());
        }
    }
}
