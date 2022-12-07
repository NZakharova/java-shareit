package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findByUserId(int userId);

    List<Item> findByNameContainingIgnoreCaseAndAvailable(String text, boolean available);

    List<Item> findByDescriptionContainingIgnoreCaseAndAvailable(String text, boolean available);

    List<Item> findByRequestId(int requestId);
}
