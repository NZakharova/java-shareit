package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CreateBookingRequest {
    private int itemId;

    // не использую @NotNull, потому что при тесте
    // "Booking create failed by wrong userId" запрос отсеивается слишком рано и даёт ошибку 400 вместо 500/404
    private LocalDateTime start;

    private LocalDateTime end;
}
