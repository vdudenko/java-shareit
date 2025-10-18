package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.utils.HeaderConstants.USER_ID_HEADER;

import java.time.LocalDateTime;
import java.util.List;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingServiceImpl bookingService;
    private final LocalDateTime start = LocalDateTime.now().plusDays(1);
    private final LocalDateTime end = start.plusDays(2);

    @Test
    void add() throws Exception {
        Long userId = 1L;
        Long userId2 = 2L;
        Long itemId = 2L;
        User owner = User.builder().id(userId).build();
        User booker = User.builder().id(userId2).build();

        BookingDto requestDto = BookingDto.builder()
                .itemId(itemId)
                .start(start)
                .end(end)
                .build();

        Item item = Item.builder()
                .id(itemId)
                .name("Дрель")
                .description("Профессиональная")
                .available(true)
                .owner(owner)
                .build();

        Booking responseBooking = Booking.builder()
                .id(100L)
                .item(item)
                .start(start)
                .end(end)
                .booker(booker)
                .build();

        Mockito.when(bookingService.add(Mockito.eq(userId), Mockito.any(BookingDto.class)))
                .thenReturn(responseBooking);

        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.item.id").value(itemId)) // ← исправлено!
                .andExpect(jsonPath("$.start").value(start.toString()))
                .andExpect(jsonPath("$.end").value(end.toString()));
    }

    @Test
    void approve() throws Exception {
        Long userId = 1L;
        Long bookingId = 100L;
        Boolean approved = true;

        Booking approvedBooking = Booking.builder()
                .id(bookingId)
                .status(Status.APPROVED)
                .build();

        Mockito.when(bookingService.approve(Mockito.eq(userId), Mockito.eq(bookingId), Mockito.eq(approved)))
                .thenReturn(approvedBooking);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(USER_ID_HEADER, userId)
                        .param("approved", approved.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getBooking() throws Exception {
        Long userId = 1L;
        Long userId2 = 2L;
        Long itemId = 1L;
        Long bookingId = 100L;
        User owner = User.builder().id(userId).build();
        User booker = User.builder().id(userId2).build();
        Item item = Item.builder()
                .id(itemId)
                .name("Дрель")
                .description("Профессиональная")
                .available(true)
                .owner(owner)
                .build();

        Booking booking = Booking.builder()
                .id(bookingId)
                .item(item)
                .start(start)
                .end(end)
                .booker(booker)
                .build();

        Mockito.when(bookingService.getBooking(Mockito.eq(userId), Mockito.eq(bookingId)))
                .thenReturn(booking);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.item.id").value(1));
    }

    @Test
    void getByState() throws Exception {
        Long userId = 1L;
        String state = "CURRENT";
        User owner = User.builder().id(userId).build();
        Item item = Item.builder()
                .id(1L)
                .name("Дрель")
                .description("Профессиональная")
                .available(true)
                .owner(owner)
                .build();
        Item item2 = Item.builder()
                .id(2L)
                .name("Дрель")
                .description("Профессиональная")
                .available(true)
                .owner(owner)
                .build();

        Booking booking1 = Booking.builder().id(1L).item(item).build();
        Booking booking2 = Booking.builder().id(2L).item(item2).build();
        List<Booking> bookings = List.of(booking1, booking2);

        Mockito.when(bookingService.getByState(Mockito.eq(userId), Mockito.eq(state)))
                .thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, userId)
                        .param("state", state))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void getOwnerBooking() throws Exception {
        Long userId = 1L;
        String state = "ALL";
        User owner = User.builder().id(userId).build();
        Item item = Item.builder()
                .id(1L)
                .name("Дрель")
                .description("Профессиональная")
                .available(true)
                .owner(owner)
                .build();

        Booking booking = Booking.builder().id(1L).item(item).build();
        List<Booking> bookings = List.of(booking);

        Mockito.when(bookingService.getOwnerBooking(Mockito.eq(userId), Mockito.eq(state)))
                .thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, userId)
                        .param("state", state))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));
    }
}
