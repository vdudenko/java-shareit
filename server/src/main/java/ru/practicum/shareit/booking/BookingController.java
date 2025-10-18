package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import java.util.List;
import static ru.practicum.shareit.utils.HeaderConstants.USER_ID_HEADER;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public Booking add(@RequestHeader(USER_ID_HEADER) long userId, @Valid @RequestBody BookingDto bookingDto) {
        return bookingService.add(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public Booking approve(@RequestHeader(USER_ID_HEADER) Long userId, @PathVariable Long bookingId, @RequestParam Boolean approved) {
        return bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public Booking getBooking(@RequestHeader(USER_ID_HEADER) long userId, @PathVariable long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<Booking> getByState(@RequestHeader(USER_ID_HEADER) long userId, @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getByState(userId, state);
    }

    @GetMapping("/owner")
    public List<Booking> getOwnerBooking(@RequestHeader(USER_ID_HEADER) long userId, @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getOwnerBooking(userId, state);
    }
}
