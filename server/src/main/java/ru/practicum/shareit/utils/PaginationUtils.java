package ru.practicum.shareit.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaginationUtils {
    public static Pageable create(Optional<Integer> from, Optional<Integer> size) {
        return create(from.orElse(0), size.orElse(Integer.MAX_VALUE));
    }

    public static Pageable create(int from, int size) {
        if (from < 0) {
            throw new IllegalArgumentException("Parameter 'from' must be greater than or equal to 0");
        }

        if (size <= 0) {
            throw new IllegalArgumentException("Parameter 'size' must be greater than 0");
        }

        return PageRequest.of(from / size, size);
    }
}
