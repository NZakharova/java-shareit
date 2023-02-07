package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.utils.ValidationException;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTests {
    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void testUpdate() {
        var user = new User(1, "some name", "email@ya.ru");

        Mockito.when(userRepository.findById(1))
                .thenReturn(Optional.of(user));

        userService.update(UserDto.builder().id(1).name("new name").build());
        assertEquals("new name", user.getName());
        assertEquals("email@ya.ru", user.getEmail());

        userService.update(UserDto.builder().id(1).email("newEmail@ya.ru").build());
        assertEquals("new name", user.getName());
        assertEquals("newEmail@ya.ru", user.getEmail());

        userService.update(UserDto.builder().id(1).name("new name2").email("newEmail2@ya.ru").build());
        assertEquals("new name2", user.getName());
        assertEquals("newEmail2@ya.ru", user.getEmail());

        Mockito.verify(userRepository, Mockito.times(3)).save(Mockito.any());
    }

    @Test
    void testUpdateFailsForInvalidData() {
        var user = new User(1, "some name", "email@ya.ru");

        Mockito.when(userRepository.findById(1))
                .thenReturn(Optional.of(user));

        var invalidName = UserDto.builder().id(1).name("").build();
        assertThrows(ValidationException.class, () -> userService.update(invalidName));

        var invalidEmail = UserDto.builder().id(1).email("").build();
        assertThrows(ValidationException.class, () -> userService.update(invalidEmail));
    }

    @Test
    void testSave() {
        var dto = UserDto.builder().name("name").email("email@ya.ru").build();

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(new User(13, "name", "email@ya.ru"));

        var id = userService.add(dto);

        assertEquals(13, id);

        Mockito.verify(userRepository, Mockito.times(1))
                .save(Mockito.any(User.class));
    }

    @Test
    void testFindsUser() {
        var user = new User(1, "name", "email@ya.ru");
        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(user));

        var dto = userService.get(1);

        assertNotNull(dto);
        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getName(), dto.getName());
        assertEquals(user.getEmail(), dto.getEmail());
    }

    @Test
    void testThrowsForAbsentUser() {
        assertThrows(NoSuchElementException.class, () -> userService.get(1));
    }

    @Test
    void testDeletesUser() {
        userService.delete(13);
        Mockito.verify(userRepository, Mockito.times(1))
                .deleteById(13);
    }
}
