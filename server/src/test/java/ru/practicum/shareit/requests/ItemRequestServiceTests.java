package ru.practicum.shareit.requests;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.utils.DateUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ItemRequestServiceTests {
    @Autowired
    ItemRequestService service;

    @MockBean
    ItemRequestRepository repository;

    @MockBean
    UserService userService;

    @Test
    void testAdd() {
        Mockito.when(repository.save(Mockito.any()))
                .thenAnswer(i -> {
                    var arg = (ItemRequest) i.getArgument(0);
                    arg.setId(1);
                    return arg;
                });

        var dto = new ItemRequestDto(1, "desc", DateUtils.now(), 2, null);
        service.add(0, dto);

        Mockito.verify(repository).save(Mockito.any());
    }

    @Test
    void testGet() {
        Mockito.when(repository.findById(1)).thenReturn(Optional.of(ItemRequest.builder().id(1).build()));
        var request = service.get(0, 1);

        assertEquals(1, request.getId());
    }

    @Test
    void testGetAll() {
        Mockito.when(repository.findByAuthorIdNotOrderByCreatedDesc(Mockito.anyInt(), Mockito.any()))
                .thenReturn(new PageImpl<>(List.of(ItemRequest.builder().id(7).build())));

        var requests = service.getAll(0, Pageable.unpaged());
        assertEquals(1, requests.size());
        assertEquals(7, requests.get(0).getId());
    }

    @Test
    void testGetAllForUser() {
        Mockito.when(repository.findByAuthorIdOrderByCreatedDesc(Mockito.anyInt(), Mockito.any()))
                .thenReturn(new PageImpl<>(List.of(ItemRequest.builder().id(7).build())));

        var requests = service.getAllForUser(0, Pageable.unpaged());
        assertEquals(1, requests.size());
        assertEquals(7, requests.get(0).getId());
    }
}
