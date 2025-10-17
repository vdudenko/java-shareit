package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    private User user;
    private CommentInfoDto commentInfoDto;
    private Item item;

    private ItemCreateDto createItemDto;

    private Item updateItemDto;

    @BeforeEach
    void create() {
        user = User.builder()
                .id(1L)
                .name("Vadim")
                .email("dudenko.vadim@gmail.com").build();

        item = Item.builder()
                .id(1L)
                .description("desc")
                .owner(user)
                .name("test")
                .available(true)
                .build();

        commentInfoDto = CommentInfoDto.builder()
                .id(1L)
                .text("test")
                .authorName("Vadim")
                .created(LocalDateTime.now())
                .build();

    }

    @Test
    void getItem() {
        Long userId = 1L;
        Long itemId = 2L;

        User owner = User.builder().id(userId).build();
        Item item = Item.builder()
                .id(itemId)
                .name("Дрель")
                .description("Профессиональная")
                .available(true)
                .owner(owner)
                .build();

        Comment comment1 = Comment.builder()
                .id(1L)
                .text("Отличная дрель!")
                .author(User.builder().name("Алексей").build())
                .created(LocalDateTime.of(2025, 1, 1, 10, 0))
                .build();
        List<Comment> comments = List.of(comment1);

        Booking currentBookingEntity = Booking.builder()
                .id(10L)
                .start(LocalDateTime.of(2025, 4, 1, 10, 0))
                .end(LocalDateTime.of(2025, 4, 5, 10, 0))
                .build();
        Booking nextBookingEntity = Booking.builder()
                .id(11L)
                .start(LocalDateTime.of(2025, 4, 10, 10, 0))
                .end(LocalDateTime.of(2025, 4, 15, 10, 0))
                .build();

        Mockito.when(itemRepository.findById(Mockito.eq(itemId))).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.findByItemIdOrderByCreatedAsc(Mockito.eq(itemId))).thenReturn(comments);
        Mockito.when(bookingRepository.findCurrentBooking(Mockito.eq(itemId), Mockito.any(LocalDateTime.class)))
                .thenReturn(Optional.of(currentBookingEntity));
        Mockito.when(bookingRepository.findNextBooking(Mockito.eq(itemId), Mockito.any(LocalDateTime.class)))
                .thenReturn(Optional.of(nextBookingEntity));

        ItemInfoDto result = itemService.getItem(userId, itemId);

        assertThat(result.getId()).isEqualTo(itemId);
        assertThat(result.getName()).isEqualTo("Дрель");
        assertThat(result.getOwner()).isEqualTo(userId);
        assertThat(result.getComments()).hasSize(1);
        assertThat(result.getLastBooking()).isNotNull();
        assertThat(result.getNextBooking()).isNotNull();

        Mockito.verify(itemRepository).findById(itemId);
        Mockito.verify(commentRepository).findByItemIdOrderByCreatedAsc(itemId);
        Mockito.verify(bookingRepository).findCurrentBooking(Mockito.eq(itemId), Mockito.any(LocalDateTime.class));
        Mockito.verify(bookingRepository).findNextBooking(Mockito.eq(itemId), Mockito.any(LocalDateTime.class));
    }

    @Test
    void getItemsByText() {
        String search = "дрель";
        String lowerSearch = "дрель";

        Item item1 = Item.builder()
                .id(1L)
                .name("Профессиональная дрель")
                .description("Мощная дрель для ремонта")
                .available(true)
                .build();

        Item item2 = Item.builder()
                .id(2L)
                .name("Отвёртка")
                .description("Для мелкого ремонта")
                .available(true)
                .build();

        List<Item> expectedItems = List.of(item1, item2);

        Mockito.when(itemRepository.getItemsByText(Mockito.eq(lowerSearch))).thenReturn(expectedItems);

        List<Item> result = itemService.getItemsByText(search);

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(expectedItems);

        Mockito.verify(itemRepository).getItemsByText(Mockito.eq(lowerSearch));
    }

    @Test
    void getItemsByTextWithEmptySearchReturnsEmptyList() {
        List<Item> result1 = itemService.getItemsByText("");
        List<Item> result2 = itemService.getItemsByText("   ");
        List<Item> result3 = itemService.getItemsByText(null);

        assertThat(result1).isEmpty();
        assertThat(result2).isEmpty();
        assertThat(result3).isEmpty();

        Mockito.verify(itemRepository, Mockito.never()).getItemsByText(Mockito.anyString());
    }

    @Test
    void addNewItem() {
        Long userId = 1L;
        ItemCreateDto createDto = ItemCreateDto.builder()
                .name("Дрель")
                .description("Профессиональная дрель")
                .available(true)
                .requestId(null) // ← нет запроса
                .build();

        User owner = User.builder()
                .id(userId)
                .name("Vadim")
                .email("test@example.com")
                .build();

        Item expectedItem = Item.builder()
                .id(100L) // ID будет присвоен при сохранении
                .name("Дрель")
                .description("Профессиональная дрель")
                .available(true)
                .owner(owner)
                .request(null)
                .build();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        Mockito.when(itemRepository.save(Mockito.any(Item.class))).thenReturn(expectedItem);

        Item result = itemService.addNewItem(userId, createDto);

        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getName()).isEqualTo("Дрель");
        assertThat(result.getDescription()).isEqualTo("Профессиональная дрель");
        assertThat(result.getAvailable()).isTrue();
        assertThat(result.getOwner()).isEqualTo(owner);
        assertThat(result.getRequest()).isNull();

        Mockito.verify(userRepository).findById(userId);
        Mockito.verify(itemRequestRepository, Mockito.never()).findById(Mockito.anyLong());
        Mockito.verify(itemRepository).save(Mockito.argThat(item ->
                item.getName().equals("Дрель") &&
                        item.getOwner().getId().equals(userId) &&
                        item.getRequest() == null
        ));
    }

    @Test
    void updateItem() {
        Long userId = 1L;
        Long itemId = 2L;

        // Исходный объект в БД
        Item existingItem = Item.builder()
                .id(itemId)
                .name("Старое имя")
                .description("Старое описание")
                .available(true)
                .owner(User.builder().id(userId).build())
                .build();

        // DTO с новыми данными
        ItemDto updateDto = ItemDto.builder()
                .name("Новое имя")
                .description("Новое описание")
                .available(false)
                .build();

        // Обновлённый объект после сохранения
        Item updatedItem = Item.builder()
                .id(itemId)
                .name("Новое имя")
                .description("Новое описание")
                .available(false)
                .owner(User.builder().id(userId).build())
                .build();

        // Мокаем репозиторий
        Mockito.when(itemRepository.findByIdAndOwnerId(itemId, userId))
                .thenReturn(Optional.of(existingItem));
        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(updatedItem);

        Item result = itemService.updateItem(userId, itemId, updateDto);

        assertThat(result.getId()).isEqualTo(itemId);
        assertThat(result.getName()).isEqualTo("Новое имя");
        assertThat(result.getDescription()).isEqualTo("Новое описание");
        assertThat(result.getAvailable()).isFalse();

        Mockito.verify(itemRepository).findByIdAndOwnerId(itemId, userId);
        Mockito.verify(itemRepository).save(Mockito.argThat(item ->
                item.getName().equals("Новое имя") &&
                        item.getDescription().equals("Новое описание") &&
                        !item.getAvailable()
        ));
    }

    @Test
    void deleteItem() {
        long userId = 1L;
        long itemId = 1L;
        itemService.deleteItem(userId, itemId);
        Mockito.verify(itemRepository).deleteByIdAndOwnerId(userId, itemId);
    }

    @Test
    void createComment() {
        Long itemId = 1L;
        Long userId = 1L;

        Booking completedBooking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .status(Status.APPROVED)
                .build();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        Mockito.when(bookingRepository.findAllByBookerIdAndItemIdAndStatusIsAndEndIsBefore(
                Mockito.eq(userId),
                Mockito.eq(itemId),
                Mockito.eq(Status.APPROVED),
                Mockito.any(LocalDateTime.class)
        )).thenReturn(Optional.of(completedBooking));

        Comment savedComment = Comment.builder()
                .id(1L)
                .text("test")
                .author(user)
                .item(item)
                .created(LocalDateTime.now())
                .build();
        Mockito.when(commentRepository.save(Mockito.any(Comment.class)))
                .thenReturn(savedComment);

        CommentDto inputDto = new CommentDto("test");
        CommentInfoDto result = itemService.createComment(itemId, userId, inputDto);

        assertEquals(1L, result.getId());
        assertEquals("test", result.getText());
    }
}
