package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
class UserDtoJsonTest {
    @Autowired
    JacksonTester<UserDto> tester;

    @Test
    void testUserSerialization() throws IOException {
        var user = new UserDto(1, "a", "a@ya.ru");

        var content = tester.write(user);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.name").isEqualTo("a");
        assertThat(content).extractingJsonPathStringValue("$.email").isEqualTo("a@ya.ru");
    }

    @Test
    void testUserDeserialization() throws IOException {
        var json = "{ \"id\": 1, \"name\": \"a\", \"email\": \"a@ya.ru\" }";

        var user = tester.parseObject(json);

        assertEquals(1, user.getId());
        assertEquals("a", user.getName());
        assertEquals("a@ya.ru", user.getEmail());
    }
}
