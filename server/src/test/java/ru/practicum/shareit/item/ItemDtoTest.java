package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoTest {
    @Autowired
    private JacksonTester<CommentDto> commentDtoJacksonTester;

    @Autowired
    private JacksonTester<CommentInfoDto> commentInfoDtoJacksonTester;

    @Autowired
    private JacksonTester<ItemCreateDto> itemCreateDtoJacksonTester;

    @Autowired
    private JacksonTester<ItemDto> itemDtoJacksonTester;

    @Autowired
    private JacksonTester<ItemInfoDto> itemInfoDtoJacksonTester;

    @Test
    void testCommentDto() throws Exception {
        CommentDto commentDto = new CommentDto("test");

        JsonContent<CommentDto> result = commentDtoJacksonTester.write(commentDto);

        assertThat(result).hasJsonPath("$.text");
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(commentDto.getText());

        CommentDto commentDtoForTest = commentDtoJacksonTester.parseObject(result.getJson());

        assertThat(commentDtoForTest).isEqualTo(commentDto);
    }

    @Test
    void testCommentInfoDto() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        CommentInfoDto commentInfoDto = CommentInfoDto.builder()
                .id(1L)
                .text("test")
                .authorName("Vadim")
                .created(now)
                .build();

        JsonContent<CommentInfoDto> result = commentInfoDtoJacksonTester.write(commentInfoDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(commentInfoDto.getId().intValue());

        assertThat(result).hasJsonPath("$.text");
        assertThat(result).extractingJsonPathStringValue("$.text")
                .isEqualTo(commentInfoDto.getText());

        assertThat(result).hasJsonPath("$.authorName");
        assertThat(result).extractingJsonPathStringValue("$.authorName")
                .isEqualTo(commentInfoDto.getAuthorName());

        assertThat(result).hasJsonPath("$.created");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(commentInfoDto.getCreated().toString());

        CommentInfoDto commentInfoDtoForTest = commentInfoDtoJacksonTester.parseObject(result.getJson());

        assertThat(commentInfoDtoForTest).isEqualTo(commentInfoDto);
    }

    @Test
    void testItemCreateDto() throws Exception {
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("test")
                .description("desc")
                .available(true)
                .requestId(1L)
                .build();

        JsonContent<ItemCreateDto> result = itemCreateDtoJacksonTester.write(itemCreateDto);

        assertThat(result).hasJsonPath("$.name");
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo(itemCreateDto.getName());

        assertThat(result).hasJsonPath("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemCreateDto.getDescription());

        assertThat(result).hasJsonPath("$.available");
        assertThat(result).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemCreateDto.getAvailable());

        assertThat(result).hasJsonPath("$.requestId");
        assertThat(result).extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(itemCreateDto.getRequestId().intValue());

        ItemCreateDto itemCreateDtoForTest = itemCreateDtoJacksonTester.parseObject(result.getJson());

        assertThat(itemCreateDtoForTest).isEqualTo(itemCreateDto);
    }

    @Test
    void testItemDto() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .name("test")
                .description("desc")
                .available(true)
                .build();

        JsonContent<ItemDto> result = itemDtoJacksonTester.write(itemDto);

        assertThat(result).hasJsonPath("$.name");
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo(itemDto.getName());

        assertThat(result).hasJsonPath("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemDto.getDescription());

        assertThat(result).hasJsonPath("$.available");
        assertThat(result).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemDto.getAvailable());

        ItemDto itemDtoForTest = itemDtoJacksonTester.parseObject(result.getJson());

        assertThat(itemDtoForTest).isEqualTo(itemDto);
    }


    @Test
    void testItemInfoDto() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        LocalDateTime start2 = end.plusDays(2);
        LocalDateTime end2 = start2.plusDays(1);

        BookingInfoDto lastBooking = BookingInfoDto.builder().start(start).end(end).build();
        BookingInfoDto nextBooking = BookingInfoDto.builder().start(start2).end(end2).build();

        List<Comment> comments = List.of(Comment.builder().id(1L).text("test").build());

        ItemInfoDto itemInfoDto = ItemInfoDto.builder()
                .id(1L)
                .name("test")
                .description("desc")
                .available(true)
                .owner(1L)
                .request(1L)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();

        JsonContent<ItemInfoDto> result = itemInfoDtoJacksonTester.write(itemInfoDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(itemInfoDto.getId().intValue());

        assertThat(result).hasJsonPath("$.name");
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo(itemInfoDto.getName());

        assertThat(result).hasJsonPath("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemInfoDto.getDescription());

        assertThat(result).hasJsonPath("$.available");
        assertThat(result).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemInfoDto.getAvailable());

        assertThat(result).hasJsonPath("$.owner");
        assertThat(result).extractingJsonPathNumberValue("$.owner")
                .isEqualTo(itemInfoDto.getOwner().intValue());

        assertThat(result).hasJsonPath("$.request");
        assertThat(result).extractingJsonPathNumberValue("$.request")
                .isEqualTo(itemInfoDto.getRequest().intValue());

        assertThat(result).hasJsonPath("$.lastBooking");
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.start")
                .isEqualTo(lastBooking.getStart().toString());
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.end")
                .isEqualTo(lastBooking.getEnd().toString());

        assertThat(result).hasJsonPath("$.nextBooking");
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.start")
                .isEqualTo(nextBooking.getStart().toString());
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.end")
                .isEqualTo(nextBooking.getEnd().toString());

        assertThat(result).hasJsonPath("$.comments");
        assertThat(result).extractingJsonPathValue("$.comments.length()").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("test");

        ItemInfoDto itemInfoDtoForTest = itemInfoDtoJacksonTester.parseObject(result.getJson());

        assertThat(itemInfoDtoForTest).isEqualTo(itemInfoDto);
    }
}
