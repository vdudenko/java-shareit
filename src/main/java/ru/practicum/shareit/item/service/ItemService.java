package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentInfoDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<ItemInfoDto> getItems(long userId);

    Item addNewItem(Long userId, Item item);

    ItemInfoDto getItem(Long userId, Long itemId);

    void deleteItem(Long userId, Long itemId);

    Item updateItem(Long userId, Long itemId, ItemDto item);

    List<Item> getItemsByText(String search);

    CommentInfoDto createComment(long userId, long itemId, CommentDto commentDto);
}
