package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
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
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public List<ItemInfoDto> getItems(long userId) {
        List<Item> items = itemRepository.findByOwnerId(userId);
        if (items.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> itemIds = items.stream().map(Item::getId).toList();
        LocalDateTime now = LocalDateTime.now();

        List<Comment> allComments = commentRepository.findByItemIdInOrderByCreatedAsc(itemIds);
        List<Booking> currentBookings = bookingRepository.findCurrentBookingsForItems(itemIds, now);
        List<Booking> nextBookings = bookingRepository.findNextBookingsForItems(itemIds, now);

        Map<Long, List<Comment>> commentsByItem = allComments.stream()
                .collect(Collectors.groupingBy(c -> c.getItem().getId()));
        Map<Long, BookingInfoDto> currentMap = currentBookings.stream()
                .collect(Collectors.toMap(
                    b -> b.getItem().getId(),
                    b -> BookingInfoDto.builder()
                            .start(b.getStart())
                            .end(b.getEnd())
                            .build(),
                    (dto1, dto2) -> dto1
                ));
        Map<Long, BookingInfoDto> nextMap = nextBookings.stream()
                .collect(Collectors.toMap(
                    b -> b.getItem().getId(),
                    b -> BookingInfoDto.builder()
                        .start(b.getStart())
                        .end(b.getEnd())
                        .build(),
                    (dto1, dto2) -> dto1
                ));

        return items.stream().map(item -> {
            Long itemId = item.getId();
            ItemInfoDto itemInfoDto = ItemMapper.toItemInfoDto(item);
            itemInfoDto.setLastBooking(currentMap.get(itemId));
            itemInfoDto.setNextBooking(nextMap.get(itemId));
            itemInfoDto.setComments(commentsByItem.getOrDefault(itemId, Collections.emptyList()));
            return itemInfoDto;
        }).collect(Collectors.toList());
    }

    @Override
    public ItemInfoDto getItem(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Объект не найден: " + itemId));

        List<Comment> comments = commentRepository.findByItemIdOrderByCreatedAsc(itemId);
        BookingInfoDto currentBooking = null;
        BookingInfoDto nextBooking = null;

        if (item.getOwner() != null && item.getOwner().getId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();

            currentBooking = bookingRepository.findCurrentBooking(itemId, now)
                    .map(b -> BookingInfoDto.builder()
                            .start(b.getStart())
                            .end(b.getEnd())
                            .build())
                    .orElse(null);

            nextBooking = bookingRepository.findNextBooking(itemId, now)
                    .map(b -> BookingInfoDto.builder()
                            .start(b.getStart())
                            .end(b.getEnd())
                            .build())
                    .orElse(null);
        }

        ItemInfoDto itemInfoDto = ItemMapper.toItemInfoDto(item);
        itemInfoDto.setLastBooking(currentBooking);
        itemInfoDto.setNextBooking(nextBooking);
        itemInfoDto.setComments(comments);

        return itemInfoDto;
    }

    @Override
    public List<Item> getItemsByText(String search) {
        if (search == null || search.trim().isEmpty()) {
            return List.of();
        }

        String lowerSearch = search.toLowerCase().trim();
        return itemRepository.getItemsByText(lowerSearch);
    }

    @Transactional
    @Override
    public Item addNewItem(Long userId, ItemCreateDto itemCreateDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        ItemRequest itemRequest = null;

        if (itemCreateDto.getRequestId() != null) {
            itemRequest = itemRequestRepository.findById(itemCreateDto.getRequestId()).orElseThrow(() -> new NotFoundException("Запрос с id = " + itemCreateDto.getRequestId() + " не найден!"));
        }

        Item item = ItemMapper.toItem(itemCreateDto);
        item.setOwner(user);
        item.setRequest(itemRequest);

        return itemRepository.save(item);
    }

    @Transactional
    @Override
    public Item updateItem(Long userId, Long itemId, ItemDto item) {
        Item itemDb = itemRepository.findByIdAndOwnerId(itemId, userId)
                .orElseThrow(() -> new NotFoundException("Объект с id = " + itemId + " не найден" + " для пользователя " + userId + " не найден"));
        itemDb.updateItem(item);
        return itemRepository.save(itemDb);
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        itemRepository.deleteByIdAndOwnerId(itemId, userId);
    }

    @Transactional
    @Override
    public CommentInfoDto createComment(Long userId, Long itemId, CommentDto commentDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Объект не найден: " + itemId));
        User author = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден: " + userId));

        bookingRepository.findAllByBookerIdAndItemIdAndStatusIsAndEndIsBefore(
                userId, itemId, Status.APPROVED, LocalDateTime.now()).orElseThrow(
                () -> new NotAvailableException("Пользователь не может оставить отзыв"));

        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(author);

        return CommentMapper.commentInfoDto(commentRepository.save(comment));
    }
}
