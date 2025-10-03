package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@Data
@Builder
public class ItemInfoDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long owner;

    private Long request;

    private BookingInfoDto lastBooking;

    private BookingInfoDto nextBooking;

    private List<Comment> comments;
}