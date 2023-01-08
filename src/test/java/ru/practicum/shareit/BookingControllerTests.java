package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.ControllerTestHelpers.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTests {
    private static final int SHARER_ID = 5;

    @Mock
    BookingService bookingService;

    @InjectMocks
    BookingController bookingController;

    MockMvc mvc;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(bookingController).build();
    }

    @Test
    void testCreateBooking() throws Exception {
        var startTime = LocalDateTime.now();
        var endTime = startTime.plusHours(1);
        var itemId = 1;
        var request = new CreateBookingRequest(itemId, startTime, endTime);

        var bookingId = 1;

        var booking = BookingDto.builder()
                .id(bookingId)
                .item(ItemDto.builder().id(itemId).build())
                .userId(SHARER_ID)
                .status(BookingStatus.WAITING)
                .start(startTime)
                .end(endTime)
                .build();

        when(bookingService.create(SHARER_ID, request))
                .thenReturn(bookingId);

        when(bookingService.get(SHARER_ID, bookingId))
                .thenReturn(booking);

        runTest(mvc, postJson("/bookings", request),
                status().isOk(),
                j("$.id", bookingId),
                j("$.item.id", itemId),
                j("$.userId", SHARER_ID),
                j("$.status", BookingStatus.WAITING.toString())
        );
    }

    @Test
    void testGetOwnedBookings() throws Exception {
        when(bookingService.getAllOwned(Mockito.eq(SHARER_ID), Mockito.any(), Mockito.any()))
                .thenReturn(Collections.emptyList());

        runTest(mvc, getJson("/bookings/owner"),
                status().isOk()
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"ALL", "CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED"})
    void testSearch(String searchKind) throws Exception {
        var kind = BookingSearchKind.valueOf(searchKind);

        when(bookingService.getAll(Mockito.eq(SHARER_ID), Mockito.eq(kind), Mockito.any()))
                .thenReturn(List.of());

        runTest(mvc, getJson("/bookings").param("state", searchKind),
                status().isOk());
    }

    @Test
    void testGetBooking() throws Exception {
        var bookingId = 3;

        when(bookingController.find(SHARER_ID, bookingId))
                .thenReturn(BookingDto.builder().id(bookingId).build());

        runTest(mvc, getJson("/bookings/" + bookingId),
                j("$.id", bookingId));
    }

    @Test
    void testApprove() throws Exception {
        var bookingId = 3;

        runTest(mvc, patchJson("/bookings/" + bookingId).param("approved", "true"),
                status().isOk());

        verify(bookingService).setApproved(SHARER_ID, bookingId, true);
    }

    private MockHttpServletRequestBuilder getJson(String path) throws Exception {
        return ControllerTestHelpers.getJson(path, null).header("X-Sharer-User-Id", Integer.toString(SHARER_ID));
    }

    private MockHttpServletRequestBuilder postJson(String path, Object value) throws Exception {
        return ControllerTestHelpers.postJson(path, value).header("X-Sharer-User-Id", Integer.toString(SHARER_ID));
    }

    private MockHttpServletRequestBuilder patchJson(String path) throws Exception {
        return ControllerTestHelpers.patchJson(path, null).header("X-Sharer-User-Id", Integer.toString(SHARER_ID));
    }
}

