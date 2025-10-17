package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRequestTest {

    @Test
    void builderShouldCreateItemRequestWithAllFields() {
        // Given
        Long id = 100L;
        String description = "Need a book";
        User requestor = new User(1L, "Vadim", "vadim@example.com");
        LocalDateTime created = LocalDateTime.now();

        // When
        ItemRequest request = ItemRequest.builder()
                .id(id)
                .description(description)
                .requestor(requestor)
                .created(created)
                .build();

        // Then
        assertThat(request.getId()).isEqualTo(id);
        assertThat(request.getDescription()).isEqualTo(description);
        assertThat(request.getRequestor()).isEqualTo(requestor);
        assertThat(request.getCreated()).isEqualTo(created);
    }

    @Test
    void equalsShouldReturnTrueWhenIdsAreEqual() {
        ItemRequest r1 = ItemRequest.builder().id(1L).build();
        ItemRequest r2 = ItemRequest.builder().id(1L).build();

        assertThat(r1).isEqualTo(r2);
    }

    @Test
    void equalsShouldReturnFalseWhenIdsAreDifferent() {
        ItemRequest r1 = ItemRequest.builder().id(1L).build();
        ItemRequest r2 = ItemRequest.builder().id(2L).build();

        assertThat(r1).isNotEqualTo(r2);
    }

    @Test
    void equalsShouldReturnFalseWhenOtherObjectIsNotItemRequest() {
        ItemRequest request = ItemRequest.builder().id(1L).build();
        String notRequest = "not an item request";

        assertThat(request).isNotEqualTo(notRequest);
    }

    @Test
    void equalsShouldReturnFalseWhenIdIsNull() {
        ItemRequest r1 = ItemRequest.builder().id(null).build();
        ItemRequest r2 = ItemRequest.builder().id(null).build();

        assertThat(r1).isNotEqualTo(r2); // по вашей логике: id != null → false
    }

    @Test
    void hashCodeShouldBeConsistent() {
        ItemRequest request = ItemRequest.builder().id(42L).build();
        int hash1 = request.hashCode();
        int hash2 = request.hashCode();

        assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    void gettersAndSettersShouldWorkCorrectly() {
        ItemRequest request = new ItemRequest();
        LocalDateTime now = LocalDateTime.now();
        User user = new User(1L, "User", "user@example.com");

        request.setId(1L);
        request.setDescription("Desc");
        request.setRequestor(user);
        request.setCreated(now);

        assertThat(request.getId()).isEqualTo(1L);
        assertThat(request.getDescription()).isEqualTo("Desc");
        assertThat(request.getRequestor()).isEqualTo(user);
        assertThat(request.getCreated()).isEqualTo(now);
    }
}
