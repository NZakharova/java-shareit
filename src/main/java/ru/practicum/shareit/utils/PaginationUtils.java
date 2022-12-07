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
            throw new IllegalArgumentException("Параметр 'from' должен быть больше либо равен нулю");
        }

        if (size <= 0) {
            throw new IllegalArgumentException("Параметр 'size' должен быть больше нуля");
        }

        return PageRequest.of(from / size, size);
    }
}
