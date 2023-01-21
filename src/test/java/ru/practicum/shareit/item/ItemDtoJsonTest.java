package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
class ItemDtoJsonTest {
    @Autowired
    JacksonTester<ItemDto> tester;

    @Test
    void testItemSerialization() throws IOException {
        var now = LocalDateTime.now();
        var comments = List.of(new CommentDto("commentText", "author", now, 3));
        var item = new ItemDto(
                1,
                2,
                "itemName",
                "desc",
                true,
                comments,
                new ShortBookingDto(4, 5),
                new ShortBookingDto(6, 7),
                8);

        var content = tester.write(item);

        assertThat(content).extractingJsonPathNumberValue("$.userId").isEqualTo(1);
        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(2);
        assertThat(content).extractingJsonPathStringValue("$.name").isEqualTo("itemName");
        assertThat(content).extractingJsonPathStringValue("$.description").isEqualTo("desc");
        assertThat(content).extractingJsonPathBooleanValue("$.available").isEqualTo(true);

        assertThat(content).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("commentText");
        assertThat(content).extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo("author");
        assertThat(content).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(3);

        assertThat(content).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(4);
        assertThat(content).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(5);
        assertThat(content).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(6);
        assertThat(content).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(7);

        assertThat(content).extractingJsonPathNumberValue("$.requestId").isEqualTo(8);
    }

    @Test
    void testItemDeserialization() throws IOException {
        var time = "2023-01-21T14:21:25.4480877";
        var json = "{" +
                "\"userId\":1," +
                "\"id\":2," +
                "\"name\":\"itemName\"," +
                "\"description\":\"desc\"," +
                "\"available\":true," +
                "\"comments\": [" +
                    "{" +
                        "\"text\":\"commentText\"," +
                        "\"authorName\":\"author\"," +
                        "\"created\":\"" + time + "\"," +
                        "\"id\":3" +
                    "}" +
                "]," +
                "\"lastBooking\": {" +
                    "\"id\":4," +
                    "\"bookerId\":5" +
                "}," +
                "\"nextBooking\":{" +
                    "\"id\":6," +
                    "\"bookerId\":7" +
                "}," +
                "\"requestId\":8" +
            "}";

        var item = tester.parseObject(json);
        assertEquals(1, item.getUserId());
        assertEquals(2, item.getId());
        assertEquals("itemName", item.getName());
        assertEquals("desc", item.getDescription());
        assertEquals(true, item.getAvailable());

        var comments = item.getComments();
        assertEquals(1, comments.size());

        var comment = comments.get(0);
        assertEquals("commentText", comment.getText());
        assertEquals("author", comment.getAuthorName());
        assertEquals(LocalDateTime.parse(time), comment.getCreated());
        assertEquals(3, comment.getId());

        assertEquals(4, item.getLastBooking().getId());
        assertEquals(5, item.getLastBooking().getBookerId());

        assertEquals(6, item.getNextBooking().getId());
        assertEquals(7, item.getNextBooking().getBookerId());

        assertEquals(8, item.getRequestId());
    }
}
