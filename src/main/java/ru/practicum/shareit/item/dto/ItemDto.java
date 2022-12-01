package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.utils.Create;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder(toBuilder = true)
public class ItemDto {
    private final Integer userId;
    private final Integer id;

    @NotBlank(groups = Create.class)
    private final String name;

    @NotBlank(groups = Create.class)
    private final String description;

    @NotNull(groups = Create.class)
    private final Boolean available;

    private List<CommentDto> comments;

    private final ShortBookingDto lastBooking;

    private final ShortBookingDto nextBooking;
}
