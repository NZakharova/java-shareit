package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    Page<Item> findByUserId(int userId, Pageable pageable);

    Page<Item> findByNameContainingIgnoreCaseAndAvailable(String text, boolean available, Pageable pageable);

    Page<Item> findByDescriptionContainingIgnoreCaseAndAvailable(String text, boolean available, Pageable pageable);

    Page<Item> findByRequestId(int requestId, Pageable pageable);
}
