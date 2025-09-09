package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    List<Item> getItems(long userId);
    Item addNewItem(Long userId, Item item);
    Optional<Item> getItem(Long itemId);
    void deleteItem(Long userId, Long itemId);
    Item updateItem(Long userId, Long itemId, ItemDto item);
    List<Item> getItemsByText(String search);
}
