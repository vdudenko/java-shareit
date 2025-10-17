package ru.practicum.shareit.request.service;


import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    @Transactional(readOnly = true)
    @Override
    public ItemRequestWithItemsDto getById(Long userId, Long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователя с таким id не существует:" + userId);
        }

        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("Запроса с таким id не существует:" + requestId));

        List<Item> items = itemRepository.findByRequestId(requestId);
        List<ItemDto> itemDtos = ItemMapper.toDtoShortList(items);

        ItemRequestWithItemsDto itemRequestDto = ItemRequestMapper.toItemRequestWithItemsDto(itemRequest);
        itemRequestDto.setItems(itemDtos);

        return itemRequestDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestWithItemsDto> getAll(Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Not found User with Id:" + userId);
        }

        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequestorIdNot(userId, pageable).getContent();

        if (itemRequests.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> requestIds = itemRequests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        List<Item> items = itemRepository.findByRequestIdIn(requestIds);
        Map<Long, List<Item>> itemsByRequestId = items.stream().collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        return itemRequests.stream()
                .map(request -> {
                    List<ItemDto> itemDtos = itemsByRequestId.getOrDefault(request.getId(), Collections.emptyList())
                            .stream()
                            .map(ItemMapper::toItemDto)
                            .collect(Collectors.toList());

                    return ItemRequestWithItemsDto.builder()
                            .id(request.getId())
                            .description(request.getDescription())
                            .requesterId(request.getRequestor().getId())
                            .items(itemDtos)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestWithItemsDto> getAllByRequester(Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Not found User with Id:" + userId);
        }

        if (from < 0 || size <= 0) {
            throw new ValidationException("Некорректные параметры пагинации");
        }

        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequestorId(userId, pageable).getContent();

        if (itemRequests.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> requestIds = itemRequests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        List<Item> items = itemRepository.findByRequestIdIn(requestIds);
        Map<Long, List<Item>> itemsByRequestId = items.stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        return itemRequests.stream()
                .map(request -> ItemRequestWithItemsDto.builder()
                        .id(request.getId())
                        .description(request.getDescription())
                        .requesterId(request.getRequestor().getId())
                        .items(itemsByRequestId.getOrDefault(request.getId(), Collections.emptyList())
                                .stream()
                                .map(ItemMapper::toItemDto)
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ItemRequest create(Long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с таким Id " + userId + " не найден"));

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequest newItem = itemRequestRepository.save(itemRequest);
        return newItem;
    }
}
