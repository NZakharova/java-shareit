package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.utils.DateUtils;

import java.util.HashMap;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTests {
    @Autowired
    TestEntityManager em;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    @Test
    void contextLoads() {
        assertNotNull(em);
        assertNotNull(bookingRepository);
    }

    @Test
    void testFindByOwnerId() {
        var owner = new User(null, "name", "a@ya.ru");
        userRepository.save(owner);

        var booker = new User(null, "booker", "b@ya.ru");
        userRepository.save(booker);

        var item = new Item(null, owner.getId(), "item", "desc", true, null);
        itemRepository.save(item);

        var booking = new Booking(null, item.getId(), BookingStatus.APPROVED, booker.getId(), DateUtils.now(), DateUtils.now());
        bookingRepository.save(booking);

        var bookings = bookingRepository.findByOwnerIdOrderByStartDateDesc(owner.getId(), Pageable.unpaged());
        var bookingList = bookings.stream().collect(Collectors.toList());

        assertEquals(1, bookingList.size());
        var dbBooking = bookingList.get(0).toBuilder().build();

        assertEquals(booking, dbBooking);
    }

    @ParameterizedTest
    @ValueSource(strings = {"WAITING", "APPROVED", "REJECTED"})
    void testFindByOwnerIdAndStatus(String testStatus) {
        var owner = new User(null, "name", "a@ya.ru");
        userRepository.save(owner);

        var booker = new User(null, "booker", "b@ya.ru");
        userRepository.save(booker);

        var item = new Item(null, owner.getId(), "item", "desc", true, null);
        itemRepository.save(item);

        var requests = new HashMap<BookingStatus, Booking>();

        for (var status : BookingStatus.values()) {
            var booking = new Booking(null, item.getId(), status, booker.getId(), DateUtils.now(), DateUtils.now());
            bookingRepository.save(booking);
            requests.put(status, booking);
        }

        var status = BookingStatus.valueOf(testStatus);

        var bookings = bookingRepository.findByOwnerIdAndStatusOrderByStartDateDesc(owner.getId(), status, Pageable.unpaged());
        var bookingList = bookings.stream().collect(Collectors.toList());

        assertEquals(1, bookingList.size());
        var dbBooking = bookingList.get(0).toBuilder().build();

        var expected = requests.get(status);
        assertEquals(expected, dbBooking);
    }

    @ParameterizedTest
    @ValueSource(strings = {"past", "future", "current"})
    void findTimedByOwnerId(String timeKind) {
        var owner = new User(null, "name", "a@ya.ru");
        userRepository.save(owner);

        var booker = new User(null, "booker", "b@ya.ru");
        userRepository.save(booker);

        var item = new Item(null, owner.getId(), "item", "desc", true, null);
        itemRepository.save(item);

        var now = DateUtils.now();

        var currentBooking = new Booking(null, item.getId(), BookingStatus.WAITING, booker.getId(), now.minusMinutes(1), now.plusMinutes(1));
        bookingRepository.save(currentBooking);

        var pastBooking = new Booking(null, item.getId(), BookingStatus.WAITING, booker.getId(), now.minusMinutes(10), now.minusMinutes(5));
        bookingRepository.save(pastBooking);

        var futureBooking = new Booking(null, item.getId(), BookingStatus.WAITING, booker.getId(), now.plusMinutes(5), now.plusMinutes(10));
        bookingRepository.save(futureBooking);

        Pair<Booking, Page<Booking>> pair;

        switch (timeKind) {
            case "past":
                pair = Pair.of(
                        pastBooking,
                        bookingRepository.findPastByOwnerIdOrderByStartDateDesc(owner.getId(), now, Pageable.unpaged()));
                break;
            case "future":
                pair = Pair.of(
                        futureBooking,
                        bookingRepository.findFutureByOwnerIdOrderByStartDateDesc(owner.getId(), now, Pageable.unpaged()));
                break;
            case "current":
                pair = Pair.of(
                        currentBooking,
                        bookingRepository.findCurrentByOwnerIdOrderByStartDateDesc(owner.getId(), now, Pageable.unpaged()));
                break;
            default:
                throw new RuntimeException("Invalid kind");
        }

        var bookings = pair.getSecond().stream().collect(Collectors.toList());

        assertEquals(1, bookings.size());
        var dbBooking = bookings.get(0).toBuilder().build();

        var expected = pair.getFirst();
        assertEquals(expected, dbBooking);
    }
}
