package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    List<Item> findByUserId(long userId);
    Optional<Item> getItemById(Long itemId);
    List<Item> getItemsByText(String search);
    Item save(Item item);
    void deleteByUserIdAndItemId(long userId, long itemId);
    Item update(long userId, long itemId, ItemDto item);

    boolean isUserHasItem(long userId, long itemId);
}
