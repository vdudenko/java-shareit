package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequestWithItemsDto getById(Long userId, Long requestId);

    ItemRequest create(Long userId, ItemRequestDto itemRequestDtoShort);

    List<ItemRequestWithItemsDto> getAll(Long userId, Integer from, Integer size);

    List<ItemRequestWithItemsDto> getAllByRequester(Long userId, Integer from, Integer size);
}
