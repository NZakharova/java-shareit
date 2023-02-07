package ru.practicum.shareit.requests;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {
    Page<ItemRequest> findByAuthorIdOrderByCreatedDesc(int userId, Pageable pageable);

    Page<ItemRequest> findByAuthorIdNotOrderByCreatedDesc(int userId, Pageable pageable);
}
