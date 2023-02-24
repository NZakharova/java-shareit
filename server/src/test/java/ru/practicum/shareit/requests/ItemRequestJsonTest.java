package ru.practicum.shareit.requests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
class ItemRequestJsonTest {
    @Autowired
    JacksonTester<ItemRequestDto> tester;

    @Test
    void testSerialization() throws Exception {
        var time = "2023-01-21T14:21:25.4480877";

        var dto = new ItemRequestDto(1, "desc", LocalDateTime.parse(time), 2, List.of(ItemDto.builder().id(3).build()));

        var json = tester.write(dto);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.description").isEqualTo("desc");
        assertThat(json).extractingJsonPathStringValue("$.created").isEqualTo(time);
        assertThat(json).extractingJsonPathNumberValue("$.authorId").isEqualTo(2);
        assertThat(json).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(3);
    }

    @Test
    void testDeserialization() throws Exception {
        var time = "2023-01-21T14:21:25.4480877";
        var json = "{" +
                "\"id\":1," +
                "\"description\":\"desc\"," +
                "\"created\":\"2023-01-21T14:21:25.4480877\"," +
                "\"authorId\":2," +
                "\"items\":[" +
                    "{" +
                        "\"userId\":null," +
                        "\"id\":3," +
                        "\"name\":null," +
                        "\"description\":null," +
                        "\"available\":null," +
                        "\"comments\":null," +
                        "\"lastBooking\":null," +
                        "\"nextBooking\":null," +
                        "\"requestId\":null" +
                    "}" +
                "]" +
            "}";

        var dto = tester.parseObject(json);

        assertEquals(1, dto.getId());
        assertEquals("desc", dto.getDescription());
        assertEquals(LocalDateTime.parse(time), dto.getCreated());
        assertEquals(2, dto.getAuthorId());
        assertEquals(3, dto.getItems().get(0).getId());
    }
}
