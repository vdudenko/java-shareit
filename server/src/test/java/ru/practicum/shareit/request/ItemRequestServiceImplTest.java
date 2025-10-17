package ru.practicum.shareit.request;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    private final LocalDateTime now = LocalDateTime.now();

    @Test
    void getByIdShouldReturnRequestWithItemsWhenUserAndRequestExist() {
        Long userId = 1L;
        Long requestId = 2L;

        User user = new User(userId, "User", "user@example.com");
        ItemRequest itemRequest = ItemRequest.builder()
                .id(requestId)
                .description("Need a drill")
                .requestor(user)
                .created(now)
                .build();

        Item item = Item.builder()
                .id(10L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .request(itemRequest)
                .build();

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findByRequestId(requestId)).thenReturn(List.of(item));

        ItemRequestWithItemsDto result = itemRequestService.getById(userId, requestId);

        assertThat(result.getId()).isEqualTo(requestId);
        assertThat(result.getDescription()).isEqualTo("Need a drill");
        assertThat(result.getRequesterId()).isEqualTo(userId);
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getName()).isEqualTo("Drill");
    }

    @Test
    void getByIdShouldThrowNotFoundExceptionWhenUserNotFound() {
        when(userRepository.existsById(999L)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getById(999L, 1L));
        assertThat(exception.getMessage()).contains("Пользователя с таким id не существует:999");
    }

    @Test
    void getByIdShouldThrowNotFoundExceptionWhenRequestNotFound() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRequestRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getById(1L, 999L));
        assertThat(exception.getMessage()).contains("Запроса с таким id не существует:999");
    }

    @Test
    void getAllShouldReturnOtherUsersRequests() {
        Long userId = 1L;
        ItemRequest request = ItemRequest.builder()
                .id(2L)
                .description("Need item")
                .requestor(new User(2L, "Other", "other@example.com"))
                .created(now)
                .build();

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findByRequestorIdNot(eq(userId), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(request)));
        when(itemRepository.findByRequestIdIn(anyList())).thenReturn(Collections.emptyList());

        List<ItemRequestWithItemsDto> result = itemRequestService.getAll(userId, 0, 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(2L);
        assertThat(result.get(0).getRequesterId()).isEqualTo(2L);
    }

    @Test
    void getAllShouldReturnEmptyListWhenNoRequests() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRequestRepository.findByRequestorIdNot(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        List<ItemRequestWithItemsDto> result = itemRequestService.getAll(1L, 0, 10);

        assertThat(result).isEmpty();
    }

    @Test
    void getAllByRequesterShouldReturnOwnRequests() {
        Long userId = 1L;
        ItemRequest request = ItemRequest.builder()
                .id(2L)
                .description("My request")
                .requestor(new User(userId, "Me", "me@example.com"))
                .created(now)
                .build();

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findByRequestorId(eq(userId), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(request)));
        when(itemRepository.findByRequestIdIn(anyList())).thenReturn(Collections.emptyList());

        List<ItemRequestWithItemsDto> result = itemRequestService.getAllByRequester(userId, 0, 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(2L);
        assertThat(result.get(0).getRequesterId()).isEqualTo(userId);
    }

    @Test
    void getAllByRequesterShouldThrowValidationExceptionWhenFromNegative() {
        when(userRepository.existsById(1L)).thenReturn(true);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemRequestService.getAllByRequester(1L, -1, 10));
        assertThat(exception.getMessage()).contains("Некорректные параметры пагинации");
    }

    @Test
    void getAllByRequesterShouldThrowValidationExceptionWhenSizeZero() {
        when(userRepository.existsById(1L)).thenReturn(true);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemRequestService.getAllByRequester(1L, 0, 0));
        assertThat(exception.getMessage()).contains("Некорректные параметры пагинации");
    }

    @Test
    void createShouldCreateItemRequest() {
        Long userId = 1L;
        ItemRequestDto dto = new ItemRequestDto("Need a book", null);
        User user = new User(userId, "User", "user@example.com");

        ItemRequest savedRequest = ItemRequest.builder()
                .id(100L)
                .description("Need a book")
                .requestor(user)
                .created(now)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(savedRequest);

        ItemRequest result = itemRequestService.create(userId, dto);

        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getDescription()).isEqualTo("Need a book");
        assertThat(result.getRequestor().getId()).isEqualTo(userId);
        verify(itemRequestRepository).save(argThat(req ->
                req.getDescription().equals("Need a book") &&
                        req.getRequestor().getId().equals(userId)
        ));
    }

    @Test
    void createShouldThrowNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.create(999L, new ItemRequestDto("desc", null)));
        assertThat(exception.getMessage()).contains("Пользователь с таким Id 999 не найден");
    }
}