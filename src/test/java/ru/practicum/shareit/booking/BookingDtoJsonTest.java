package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
class BookingDtoJsonTest {
    @Autowired
    JacksonTester<BookingDto> tester;

    @Test
    void testBookingSerialization() throws IOException {
        var time = "2023-01-21T14:21:25.4480877";
        var timePlusHour = "2023-01-21T14:22:25.4480877";

        var now = LocalDateTime.parse(time);
        var plusHour = LocalDateTime.parse(timePlusHour);

        var itemDto = ItemDto.builder().id(3).build();
        var userDto = UserDto.builder().id(4).build();

        var booking = new BookingDto(1, 2, now, plusHour, BookingStatus.WAITING, itemDto, userDto);

        var json = tester.write(booking);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathNumberValue("$.userId").isEqualTo(2);
        assertThat(json).extractingJsonPathStringValue("$.start").isEqualTo(time);
        assertThat(json).extractingJsonPathStringValue("$.end").isEqualTo(timePlusHour);
        assertThat(json).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
        assertThat(json).extractingJsonPathNumberValue("$.item.id").isEqualTo(3);
        assertThat(json).extractingJsonPathNumberValue("$.booker.id").isEqualTo(4);
    }

    @Test
    void testBookingDeserialization() throws IOException {
        var json = "{" +
                "\"id\":1," +
                "\"userId\":2," +
                "\"start\":\"2023-01-21T14:21:25.4480877\"," +
                "\"end\":\"2023-01-21T14:22:25.4480877\"," +
                "\"status\":\"WAITING\"," +
                "\"item\": {" +
                    "\"userId\":null," +
                    "\"id\":3," +
                    "\"name\":null," +
                    "\"description\":null," +
                    "\"available\":null," +
                    "\"comments\":null," +
                    "\"lastBooking\":null," +
                    "\"nextBooking\":null," +
                    "\"requestId\":null" +
                "}," +
                "\"booker\":{" +
                    "\"id\":4," +
                    "\"name\":null," +
                    "\"email\":null" +
                "}" +
            "}";

        var time = LocalDateTime.parse("2023-01-21T14:21:25.4480877");
        var timePlusHour = LocalDateTime.parse("2023-01-21T14:22:25.4480877");

        var booking = tester.parseObject(json);

        assertEquals(1, booking.getId());
        assertEquals(2, booking.getUserId());
        assertEquals(time, booking.getStart());
        assertEquals(timePlusHour, booking.getEnd());
        assertEquals(BookingStatus.WAITING, booking.getStatus());
        assertEquals(3, booking.getItem().getId());
        assertEquals(4, booking.getBooker().getId());
    }
}
