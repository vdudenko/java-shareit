package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    private final LocalDateTime now = LocalDateTime.now();
    private final LocalDateTime start = now.plusDays(1);
    private final LocalDateTime end = now.plusDays(3);

    @Test
    void addShouldCreateBookingWhenValidData() {
        // Given
        Long userId = 1L;
        Long itemId = 2L;

        User booker = new User(userId, "Booker", "booker@example.com");
        Item item = Item.builder()
                .id(itemId)
                .available(true)
                .owner(new User(3L, "Owner", "owner@example.com"))
                .build();

        BookingDto dto = BookingDto.builder()
                .itemId(itemId)
                .start(start)
                .end(end)
                .build();

        Booking savedBooking = Booking.builder()
                .id(100L)
                .item(item)
                .booker(booker)
                .start(start)
                .end(end)
                .status(Status.WAITING)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

        Booking result = bookingService.add(userId, dto);

        assertEquals(100L, result.getId());
        assertEquals(Status.WAITING, result.getStatus());
        assertEquals(itemId, result.getItem().getId());
        assertEquals(userId, result.getBooker().getId());

        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void addShouldThrowNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.add(1L, BookingDto.builder().build()));
        assertTrue(exception.getMessage().contains("Пользователь не найден"));
    }

    @Test
    void addShouldThrowNotFoundExceptionWhenItemNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(itemRepository.findById(2L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.add(1L, BookingDto.builder().itemId(2L).build()));
        assertTrue(exception.getMessage().contains("Такого айтема не существует"));
    }

    @Test
    void addShouldThrowNotAvailableExceptionWhenItemNotAvailable() {
        User user = new User(1L, "User", "user@example.com");
        Item item = Item.builder().id(2L).available(false).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(item));

        NotAvailableException exception = assertThrows(NotAvailableException.class,
                () -> bookingService.add(1L, BookingDto.builder().itemId(2L).build()));
        assertEquals("Айтем не доступен", exception.getMessage());
    }

    @Test
    void addShouldThrowConditionsNotMetExceptionWhenEndBeforeStart() {
        User user = new User(1L, "User", "user@example.com");
        Item item = Item.builder().id(2L).available(true).build();

        BookingDto invalidDto = BookingDto.builder()
                .itemId(2L)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(item));

        ConditionsNotMetException exception = assertThrows(ConditionsNotMetException.class,
                () -> bookingService.add(1L, invalidDto));
        assertTrue(exception.getMessage().contains("Время завершения меньше"));
    }

    @Test
    void approveShouldApproveBookingWhenOwner() {
        Long ownerId = 3L;
        Long bookerId = 4L;
        Long bookingId = 100L;

        User owner = User.builder()
                .id(ownerId)
                .name("Owner")
                .email("owner@example.com")
                .build();
        User booker = User.builder()
                .id(bookerId)
                .name("Owner")
                .email("owner@example.com")
                .build();

        Item item = Item.builder()
                .id(2L)
                .owner(owner)
                .build();

        Booking booking = Booking.builder()
                .id(bookingId)
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();

        Booking approvedBooking = Booking.builder()
                .id(bookingId)
                .item(item)
                .status(Status.APPROVED)
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(approvedBooking);

        Booking result = bookingService.approve(ownerId, bookingId, true);

        assertEquals(Status.APPROVED, result.getStatus());
    }

    @Test
    void approveShouldRejectBookingWhenOwnerAndApprovedFalse() {
        Long ownerId = 3L;
        Long bookerId = 4L;
        Long bookingId = 100L;

        User owner = new User(ownerId, "Owner", "owner@example.com");
        User booker = new User(bookerId, "Booker", "booker@example.com");
        Item item = Item.builder().id(2L).owner(owner).build();
        Booking booking = Booking.builder()
                .id(bookingId)
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        bookingService.approve(ownerId, bookingId, false);

        verify(bookingRepository).save(argThat(b -> b.getStatus() == Status.REJECTED));
    }

    @Test
    void approveShouldThrowNotAvailableExceptionWhenNotOwner() {
        Long nonOwnerId = 999L;
        Long bookingId = 100L;

        User owner = new User(3L, "Owner", "owner@example.com");
        Item item = Item.builder().id(2L).owner(owner).build();
        Booking booking = Booking.builder().item(item).build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        NotAvailableException exception = assertThrows(NotAvailableException.class,
                () -> bookingService.approve(nonOwnerId, bookingId, true));
        assertTrue(exception.getMessage().contains("Поддтвердить бронь может только владелец"));
    }

    @Test
    void getBookingShouldReturnBookingWhenBooker() {
        Long bookerId = 1L;
        Long bookingId = 100L;

        User booker = new User(bookerId, "Booker", "booker@example.com");
        User owner = new User(2L, "Owner", "owner@example.com");
        Item item = Item.builder().id(3L).owner(owner).build();
        Booking booking = Booking.builder()
                .id(bookingId)
                .booker(booker)
                .item(item)
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        Booking result = bookingService.getBooking(bookerId, bookingId);
        assertEquals(bookingId, result.getId());
    }

    @Test
    void getBookingShouldReturnBookingWhenOwner() {
        Long ownerId = 2L;
        Long bookingId = 100L;

        User booker = new User(1L, "Booker", "booker@example.com");
        User owner = new User(ownerId, "Owner", "owner@example.com");
        Item item = Item.builder().id(3L).owner(owner).build();
        Booking booking = Booking.builder()
                .id(bookingId)
                .booker(booker)
                .item(item)
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        Booking result = bookingService.getBooking(ownerId, bookingId);
        assertEquals(bookingId, result.getId());
    }

    @Test
    void getBookingShouldThrowNotFoundExceptionWhenNotOwnerOrBooker() {
        Long strangerId = 999L;
        Long bookingId = 100L;

        User booker = new User(1L, "Booker", "booker@example.com");
        User owner = new User(2L, "Owner", "owner@example.com");
        Item item = Item.builder().id(3L).owner(owner).build();
        Booking booking = Booking.builder()
                .id(bookingId)
                .booker(booker)
                .item(item)
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getBooking(strangerId, bookingId));
        assertTrue(exception.getMessage().contains("У вас нет такого бронирования"));
    }

    @Test
    void getByStateShouldReturnAllBookingsWhenStateAll() {
        Long userId = 1L;
        List<Booking> bookings = List.of(new Booking(), new Booking());

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(userId)).thenReturn(bookings);

        List<Booking> result = bookingService.getByState(userId, "ALL");
        assertEquals(2, result.size());
        verify(bookingRepository).findAllByBookerIdOrderByStartDesc(userId);
    }

    @Test
    void getByStateShouldReturnPastBookings() {
        Long userId = 1L;
        List<Booking> bookings = List.of(new Booking());

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(eq(userId), any(LocalDateTime.class)))
                .thenReturn(bookings);

        List<Booking> result = bookingService.getByState(userId, "PAST");
        assertEquals(1, result.size());
    }

    @Test
    void getOwnerBookingShouldReturnAllOwnerBookings() {
        Long ownerId = 1L;
        List<Booking> bookings = List.of(new Booking());

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId)).thenReturn(bookings);

        List<Booking> result = bookingService.getOwnerBooking(ownerId, "ALL");
        assertEquals(1, result.size());
        verify(bookingRepository).findAllByItemOwnerIdOrderByStartDesc(ownerId);
    }
}