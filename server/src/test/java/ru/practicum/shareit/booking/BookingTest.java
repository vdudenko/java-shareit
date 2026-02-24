package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BookingTest {

    @Test
    void builderShouldCreateBookingWithAllFields() {
        // Given
        Long id = 100L;
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusDays(2);
        User booker = new User(1L, "Booker", "booker@example.com");
        Item item = Item.builder()
                .id(2L)
                .name("Дрель")
                .available(true)
                .build();
        Status status = Status.APPROVED;

        // When
        Booking booking = Booking.builder()
                .id(id)
                .start(start)
                .end(end)
                .booker(booker)
                .item(item)
                .status(status)
                .build();

        // Then
        assertThat(booking.getId()).isEqualTo(id);
        assertThat(booking.getStart()).isEqualTo(start);
        assertThat(booking.getEnd()).isEqualTo(end);
        assertThat(booking.getBooker()).isEqualTo(booker);
        assertThat(booking.getItem()).isEqualTo(item);
        assertThat(booking.getStatus()).isEqualTo(status);
    }

    @Test
    void equalsShouldReturnTrueWhenIdsAreEqual() {
        Booking booking1 = Booking.builder().id(1L).build();
        Booking booking2 = Booking.builder().id(1L).build();

        assertThat(booking1).isEqualTo(booking2);
    }

    @Test
    void equalsShouldReturnFalseWhenIdsAreDifferent() {
        Booking booking1 = Booking.builder().id(1L).build();
        Booking booking2 = Booking.builder().id(2L).build();

        assertThat(booking1).isNotEqualTo(booking2);
    }

    @Test
    void equalsShouldReturnFalseWhenOtherObjectIsNotBooking() {
        Booking booking = Booking.builder().id(1L).build();
        String notBooking = "not a booking";

        assertThat(booking).isNotEqualTo(notBooking);
    }

    @Test
    void equalsShouldReturnFalseWhenIdIsNull() {
        Booking booking1 = Booking.builder().id(null).build();
        Booking booking2 = Booking.builder().id(null).build();

        assertThat(booking1).isNotEqualTo(booking2); // по вашей логике: id != null → false
    }

    @Test
    void hashCodeShouldBeBasedOnId() {
        Booking booking = Booking.builder().id(42L).build();
        assertThat(booking.hashCode()).isEqualTo(42);
    }

    @Test
    void gettersAndSettersShouldWorkCorrectly() {
        Booking booking = new Booking();
        LocalDateTime now = LocalDateTime.now();

        booking.setId(1L);
        booking.setStart(now);
        booking.setEnd(now.plusHours(2));
        booking.setBooker(new User(1L, "User", "user@example.com"));
        booking.setItem(Item.builder().id(2L).build());
        booking.setStatus(Status.WAITING);

        assertThat(booking.getId()).isEqualTo(1L);
        assertThat(booking.getStart()).isEqualTo(now);
        assertThat(booking.getEnd()).isEqualTo(now.plusHours(2));
        assertThat(booking.getBooker().getId()).isEqualTo(1L);
        assertThat(booking.getItem().getId()).isEqualTo(2L);
        assertThat(booking.getStatus()).isEqualTo(Status.WAITING);
    }
}
