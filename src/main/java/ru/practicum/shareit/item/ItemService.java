package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.utils.InvalidObjectException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {
    private final ItemValidator itemValidator;
    private final ItemDtoValidator itemDtoValidator;
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public ItemService(ItemValidator itemValidator, ItemDtoValidator itemDtoValidator, ItemStorage storage, UserStorage userStorage) {
        this.itemValidator = itemValidator;
        this.itemDtoValidator = itemDtoValidator;
        this.itemStorage = storage;
        this.userStorage = userStorage;
    }

    ItemDto find(int id) {
        return ItemMapper.toDto(itemStorage.find(id));
    }

    List<ItemDto> findAll() {
        return itemStorage.findAll().stream().map(ItemMapper::toDto).collect(Collectors.toList());
    }

    List<ItemDto> findAll(int userId) {
        return itemStorage.findAll(userId).stream().map(ItemMapper::toDto).collect(Collectors.toList());
    }

    int add(ItemDto item) {
        userStorage.find(item.getUserId()); // проверим что пользователь существует

        itemDtoValidator.validateForAdd(item);
        Item i = ItemMapper.toModel(item);
        itemValidator.validate(i);
        return itemStorage.add(i);
    }

    void update(ItemDto item) {
        itemDtoValidator.validateForUpdate(item);

        if (item.getUserId() != null) {
            var existing = itemStorage.find(item.getId());
            if (existing.getUserId() != item.getUserId()) {
                throw new InvalidObjectException("Данный предмет принадлежит другому пользователю");
            }
        }

        if (item.getName() != null) {
            itemStorage.updateName(item.getId(), item.getName());
        }

        if (item.getDescription() != null) {
            itemStorage.updateDescription(item.getId(), item.getDescription());
        }

        if (item.getAvailable() != null) {
            itemStorage.updateAvailable(item.getId(), item.getAvailable());
        }
    }

    void delete(int id) {
        itemStorage.delete(id);
    }

    public List<ItemDto> search(String text) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        } else {
            return itemStorage.search(text).stream().map(ItemMapper::toDto).collect(Collectors.toList());
        }
    }
}
