package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    private User owner;
    private User booker;
    private Item item;
    private ItemRequest itemRequest;
    private Booking currentBooking;
    private Booking nextBooking;
    private Comment comment;

    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        owner = new User(1L, "Owner", "owner@example.com");
        booker = new User(2L, "Booker", "booker@example.com");
        itemRequest = ItemRequest.builder().id(10L).build();
        item = Item.builder()
                .id(1L)
                .name("Дрель")
                .description("Профессиональная")
                .available(true)
                .owner(owner)
                .request(itemRequest)
                .build();
        currentBooking = Booking.builder()
                .id(100L)
                .item(item)
                .start(now.minusDays(2))
                .end(now.plusDays(2))
                .status(Status.APPROVED)
                .build();
        nextBooking = Booking.builder()
                .id(101L)
                .item(item)
                .start(now.plusDays(3))
                .end(now.plusDays(5))
                .status(Status.APPROVED)
                .build();
        comment = Comment.builder()
                .id(1L)
                .text("Отличная дрель!")
                .author(booker)
                .item(item)
                .created(now.minusHours(1))
                .build();
    }

    @Test
    void getItemsShouldReturnItemsWithBookingsAndComments() {
        when(itemRepository.findByOwnerId(owner.getId())).thenReturn(List.of(item));
        when(commentRepository.findByItemIdInOrderByCreatedAsc(List.of(1L))).thenReturn(List.of(comment));
        when(bookingRepository.findCurrentBookingsForItems(eq(List.of(1L)), any(LocalDateTime.class)))
                .thenReturn(List.of(currentBooking));
        when(bookingRepository.findNextBookingsForItems(eq(List.of(1L)), any(LocalDateTime.class)))
                .thenReturn(List.of(nextBooking));

        List<ItemInfoDto> result = itemService.getItems(owner.getId());

        assertThat(result).hasSize(1);
        ItemInfoDto dto = result.get(0);
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getOwner()).isEqualTo(owner.getId());
        assertThat(dto.getLastBooking()).isNotNull();
        assertThat(dto.getNextBooking()).isNotNull();
        assertThat(dto.getComments()).hasSize(1);
    }

    @Test
    void getItemsShouldReturnEmptyListWhenNoItems() {
        when(itemRepository.findByOwnerId(owner.getId())).thenReturn(Collections.emptyList());

        List<ItemInfoDto> result = itemService.getItems(owner.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void getItemShouldReturnItemWithBookingsWhenUserIsOwner() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemIdOrderByCreatedAsc(1L)).thenReturn(List.of(comment));
        when(bookingRepository.findCurrentBooking(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.of(currentBooking));
        when(bookingRepository.findNextBooking(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.of(nextBooking));

        ItemInfoDto result = itemService.getItem(owner.getId(), 1L);

        assertThat(result.getLastBooking()).isNotNull();
        assertThat(result.getNextBooking()).isNotNull();
        assertThat(result.getComments()).hasSize(1);
    }

    @Test
    void getItemShouldReturnItemWithoutBookingsWhenUserIsNotOwner() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemIdOrderByCreatedAsc(1L)).thenReturn(List.of(comment));

        ItemInfoDto result = itemService.getItem(booker.getId(), 1L);

        assertThat(result.getLastBooking()).isNull();
        assertThat(result.getNextBooking()).isNull();
        assertThat(result.getComments()).hasSize(1);
    }

    @Test
    void getItemShouldThrowNotFoundExceptionWhenItemNotFound() {
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.getItem(owner.getId(), 999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Объект не найден: 999");
    }

    @Test
    void getItemsByTextShouldReturnMatchingItems() {
        String search = "дрель";
        Item foundItem = Item.builder().id(1L).name("Дрель").available(true).build();
        when(itemRepository.getItemsByText(search.toLowerCase())).thenReturn(List.of(foundItem));

        List<Item> result = itemService.getItemsByText(search);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Дрель");
    }

    @Test
    void getItemsByTextShouldReturnEmptyListWhenSearchIsEmpty() {
        assertThat(itemService.getItemsByText("")).isEmpty();
        assertThat(itemService.getItemsByText("   ")).isEmpty();
        assertThat(itemService.getItemsByText(null)).isEmpty();
    }

    @Test
    void addNewItemShouldCreateItemWithRequestId() {
        Long userId = 1L;
        Long requestId = 10L;
        ItemCreateDto dto = ItemCreateDto.builder()
                .name("Новая дрель")
                .description("Описание")
                .available(true)
                .requestId(requestId)
                .build();

        User user = new User(userId, "User", "user@example.com");
        ItemRequest request = ItemRequest.builder().id(requestId).build();
        Item savedItem = Item.builder()
                .id(100L)
                .name("Новая дрель")
                .owner(user)
                .request(request)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(itemRepository.save(any(Item.class))).thenReturn(savedItem);

        Item result = itemService.addNewItem(userId, dto);

        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getRequest().getId()).isEqualTo(requestId);
        verify(itemRepository).save(argThat(i ->
                i.getName().equals("Новая дрель") &&
                        i.getOwner().getId().equals(userId) &&
                        i.getRequest().getId().equals(requestId)
        ));
    }

    @Test
    void addNewItemShouldCreateItemWithoutRequestId() {
        Long userId = 1L;
        ItemCreateDto dto = ItemCreateDto.builder()
                .name("Дрель без запроса")
                .description("Описание")
                .available(true)
                .build();

        User user = new User(userId, "User", "user@example.com");
        Item savedItem = Item.builder()
                .id(100L)
                .name("Дрель без запроса")
                .owner(user)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(savedItem);

        Item result = itemService.addNewItem(userId, dto);

        assertThat(result.getRequest()).isNull();
    }

    @Test
    void addNewItemShouldThrowNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.addNewItem(999L, ItemCreateDto.builder().build()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с id = 999 не найден");
    }

    @Test
    void addNewItemShouldThrowNotFoundExceptionWhenRequestNotFound() {
        Long userId = 1L;
        Long requestId = 999L;
        ItemCreateDto dto = ItemCreateDto.builder().requestId(requestId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.addNewItem(userId, dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Запрос с id = 999 не найден");
    }

    @Test
    void updateItemShouldUpdateItem() {
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto updateDto = ItemDto.builder()
                .name("Обновлённая дрель")
                .description("Новое описание")
                .available(false)
                .build();

        Item existingItem = Item.builder()
                .id(itemId)
                .name("Старая дрель")
                .owner(new User(userId, "Owner", "owner@example.com"))
                .build();

        Item updatedItem = Item.builder()
                .id(itemId)
                .name("Обновлённая дрель")
                .description("Новое описание")
                .available(false)
                .owner(existingItem.getOwner())
                .build();

        when(itemRepository.findByIdAndOwnerId(itemId, userId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);

        Item result = itemService.updateItem(userId, itemId, updateDto);

        assertThat(result.getName()).isEqualTo("Обновлённая дрель");
        assertThat(result.getAvailable()).isFalse();
    }

    @Test
    void updateItemShouldThrowNotFoundExceptionWhenItemNotFound() {
        when(itemRepository.findByIdAndOwnerId(999L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.updateItem(1L, 999L, ItemDto.builder().build()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Объект с id = 999 не найден");
    }

    @Test
    void deleteItemShouldCallRepository() {
        itemService.deleteItem(1L, 1L);
        verify(itemRepository).deleteByIdAndOwnerId(1L, 1L);
    }

    @Test
    void createCommentShouldCreateComment() {
        Long userId = 2L;
        Long itemId = 1L;
        CommentDto dto = new CommentDto("Отлично!");

        Comment savedComment = Comment.builder()
                .id(100L)
                .text("Отлично!")
                .author(booker)
                .item(item)
                .created(now)
                .build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndItemIdAndStatusIsAndEndIsBefore(
                eq(userId), eq(itemId), eq(Status.APPROVED), any(LocalDateTime.class)))
                .thenReturn(Optional.of(currentBooking));
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        CommentInfoDto result = itemService.createComment(userId, itemId, dto);

        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getText()).isEqualTo("Отлично!");
    }

    @Test
    void createCommentShouldThrowNotAvailableExceptionWhenNoBooking() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndItemIdAndStatusIsAndEndIsBefore(
                eq(2L), eq(1L), eq(Status.APPROVED), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.createComment(2L, 1L, new CommentDto("Text")))
                .isInstanceOf(NotAvailableException.class)
                .hasMessageContaining("Пользователь не может оставить отзыв");
    }

    @Test
    void createCommentShouldThrowNotFoundExceptionWhenItemNotFound() {
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.createComment(1L, 999L, new CommentDto("Text")))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Объект не найден: 999");
    }
}