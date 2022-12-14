package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByBookerIdOrderByStartDateDesc(int bookerId);

    List<Booking> findByBookerIdAndStatusOrderByStartDateDesc(int bookerId, BookingStatus status);

    List<Booking> findByBookerIdAndStartDateAfterOrderByStartDateDesc(int bookerId, LocalDateTime date);

    List<Booking> findByBookerIdAndEndDateBeforeOrderByStartDateDesc(int bookerId, LocalDateTime date);

    List<Booking> findByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(int bookerId, LocalDateTime date, LocalDateTime date2);

    @Query("SELECT b FROM Booking b " +
            "INNER JOIN Item i ON i.id = b.itemId AND i.userId = ?1 " +
            "WHERE b.status = ?2 " +
            "ORDER BY b.startDate DESC")
    List<Booking> findByOwnerIdAndStatusOrderByStartDateDesc(int ownerId, BookingStatus status);

    @Query("SELECT b FROM Booking b " +
            "INNER JOIN Item i on i.id = b.itemId AND i.userId = ?1 " +
            "WHERE b.endDate < ?2 " +
            "ORDER BY b.startDate DESC")
    List<Booking> findPastByOwnerIdOrderByStartDateDesc(int ownerId, LocalDateTime date);

    @Query("SELECT b FROM Booking b " +
            "INNER JOIN Item i on i.id = b.itemId AND i.userId = ?1 " +
            "WHERE b.startDate > ?2 " +
            "ORDER BY b.startDate DESC")
    List<Booking> findFutureByOwnerIdOrderByStartDateDesc(int ownerId, LocalDateTime date);


    @Query("SELECT b FROM Booking b " +
            "INNER JOIN Item i on i.id = b.itemId AND i.userId = ?1 " +
            "WHERE b.startDate < ?2 AND b.endDate > ?2" +
            "ORDER BY b.startDate DESC")
    List<Booking> findCurrentByOwnerIdOrderByStartDateDesc(int ownerId, LocalDateTime date);

    @Query("SELECT b FROM Booking b " +
            "INNER JOIN Item i on i.id = b.itemId AND i.userId = ?1 " +
            "ORDER BY b.startDate DESC")
    List<Booking> findByOwnerIdOrderByStartDateDesc(int ownerId);

    Booking findFirstByItemIdAndEndDateBeforeOrderByStartDateDesc(int itemId, LocalDateTime date);

    Booking findFirstByItemIdAndEndDateAfterOrderByStartDateAsc(int itemId, LocalDateTime date);

    List<Booking> findByBookerIdAndItemId(int bookerId, int itemId);
}
