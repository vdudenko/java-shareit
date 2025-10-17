package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.enums.Status;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> bookingDtoJacksonTester;

    @Autowired
    private JacksonTester<BookingInfoDto> bookingInfoDtoJacksonTester;

    @Test
    void testBookingDto() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(2);
        BookingDto bookingDto = BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(1L)
                .booker(2L)
                .status(Status.APPROVED)
                .build();

        JsonContent<BookingDto> result = bookingDtoJacksonTester.write(bookingDto);

        BookingDto parsed = bookingDtoJacksonTester.parseObject(result.getJson());

        assertThat(result).hasJsonPath("$.start");
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingDto.getStart().toString());

        assertThat(result).hasJsonPath("$.end");
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(bookingDto.getEnd().toString());

        assertThat(result).hasJsonPath("$.itemId");
        assertThat(result).extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(bookingDto.getItemId().intValue());

        assertThat(result).hasJsonPath("$.booker");
        assertThat(result).extractingJsonPathNumberValue("$.booker")
                .isEqualTo(bookingDto.getBooker().intValue());

        assertThat(result).hasJsonPath("$.status");
        assertThat(result).extractingJsonPathStringValue("$.status")
                .isEqualTo(bookingDto.getStatus().name());

        BookingDto commentDtoForTest = bookingDtoJacksonTester.parseObject(result.getJson());

        assertThat(commentDtoForTest).isEqualTo(bookingDto);
    }

    @Test
    void testBookingInfoDto() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(2);
        BookingInfoDto bookingInfoDto = BookingInfoDto.builder()
                .start(start)
                .end(end)
                .build();

        JsonContent<BookingInfoDto> result = bookingInfoDtoJacksonTester.write(bookingInfoDto);

        assertThat(result).hasJsonPath("$.start");
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingInfoDto.getStart().toString());

        assertThat(result).hasJsonPath("$.end");
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(bookingInfoDto.getEnd().toString());

        BookingInfoDto bookingInfoDtoForTest = bookingInfoDtoJacksonTester.parseObject(result.getJson());

        assertThat(bookingInfoDtoForTest).isEqualTo(bookingInfoDto);
    }
}
