package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.ControllerTestHelpers;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.utils.DateUtils;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.ControllerTestHelpers.*;

@ExtendWith(MockitoExtension.class)
class ItemControllerTests {
    private static final int SHARER_ID = 5;

    @Mock
    ItemService itemService;

    @InjectMocks
    ItemController itemController;

    MockMvc mvc;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(itemController).build();
    }

    @Test
    void testCreateItem() throws Exception {
        var itemNoId = ItemDto.builder()
                .name("item")
                .description("desc")
                .available(true)
                .build();

        var itemWithUserId = itemNoId.toBuilder().userId(SHARER_ID).build();

        when(itemService.add(itemWithUserId)).thenReturn(1);

        var fullItem = itemWithUserId.toBuilder().id(1).build();
        when(itemService.get(1)).thenReturn(fullItem);

        runTest(mvc, postJson("/items", itemNoId), status().isOk());
    }

    @Test
    void testUpdateItem() throws Exception {
        var itemId = 1;
        var item = ItemDto.builder()
                .id(itemId)
                .name("item")
                .description("desc")
                .available(true)
                .build();

        var fullItem = item.toBuilder().userId(SHARER_ID).build();

        when(itemService.get(itemId)).thenReturn(fullItem);

        runTest(mvc, patchJson("/items/" + itemId, item), status().isOk());

        verify(itemService).update(fullItem);
    }

    @Test
    void testCreateItemForRequest() throws Exception {
        var itemNoId = ItemDto.builder()
                .name("item")
                .description("desc")
                .available(true)
                .requestId(3)
                .build();

        var itemWithUserId = itemNoId.toBuilder().userId(SHARER_ID).build();

        when(itemService.add(itemWithUserId)).thenReturn(1);

        var fullItem = itemWithUserId.toBuilder().id(1).build();
        when(itemService.get(1)).thenReturn(fullItem);

        runTest(mvc, postJson("/items", itemNoId), status().isOk());
    }

    @Test
    void testGetItem() throws Exception {
        var lastBooking = new ShortBookingDto(1, 3);
        var nextBooking = new ShortBookingDto(2, 4);

        var item = ItemDto.builder()
                .id(1)
                .userId(2)
                .available(true)
                .name("item")
                .description("desc")
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .requestId(6)
                .build();

        when(itemService.get(item.getId(), SHARER_ID)).thenReturn(item);

        runTest(mvc, getJson("/items/1", item), status().isOk());
    }

    @Test
    void testGetItems() throws Exception {
        var item1 = ItemDto.builder()
                .id(1)
                .userId(2)
                .name("item")
                .description("desc")
                .build();

        var item2 = item1.toBuilder().id(2).build();

        var items = List.of(item1, item2);

        when(itemService.getAll(Mockito.eq(SHARER_ID), Mockito.any())).thenReturn(items);

        runTest(mvc, getJson("/items", items), status().isOk());
    }

    @Test
    void testCreateComment() throws Exception {
        var comment = CommentDto.builder().text("my comment").build();

        var itemId = 1;
        var commentId = 3;
        var authorName = "a";

        var fullComment = comment.toBuilder()
                .id(commentId)
                .authorName(authorName)
                .created(DateUtils.now())
                .build();

        when(itemService.addComment(Mockito.eq(SHARER_ID), Mockito.eq(itemId), Mockito.any()))
                .thenReturn(commentId);

        when(itemService.findComment(commentId))
                .thenReturn(fullComment);

        runTest(mvc, postJson("/items/" + itemId + "/comment", comment), status().isOk());
    }

    @Test
    void testDeleteItem() throws Exception {
        int itemId = 3;

        runTest(mvc, deleteJson("/items/" + itemId), status().isOk());

        verify(itemService).delete(3);
    }

    @Test
    void testSearchItem() throws Exception {
        String query = "some text";

        var item1 = ItemDto.builder().id(7).build();
        var item2 = ItemDto.builder().id(14).build();
        var items = List.of(item1, item2);

        when(itemService.search(Mockito.eq(query), Mockito.any(Pageable.class)))
                .thenReturn(items);

        runTest(mvc, getJson("/items/search", null).param("text", query), status().isOk());
    }

    private MockHttpServletRequestBuilder getJson(String path, Object value) throws Exception {
        return ControllerTestHelpers.getJson(path, value).header("X-Sharer-User-Id", Integer.toString(SHARER_ID));
    }

    private MockHttpServletRequestBuilder postJson(String path, Object value) throws Exception {
        return ControllerTestHelpers.postJson(path, value).header("X-Sharer-User-Id", Integer.toString(SHARER_ID));
    }

    private MockHttpServletRequestBuilder deleteJson(String path) {
        return ControllerTestHelpers.deleteJson(path).header("X-Sharer-User-Id", Integer.toString(SHARER_ID));
    }

    private MockHttpServletRequestBuilder patchJson(String path, Object value) throws Exception {
        return ControllerTestHelpers.patchJson(path, value).header("X-Sharer-User-Id", Integer.toString(SHARER_ID));
    }
}

