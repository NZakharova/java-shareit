package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    Page<Item> findByUserIdOrderByIdAsc(int userId, Pageable pageable);

    Page<Item> findByNameContainingIgnoreCaseAndAvailableOrderByIdAsc(String text, boolean available, Pageable pageable);

    Page<Item> findByDescriptionContainingIgnoreCaseAndAvailableOrderByIdAsc(String text, boolean available, Pageable pageable);

    Page<Item> findByRequestIdOrderByIdAsc(int requestId, Pageable pageable);
}