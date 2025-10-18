package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRequestMapperTest {

    @Test
    void toItemRequest_shouldConvertDtoToEntity() {
        // Given
        ItemRequestDto dto = new ItemRequestDto("Need a book", null);

        // When
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(dto);

        // Then
        assertThat(itemRequest.getDescription()).isEqualTo("Need a book");
        assertThat(itemRequest.getId()).isNull();
        assertThat(itemRequest.getRequestor()).isNull();
        assertThat(itemRequest.getCreated()).isNull();
    }

    @Test
    void toItemRequestWithItemsDto_shouldConvertEntityToDto() {
        // Given
        Long userId = 1L;
        Long requestId = 2L;
        LocalDateTime now = LocalDateTime.now();

        User user = new User(userId, "Vadim", "vadim@example.com");
        ItemRequest itemRequest = ItemRequest.builder()
                .id(requestId)
                .description("Need a drill")
                .requestor(user)
                .created(now)
                .build();

        // When
        ItemRequestWithItemsDto dto = ItemRequestMapper.toItemRequestWithItemsDto(itemRequest);

        // Then
        assertThat(dto.getId()).isEqualTo(requestId);
        assertThat(dto.getDescription()).isEqualTo("Need a drill");
        assertThat(dto.getRequesterId()).isEqualTo(userId);
        assertThat(dto.getCreated()).isEqualTo(now);
        assertThat(dto.getItems()).isNull(); // items устанавливаются отдельно в сервисе
    }
}