package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    Booking add(long userId, BookingDto booking);

    Booking approve(Long userId, Long bookingId, Boolean approved);

    Booking getBooking(long userId, long bookingId);

    List<Booking> getByState(long userId, String state);

    List<Booking> getOwnerBooking(long userId, String state);
}
