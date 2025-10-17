package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestRepositoryTest {
    private final ItemRequestServiceImpl itemRequestService;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private ItemRequestWithItemsDto itemRequestWithItemsDto;
    private ItemRequestDto itemRequestDto;
    private User user;
    private Long requestId;
    private LocalDateTime created;

    @BeforeEach
    void beforeEach() {
        created = LocalDateTime.now();
        User userDto = User.builder()
                .name("Vadim")
                .email("dudenko.vadim@google.com")
                .build();

        user = userRepository.save(userDto);

        ItemRequest itemRequest = ItemRequest.builder()
                .description("desc")
                .requestor(user)
                .created(created)
                .build();

        itemRequest = itemRequestRepository.save(itemRequest);
        requestId = itemRequest.getId();

        itemRequestDto = new ItemRequestDto("desc", user.getId());
        itemRequestWithItemsDto = ItemRequestWithItemsDto.builder()
                .id(requestId)
                .description("desc")
                .requesterId(user.getId())
                .created(created)
                .items(List.of())
                .build();;
    }

    @Test
    void create() {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setCreated(created);
        ItemRequest result = itemRequestService.create(user.getId(), itemRequestDto);

        assertEquals(requestId + 1, result.getId());
        assertEquals(user.getId(), result.getRequestor().getId());
        assertEquals("desc", result.getDescription());
    }

    @Test
    void getAllByRequester() {
        Integer from = 0;
        Integer size = 10;

        List<ItemRequestWithItemsDto> result = itemRequestService.getAllByRequester(user.getId(), from, size);
        List<ItemRequestWithItemsDto> dtoList = List.of(itemRequestWithItemsDto);

        assertEquals(dtoList.get(0).getId(), result.get(0).getId());
        assertEquals(dtoList.get(0).getDescription(), result.get(0).getDescription());
        assertEquals(dtoList.get(0).getRequesterId(), result.get(0).getRequesterId());
        assertEquals(dtoList.get(0).getItems().size(), result.get(0).getItems().size());
    }

    @Test
    void getAll() {
        Integer from = 0;
        Integer size = 10;

        List<ItemRequestWithItemsDto> result = itemRequestService.getAll(user.getId(), from, size);
        assertEquals(List.of(), result);
    }

    @Test
    void getById() {
        ItemRequestWithItemsDto result = itemRequestService.getById(user.getId(), requestId);

        assertEquals(requestId, result.getId());
        assertEquals(user.getId(), result.getRequesterId());
        assertEquals("desc", result.getDescription());
    }
}
