package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

class ItemTest {

    @Test
    void updateItem_shouldUpdateFieldsWhenDtoFieldsAreNotNull() {
        Item item = Item.builder()
                .name("Old name")
                .description("Old description")
                .available(true)
                .build();

        ItemDto dto = ItemDto.builder()
                .name("New name")
                .description("New description")
                .available(false)
                .build();

        item.updateItem(dto);

        assertThat(item.getName()).isEqualTo("New name");
        assertThat(item.getDescription()).isEqualTo("New description");
        assertThat(item.getAvailable()).isFalse();
    }

    @Test
    void updateItemShouldNotUpdateFieldsWhenDtoFieldsAreNull() {
        Item item = Item.builder()
                .name("Old name")
                .description("Old description")
                .available(true)
                .build();

        ItemDto dto = ItemDto.builder()
                .name(null)
                .description(null)
                .available(null)
                .build();

        item.updateItem(dto);

        assertThat(item.getName()).isEqualTo("Old name");
        assertThat(item.getDescription()).isEqualTo("Old description");
        assertThat(item.getAvailable()).isTrue();
    }

    @Test
    void equals_shouldReturnTrueWhenIdsAreEqual() {
        Item item1 = Item.builder().id(1L).build();
        Item item2 = Item.builder().id(1L).build();

        assertThat(item1).isEqualTo(item2);
    }

    @Test
    void equalsShouldReturnFalseWhenIdsAreDifferent() {
        Item item1 = Item.builder().id(1L).build();
        Item item2 = Item.builder().id(2L).build();

        assertThat(item1).isNotEqualTo(item2);
    }

    @Test
    void equalsShouldReturnFalseWhenOtherObjectIsNotItem() {
        Item item = Item.builder().id(1L).build();
        String notItem = "not an item";

        assertThat(item).isNotEqualTo(notItem);
    }

    @Test
    void equalsShouldReturnFalseWhenIdIsNull() {
        Item item1 = Item.builder().id(null).build();
        Item item2 = Item.builder().id(null).build();

        assertThat(item1).isNotEqualTo(item2); // по вашей логике: id != null → false
    }

    @Test
    void hashCodeShouldBeBasedOnId() {
        Item item = Item.builder().id(42L).build();
        assertThat(item.hashCode()).isEqualTo(42);
    }

    @Test
    void builderShouldCreateItemWithAllFields() {
        User owner = new User(1L, "Owner", "owner@example.com");
        ItemRequest request = ItemRequest.builder().id(10L).build();

        Item item = Item.builder()
                .id(100L)
                .name("Дрель")
                .description("Профессиональная")
                .owner(owner)
                .request(request)
                .available(true)
                .build();

        assertThat(item.getId()).isEqualTo(100L);
        assertThat(item.getName()).isEqualTo("Дрель");
        assertThat(item.getDescription()).isEqualTo("Профессиональная");
        assertThat(item.getOwner()).isEqualTo(owner);
        assertThat(item.getRequest()).isEqualTo(request);
        assertThat(item.getAvailable()).isTrue();
    }
}