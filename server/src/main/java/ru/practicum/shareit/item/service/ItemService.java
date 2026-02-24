package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<ItemInfoDto> getItems(long userId);

    Item addNewItem(Long userId, ItemCreateDto item);

    ItemInfoDto getItem(Long userId, Long itemId);

    void deleteItem(Long userId, Long itemId);

    Item updateItem(Long userId, Long itemId, ItemDto item);

    List<Item> getItemsByText(String search);

    CommentInfoDto createComment(Long userId, Long itemId, CommentDto commentDto);
}
