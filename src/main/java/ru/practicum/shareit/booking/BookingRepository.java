package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByBookerIdOrderByStartDateDesc(int bookerId);
    List<Booking> findByBookerIdAndStatusOrderByStartDateDesc(int bookerId, BookingStatus status);
    List<Booking> findByBookerIdAndStartDateAfterOrderByStartDateDesc(int bookerId, LocalDateTime date);
    List<Booking> findByBookerIdAndEndDateBeforeOrderByStartDateDesc(int bookerId, LocalDateTime date);
    List<Booking> findByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(int bookerId, LocalDateTime date, LocalDateTime date2);

    Booking findFirstByItemIdAndEndDateBeforeOrderByStartDateDesc(int itemId, LocalDateTime date);
    Booking findFirstByItemIdAndEndDateAfterOrderByStartDateAsc(int itemId, LocalDateTime date);
}
