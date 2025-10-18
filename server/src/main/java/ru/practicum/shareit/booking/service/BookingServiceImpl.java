package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Booking add(long userId, BookingDto bookingDto) {
        User booker = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден: " + userId));

        Long itemId = bookingDto.getItemId();
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Такого айтема не существует" + itemId));

        if (!item.getAvailable()) {
            throw new NotAvailableException("Айтем не доступен");
        }

        if (!bookingDto.getEnd().isAfter(bookingDto.getStart())) {
            throw new ConditionsNotMetException("Время завершения меньше чем время начала бронирования");
        }
        log.info("Создание бронирования {} - {}", item.getId(), booker.getId());
        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking approve(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронь не найдена: " + bookingId));
        Item item = booking.getItem();

        if (!userId.equals(item.getOwner().getId())) {
            throw new NotAvailableException("Поддтвердить бронь может только владелец объекта.");
        }
        log.info("Бронирование подтверждено {} - {}", booking.getItem().getId(), booking.getBooker().getId());
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);

        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBooking(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        Item item = booking.getItem();

        if (userId != item.getOwner().getId() && userId != booking.getBooker().getId()) {
            throw new NotFoundException("У вас нет такого бронирования");
        }

        return booking;
    }

    @Override
    public List<Booking> getByState(long userId, String state) {
        State gotState = State.from(state);
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден: " + userId));

        return switch (gotState) {
            case ALL -> bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
            case PAST -> bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
            case FUTURE ->
                    bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
            case CURRENT ->
                    bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
            case WAITING -> bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
            case REJECTED -> bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
        };
    }

    @Override
    public List<Booking> getOwnerBooking(long userId, String state) {
        State gotState = State.from(state);
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден: " + userId));

        return switch (gotState) {
            case ALL -> bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
            case PAST -> bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
            case FUTURE ->
                    bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
            case CURRENT ->
                    bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
            case WAITING -> bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
            case REJECTED -> bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
        };
    }
}
