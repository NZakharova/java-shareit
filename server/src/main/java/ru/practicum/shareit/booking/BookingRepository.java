package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    Page<Booking> findByBookerIdOrderByStartDateDesc(int bookerId, Pageable pageable);

    Page<Booking> findByBookerIdAndStatusOrderByStartDateDesc(int bookerId, BookingStatus status, Pageable pageable);

    Page<Booking> findByBookerIdAndStartDateAfterOrderByStartDateDesc(int bookerId, LocalDateTime date, Pageable pageable);

    Page<Booking> findByBookerIdAndEndDateBeforeOrderByStartDateDesc(int bookerId, LocalDateTime date, Pageable pageable);

    Page<Booking> findByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(int bookerId, LocalDateTime date, LocalDateTime date2, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "INNER JOIN Item i on i.id = b.itemId AND i.userId = ?1 " +
            "WHERE b.endDate < ?2 " +
            "ORDER BY b.startDate DESC")
    Page<Booking> findPastByOwnerIdOrderByStartDateDesc(int ownerId, LocalDateTime date, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "INNER JOIN Item i on i.id = b.itemId AND i.userId = ?1 " +
            "WHERE b.startDate > ?2 " +
            "ORDER BY b.startDate DESC")
    Page<Booking> findFutureByOwnerIdOrderByStartDateDesc(int ownerId, LocalDateTime date, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "INNER JOIN Item i on i.id = b.itemId AND i.userId = ?1 " +
            "WHERE b.startDate < ?2 AND b.endDate > ?2" +
            "ORDER BY b.startDate DESC")
    Page<Booking> findCurrentByOwnerIdOrderByStartDateDesc(int ownerId, LocalDateTime date, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "INNER JOIN Item i on i.id = b.itemId AND i.userId = ?1 " +
            "ORDER BY b.startDate DESC")
    Page<Booking> findByOwnerIdOrderByStartDateDesc(int ownerId, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "INNER JOIN Item i ON i.id = b.itemId AND i.userId = ?1 " +
            "WHERE b.status = ?2 " +
            "ORDER BY b.startDate DESC")
    Page<Booking> findByOwnerIdAndStatusOrderByStartDateDesc(int ownerId, BookingStatus status, Pageable pageable);

    Booking findFirstByItemIdAndEndDateBeforeOrderByStartDateDesc(int itemId, LocalDateTime date);

    Booking findFirstByItemIdAndEndDateAfterOrderByStartDateAsc(int itemId, LocalDateTime date);

    Page<Booking> findByBookerIdAndItemId(int bookerId, int itemId, Pageable pageable);
}
