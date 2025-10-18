package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerId(long userId);

    List<Item> findByRequestId(long requestId);

    void deleteByIdAndOwnerId(long itemId, long userId);

    Optional<Item> findByIdAndOwnerId(long itemId, long userId);

    @Query("""
            SELECT i
            FROM Item AS i
            WHERE i.available = true
            AND (LOWER(i.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(i.description) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    List<Item> getItemsByText(@Param("search") String search);

    List<Item> findByRequestIdIn(List<Long> requestIds);
}
