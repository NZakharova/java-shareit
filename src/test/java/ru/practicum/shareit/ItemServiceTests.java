package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.utils.InvalidObjectException;
import ru.practicum.shareit.utils.ValidationException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemServiceTests {
    @Autowired
    private ItemService itemService;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setup() {
        addUsers();
        addItems();
    }

    private void addUsers() {
        for (int i = 0; i < 3; i++) {
            Mockito.when(userService.get(i))
                    .thenReturn(UserDto.builder().id(i).build());
        }
    }

    private void addItems() {
        var items = List.of(
                new Item(1, 1, "pen", "black", true, null),
                new Item(2, 1, "ruler", "ruler, 32cm", true, null),
                new Item(3, 1, "pencil", "red", true, null),
                new Item(4, 1, "pan", "metal", true, null),
                new Item(5, 1, "table", "wooden", true, null),
                new Item(6, 1, "sticker", "paper", true, null)
        );

        for (var item : items) {
            Mockito.when(itemRepository.findById(item.getId()))
                    .thenReturn(Optional.of(item));
        }

        Mockito.when(itemRepository.findByNameContainingIgnoreCaseAndAvailable(Mockito.anyString(), Mockito.anyBoolean(), Mockito.any()))
                .thenAnswer(invocation -> {
                    var query = (String) invocation.getArgument(0);
                    return new PageImpl<>(items.stream().filter(i -> i.getName().contains(query)).collect(Collectors.toList()));
                });

        Mockito.when(itemRepository.findByDescriptionContainingIgnoreCaseAndAvailable(Mockito.anyString(), Mockito.anyBoolean(), Mockito.any()))
                .thenAnswer(invocation -> {
                    var query = (String) invocation.getArgument(0);
                    return new PageImpl<>(items.stream().filter(i -> i.getDescription().contains(query)).collect(Collectors.toList()));
                });
    }

    @Test
    void testCannotAddItemForAbsentUser() {
        int absentUserId = 731;
        var dto = getValidDto().userId(absentUserId).build();

        Mockito.when(userService.get(absentUserId))
                .thenThrow(new NoSuchElementException());

        assertThrows(NoSuchElementException.class, () -> itemService.add(dto));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void testCannotAddItemWithInvalidName(String value) {
        var dto = getValidDto().name(value).build();

        assertThrows(ValidationException.class, () -> itemService.add(dto));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void testCannotAddItemWithInvalidDescription(String value) {
        var dto = getValidDto().description(value).build();

        assertThrows(ValidationException.class, () -> itemService.add(dto));
    }

    @Test
    void testCanAddItem() {
        var dto = getValidDto().build();

        Mockito.when(itemRepository.save(Mockito.any()))
                .thenAnswer(i -> {
                    var item = (Item) i.getArgument(0);
                    item.setId(10);
                    return item;
                });

        var id = itemService.add(dto);
        assertEquals(10, id);

        Mockito.verify(itemRepository, Mockito.times(1))
                .save(Mockito.any());
    }

    @Test
    void testItemUpdate() {
        var itemId = 3;
        var item = itemService.get(itemId);

        var updatedDto = item.toBuilder()
                .description("new description")
                .name("new name")
                .available(false)
                .build();

        itemService.update(updatedDto);

        Mockito.verify(itemRepository, Mockito.times(1))
                .save(Mockito.any());

        var updatedItem = itemService.get(3);
        assertEquals("new name", updatedItem.getName());
        assertEquals("new description", updatedItem.getDescription());
        assertEquals(false, updatedItem.getAvailable());
    }

    @Test
    void testCannotUpdateItemForOtherUser() {
        var item = itemService.get(3);

        var updatedDto = item.toBuilder().userId(item.getUserId() + 1).build();

        assertThrows(InvalidObjectException.class, () -> itemService.update(updatedDto));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void testSearchWithEmptyQuery(String value) {
        var result = itemService.search(value, Pageable.unpaged());
        assertTrue(result.isEmpty());
    }

    @Test
    void testSearchByName() {
        var result = itemService.search("pen", Pageable.unpaged());
        assertIterableEquals(List.of(1, 3), result.stream().map(ItemDto::getId).collect(Collectors.toList()));
    }

    @Test
    void testSearchByDescription() {
        var result = itemService.search("wooden", Pageable.unpaged());
        assertEquals(1, result.size());

        var item = result.get(0);
        assertEquals(5, item.getId());
    }

    private static ItemDto.ItemDtoBuilder getValidDto() {
        return ItemDto
                .builder()
                .userId(1)
                .name("name")
                .description("description")
                .available(true);
    }
}
