package ru.practicum.shareit.requests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.ControllerTestHelpers;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.ControllerTestHelpers.runTest;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTests {
    private static final int SHARER_ID = 5;

    @Mock
    ItemRequestService service;

    @InjectMocks
    ItemRequestController controller;

    MockMvc mvc;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testCreate() throws Exception {
        var request = ItemRequestDto.builder().description("desc").build();

        runTest(mvc, postJson("/requests", request), status().isOk());

        Mockito.verify(service)
                .add(Mockito.eq(SHARER_ID), Mockito.any());
    }

    @Test
    void testGetAll() throws Exception {
        runTest(mvc, getJson("/requests/all", null), status().isOk());

        Mockito.verify(service)
                .getAll(Mockito.eq(SHARER_ID), Mockito.any());
    }

    @Test
    void testGetAllForUser() throws Exception {
        runTest(mvc, getJson("/requests", null), status().isOk());

        Mockito.verify(service)
                .getAllForUser(Mockito.eq(SHARER_ID), Mockito.any());
    }

    @Test
    void testGet() throws Exception {
        runTest(mvc, getJson("/requests/1", null), status().isOk());

        Mockito.verify(service).get(SHARER_ID, 1);
    }

    private MockHttpServletRequestBuilder getJson(String path, Object value) throws Exception {
        return ControllerTestHelpers.getJson(path, value).header("X-Sharer-User-Id", Integer.toString(SHARER_ID));
    }

    private MockHttpServletRequestBuilder postJson(String path, Object value) throws Exception {
        return ControllerTestHelpers.postJson(path, value).header("X-Sharer-User-Id", Integer.toString(SHARER_ID));
    }
}
