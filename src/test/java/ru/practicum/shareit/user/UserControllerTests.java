package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.ControllerTestHelpers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTests {
    @Mock
    UserService userService;

    @InjectMocks
    UserController userController;

    MockMvc mvc;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testCreateUser() throws Exception {
        var user = UserDto.builder().email("a@ya.ru").name("a").build();

        when(userService.add(Mockito.any())).thenReturn(1);
        when(userService.get(1)).thenReturn(user.toBuilder().id(1).build());

        runTest(mvc, postJson("/users", user), status().isOk());
    }

    @Test
    void testUpdateUser() throws Exception {
        var userNoId = UserDto.builder().email("a@ya.ru").name("a").build();
        var userWithId = userNoId.toBuilder().id(1).build();

        when(userService.get(1)).thenReturn(userWithId);

        runTest(mvc, patchJson("/users/1", userNoId), status().isOk());

        verify(userService).update(userWithId);
    }


    @Test
    void testGetUser() throws Exception {
        var user = UserDto.builder().id(1).email("a@ya.ru").name("a").build();

        when(userService.get(1)).thenReturn(user);

        runTest(mvc, getJson("/users/1", user), status().isOk());
    }

    @Test
    void testGetUsers() throws Exception {
        var user1 = UserDto.builder().id(1).email("a@ya.ru").name("a").build();
        var user2 = UserDto.builder().id(2).email("b@ya.ru").name("b").build();

        var users = List.of(user1, user2);
        when(userService.getAll()).thenReturn(users);

        runTest(mvc, getJson("/users", users), status().isOk());
    }

    @Test
    void testDeleteUser() throws Exception {
        runTest(mvc, deleteJson("/users/1"));
        verify(userService).delete(1);

        runTest(mvc, deleteJson("/users/2"));
        verify(userService).delete(2);
    }
}

