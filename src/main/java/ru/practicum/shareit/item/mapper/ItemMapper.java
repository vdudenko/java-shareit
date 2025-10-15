package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static ItemInfoDto toItemInfoDto(Item item) {
        return ItemInfoDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner() != null ? item.getOwner().getId() : null)
                .request(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }
}
