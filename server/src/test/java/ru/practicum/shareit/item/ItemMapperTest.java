package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemMapperTest {

    @Test
    void toItemInfoDto_shouldConvertItemToItemInfoDto() {
        // Given
        Long ownerId = 1L;
        Long requestId = 2L;
        User owner = new User(ownerId, "Owner", "owner@example.com");
        ItemRequest request = ItemRequest.builder().id(requestId).build();
        Item item = Item.builder()
                .id(100L)
                .name("Дрель")
                .description("Профессиональная")
                .available(true)
                .owner(owner)
                .request(request)
                .build();

        // When
        ItemInfoDto dto = ItemMapper.toItemInfoDto(item);

        // Then
        assertThat(dto.getId()).isEqualTo(100L);
        assertThat(dto.getName()).isEqualTo("Дрель");
        assertThat(dto.getDescription()).isEqualTo("Профессиональная");
        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getOwner()).isEqualTo(ownerId);
        assertThat(dto.getRequest()).isEqualTo(requestId);
    }

    @Test
    void toItemInfoDto_shouldHandleNullOwnerAndRequest() {
        Item item = Item.builder()
                .id(1L)
                .name("Item")
                .owner(null)
                .request(null)
                .build();

        ItemInfoDto dto = ItemMapper.toItemInfoDto(item);

        assertThat(dto.getOwner()).isNull();
        assertThat(dto.getRequest()).isNull();
    }

    @Test
    void toItemDto_shouldConvertItemToItemDto() {
        Item item = Item.builder()
                .name("Дрель")
                .description("Описание")
                .available(false)
                .build();

        ItemDto dto = ItemMapper.toItemDto(item);

        assertThat(dto.getName()).isEqualTo("Дрель");
        assertThat(dto.getDescription()).isEqualTo("Описание");
        assertThat(dto.getAvailable()).isFalse();
    }

    @Test
    void toItem_shouldConvertItemCreateDtoToItem() {
        ItemCreateDto dto = ItemCreateDto.builder()
                .name("Новая дрель")
                .description("Новое описание")
                .available(true)
                .build();

        Item item = ItemMapper.toItem(dto);

        assertThat(item.getName()).isEqualTo("Новая дрель");
        assertThat(item.getDescription()).isEqualTo("Новое описание");
        assertThat(item.getAvailable()).isTrue();
        assertThat(item.getId()).isNull();
        assertThat(item.getOwner()).isNull();
        assertThat(item.getRequest()).isNull();
    }

    @Test
    void toDtoShortList_shouldConvertListOfItemsToItemDtos() {
        Item item1 = Item.builder().name("Item1").available(true).build();
        Item item2 = Item.builder().name("Item2").available(false).build();

        List<ItemDto> dtos = ItemMapper.toDtoShortList(List.of(item1, item2));

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).getName()).isEqualTo("Item1");
        assertThat(dtos.get(1).getName()).isEqualTo("Item2");
    }

    @Test
    void toDtoShortList_shouldReturnEmptyList_whenInputIsNull() {
        List<ItemDto> dtos = ItemMapper.toDtoShortList(null);
        assertThat(dtos).isEmpty();
    }

    @Test
    void toDtoShortList_shouldReturnEmptyList_whenInputIsEmpty() {
        List<ItemDto> dtos = ItemMapper.toDtoShortList(Collections.emptyList());
        assertThat(dtos).isEmpty();
    }
}