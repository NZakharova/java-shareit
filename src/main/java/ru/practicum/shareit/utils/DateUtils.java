package ru.practicum.shareit.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtils {
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
}
