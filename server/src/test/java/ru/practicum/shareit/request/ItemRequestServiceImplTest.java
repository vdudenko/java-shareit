package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Mock
    private UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Test
    void create() {
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .name("Vadim")
                .email("dudenko.vadim@google.com")
                .build();

        ItemRequestDto itemRequestDto = new ItemRequestDto("desc", userId);

        ItemRequest itemRequestExpected = ItemRequest.builder()
                .id(1L)
                .description("desc")
                .requestor(user)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(Mockito.any())).thenReturn(itemRequestExpected);

        ItemRequest itemRequest = itemRequestService.create(userId, itemRequestDto);

        assertThat(itemRequest).isEqualTo(itemRequestExpected);
    }

    @Test
    void createShouldReturnUserNotFound() {
        Long userId = 1L;

        when(userRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        ItemRequestDto itemRequestDto = new ItemRequestDto("desc", userId);

        assertThrows(NotFoundException.class, () -> itemRequestService.create(userId, itemRequestDto));

        Mockito.verify(itemRequestRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void getAllByRequester() {
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .name("Vadim")
                .email("dudenko.vadim@google.com")
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("desc")
                .requestor(user)
                .build();

        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(itemRequestRepository.findByRequestorId(Mockito.eq(userId), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(itemRequest)));
        Mockito.when(itemRepository.findByRequestIdIn(Mockito.anyList())).thenReturn(List.of());

        List<ItemRequestWithItemsDto> result = itemRequestService.getAllByRequester(userId, 0, 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getDescription()).isEqualTo("desc");
        assertThat(result.get(0).getRequesterId()).isEqualTo(userId);
        assertThat(result.get(0).getItems()).isEmpty();
    }

    @Test
    void getAllByRequesterShouldReturnNotFoundException() {
        Long userId = 1L;
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(false);
        assertThrows(NotFoundException.class, () -> itemRequestService.getAllByRequester(userId, 0, 10));
        Mockito.verify(itemRequestRepository, Mockito.never()).findByRequestorId(Mockito.anyLong(), Mockito.any());
    }

    @Test
    void getAll() {
        Long userId = 1L;
        Long userId2 = 2L;     // владелец чужого запроса

        User user2 = User.builder()
                .id(userId2) // ← исправлено!
                .name("Vadim")
                .email("dudenko.vadim@gmail.com")
                .build();

        ItemRequest itemRequest2 = ItemRequest.builder()
                .id(2L)
                .description("desc")
                .requestor(user2)
                .build();

        Mockito.when(userRepository.existsById(userId)).thenReturn(true);

        Mockito.when(itemRequestRepository.findByRequestorIdNot(Mockito.eq(userId), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(itemRequest2)));

        Mockito.when(itemRepository.findByRequestIdIn(Mockito.anyList())).thenReturn(List.of());

        List<ItemRequestWithItemsDto> result = itemRequestService.getAll(userId, 0, 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(2L);
        assertThat(result.get(0).getDescription()).isEqualTo("desc");
        assertThat(result.get(0).getRequesterId()).isEqualTo(userId2);
        assertThat(result.get(0).getItems()).isEmpty();
    }

    @Test
    void getAllShouldReturnNotFoundException() {
        Long userId = 2L;

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemRequestService.getAll(userId, 0, 10));

        Mockito.verify(itemRequestRepository, Mockito.never()).findByRequestorIdNot(Mockito.anyLong(), Mockito.any());
    }

    @Test
    void getById() {
        Long userId = 1L;
        Long requestId = 1L;
        User user = User.builder()
                .id(userId) // ← исправлено!
                .name("Vadim")
                .email("dudenko.vadim@gmail.com")
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .id(requestId)
                .description("desc")
                .requestor(user)
                .build();

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(itemRequest));
        Mockito.when(itemRepository.findByRequestId(Mockito.anyLong())).thenReturn(List.of());

        ItemRequestWithItemsDto expected = ItemRequestWithItemsDto.builder()
                .id(1L)
                .description("desc")
                .requesterId(userId)
                .items(List.of())
                .build();

        ItemRequestWithItemsDto actual = itemRequestService.getById(userId, requestId);

        assertEquals(expected, actual);
    }

    @Test
    void getByIdShouldReturnNotFoundException() {
        Long userId = 2L;

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemRequestService.getById(userId, 1L));
    }
}
